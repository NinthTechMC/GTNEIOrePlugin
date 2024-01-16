package pistonmc.gtnop.api;

public interface IOreWorldNamed extends IOreWorld {
    /** Get the dimension name of this world */
    String getDimensionName();

    /** Get the modid:name:meta icon string */
    String getIconString();

    /** Called in post-init to find the icon block and cache it*/
    void initIcon();

    /** Create the initialization object for this world */
    IWorldInit createWorldInit();
}
