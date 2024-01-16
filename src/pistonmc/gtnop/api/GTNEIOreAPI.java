package pistonmc.gtnop.api;

import java.util.List;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Interface for other mods to use GTNEIOrePlugin
 */
public abstract class GTNEIOreAPI {
    private static GTNEIOreAPI instance;

    /**
     * Get the API instance. This is available after the pre-init phase
     * of GTNEIOrePlugin. You mod should load after me
     */
    public static GTNEIOreAPI getInstance() {
        return instance;
    }

    protected GTNEIOreAPI() {
        instance = this;
    }

    public abstract String getVersion();
    public abstract String getModName();

    /**
     * If the stack is an item that this mod uses to represent a dimension,
     * returns that IOreWorld. Otherwise returns null
     */
    public abstract IOreWorld getOreWorldFromItemStack(ItemStack stack);

    /**
     * Register a world to be added to the config by default.
     * Note that it will only be added if a config file doesn't exist
     *
     * Call this on pre-init phase of your mod.
     *
     * See TwilightForest.java for example
     */
    public abstract void registerOreWorldConfig(IOreWorldConfig config);

    /**
     * Create a ore world by name that you can register later
     *
     * The name should be what you get by calling world.provider.getDimensionName()
     * of the actual World
     *
     * @param name The name of the dimension
     * @param icon The MODID:BLOCKNAME:META of the icon block
     */
    public abstract IOreWorldNamed createOreWorld(String name, String icon);

    /**
     * Register a dimension that contains GT ore data
     *
     * Call this in pre-init phase of your mod.
     *
     * @param side The side to register on
     */
    public abstract void registerOreWorld(Side side, IOreWorld oreWorld);

    /**
     * Register a manager for a vein type.
     *
     * Call this in pre-init or init phase of your mod. The manager itself
     * will be initialized in the post-init phase
     *
     * Each manager will have its own NEI tab
     */
    public abstract void registerOreLayerManager(IOreManager<IOreLayer> manager);

    /**
     * Register a manager for single-ore-block type
     *
     * Call this in pre-init or init phase of your mod. The manager itself
     * will be initialized in the post-init phase
     *
     * Each manager will have its own NEI tab
     */
    public abstract void registerOreSmallManager(IOreManager<IOreSmall> manager);

    /**
     * Set the tooltip on ore stacks.
     *
     * This is used to display vein layouts in GT, but can be used for anything
     * with the proper localization entries added
     */
    public static List<ItemStack> setLayoutTooltipNBT(List<ItemStack> stacks, String genTypeKey, EnumRarity rarity) {
        for (ItemStack s: stacks) {
            NBTTagCompound nbt = s.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                s.setTagCompound(nbt);
            }
            nbt.setString(NBT_ORE_GEN_TYPE, genTypeKey);
            nbt.setInteger(NBT_ORE_RARITY, rarity.ordinal());
        }
        return stacks;
    }

    /**
     * Set the chance tooltip on dimensions
     */
    public static ItemStack setChanceTooltipNBT(ItemStack s, float chance) {
        NBTTagCompound nbt = s.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            s.setTagCompound(nbt);
        }
        nbt.setFloat(NBT_ORE_CHANCE, chance);
        return s;
    }

    public static final String NBT_ORE_GEN_TYPE = "GTNEIOreGenType";
    public static final String NBT_ORE_RARITY = "GTNEIOreRarity";
    public static final String NBT_ORE_CHANCE = "GTNEIOreChance";
}
