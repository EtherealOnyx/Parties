package io.sedu.mc.parties.data;

import io.sedu.mc.parties.Parties;
import io.sedu.mc.parties.api.mod.gamestages.ServerSyncType;
import io.sedu.mc.parties.api.mod.openpac.PACCompatManager;
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
    public static ForgeConfigSpec.BooleanValue partiesSync;
    public static ForgeConfigSpec.BooleanValue partyPersistence;
    public static ForgeConfigSpec.BooleanValue allowGlobalUpdates;
    public static ForgeConfigSpec.ConfigValue<ServerSyncType> syncGameStages;



    public static void registerServerConfig(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Server Party Settings");
        SERVER_BUILDER.push("party-timers");
        playerUpdateInterval = SERVER_BUILDER.comment("Delay (in ticks) for player packet syncing (hunger, xp)")
                                             .defineInRange("playerUpdateInterval", 10, 10, 200);
        playerSlowUpdateInterval = SERVER_BUILDER.comment("Delay (in ticks) for player packet syncing for less frequent items (World Temp, etc)")
                                                 .defineInRange("playerSlowUpdateInterval", 40, 40, 800);
        playerAcceptTimer = SERVER_BUILDER.comment("Delay (in seconds) for player to accept invite before it automatically expires.")
                                          .defineInRange("playerAcceptTimer", 30, 5, 60);
        SERVER_BUILDER.pop();
        SERVER_BUILDER.push("party-mechanics");
        partySize = SERVER_BUILDER.comment("Max size for a party")
                                  .defineInRange("partySize", 5, 2, Integer.MAX_VALUE);
        friendlyFire = SERVER_BUILDER.comment("Allow players to attack each other in parties")
                                     .define("friendlyFire", false);
        enableShare = SERVER_BUILDER.comment("Allow players to share XP in a party.")
                                    .define("enableShare", true);
        globalShare = SERVER_BUILDER.comment("Enables XP Sharing between party members regardless of distance from each other.")
                                    .define("globalShare", true);
        partyPersistence = SERVER_BUILDER.comment("Makes parties persist between server launches. Disabling this removes all saved parties - they will have to be remade.")
                                         .define("partyPersistence", true);

        SERVER_BUILDER.pop();
        SERVER_BUILDER.push("mod-support");
        partiesSync = SERVER_BUILDER.comment("This allows for integration with other party mods. Enabling this forces party persistence to be enabled as well.",
                                                 "true - Enables party sync between Parties mod and Open Parties and Claims.",
                                             "false - Causes the Parties Mod to use its own party system by itself.",
                                             "NOTE: If enabled, the partySize setting is ignored.")
                                        .define("partiesSync", true);

        allowGlobalUpdates = SERVER_BUILDER.comment("This allows for extra modded information (like cast bars) to update regardless of distance.",
                                             "true - Enables this feature.  Members have all information shared regardless of distance.",
                                             "false - Disables this feature. Members have to be within tracking range for updates.",
                                             "APPLIES TO: Cast Bar Element")
                                    .define("allowGlobalUpdates", true);

        syncGameStages = SERVER_BUILDER.comment("Allow you to enable specific type of game stage syncing between party members.",
                                             "ALL - Players can opt-in to allow all game stage syncing, including previous entries.",
                                             "FUTURE - Players can only opt-in to allow future game stages to be synced",
                                             "NONE - Game stage syncing is disabled completely.")
                                    .defineEnum("syncGameStages", ServerSyncType.NONE);
        SERVER_BUILDER.pop();
    }

    public static boolean isPersistDisabled() {
        if (partyPersistence.get())
            return false;
        else {
            if (isPartySyncEnabled()) {
                Parties.LOGGER.error("[Parties] Party sync is enabled, so party persistence cannot be disabled. Enabling...");
                partyPersistence.set(true);
                partyPersistence.save();
                return false;
            }
        }
        return true;
    }

    public static boolean isPartySyncEnabled() {
        //TODO: FTB Teams Support
        if (partiesSync.get()) {
            if (!PACCompatManager.active()) {
                Parties.LOGGER.error("[Parties] Party sync was enabled but no syncable mods were found. Disabling...");
                partiesSync.set(false);
                partiesSync.save();
                return false;
            }
            return true;
        }
        return false;
    }
}
