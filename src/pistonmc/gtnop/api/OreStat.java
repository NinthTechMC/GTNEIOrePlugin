package pistonmc.gtnop.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;
import pistonmc.gtnop.core.BlockWorld;

/**
 * Compute result when clicking on an item in NEI
 *
 * Each OreState represents one page in the result
 */
public class OreStat<TOre extends IOre> {
    public final TOre ore;
    public final IOreWorld world;

    public OreStat(TOre ore) {
        this(ore, null);
    }

    public OreStat(TOre ore, IOreWorld world) {
        this.ore = ore;
        this.world = world;
    }

    public String getLocalizedName() {
        return this.ore.getLocalizedName();
    }

    /**
     * Get the ores to display
     */
    public List<List<ItemStack>> getOres() {
        return this.ore.getOreStacks(this.world);
    }

    /**
     * Get the other drops to display
     *
     * If empty, worlds are allowed to take up the space
     */
    public List<List<ItemStack>> getOtherDrops() {
        return this.ore.getOtherDropStacks(this.world);
    }

    /**
     * Get the worlds to display
     */
    public List<ItemStack> getDimensions() {
        List<BlockWorld> dimensions = new ArrayList<>();
        for (BlockWorld block : BlockWorld.ALL) {
            if (ore.isGeneratedIn(block.getOreWorld())) {
                dimensions.add(block);
            }
        }
        Collections.sort(dimensions, ore::compareDimension);
        List<ItemStack> dimensionItems = new ArrayList<>(dimensions.size());
        for (BlockWorld dimension : dimensions) {
            ItemStack stack = new ItemStack(dimension);
            ore.modifyDimensionStack(stack, dimension.getOreWorld());
            dimensionItems.add(stack);
        }
        return dimensionItems;
    }

}
