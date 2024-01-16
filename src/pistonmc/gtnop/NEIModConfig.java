package pistonmc.gtnop;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import pistonmc.gtnop.api.IOreLayer;
import pistonmc.gtnop.api.IOreManager;
import pistonmc.gtnop.api.IOreSmall;
import pistonmc.gtnop.nei.NEIHandlerOreLayer;
import pistonmc.gtnop.nei.NEIHandlerOreSmall;

/**
 * Entry point for NEI integration
 *
 * According to NEI this must be named NEIxxxConfig, IDK if that's true 
 */
public class NEIModConfig implements IConfigureNEI {

    @Override
    public String getName() {
        return ModInfo.NAME;
    }

    @Override
    public String getVersion() {
        return ModInfo.VERSION;
    }

    @Override
    public void loadConfig() {
        System.out.println("Loading NEI GTNEIOrePlugin config...");
        for (IOreManager<IOreLayer> manager: ModMain.instance.oreLayerManagers) {
            NEIHandlerOreLayer handler = new NEIHandlerOreLayer(manager);
            API.registerRecipeHandler(handler);
            API.registerUsageHandler(handler);
        }
        for (IOreManager<IOreSmall> manager: ModMain.instance.oreSmallManagers) {
            NEIHandlerOreSmall handler = new NEIHandlerOreSmall(manager);
            API.registerRecipeHandler(handler);
            API.registerUsageHandler(handler);
        }
    }

    public static String managerIdToHandlerId(String managerId) {
        return "gtnop.handler." + managerId;
    }

    public static final int GUI_WIDTH = 166;
    public static final int GUI_FIRST_LINE_Y = 27;
    public static final int GUI_LINE_HEIGHT = 13;
    public static final int GUI_HALF_X = 88;

}
