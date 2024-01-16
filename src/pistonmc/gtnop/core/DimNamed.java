package pistonmc.gtnop.core;

import java.util.HashMap;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import pistonmc.gtnop.api.IOreLayer;
import pistonmc.gtnop.api.IOreWorld;
import pistonmc.gtnop.api.IOreWorldNamed;
import pistonmc.gtnop.api.IWorldInit;
import pistonmc.gtnop.api.IOreManager;

/**
 * A dimension identified by name. Loaded from config file
 */
public class DimNamed implements IOreWorldNamed {
    private final String dimName;
    private final String iconString;

    private Block icon;
    private int iconMeta;

    private HashMap<IOreManager<?>, Integer> totalOreLayerWeight = new HashMap<>();

    public DimNamed(String dimName, String iconString) {
        this.dimName = dimName;
        this.iconString = iconString;
    }

    @Override
    public void registerOreLayer(IOreManager<?> manager, IOreLayer ore) {
        Integer total = this.totalOreLayerWeight.get(manager);
        if (total == null) {
            this.totalOreLayerWeight.put(manager, ore.getWeight());
        } else {
            this.totalOreLayerWeight.put(manager, total + ore.getWeight());
        }
    }

    @Override
    public int getTotalOreLayerWeight(IOreManager<?> manager) {
        return this.totalOreLayerWeight.getOrDefault(manager, 0);
    }

    @Override
    public String getDimensionName() {
        return this.dimName;
    }

    @Override
    public String getIconString() {
        return this.iconString;
    }

    @Override
    public IWorldInit createWorldInit() {
        final WorldFake world = new WorldFake(this.getDimensionName());
        return new IWorldInit() {

            @Override
            public IOreWorld getOreWorld() {
                return DimNamed.this;
            }

            @Override
            public World getWorldForQueryingGenerator() {
                return world;
            }
        };
    }

    @Override
    public Block getIcon() {
        if (this.icon != null) {
            return this.icon;
        }
        return Blocks.air;
    }

    @Override
    public int getIconMeta() {
        return this.iconMeta;
    }

    public void initIcon() {
        String[] parts = this.iconString.split(":");
        if (parts.length < 2) {
            this.icon = Blocks.air;
            return;
        }
        String modid = parts[0].trim();
        String name = parts[1].trim();
        Block block = GameRegistry.findBlock(modid, name);
        if (block == null) {
            this.icon = Blocks.air;
        } else {
            this.icon = block;
        }
        if (parts.length >= 3) {
            try {
                this.iconMeta = Integer.parseInt(parts[2].trim());
            } catch (NumberFormatException e) {
                this.iconMeta = 0;
            }
        }
    }

    @Override
    public String getCleanedName() {
        return this.dimName.replaceAll(" ", "");
    }



}
