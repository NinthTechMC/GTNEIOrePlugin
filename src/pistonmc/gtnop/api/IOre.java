package pistonmc.gtnop.api;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

/**
 * Shared properties of ores
 */
public interface IOre {
    /** 
     * Get the internal name for this ore/vein type
     *
     * Used to identify registered ore instances
     */
    public String getName();

    /**
     * Override this to not use the default gtnop.ore_stat.<name> translation
     */
    public default String getUnlocalizedName() {
        return "gtnop.ore_stat." + this.getName();
    }

    /**
     * This is displayed in the NEI tab as the first line
     *
     * Override this if the ore has a special translation logic
     */
    public default String getLocalizedName() {
        return StatCollector.translateToLocal(this.getUnlocalizedName());
    }

    /**
     * Initialize the ore
     */
    public void init(IOreManager<?> manager, List<IOreWorld> worlds, List<IWorldInit> extraWorlds);

    public String getRestrictedBiomeInfo();

    public int getMinY();

    public int getMaxY();

    public boolean isGeneratedIn(IOreWorld dimension);

    /**
     * Get the list of items that are in this ore type
     *
     * Each inner list is displayed as a cycling item
     *
     * If a particular dimension is being viewed, it will be passed in. Otherwise the argument
     * will be null. Use this to filter out variants of ores and drops if some
     * are dimension-specific.
     */
    public List<List<ItemStack>> getOreStacks(IOreWorld oreWorld);

    /**
     * Get the list of items that are not ores, but can be dropped
     *
     * Each inner list is displayed as a cycling item
     *
     * If a particular dimension is being viewed, it will be passed in. Otherwise the argument
     * will be null. Use this to filter out variants of ores and drops if some
     * are dimension-specific.
     */
    public List<List<ItemStack>> getOtherDropStacks(IOreWorld oreWorld);

    /**
     * Compare dimension for display order
     */
    public int compareDimension(IOreWorldAccess a, IOreWorldAccess b);

    /**
     * Modify the displayed stack for a dimension. (Add tooltip, etc)
     */
    public void modifyDimensionStack(ItemStack stack, IOreWorld dimension);
}
