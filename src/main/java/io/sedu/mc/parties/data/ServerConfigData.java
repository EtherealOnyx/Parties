package io.sedu.mc.parties.data;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfigData {
    public static ForgeConfigSpec.IntValue playerUpdateInterval;
    public static ForgeConfigSpec.IntValue playerSlowUpdateInterval;
    public static ForgeConfigSpec.IntValue playerAcceptTimer;

    //This needs to sync with clients if it is a config...
    public static Integer playerMessageCooldown = 10;
    public static ForgeConfigSpec.IntValue partySize;
    public static ForgeConfigSpec.BooleanValue friendlyFire;
    public static ForgeConfigSpec.BooleanValue globalShare;
    public static ForgeConfigSpec.BooleanValue enableShare;

    public static void registerServerConfig(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Server Party Settings").push("party");

        playerUpdateInterval = SERVER_BUILDER.comment("Delay (in ticks) for player packet syncing (hunger, xp)")
                                             .defineInRange("playerUpdateInterval", 10, 10, 200);
        playerSlowUpdateInterval = SERVER_BUILDER.comment("Delay (in ticks) for player packet syncing for less frequent items (World Temp, etc)")
                .defineInRange("playerSlowUpdateInterval", 40, 40, 800);
        playerAcceptTimer = SERVER_BUILDER.comment("Delay (in seconds) for player to accept invite before it automatically expires.")
                                          .defineInRange("playerAcceptTimer", 30, 5, 60);
        partySize = SERVER_BUILDER.comment("Max size for a party")
                                  .defineInRange("partySize", 5, 2, Integer.MAX_VALUE);
        friendlyFire = SERVER_BUILDER.comment("Allow players to attack each other in parties")
                                     .define("friendlyFire", false);
        enableShare = SERVER_BUILDER.comment("Allow players to share XP in a party.")
                .define("enableShare", true);
        globalShare = SERVER_BUILDER.comment("Enables XP Sharing between party members regardless of distance from each other.")
                                    .define("globalShare", true);

        SERVER_BUILDER.pop();
    }
}
