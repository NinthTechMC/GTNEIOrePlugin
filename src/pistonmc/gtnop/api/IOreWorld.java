package pistonmc.gtnop.api;

import net.minecraft.block.Block;
import net.minecraft.util.StatCollector;

/**
 * Handler for ore stats in a world
 */
public interface IOreWorld {
    /** Get a icon used to represent this world */
    public Block getIcon();

    public default int getIconMeta() {
        return 0;
    }

    /** 
     * Get the cleaned name of this world 
     *
     * Cleaned name should not contain spaces
     */
    public String getCleanedName();

    /** Get the unlocalized name of this world */
    public default String getUnlocalizedName() {
        return "gtnop.world." + this.getCleanedName();
    }

    public default String getLocalizedName() {
        return StatCollector.translateToLocal(this.getUnlocalizedName());
    }

    /**
     * Called for every IOreLayer that is generated in this world
     * for the given manager
     */
    public void registerOreLayer(IOreManager<?> manager, IOreLayer ore);

    /**
     * Get the sum of all vein weights that could generate in this world
     * for the given manager
     */
    public int getTotalOreLayerWeight(IOreManager<?> manager);

}
