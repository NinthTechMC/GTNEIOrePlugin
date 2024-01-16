package pistonmc.gtnop.api;

import net.minecraft.item.ItemStack;

/**
 * Specialized ore for ore layers (veins)
 */
public interface IOreLayer extends IOre {
    /**
     * Get the weight of this ore. The chance
     * is calculated by (weight / totalWeight)
     */
    public int getWeight();

    /**
     * Get the size data. Return negative if not relavant
     */
    public int getSize();

    /**
     * Get the density data. Return negative if not relavant
     */
    public int getDensity();

    /**
     * Get the chance of this ore in the given dimension
     */
    public float getChanceIn(IOreWorld dimension);

    /**
     * Compare dimension for display order
     */
    @Override
    public default int compareDimension(IOreWorldAccess a, IOreWorldAccess b) {
        float chanceA = this.getChanceIn(a.getOreWorld());
        float chanceB = this.getChanceIn(b.getOreWorld());
        // higher chance first
        return Float.compare(chanceB, chanceA);
    }

    /**
     * Modify the displayed stack for a dimension. (Add tooltip, etc)
     */
    @Override
    public default void modifyDimensionStack(ItemStack stack, IOreWorld dimension) {
        float chance = this.getChanceIn(dimension);
        GTNEIOreAPI.setChanceTooltipNBT(stack, chance);
    }
}
