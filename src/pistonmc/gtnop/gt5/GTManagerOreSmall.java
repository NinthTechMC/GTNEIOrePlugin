package pistonmc.gtnop.gt5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.GT_Worldgen_GT_Ore_SmallPieces;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import pistonmc.gtnop.api.IOreManager;
import pistonmc.gtnop.api.IOreSmall;
import pistonmc.gtnop.api.IOreWorld;
import pistonmc.gtnop.api.IOreWorldAccess;
import pistonmc.gtnop.api.IWorldInit;
import pistonmc.gtnop.api.OreStat;

public class GTManagerOreSmall implements IOreManager<IOreSmall>{
    // since each small ore has its unique material meta
    // we keep a sorted array and use binary search
    private static int compareOres(IOreSmall a, IOreSmall b) {
        if (!(a instanceof IGTOreSmall)) {
            return 0;
        }
        if (!(b instanceof IGTOreSmall)) {
            return 0;
        }
        IGTOreSmall aSmall = (IGTOreSmall) a;
        IGTOreSmall bSmall = (IGTOreSmall) b;
        return Integer.compare(aSmall.getMeta(), bSmall.getMeta());
    }
    private Item itemOre;
    private Item itemOreGC;
    private List<IOreSmall> allOres = new ArrayList<>();
    // need this for items like diamond and emeralds that are not part of the GT meta system
    private HashMap<Item, List<IOreSmall>> exceptionItems = new HashMap<>();

    @Override
    public List<IOreSmall> getAllOres() {
        return this.allOres;
    }

    @Override
    public String getCleanedName() {
        return "gt.small";
    }

    @Override
    public String getIconString() {
        return "minecraft:stone_pickaxe";
    }

    @Override
    public void init(List<IOreWorld> worlds, List<IWorldInit> extraWorlds) {
        this.itemOre = Item.getItemFromBlock(GregTech_API.sBlockOres1);
        this.itemOreGC = Item.getItemFromBlock(GregTech_API.sBlockOresGC);

        int count = 0;
        int exceptionCount = 0;

        for (GT_Worldgen worldgen: GregTech_API.sWorldgenList) {
            if (!(worldgen instanceof GT_Worldgen_GT_Ore_SmallPieces)) {
                continue;
            }
            if (!worldgen.mWorldGenName.startsWith("ore.small.")) {
                continue;
            }
            GT_Worldgen_GT_Ore_SmallPieces oreWorldgen = (GT_Worldgen_GT_Ore_SmallPieces) worldgen;
            GTOreSmall ore = new GTOreSmall(oreWorldgen);
            ore.init(this, worlds, extraWorlds);

            List<List<ItemStack>> drops = ore.getOtherDropStacks(null);
            for (List<ItemStack> drop: drops) {
                for (ItemStack dropStack: drop) {
                    if (dropStack.getUnlocalizedName().startsWith("gt.metaitem")) {
                        // probably not an exception
                        continue;
                    }
                    System.out.println("Found exception item " + dropStack.getUnlocalizedName());
                    exceptionCount++;
                    List<IOreSmall> ores = this.exceptionItems.get(dropStack.getItem());
                    if (ores == null) {
                        ores = new ArrayList<>();
                        this.exceptionItems.put(dropStack.getItem(), ores);
                    }
                    ores.add(ore);
                }
            }

            this.allOres.add(ore);
            count++;
        }


        Collections.sort(this.allOres, GTManagerOreSmall::compareOres);
        System.out.println("Loaded " + count + " small ores");
        System.out.println("Found " + exceptionCount + " exception items that are not gt.metaitem but dropped");
    }

    @Override
    public List<OreStat<IOreSmall>> getOreStatsFor(ItemStack stack) {
        // if the stack is 
        // stone dust, neterrack dust, endstone dust, black/red granite dust, marble or basalt dust
        // selecting it will select all ores with that material
        stack = GregTech_API.getUnificatedOreDictStack(stack);
        // @formatter:off
        Materials[] materials = {
            Materials.Stone,
            Materials.Netherrack,
            Materials.Endstone,
            Materials.GraniteBlack,
            Materials.GraniteRed,
            Materials.Marble,
            Materials.Basalt,
        };
        // @formatter:on
        IOreWorld filter = null;
        int variant = -1;
        for (Materials m: materials) {
            if (this.isDust(stack, m)) {
                filter = new MaterialFilter(variant, m);
                break;
            }
            variant++;
        }

        List<IOreSmall> ores = this.getOresWithFilter(stack, filter);
        if (ores == null) {
            return null;
        }
        List<OreStat<IOreSmall>> stats = new ArrayList<>();
        for (IOreSmall ore : ores) {
            stats.add(new OreStat<>(ore));
        }
        return stats;
    }

