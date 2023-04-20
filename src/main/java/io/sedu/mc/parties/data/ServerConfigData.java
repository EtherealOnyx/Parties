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
    public static ForgeConfigSpec.EnumValue<PartySync> partiesSyncType;
    public static ForgeConfigSpec.BooleanValue partyPersistence;


    enum PartySync {
        NONE,
        OPEN_PAC,
        PARTIES,
      //FTB_TEAMS
    }

    public static void registerServerConfig(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Server Party Settings").push("party");

        partiesSyncType = SERVER_BUILDER.comment("This allows for integration with other party mods. Any option aside from 'NONE' forces partyPersistence to true. The following options are:",
                                                 "NONE     - Ignore other party mods and use the Parties Mod as is. ",
                                                 "OPEN_PAC - Relies on Open-PAC for parties. This disables all commands from Parties and forces it to sync with Open_PAC.",
                                                 "PARTIES  - Relies on Parties for parties. This makes Open_PAC parties sync with this mod.")
                                        .defineEnum("partiesSyncType", PartySync.NONE);
        partyPersistence = SERVER_BUILDER.comment("Makes parties persist on server launches. Disabling this removes all saved parties - they will have to be remade.")
                                         .define("partyPersistence", true);

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

    public static boolean isPersistEnabled() {
        if (partyPersistence.get())
            return true;
        else {
            if (partiesSyncType.get() != PartySync.NONE) {
                partyPersistence.set(true);
                partyPersistence.save();
                return true;
            }
        }
        return false;
    }
}
