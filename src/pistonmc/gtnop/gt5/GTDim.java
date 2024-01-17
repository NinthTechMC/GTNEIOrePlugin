package pistonmc.gtnop.gt5;

import java.util.HashMap;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import pistonmc.gtnop.api.IOreLayer;
import pistonmc.gtnop.api.IOreWorld;
import pistonmc.gtnop.api.IOreManager;

public abstract class GTDim implements IOreWorld {
    private HashMap<IOreManager<?>, Integer> totalOreLayerWeight = new HashMap<>();

    @Override
    public void registerOreLayer(IOreManager<?> manager, IOreLayer ore) {
        Integer total = this.totalOreLayerWeight.get(manager);
        if (total == null) {
            this.totalOreLayerWeight.put(manager, ore.getWeight());
        } else {
            this.totalOreLayerWeight.put(manager, total + ore.getWeight());
        }
    }

    @Override
    public int getTotalOreLayerWeight(IOreManager<?> manager) {
        return this.totalOreLayerWeight.getOrDefault(manager, 0);
    }

    public abstract boolean isGenerated(GTOreLayer ore);
    public abstract boolean isGenerated(GTOreSmall ore);
    public abstract boolean isOreAllowed(Block block, int variant);
    public abstract boolean isDropAllowed(OrePrefixes type, Materials material);

    public static class Overworld extends GTDim {

        @Override
        public Block getIcon() {
            return Blocks.grass;
        }

        @Override
        public String getCleanedName() {
            return "overworld";
        }

        @Override
        public boolean isGenerated(GTOreLayer ore) {
            return ore.worldgen.mOverworld;
        }

        @Override
        public boolean isGenerated(GTOreSmall ore) {
            return ore.worldgen.mOverworld;
        }

        @Override
        public boolean isOreAllowed(Block block, int variant) {
            // @formatter:off
            return block == GregTech_API.sBlockOres1
                && variant != GTMeta.VARIANT_NETHER 
                && variant != GTMeta.VARIANT_END; 
            // @formatter:on
        }

        @Override
        public boolean isDropAllowed(OrePrefixes type, Materials material) {
            return material != Materials.Netherrack && material != Materials.Endstone;
        }
    }

    public static class Nether extends GTDim {

        @Override
        public Block getIcon() {
            return Blocks.netherrack;
        }

        @Override
        public String getCleanedName() {
            return "nether";
        }

        @Override
        public boolean isGenerated(GTOreLayer ore) {
            return ore.worldgen.mNether;
        }

        @Override
        public boolean isGenerated(GTOreSmall ore) {
            return ore.worldgen.mNether;
        }

        @Override
        public boolean isOreAllowed(Block block, int variant) {
            // @formatter:off
            return block == GregTech_API.sBlockOres1
                && variant != GTMeta.VARIANT_OVERWORLD 
                && variant != GTMeta.VARIANT_END; 
            // @formatter:on
        }

        @Override
        public boolean isDropAllowed(OrePrefixes type, Materials material) {
            return material != Materials.Stone && material != Materials.Endstone;
        }
    }

    public static class End extends GTDim {

        @Override
        public Block getIcon() {
            return Blocks.end_portal_frame;
        }

        @Override
        public String getCleanedName() {
            return "end";
        }

        @Override
        public boolean isGenerated(GTOreLayer ore) {
            return ore.worldgen.mEnd;
        }

        @Override
        public boolean isGenerated(GTOreSmall ore) {
            return ore.worldgen.mEnd;
        }

        @Override
        public boolean isOreAllowed(Block block, int variant) {
            return block == GregTech_API.sBlockOres1 && variant == GTMeta.VARIANT_END;
        }

        @Override
        public boolean isDropAllowed(OrePrefixes type, Materials material) {
            if (material == Materials.Endstone) {
                return true;
            }
            // @formatter:off
            return material != Materials.Stone 
                && material != Materials.Netherrack
                && material != Materials.GraniteBlack
                && material != Materials.GraniteRed
                && material != Materials.Marble
                && material != Materials.Basalt;
            // @formatter:on
        }
    }

    public static class EndAsteroid extends GTDim {

        @Override
        public Block getIcon() {
            return Blocks.end_stone;
        }

        @Override
        public String getCleanedName() {
            return "end_asteroid";
        }

        @Override
        public boolean isGenerated(GTOreLayer ore) {
            return GTSupport.getInstance().oreLayerEndAsteroids && ore.worldgen.mEndAsteroid;
        }

