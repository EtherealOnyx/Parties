package eomods.combatoverhaul.eoparties.lib;

import java.util.HashSet;

public class Reference {
    //Version
    public static final String VERSION = "0.0.1";

    //Mod Info
    public static final String MODID = "eoparties";
    public static final String GROUP = "eomods.combatoverhaul.";
    public static final String NAME = "CO Party System";

    //Proxy
    public static final String P_CLIENT = GROUP + MODID + ".proxy.ClientProxy";
    public static final String P_SERVER = GROUP + MODID + ".proxy.ServerProxy";
    public static final int MAX_PARTY_SIZE = 5;

    public static HashSet<String> VALID_MEMBERS = new HashSet<>();

}
