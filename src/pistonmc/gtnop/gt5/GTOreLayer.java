package pistonmc.gtnop.gt5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import gregtech.common.GT_Worldgen_GT_Ore_Layer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import pistonmc.gtnop.api.GTNEIOreAPI;
import pistonmc.gtnop.api.IOreLayer;
import pistonmc.gtnop.api.IOreManager;
import pistonmc.gtnop.api.IOreWorld;
import pistonmc.gtnop.api.IWorldInit;

/**
 * Represents info for a vein
 *
 * Backed by GT_Worldgen_GT_Ore_Layer
 */
public class GTOreLayer implements IOreLayer {

    GT_Worldgen_GT_Ore_Layer worldgen;

    private IOreManager<?> manager;

    /** extra dimensions that can generate this vein*/
    private IOreWorld[] extraDimensions;

    public GTOreLayer(GT_Worldgen_GT_Ore_Layer worldgen) {
        this.worldgen = worldgen;
    }

    @Override
    public void init(IOreManager<?> manager, List<IOreWorld> worlds, List<IWorldInit> extraWorlds) {
        this.manager = manager;
        for (IOreWorld dim: worlds) {
            if (this.isGeneratedIn(dim)) {
                dim.registerOreLayer(manager, this);
            }
        }

        List<IOreWorld> extraDimensions = new ArrayList<IOreWorld>(extraWorlds.size());
        for (IWorldInit worldInit: extraWorlds) {
            World world = worldInit.getWorldForQueryingGenerator();
            if (GTWorldgenHelper.isGenerationAllowed(this.worldgen, world, this.worldgen.mOverworld)) {
                IOreWorld dim = worldInit.getOreWorld();
                dim.registerOreLayer(manager, this);
                extraDimensions.add(dim);
            }
        }

        this.extraDimensions = extraDimensions.toArray(new IOreWorld[extraDimensions.size()]);
    }

    @Override
    public String getName() {
        // this will be ore.mix.something
        return this.worldgen.mWorldGenName;
    }

    @Override
    public String getRestrictedBiomeInfo() {
        if (!GTSupport.getInstance().oreLayerRestrictedBiomes) {
            return null;
        }
        String biome = this.worldgen.mRestrictBiome;
        if (null == biome || "None".equals(biome)) {
            return null;
        }
        return biome;
    }

    @Override
    public int getMinY() {
        return this.worldgen.mMinY;
    }

    @Override
    public int getMaxY() {
        return this.worldgen.mMaxY;
    }

    @Override
    public float getChanceIn(IOreWorld dimension) {
        return (float) this.getWeight() / dimension.getTotalOreLayerWeight(this.manager);
    }

    public int getPrimaryMeta() {
        return this.worldgen.mPrimaryMeta;
    }

    public int getSecondaryMeta() {
        return this.worldgen.mSecondaryMeta;
    }

    public int getBetweenMeta() {
        return this.worldgen.mBetweenMeta;
    }

    public int getSporadicMeta() {
        return this.worldgen.mSporadicMeta;
    }

    @Override
    public int getWeight() {
        return this.worldgen.mWeight;
    }

    @Override
    public int getSize() {
        return this.worldgen.mSize;
    }

    @Override
    public int getDensity() {
        return this.worldgen.mDensity;
    }


    @Override
    public boolean isGeneratedIn(IOreWorld dimension) {
        if (dimension instanceof GTDim) {
            return ((GTDim) dimension).isGenerated(this);
        }
        if (this.extraDimensions == null) {
            return false;
        }
        for (IOreWorld dim: this.extraDimensions) {
            if (dim == dimension) {
                return true;
            }
        }
        return false;
    }


    @Override
    public List<List<ItemStack>> getOreStacks(IOreWorld world) {
        return Arrays.asList(
            GTNEIOreAPI.setLayoutTooltipNBT(GTMeta.getOreVariants(this.getPrimaryMeta(), world), "primary", EnumRarity.common),
            GTNEIOreAPI.setLayoutTooltipNBT(GTMeta.getOreVariants(this.getSecondaryMeta(), world), "secondary", EnumRarity.common),
            GTNEIOreAPI.setLayoutTooltipNBT(GTMeta.getOreVariants(this.getBetweenMeta(), world), "between", EnumRarity.uncommon),
            GTNEIOreAPI.setLayoutTooltipNBT(GTMeta.getOreVariants(this.getSporadicMeta(), world), "sporadic", EnumRarity.rare)
        );
    }

    @Override
    public List<List<ItemStack>> getOtherDropStacks(IOreWorld world) {
        return Collections.emptyList();
    }

}
