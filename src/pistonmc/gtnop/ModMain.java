package pistonmc.gtnop;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import codechicken.nei.api.API;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import pistonmc.gtnop.api.GTNEIOreAPI;
import pistonmc.gtnop.api.IOreLayer;
import pistonmc.gtnop.api.IOreManager;
import pistonmc.gtnop.api.IOreSmall;
import pistonmc.gtnop.api.IOreWorld;
import pistonmc.gtnop.api.IOreWorldConfig;
import pistonmc.gtnop.api.IOreWorldNamed;
import pistonmc.gtnop.api.IWorldInit;
import pistonmc.gtnop.core.BlockWorld;
import pistonmc.gtnop.core.DimNamed;
import pistonmc.gtnop.gt5.GTDim;
import pistonmc.gtnop.gt5.GTManagerOreSmall;
import pistonmc.gtnop.gt5.GTManagerOreLayer;
import pistonmc.gtnop.gt5.GTSupport;
import pistonmc.gtnop.integration.TwilightForest;

@Mod(
    modid = ModInfo.MODID,
    version = ModInfo.VERSION, 
    dependencies = "required-after:gregtech;required-after:NotEnoughItems;after:TwilightForest")
public class ModMain extends GTNEIOreAPI {
    public static Logger logger = LogManager.getLogger(ModInfo.NAME);
    @Mod.Instance(ModInfo.MODID)
    public static ModMain instance;

    static class OreWorldRegistrationRequest {
        Side side;
        IOreWorld oreWorld;
    }
    private List<OreWorldRegistrationRequest> oreWorldRegistrationRequests = new ArrayList<>();

    public final List<IOreWorldConfig> oreWorldConfigs = new ArrayList<>();
    public final List<IOreWorld> oreWorlds = new ArrayList<>();
    public final List<IOreManager<IOreLayer>> oreLayerManagers = new ArrayList<>();
    public final List<IOreManager<IOreSmall>> oreSmallManagers = new ArrayList<>();

    public ModMain() {
        logger.info("API is now available for myself and other mods to call!");
    }

    @Override
    public String getVersion() {
        return ModInfo.VERSION;
    }

    @Override
    public String getModName() {
        return ModInfo.NAME;
    }

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        GTSupport gtSupport = GTSupport.getInstance();
        gtSupport.check();
        this.checkModSupport(gtSupport);

