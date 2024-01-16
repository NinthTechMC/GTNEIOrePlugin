package pistonmc.gtnop.gt5;

import java.util.ArrayList;
import java.util.List;
import gregtech.api.GregTech_API;
import gregtech.common.GT_Worldgen_GT_Ore_Layer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import pistonmc.gtnop.api.IOreLayer;
import pistonmc.gtnop.api.IOreWorld;
import pistonmc.gtnop.api.IWorldInit;
import pistonmc.gtnop.api.IOreManager;

public class GTManagerOreLayer implements IOreManager<IOreLayer> {
    private Item itemOre;
    private List<IOreLayer> allOres = new ArrayList<>();

    @Override
    public List<IOreLayer> getAllOres() {
        return this.allOres;
    }

    @Override
    public void init(List<IOreWorld> worlds, List<IWorldInit> extraWorlds) {
        this.itemOre = Item.getItemFromBlock(GregTech_API.sBlockOres1);

        System.out.println("Found " + GT_Worldgen_GT_Ore_Layer.sList.size() + " ore layers to load");
        
        for (GT_Worldgen_GT_Ore_Layer tWorldGen: GT_Worldgen_GT_Ore_Layer.sList) {
            GTOreLayer oreLayer = new GTOreLayer(tWorldGen);
            oreLayer.init(this, worlds, extraWorlds);
            this.allOres.add(oreLayer);
        }

        System.out.println("Loaded " + this.allOres.size() + " ore layers");
    }

    @Override
    public List<IOreLayer> getOresFor(ItemStack stack) {
        if (stack.getItem() != this.itemOre) {
            return null;
        }
        int damage = stack.getItemDamage();
        if (damage >= GTMeta.SMALL_ORE_OFFSET) {
            // is a small ore
            return null;
        }
        int meta = GTMeta.getMaterialFromMeta(damage);
        List<IOreLayer> ores = new ArrayList<>();
        for (IOreLayer x: this.allOres) {
            GTOreLayer oreLayer = (GTOreLayer) x;
            if (oreLayer.getPrimaryMeta() == meta || oreLayer.getSecondaryMeta() == meta
                    || oreLayer.getBetweenMeta() == meta || oreLayer.getSporadicMeta() == meta) {
                ores.add(oreLayer);
            }
        }
        return ores;
    }

    @Override
    public String getCleanedName() {
        return "gt.vein";
    }

}
