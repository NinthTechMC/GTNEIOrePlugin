package pistonmc.gtnop.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;

/**
 * Manager for ores in a category (e.g. veins or small ores)
 */
public interface IOreManager<TOre extends IOre> {
    /**
     * Called on mod load complete to register ores with worlds
     *
     * This should also register all the ores so getAllOres() is fast
     */
    public void init(List<IOreWorld> allWorlds, List<IWorldInit> extraWorlds);

    /**
     * Get all registered ores
     */
    public List<TOre> getAllOres();

    /**
     * This is the id used in a number of places
     *
     * - registering handler id in NEI
     * - gtnop.gui.nei.title.<id> is the title for the NEI tab
     *
     * This is used before calling init()
     */
    public String getCleanedName();

    /**
     * Get the icon string like modid:name, used to display icon for the NEI tab
     *
     * This is used before calling init()
     */
    public default String getIconString() {
        return "minecraft:diamond_pickaxe";
    }

    /**
     * Get the mod name to display in the NEI tab
     *
     * This is used before calling init()
     */
    public default String getModName() {
        return GTNEIOreAPI.getInstance().getModName();
    }

    /**
     * Get related ore data when inspecting the given item stack
     *
     * Returns empty map if the given item stack is not related to any ore in this category
     */
    public default List<OreStat<TOre>> getRelated(ItemStack stack) {
        // if viewing a dimension, filter the stacks to only the ones in that dimension
        IOreWorld dimension = GTNEIOreAPI.getInstance().getOreWorldFromItemStack(stack);
        if (dimension != null) {
            return this.getAllInDimension(dimension);
        }

        List<OreStat<TOre>> stats = this.getOreStatsFor(stack);
        if (stats != null && !stats.isEmpty()) {
            return stats;
        }
        return Collections.emptyList();
    }

    public default List<OreStat<TOre>> getAllInDimension(IOreWorld dimension) {
        List<OreStat<TOre>> stats = new ArrayList<>();
        for (TOre ore: this.getAllOres()) {
            if (ore.isGeneratedIn(dimension)) {
                stats.add(new OreStat<>(ore, dimension));
            }
        }
        return stats;
    }

    /**
     * Get all ores in this category
     */
    public default List<OreStat<TOre>> getAllStats() {
        List<TOre> allOres = this.getAllOres();
        List<OreStat<TOre>> stats = new ArrayList<>(allOres.size());
        for (TOre ore: allOres) {
            stats.add(new OreStat<>(ore));
        }
        return Collections.unmodifiableList(stats);
    }

    /**
     * Get related ore data when inspecting the given (non-dimension) item stack
     *
     * Override if you need to override the filter behavior
     */
    public default List<OreStat<TOre>> getOreStatsFor(ItemStack stack) {
        List<TOre> ores = this.getOresFor(stack);
        if (ores == null) {
            return null;
        }
        List<OreStat<TOre>> stats = new ArrayList<>();
        for (TOre ore : ores) {
            stats.add(new OreStat<>(ore));
        }
        return stats;
    }

    /**
     * Get related ore data when inspecting the given (non-dimension) item stack
     */
    public List<TOre> getOresFor(ItemStack stack);

}