        this.registerOreWorldConfig(new TwilightForest());
        this.registerOreLayerManager(new GTManagerOreLayer());
        this.registerOreSmallManager(new GTManagerOreSmall());
    }

    @EventHandler 
    public void onInit(FMLInitializationEvent event) {
        logger.info("Loading dimensions from config");
        ConfigHandler config = new ConfigHandler();

        if (config.getDimList().isEmpty()) {
            logger.info("No dimensions found in config, generating default config");
            // only generate default config if no config exists
            for (IOreWorldConfig c: this.oreWorldConfigs) {
                if (!c.isEnabled()) {
                    continue;
                }
                String cDimName = c.getDimensionName();
                boolean found = false;
                for (IOreWorldNamed dim: config.getDimList()) {
                    if (dim.getDimensionName().equals(cDimName)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    logger.info("Adding dimension " + cDimName + " to config");
                    config.addDim(c.createOreWorld());
                }
            }
            config.save();
        }

        GTSupport gtSupport = GTSupport.getInstance();
        // register default world handlers
        this.registerOreWorldInternal(event.getSide(), new GTDim.Overworld());
        this.registerOreWorldInternal(event.getSide(), new GTDim.Nether());
        this.registerOreWorldInternal(event.getSide(), new GTDim.End());
        if (gtSupport.oreLayerEndAsteroids) {
            this.registerOreWorldInternal(event.getSide(), new GTDim.EndAsteroid());
        }
        if (gtSupport.oreLayerMoon || gtSupport.oreSmallMoon) {
            this.registerOreWorldInternal(event.getSide(), new GTDim.Moon());
        }
        if (gtSupport.oreLayerMars || gtSupport.oreSmallMars) {
            this.registerOreWorldInternal(event.getSide(), new GTDim.Mars());
        }
        if (gtSupport.oreLayerAsteroids) {
            this.registerOreWorldInternal(event.getSide(), new GTDim.Asteroids());
        }

        // register world handlers from config
        for (IOreWorld dim: config.getDimList()) {
            this.registerOreWorldInternal(event.getSide(), dim);
        }

        List<OreWorldRegistrationRequest> requests = this.oreWorldRegistrationRequests;
        this.oreWorldRegistrationRequests = null;
        for (OreWorldRegistrationRequest request: requests) {
            this.registerOreWorldInternal(request.side, request.oreWorld);
        }

        if (event.getSide() == Side.CLIENT) {
            logger.info("Registering NEI handler infos");
            for (IOreManager<?> manager: this.oreLayerManagers) {
                this.registerNEIHandlerInfo(manager);
            }
            for (IOreManager<?> manager: this.oreSmallManagers) {
                this.registerNEIHandlerInfo(manager);
            }
        }
    }

    @EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event) {
        if (event.getSide() != Side.CLIENT) {
            return;
        }

        // init config dim icons
        logger.info("Initializing dimension icons");
        List<IWorldInit> extraWorlds = new ArrayList<>(this.oreWorlds.size());
        for (IOreWorld dim : this.oreWorlds) {
            if (!(dim instanceof IOreWorldNamed)) {
                continue;
            }
            IOreWorldNamed dimNamed = (IOreWorldNamed) dim;
            dimNamed.initIcon();
            extraWorlds.add(dimNamed.createWorldInit());
        }

        // initialize managers
        logger.info("Initializing managers");
        for (IOreManager<?> manager: this.oreLayerManagers) {
            manager.init(this.oreWorlds, extraWorlds);
        }
        for (IOreManager<?> manager: this.oreSmallManagers) {
            manager.init(this.oreWorlds, extraWorlds);
        }

        MinecraftForge.EVENT_BUS.register(new TooltipHandler());
    }

    private void checkModSupport(GTSupport gtSupport) {
        if (!Loader.isModLoaded("GalacticraftCore")) {
            logger.info("Galacticraft not found, disabling Galacticraft support");
            gtSupport.oreLayerMoon = false;
            gtSupport.oreLayerMars = false;
            gtSupport.oreLayerAsteroids = false;
            gtSupport.oreSmallMoon = false;
            gtSupport.oreSmallMars = false;
        }
    }

    private void registerNEIHandlerInfo(IOreManager<?> manager) {
        NBTTagCompound nbt = new NBTTagCompound();
        String managerId = manager.getCleanedName();
        nbt.setString("handler", NEIModConfig.managerIdToHandlerId(managerId));
        nbt.setString("modName", manager.getModName());
        nbt.setString("modId", ModInfo.MODID);
        nbt.setBoolean("modRequired", true);
        nbt.setString("itemName", manager.getIconString());
        nbt.setInteger("handlerHeight", 160);
        nbt.setInteger("handlerWidth", NEIModConfig.GUI_WIDTH);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerHandlerInfo", nbt);
    }

    @Override
    public IOreWorldNamed createOreWorld(String name, String icon) {
        return new DimNamed(name, icon);
    }

    @Override
    public void registerOreWorld(Side side, IOreWorld oreWorld) {
        if (this.oreWorldRegistrationRequests == null) {
            throw new RuntimeException("You are registering too late. Do it in pre-init phase");
        }
        OreWorldRegistrationRequest request = new OreWorldRegistrationRequest();
        request.side = side;
        request.oreWorld = oreWorld;
        this.oreWorldRegistrationRequests.add(request);
    }

    private void registerOreWorldInternal(Side side, IOreWorld oreWorld) {
        logger.info("Registering ore world " + oreWorld.getUnlocalizedName());
        this.oreWorlds.add(oreWorld);
        int renderId = 0;
        if (side == Side.CLIENT) {
            renderId = RenderingRegistry.getNextAvailableRenderId();
        }
        BlockWorld block = new BlockWorld(renderId, oreWorld);
        BlockWorld.register(block);
        GameRegistry.registerBlock(block, "gtnop.block_dim." + oreWorld.getCleanedName());
        if (side == Side.CLIENT) {
            RenderHandler handler = new RenderHandler(block);
            RenderingRegistry.registerBlockHandler(renderId, handler);
            API.hideItem(new ItemStack(block));
        }
    }

    @Override
    public void registerOreWorldConfig(IOreWorldConfig config) {
        this.oreWorldConfigs.add(config);
    }


    @Override
    public void registerOreLayerManager(IOreManager<IOreLayer> manager) {
        this.oreLayerManagers.add((IOreManager<IOreLayer>) manager);
    }

    @Override
    public void registerOreSmallManager(IOreManager<IOreSmall> manager) {
        this.oreSmallManagers.add((IOreManager<IOreSmall>) manager);
    }

    
    @Override
    public IOreWorld getOreWorldFromItemStack(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock) item).field_150939_a;
            if (block instanceof BlockWorld) {
                return ((BlockWorld) block).getOreWorld();
            }
        }
        return null;
    }

}
