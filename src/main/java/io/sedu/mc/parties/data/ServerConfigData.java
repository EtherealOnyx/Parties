package io.sedu.mc.parties.data;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfigData {
    public static ForgeConfigSpec.IntValue playerUpdateInterval;
    public static ForgeConfigSpec.IntValue playerAcceptTimer;

    //This needs to sync with clients if it is a config...
    public static Integer playerMessageCooldown = 10;
    public static ForgeConfigSpec.IntValue partySize;
    public static ForgeConfigSpec.BooleanValue friendlyFire;

    public static void registerServerConfig(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Server Party Settings").push("party");

        playerUpdateInterval = SERVER_BUILDER.comment("Delay (in ticks) for player packet syncing (hunger, xp)")
                                             .defineInRange("playerUpdateInterval", 10, 10, 200);
        playerAcceptTimer = SERVER_BUILDER.comment("Delay (in seconds) for player to accept invite before it automatically expires.")
                                          .defineInRange("playerAcceptTimer", 30, 5, 60);
        partySize = SERVER_BUILDER.comment("Max size for a party")
                                  .defineInRange("partySize", 5, 2, Integer.MAX_VALUE);
        friendlyFire = SERVER_BUILDER.comment("Allow players to attack each other in parties")
                                     .define("friendlyFire", false);
        SERVER_BUILDER.pop();
    }
}
