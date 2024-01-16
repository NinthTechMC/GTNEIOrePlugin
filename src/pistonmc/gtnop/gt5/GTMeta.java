package pistonmc.gtnop.gt5;

import java.util.ArrayList;
import java.util.List;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import pistonmc.gtnop.api.IOreWorld;

/**
 * Helpers for juggling GT's metadata system
 */
public class GTMeta {

    // ore variants
    public static final String CORE_BLOCK_NAME = "gt.blockores";
    public static final int CORE_VARIANT_COUNT = 7;
    public static final int VARIANT_OVERWORLD = 0;
    public static final int VARIANT_NETHER = 1; 
    public static final int VARIANT_END = 2; 
    public static final int VARIANT_BLACK_GRANITE = 3; 
    public static final int VARIANT_RED_GRANITE = 4; 
    public static final int VARIANT_MARBLE = 5;
    public static final int VARIANT_BASALT = 6;
    public static final String GC_BLOCK_NAME = "gt.blockores.gc";
    public static final int GC_VARIANT_COUNT = 4;
    public static final int VARIANT_MOON_DIRT = 0;
    public static final int VARIANT_MOON_ROCK = 1;
    public static final int VARIANT_MOON_TURF = 2;
    public static final int VARIANT_MARS = 3;

    public static final int SMALL_ORE_OFFSET = 16000;
    public static final int MATERIAL_MODULO = 1000;
    /**
     * Get the different textures of a GT (big) ore block
     */
    public static List<ItemStack> getOreVariants(int metaMaterial, IOreWorld world) {
        metaMaterial = getMaterialFromMeta(metaMaterial);
        List<ItemStack> oreList = new ArrayList<ItemStack>(CORE_VARIANT_COUNT+GC_VARIANT_COUNT);
        Block ore = GregTech_API.sBlockOres1;
        for (int i = 0; i < CORE_VARIANT_COUNT; i++) {
            if (!isOreAllowed(world, ore, i)) {
                continue;
            }
            ItemStack stack = new ItemStack(ore, 1, metaMaterial + i * MATERIAL_MODULO);
            oreList.add(stack);
        }
        GTSupport support = GTSupport.getInstance();
        Block oreGC = GregTech_API.sBlockOresGC;
        if (support.oreLayerMoon) {
            for (int i = 0; i < GC_VARIANT_COUNT; i++) {
                if (!support.oreLayerMoon && i < VARIANT_MARS) {
                    continue;
                }
                if (!support.oreLayerMars && i == VARIANT_MARS) {
                    continue;
                }
                if (!isOreAllowed(world, oreGC, i)) {
                    continue;
                }
                ItemStack stack = new ItemStack(oreGC, 1, metaMaterial + i * MATERIAL_MODULO);
                oreList.add(stack);
            }
        }
        return oreList;
    }
    /**
     * Get the different textures of a GT small ore block
     */
    public static List<ItemStack> getSmallOreVariants(int metaMaterial, IOreWorld world) {
        metaMaterial = getMaterialFromMeta(metaMaterial);
        List<ItemStack> oreList = new ArrayList<ItemStack>(CORE_VARIANT_COUNT+GC_VARIANT_COUNT);
        Block ore = GregTech_API.sBlockOres1;
        for (int i = 0; i < CORE_VARIANT_COUNT; i++) {
            if (!isOreAllowed(world, ore, i)) {
                continue;
            }
            ItemStack stack = new ItemStack(ore, 1, metaMaterial + SMALL_ORE_OFFSET + i * MATERIAL_MODULO);
            oreList.add(stack);
        }
        GTSupport support = GTSupport.getInstance();
        Block oreGC = GregTech_API.sBlockOresGC;
        if (support.oreLayerMoon) {
            for (int i = 0; i < GC_VARIANT_COUNT; i++) {
                if (!support.oreLayerMoon && i < VARIANT_MARS) {
                    continue;
                }
                if (!support.oreLayerMars && i == VARIANT_MARS) {
                    continue;
                }
                if (!isOreAllowed(world, oreGC, i)) {
                    continue;
                }
                ItemStack stack = new ItemStack(oreGC, 1, metaMaterial + SMALL_ORE_OFFSET + i * MATERIAL_MODULO);
                oreList.add(stack);
            }
        }
        return oreList;
    }

    public static boolean isOreAllowed(IOreWorld world, Block ore, int variant) {
        if (world instanceof GTDim) {
            return ((GTDim) world).isOreAllowed(ore, variant);
        }
        return true;
    }

    public static boolean isDropAllowed(IOreWorld world, OrePrefixes type, Materials material) {
        if (world instanceof GTDim) {
            return ((GTDim) world).isDropAllowed(type, material);
        }
        return true;
    }

    /**
     * Get the variant index for ores
     */
    public static int getVariantFromMeta(int meta) {
        if (meta >= SMALL_ORE_OFFSET) {
            meta -= SMALL_ORE_OFFSET;
        }

        return meta / MATERIAL_MODULO;
    }

    /**
     * Get the material index for ores and items
     */
    public static int getMaterialFromMeta(int meta) {
        return meta % MATERIAL_MODULO;
    }

    public static Materials getMaterial(int meta) {
        return GregTech_API.sGeneratedMaterials[getMaterialFromMeta(meta)];
    }

}
