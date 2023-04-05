package io.sedu.mc.parties.data;

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
    public static void registerClientConfig(ForgeConfigSpec.Builder CLIENT_BUILDER) {
        CLIENT_BUILDER.comment("Client Party Settings | Type /party reload in-game to reload this configuration.").push("party");

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
        CLIENT_BUILDER.pop();
    }
}
