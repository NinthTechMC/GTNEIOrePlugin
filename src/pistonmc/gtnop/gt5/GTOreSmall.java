package pistonmc.gtnop.gt5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GT_LanguageManager;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.common.GT_Worldgen_GT_Ore_SmallPieces;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import pistonmc.gtnop.api.IOreManager;
import pistonmc.gtnop.api.IOreWorld;
import pistonmc.gtnop.api.IOreWorldAccess;
import pistonmc.gtnop.api.IWorldInit;

public class GTOreSmall implements IGTOreSmall {
    private static final OrePrefixes[] PREFIXES = {
        OrePrefixes.gemExquisite,
        OrePrefixes.gemFlawless,
        OrePrefixes.gem,
        OrePrefixes.gemFlawed,
        OrePrefixes.gemChipped,
        OrePrefixes.crushed,
        OrePrefixes.dustImpure,
    };

    GT_Worldgen_GT_Ore_SmallPieces worldgen;

    /** extra dimensions that can generate this ore*/
    private IOreWorld[] extraDimensions;

    public GTOreSmall(GT_Worldgen_GT_Ore_SmallPieces worldgen) {
        this.worldgen = worldgen;
    }

    @Override
    public void init(IOreManager<?> manager, List<IOreWorld> worlds, List<IWorldInit> extraWorlds) {
        List<IOreWorld> extraDimensions = new ArrayList<IOreWorld>(extraWorlds.size());
        for (IWorldInit worldInit: extraWorlds) {
            World world = worldInit.getWorldForQueryingGenerator();
            if (GTWorldgenHelper.isGenerationAllowed(this.worldgen, world, this.worldgen.mOverworld)) {
                extraDimensions.add(worldInit.getOreWorld());
            }
        }

        this.extraDimensions = extraDimensions.toArray(new IOreWorld[extraDimensions.size()]);
    }

    @Override
    public String getName() {
        return this.worldgen.mWorldGenName;
    }

    @Override
    public String getLocalizedName() {
        int smallOreMeta = this.getMeta() + GTMeta.SMALL_ORE_OFFSET;
        String unlocaliazed = "gt.blockores." + smallOreMeta + ".name";
        return GT_LanguageManager.getTranslation(unlocaliazed);
    }

    @Override
    public String getRestrictedBiomeInfo() {
        if (!GTSupport.getInstance().oreSmallRestrictedBiomes) {
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
    public int getAmountPerChunk() {
        return this.worldgen.mAmount;
    }

    @Override
    public int getMeta() {
        return this.worldgen.mMeta;
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
        return Collections.singletonList(
            GTMeta.getSmallOreVariants(this.getMeta(), world)
        );
    }

    @Override
    public List<List<ItemStack>> getOtherDropStacks(IOreWorld world) {
        Materials material = GTMeta.getMaterial(this.getMeta());
        List<List<ItemStack>> result = new ArrayList<List<ItemStack>>(PREFIXES.length);
        for (OrePrefixes prefix: PREFIXES) {
            if (!GTMeta.isDropAllowed(world, prefix, material)) {
                continue;
            }
            ItemStack stack = GT_OreDictUnificator.get(prefix, material, 1L);
            if (stack == null) {
                continue;
            }
            result.add(Collections.singletonList(stack));
        }
        return result;
    }

    @Override
    public int compareDimension(IOreWorldAccess a, IOreWorldAccess b) {
        return 0;
    }

    @Override
    public void modifyDimensionStack(ItemStack stack, IOreWorld dimension) {
    }
}
