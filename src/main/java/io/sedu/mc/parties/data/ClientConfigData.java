package io.sedu.mc.parties.data;

import io.sedu.mc.parties.api.mod.gamestages.SyncType;
import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfigData {

    public static ForgeConfigSpec.BooleanValue renderPotionEffects;
    public static ForgeConfigSpec.BooleanValue renderXPBar;
    public static ForgeConfigSpec.BooleanValue renderPlayerHealth;
    public static ForgeConfigSpec.BooleanValue renderPlayerArmor;
    public static ForgeConfigSpec.BooleanValue renderHunger;
    public static ForgeConfigSpec.BooleanValue renderThirst;
    public static ForgeConfigSpec.BooleanValue renderTemperature;
    public static ForgeConfigSpec.BooleanValue renderMana;
    public static ForgeConfigSpec.BooleanValue renderSSMana;
    public static ForgeConfigSpec.BooleanValue renderFeathers;
    //public static ForgeConfigSpec.BooleanValue renderStamina;
    //public static ForgeConfigSpec.ConfigValue<String> defaultPreset;
    public static ForgeConfigSpec.BooleanValue forceModelRotation;
    public static ForgeConfigSpec.IntValue rotationOffset;
    public static ForgeConfigSpec.BooleanValue renderSelfFrame;

    //public static ForgeConfigSpec.IntValue xPos;
    //public static ForgeConfigSpec.IntValue yPos;
    //public static ForgeConfigSpec.IntValue xPosParty;
    //public static ForgeConfigSpec.IntValue yPosParty;
    //public static ForgeConfigSpec.DoubleValue scale;
    //public static ForgeConfigSpec.DoubleValue partyScale;
    public static ForgeConfigSpec.ConfigValue<SyncType> defaultSync;
    public static void registerClientConfig(ForgeConfigSpec.Builder CLIENT_BUILDER) {
        CLIENT_BUILDER.comment("--------------------------------------------------------",
                               "Client Party Settings",
                               "Type /party reload in-game to reload this configuration.",
                               "--------------------------------------------------------");
        //CLIENT_BUILDER.push("presets");
        //defaultPreset = CLIENT_BUILDER.comment("The default preset to load on the client. It's automatically set to load your last preset used.")
                                      //.define("defaultPreset", "");
        //CLIENT_BUILDER.pop();
        CLIENT_BUILDER.push("hide-overlays");
        renderPotionEffects = CLIENT_BUILDER.comment("Enable the Vanilla Potion Effect HUD")
                                     .define("renderPotionEffects", false);
        renderXPBar = CLIENT_BUILDER.comment("Enable the vanilla XP Bar Display")
                                            .define("renderXPBar", true);
        renderPlayerHealth = CLIENT_BUILDER.comment("Enable the Vanilla Health HUD")
                                            .define("renderPlayerHealth", true);
        renderPlayerArmor = CLIENT_BUILDER.comment("Enable the Vanilla Armor HUD")
                                            .define("renderPlayerArmor", true);
        renderHunger = CLIENT_BUILDER.comment("Enable the Vanilla Hunger Display")
                                     .define("renderHunger", true);
        renderThirst = CLIENT_BUILDER.comment("Enable the Thirst Bar Display (Tough as Nails, Thirst was Taken)")
                                     .define("renderThirst", true);
        renderTemperature = CLIENT_BUILDER.comment("Enable the Temperature Display (Tough as Nails)")
                                     .define("renderTemperature", true);
        renderMana = CLIENT_BUILDER.comment("Enable the Mana Display (Ars Nouveau)")
                                          .define("renderMana", true);
        renderSSMana = CLIENT_BUILDER.comment("Enable the Mana Display (Spells and Shields)")
                                     .define("renderSSMana", true);
        renderFeathers = CLIENT_BUILDER.comment("Enable the Stamina display (Feathers Mod)")
                                   .define("renderFeathers", true);
        //renderStamina = CLIENT_BUILDER.comment("Enable the Stamina display (Epic Fight Mod)")
                                       //.define("renderStamina", true);
        CLIENT_BUILDER.pop();
        CLIENT_BUILDER.push("party-ui");
        renderSelfFrame = CLIENT_BUILDER.comment("Render your information as a party member.",
                                                 "If true, this will include you in the party list. This will also render your information when outside the party.",
                                                 "If false, your information will NOT be rendered both in a party and outside a party.")
                                        .define("renderSelfFrame", true);
        //xPos = CLIENT_BUILDER.comment("X Position of the player frame.",
                                      //"Note: The player frame is bounded by the screen size.")
                             //.defineInRange("xPos", 8, 0, Integer.MAX_VALUE);
        //yPos = CLIENT_BUILDER.comment("Y Position of the player frame.",
                                      //"Note: The player frame is bounded by the screen size.")
                             //.defineInRange("yPos", 8, 0, Integer.MAX_VALUE);
        //xPosParty = CLIENT_BUILDER.comment("X Position of the party frame.",
                                      //"Note: The party frame is bounded by the screen size.")
                             //.defineInRange("xPosParty", 8, 0, Integer.MAX_VALUE);
        //yPosParty = CLIENT_BUILDER.comment("Y Position of the party frame.",
                                      //"Note: The party frame is bounded by the screen size.")
                             //.defineInRange("yPosParty", 224, 0, Integer.MAX_VALUE);
        //scale = CLIENT_BUILDER.comment("The scale of the self frame.")
                             //.defineInRange("scale", 1.0, 0.5, 2.0);
        //partyScale = CLIENT_BUILDER.comment("The scale of the party frame.")
                             //.defineInRange("partyScale", .5, 0.5, 2.0);


        forceModelRotation = CLIENT_BUILDER.comment("Makes all the models drawn on the party frame face forward if true.",
                                                    "Self model is drawn when the head element has Head Type at 1, or 2 for the entire party.")
                .define("forceModelRotation", false);
        rotationOffset = CLIENT_BUILDER.comment("Offset of the front-facing model if forceModelRotation is enabled.",
                                                "Negative values make the model face right, while positive values make the model face left.")
                .defineInRange("rotationOffset", -20, -180, 180);
        CLIENT_BUILDER.pop();

        CLIENT_BUILDER.push("mod-options");
        defaultSync = CLIENT_BUILDER.comment("This allows you to set the type of automatic syncing you'd like for GameStages support",
                                             "When prompted from a party invite. Syncing only occurs if players allow it.",
                                             "ALL - Prompt disabled, all game stages are synced. Only works if server allows it.",
                                             "FUTURE - Prompt disabled, future game stages are synced. Only works if server allows it.",
                                             "NONE - Prompt disabled, no game stages are synced at all.",
                                             "PROMPT - A prompt asking for sync permission is sent each time you join a party.")
                .defineEnum("defaultSync", SyncType.NONE);
        CLIENT_BUILDER.pop();

    }
}
