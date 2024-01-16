package pistonmc.gtnop.nei;

import java.util.ArrayList;
import java.util.List;
import codechicken.nei.PositionedStack;
import net.minecraft.item.ItemStack;
import pistonmc.gtnop.NEIModConfig;
import pistonmc.gtnop.api.OreStat;

/**
 * Used for caching stacks displayed in a recipe
 */
public class RecipeItems {
    OreStat<?> ore;

    private List<PositionedStack> ores;
    private List<PositionedStack> dimensions;
    private List<PositionedStack> otherDrops;

    public RecipeItems(OreStat<?> stat) {
        this.ore = stat;
    }

    public List<PositionedStack> getOres(int cycleTicks) {
        if (this.ores != null) {
            this.updatePermutation(this.ores, cycleTicks);
            return this.ores;
        }

        ArrayList<PositionedStack> oreList = new ArrayList<>();
        
        int x = 5;
        for (List<ItemStack> stack: this.ore.getOres()) {
            oreList.add(new PositionedStack(stack, x, 5));
            x += 18;
        }
        this.ores = oreList;
        this.updatePermutation(this.ores, cycleTicks);
        return this.ores;
    }

    public List<PositionedStack> getOtherDrops() {
        if (this.otherDrops != null) {
            return this.otherDrops;
        }

        ArrayList<PositionedStack> otherDropList = new ArrayList<>();
        int x = NEIModConfig.GUI_HALF_X;
        int y = this.getLowerStacksY();
        int c = 0;
        int columns = 4;
        for (List<ItemStack> stack: this.ore.getOtherDrops()) {
            otherDropList.add(new PositionedStack(stack, x, y));
            c++;
            if (c == columns) {
                x = NEIModConfig.GUI_HALF_X;
                y += 18;
                c = 0;
            } else {
                x += 18;
            }
        }

        this.otherDrops = otherDropList;
        return this.otherDrops;
    }

    public List<PositionedStack> getDimensions(int columns) {
        if (this.dimensions != null) {
            return this.dimensions;
        }

        ArrayList<PositionedStack> dimensionList = new ArrayList<>();
        int x = 5;
        int y = this.getLowerStacksY();
        int c = 0;
        for (ItemStack stack: this.ore.getDimensions()) {
            dimensionList.add(new PositionedStack(stack, x, y));
            c++;
            if (c == columns) {
                x = 5;
                y += 18;
                c = 0;
            } else {
                x += 18;
            }
        }

        this.dimensions = dimensionList;
        return this.dimensions;
    }

    private void updatePermutation(List<PositionedStack> stacks, int cycleTicks) {
        for (PositionedStack stack: stacks) {
            stack.setPermutationToRender((cycleTicks / 20) % stack.items.length);
        }
    }

    private int getLowerStacksY() {
        return 5 + 20 + 13 * 5;
    }
}
