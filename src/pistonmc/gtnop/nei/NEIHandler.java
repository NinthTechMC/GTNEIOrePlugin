package pistonmc.gtnop.nei;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import pistonmc.gtnop.ModInfo;
import pistonmc.gtnop.NEIModConfig;
import pistonmc.gtnop.api.IOre;
import pistonmc.gtnop.api.IOreManager;
import pistonmc.gtnop.api.OreStat;

/**
 * Base class for NEI handlers
 */
public abstract class NEIHandler<TOre extends IOre> extends TemplateRecipeHandler {
    /**
     * NEI recipe type that can be created from OreStat
     */
    protected abstract class Recipe extends CachedRecipe {
        protected final OreStat<TOre> stat;
        protected final RecipeItems items;

        protected Recipe(OreStat<TOre> stat) {
            this.stat = stat;
            this.items = new RecipeItems(stat);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            // cycleticks is from outer class
            return this.items.getOres(cycleticks);
        }

        @Override
        public abstract List<PositionedStack> getOtherStacks();

        @Override
        public PositionedStack getResult() {
            return null;
        }
    }

    /**
     * id of the manager
     *
     * This is needed because NEI tries to access manager in the super constructor
     */
    protected String id;
    protected IOreManager<TOre> manager;
    private Set<String> seen;

    protected NEIHandler(IOreManager<TOre> manager) {
        this.id = manager.getCleanedName();
        this.manager = manager;
        this.seen = new HashSet<>();

        this.loadTransferRects();
        RecipeTransferRectHandler.registerRectsToGuis(this.getRecipeTransferRectGuis(), this.transferRects);
    }

    // making sure subclass implements this
    @Override
    public abstract TemplateRecipeHandler newInstance();

    /**
     * Add a recipe to the handler
     */
    protected abstract void addRecipe(OreStat<TOre> stat);

    /**
     * Draw specific text
     */
    protected abstract void drawExtraText(OreStat<TOre> stat);

    @Override
    public String getHandlerId() {
        return NEIModConfig.managerIdToHandlerId(this.id);
    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }

    @Override
    public String getRecipeName() {
        return I18n.format("gtnop.gui.nei.title." + this.id);
    }

    @Override
    public String getGuiTexture() {
        // Piston: I am not sure if this is needed, but the original code has it
        return ModInfo.MODID + ":textures/gui/nei/guiBase.png";
    }

    @Override
    public void loadTransferRects() {
        if (this.id == null) {
            // NEI tries to do this at construction time
            // when id is not set yet
            // we do it later manually
            return;
        }
        int stringLength = GuiDraw.getStringWidth(EnumChatFormatting.BOLD + I18n.format("gui.nei.seeAll"));
        transferRects.add(
            new RecipeTransferRect(
                new Rectangle(NEIModConfig.GUI_WIDTH-stringLength-3, 5, stringLength, 9), 
                this.id, new Object[0])
        );    
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(this.id)) {
            // when loading through transfer rect, override the input results
            // to all ores
            for (OreStat<TOre> stat: this.manager.getAllStats()) {
                this.addRecipe(stat);
            }
            return;
        }
        if (!outputId.equals("item")) {
            return;
        }
        this.seen.clear();
        for (Object o: results) {
            if (!(o instanceof ItemStack)) {
                continue;
            }
            this.addRecipes((ItemStack)o);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        this.seen.clear();
        this.addRecipes(result);
    }

    private void addRecipes(ItemStack stack) {
        for (OreStat<TOre> stat: this.manager.getRelated(stack)) {
            if (!this.seen.add(stat.ore.getName())) {
                continue;
            }
            this.addRecipe(stat);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void drawExtras(int recipe) {
        Recipe recipeInst = (Recipe) this.arecipes.get(recipe);
        OreStat<TOre> stat = recipeInst.stat;
        int y = NEIModConfig.GUI_FIRST_LINE_Y;
        GuiDraw.drawString(stat.getLocalizedName(), 5, y, 0x404040, false);
        y+=NEIModConfig.GUI_LINE_HEIGHT;
        GuiDraw.drawString(I18n.format("gtnop.gui.nei.info.height", String.valueOf(stat.ore.getMinY()), String.valueOf(stat.ore.getMaxY())), 5, y, 0x404040, false);

        this.drawExtraText(stat);
        
        y = NEIModConfig.GUI_FIRST_LINE_Y + NEIModConfig.GUI_LINE_HEIGHT * 3;
        String biome = stat.ore.getRestrictedBiomeInfo();
        if (biome != null) {
            GuiDraw.drawString(I18n.format("gtnop.gui.nei.info.only_biome", biome), 5, y, 0x404040, false);
        }
        y+=NEIModConfig.GUI_LINE_HEIGHT;
        GuiDraw.drawString(I18n.format("gtnop.gui.nei.info.dim"), 5, y, 0x404040, false);
        GuiDraw.drawStringR(EnumChatFormatting.BOLD + I18n.format("gtnop.gui.nei.seeAll"), NEIModConfig.GUI_WIDTH-3, 5, 0x404040, false);
    }




}