        @Override
        public boolean isGenerated(GTOreSmall ore) {
            return false;
        }

        @Override
        public boolean isOreAllowed(Block block, int variant) {
            return block == GregTech_API.sBlockOres1 && variant == GTMeta.VARIANT_END;
        }

        @Override
        public boolean isDropAllowed(OrePrefixes type, Materials material) {
            if (material == Materials.Endstone) {
                return true;
            }
            // @formatter:off
            return material != Materials.Stone 
                && material != Materials.Netherrack
                && material != Materials.GraniteBlack
                && material != Materials.GraniteRed
                && material != Materials.Marble
                && material != Materials.Basalt;
            // @formatter:on
        }
    }

    public static class Moon extends GTDim {

        Block icon;

        @Override
        public Block getIcon() {
            if (icon == null) {
                icon = GameRegistry.findBlock("GalacticraftCore", "tile.moonBlock");
            }
            if (icon == null) {
                return Blocks.air;
            }
            return icon;
        }

        @Override
        public int getIconMeta() {
            return 3; // moon dirt
        }

        @Override
        public String getCleanedName() {
            return "moon";
        }

        @Override
        public boolean isGenerated(GTOreLayer ore) {
            return GTSupport.getInstance().oreLayerMoon && ore.worldgen.mMoon;
        }

        @Override
        public boolean isGenerated(GTOreSmall ore) {
            return GTSupport.getInstance().oreSmallMoon && ore.worldgen.mMoon;
        }

        @Override
        public boolean isOreAllowed(Block block, int variant) {
            return block == GregTech_API.sBlockOresGC;
        }

        @Override
        public boolean isDropAllowed(OrePrefixes type, Materials material) {
            // @formatter:off
            return material != Materials.Stone 
                && material != Materials.Netherrack
                && material != Materials.GraniteBlack
                && material != Materials.GraniteRed
                && material != Materials.Marble
                && material != Materials.Basalt
                && material != Materials.Endstone;
            // @formatter:on
        }
    }

    public static class Mars extends GTDim {

        Block icon;

        @Override
        public Block getIcon() {
            if (icon == null) {
                icon = GameRegistry.findBlock("GalacticraftMars", "tile.mars");
            }
            if (icon == null) {
                return Blocks.air;
            }
            return icon;
        }

        @Override
        public int getIconMeta() {
            return 9; // mars stone
        }

        @Override
        public String getCleanedName() {
            return "mars";
        }

        @Override
        public boolean isGenerated(GTOreLayer ore) {
            return GTSupport.getInstance().oreLayerMars && ore.worldgen.mMars;
        }

        @Override
        public boolean isGenerated(GTOreSmall ore) {
            return GTSupport.getInstance().oreSmallMars && ore.worldgen.mMars;
        }

        @Override
        public boolean isOreAllowed(Block block, int variant) {
            return block == GregTech_API.sBlockOresGC;
        }

        @Override
        public boolean isDropAllowed(OrePrefixes type, Materials material) {
            // @formatter:off
            return material != Materials.Stone 
                && material != Materials.Netherrack
                && material != Materials.GraniteBlack
                && material != Materials.GraniteRed
                && material != Materials.Marble
                && material != Materials.Basalt
                && material != Materials.Endstone;
            // @formatter:on
        }
    }


    public static class Asteroids extends GTDim {

        Block icon;

        @Override
        public Block getIcon() {
            if (icon == null) {
                icon = GameRegistry.findBlock("GalacticraftMars", "tile.asteroidsBlock");
            }
            if (icon == null) {
                return Blocks.air;
            }
            return icon;
        }

        @Override
        public String getCleanedName() {
            return "asteroid";
        }

        @Override
        public boolean isGenerated(GTOreLayer ore) {
            return GTSupport.getInstance().oreLayerAsteroids && ore.worldgen.mAsteroid;
        }

        @Override
        public boolean isGenerated(GTOreSmall ore) {
            return false;
        }

        @Override
        public boolean isOreAllowed(Block block, int variant) {
            return block == GregTech_API.sBlockOresGC;
        }

        @Override
        public boolean isDropAllowed(OrePrefixes type, Materials material) {
            // @formatter:off
            return material != Materials.Stone 
                && material != Materials.Netherrack
                && material != Materials.GraniteBlack
                && material != Materials.GraniteRed
                && material != Materials.Marble
                && material != Materials.Basalt
                && material != Materials.Endstone;
            // @formatter:on
        }
    }
}