    private boolean isDust(ItemStack stack, Materials material) {
        ItemStack dust = GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Stone, 1);
        if (dust == null) {
            return false;
        }
        return dust.getItem() == stack.getItem() && dust.getItemDamage() == stack.getItemDamage();
    }

    @Override
    public List<IOreSmall> getOresFor(ItemStack stack) {
        stack = GregTech_API.getUnificatedOreDictStack(stack);
        return this.getOresWithFilter(stack, null);
    }

    private List<IOreSmall> getOresWithFilter(ItemStack stack, IOreWorld filter) {
        List<IOreSmall> potentials;
        List<IOreSmall> exception = this.exceptionItems.get(stack.getItem());
        if (exception != null) {
            potentials = exception;
        } else {
            // small ores only have 1 material per ore
            // so the same item cannot have more than one ore
            // this may not be the actual material if the stack is not a GT material stack
            // however, it will give us the only possible ore that could contain it
            int oreMaterial = GTMeta.getMaterialFromMeta(stack.getItemDamage());

            int index = Collections.binarySearch(this.allOres, new MetaTarget(oreMaterial), GTManagerOreSmall::compareOres);
            if (index < 0) {
                return null;
            }
            IOreSmall ore = this.allOres.get(index);
            // if we are indeed looking at the ore item, just return it
            if (stack.getItem() == this.itemOre || stack.getItem() == this.itemOreGC) {
                if (stack.getItemDamage() < GTMeta.SMALL_ORE_OFFSET) {
                    // is a big ore
                    return null;
                }
                return Collections.singletonList(ore);
            }
            potentials = Collections.singletonList(ore);
        }

        // otherwise see if it's a valid drop from the potential ores
        List<IOreSmall> results = new ArrayList<>();
        outmost: for (IOreSmall ore: potentials) {
            List<List<ItemStack>> drops = ore.getOtherDropStacks(filter);
            for (List<ItemStack> drop: drops) {
                for (ItemStack dropStack: drop) {
                    if (dropStack.getItem() == stack.getItem() && dropStack.getItemDamage() == stack.getItemDamage()) {
                        results.add(ore);
                        continue outmost;
                    }
                }
            }
        }

        return results;
    }

    static class MetaTarget implements IGTOreSmall {
        int meta;
        public MetaTarget(int meta) {
            this.meta = meta;
        }

        @Override
        public int getMeta() {
            return this.meta;
        }
        // @formatter:off
        public String getName() { return null; }
        public void init(IOreManager<?> manager, List<IOreWorld> worlds,
                List<IWorldInit> extraWorlds) { }
        public String getRestrictedBiomeInfo() { return null; }
        public int getMinY() { return 0; }
        public int getMaxY() { return 0; }
        public boolean isGeneratedIn(IOreWorld dimension) { return false; }
        public List<List<ItemStack>> getOreStacks(IOreWorld oreWorld) { return null; }
        public List<List<ItemStack>> getOtherDropStacks(IOreWorld oreWorld) { return null; }
        public int compareDimension(IOreWorldAccess a, IOreWorldAccess b) { return 0; }
        public void modifyDimensionStack(ItemStack stack, IOreWorld dimension) { }
        public int getAmountPerChunk() { return 0; }
        // @formatter:on
    }

    static class MaterialFilter extends GTDim {
        int variant;
        Materials material;

        MaterialFilter(int variant, Materials material) {
            this.variant = variant;
            this.material = material;
        }

        @Override
        public boolean isOreAllowed(Block block, int variant) {
            return this.variant == variant;
        }

        @Override
        public boolean isDropAllowed(OrePrefixes type, Materials material) {
            return this.material == material;
        }

        @Override
        public Block getIcon() {
            return null;
        }

        @Override
        public String getCleanedName() {
            return null;
        }

        @Override
        public boolean isGenerated(GTOreLayer ore) {
            return false;
        }

        @Override
        public boolean isGenerated(GTOreSmall ore) {
            return false;
        }
    }
}
