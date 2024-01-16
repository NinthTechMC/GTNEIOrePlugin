package pistonmc.gtnop.integration;

import cpw.mods.fml.common.Loader;
import pistonmc.gtnop.api.GTNEIOreAPI;
import pistonmc.gtnop.api.IOreWorldConfig;
import pistonmc.gtnop.api.IOreWorldNamed;

public class TwilightForest implements IOreWorldConfig {

    @Override
    public boolean isEnabled() {
        return Loader.isModLoaded("TwilightForest");
    }

    @Override
    public String getDimensionName() {
        return "Twilight Forest";
    }

    @Override
    public IOreWorldNamed createOreWorld() {
        return GTNEIOreAPI.getInstance().createOreWorld("Twilight Forest", "TwilightForest:tile.TFLog");
    }

}
