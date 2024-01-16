package pistonmc.gtnop.nei;

import java.util.ArrayList;
import java.util.List;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.resources.I18n;
import pistonmc.gtnop.NEIModConfig;
import pistonmc.gtnop.api.IOreManager;
import pistonmc.gtnop.api.IOreSmall;
import pistonmc.gtnop.api.OreStat;

/**
 * NEI handler for small ores (IOreSmall)
 */
public class NEIHandlerOreSmall extends NEIHandler<IOreSmall> {

    class RecipeOreSmall extends Recipe {

        RecipeOreSmall(OreStat<IOreSmall> stat) {
            super(stat);
        }

        @Override
        public List<PositionedStack> getOtherStacks() {
            ArrayList<PositionedStack> list = new ArrayList<>();
            list.addAll(this.items.getDimensions(4 /* columns */));
            list.addAll(this.items.getOtherDrops());
            return list;
        }
    }

    public NEIHandlerOreSmall(IOreManager<IOreSmall> manager) {
        super(manager);
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new NEIHandlerOreSmall(this.manager);
    }

    @Override
    protected void addRecipe(OreStat<IOreSmall> stat) {
        this.arecipes.add(new RecipeOreSmall(stat));
    }

    @Override
    protected void drawExtraText(OreStat<IOreSmall> stat) {
        int y = NEIModConfig.GUI_FIRST_LINE_Y + NEIModConfig.GUI_LINE_HEIGHT * 2;
        int amount = stat.ore.getAmountPerChunk();
        if (amount >= 0) {
            GuiDraw.drawString(I18n.format("gtnop.gui.nei.info.amount", String.valueOf(amount)), 5, y, 0x404040, false);
        }

        y = NEIModConfig.GUI_FIRST_LINE_Y + NEIModConfig.GUI_LINE_HEIGHT * 4;
        GuiDraw.drawString(I18n.format("gtnop.gui.nei.info.drops"), NEIModConfig.GUI_HALF_X, y, 0x404040, false);
        
    }
}
