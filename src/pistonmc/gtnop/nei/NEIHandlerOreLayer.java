package pistonmc.gtnop.nei;

import java.util.List;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.resources.I18n;
import pistonmc.gtnop.NEIModConfig;
import pistonmc.gtnop.api.IOreLayer;
import pistonmc.gtnop.api.IOreManager;
import pistonmc.gtnop.api.OreStat;

/**
 * Recipe handler for veins (IOreLayer)
 */
public class NEIHandlerOreLayer extends NEIHandler<IOreLayer> {

    class RecipeOreLayer extends Recipe {

        RecipeOreLayer(OreStat<IOreLayer> stat) {
            super(stat);
        }

        @Override
        public List<PositionedStack> getOtherStacks() {
            return this.items.getDimensions(9 /* columns */);
        }
    }

    public NEIHandlerOreLayer(IOreManager<IOreLayer> manager) {
        super(manager);
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new NEIHandlerOreLayer(this.manager);
    }

    @Override
    protected void addRecipe(OreStat<IOreLayer> stat) {
        this.arecipes.add(new RecipeOreLayer(stat));
    }

    @Override
    protected void drawExtraText(OreStat<IOreLayer> stat) {
        int y = NEIModConfig.GUI_FIRST_LINE_Y + NEIModConfig.GUI_LINE_HEIGHT * 2;
        int size = stat.ore.getSize();
        if (size >= 0) {
            GuiDraw.drawString(I18n.format("gtnop.gui.nei.info.size", String.valueOf(size)), 5, y, 0x404040, false);
        }
        int density = stat.ore.getDensity();
        if (density >= 0) {
            GuiDraw.drawString(I18n.format("gtnop.gui.nei.info.density", String.valueOf(density)), NEIModConfig.GUI_HALF_X, y, 0x404040, false);
        }
    }

}
