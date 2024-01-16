package pistonmc.gtnop.gt5;

/**
 * Check support status against various GT versions
 */
public class GTSupport {
    private static GTSupport instance = new GTSupport();
    public static GTSupport getInstance() {
        return instance;
    }

    public boolean oreLayerRestrictedBiomes = false;
    public boolean oreLayerEndAsteroids = false;
    public boolean oreLayerMoon = false;
    public boolean oreLayerMars = false;
    public boolean oreLayerAsteroids = false;

    public boolean oreSmallRestrictedBiomes = false;
    public boolean oreSmallMoon = false;
    public boolean oreSmallMars = false;

    public void check() {
        checkOreLayer();
        checkOreSmall();
    }

    private void checkOreLayer() {
        Class<?> clazzGTOreLayer = null;
        try {
            clazzGTOreLayer = Class.forName("gregtech.common.GT_Worldgen_GT_Ore_Layer");
        } catch (ClassNotFoundException e) {}
        if (clazzGTOreLayer == null) {
            return;
        }
        try {
            clazzGTOreLayer.getField("mRestrictBiome");
            this.oreLayerRestrictedBiomes = true;
        } catch (Exception e) {}
        try {
            clazzGTOreLayer.getField("mEndAsteroid");
            this.oreLayerEndAsteroids = true;
        } catch (Exception e) {}
        try {
            clazzGTOreLayer.getField("mMoon");
            this.oreLayerMoon = true;
        } catch (Exception e) {}
        try {
            clazzGTOreLayer.getField("mMars");
            this.oreLayerMars = true;
        } catch (Exception e) {}
        try {
            clazzGTOreLayer.getField("mAsteroid");
            this.oreLayerAsteroids = true;
        } catch (Exception e) {}
    }

    private void checkOreSmall() {
        Class<?> clazzGTOreSmall = null;
        try {
            clazzGTOreSmall = Class.forName("gregtech.common.GT_Worldgen_GT_Ore_SmallPieces");
        } catch (ClassNotFoundException e) {}
        if (clazzGTOreSmall == null) {
            return;
        }
        try {
            clazzGTOreSmall.getField("mRestrictBiome");;
            this.oreSmallRestrictedBiomes = true;
        } catch (Exception e) {}
        try {
            clazzGTOreSmall.getField("mMoon");
            this.oreSmallMoon = true;
        } catch (Exception e) {}
        try {
            clazzGTOreSmall.getField("mMars");
            this.oreSmallMars = true;
        } catch (Exception e) {}
    }
}
