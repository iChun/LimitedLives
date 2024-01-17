package me.ichun.mods.limitedlives.loader.forge;

import me.ichun.mods.limitedlives.common.LimitedLives;
import me.ichun.mods.limitedlives.common.core.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigForge extends Config
{
    public ConfigForge(ForgeConfigSpec.Builder builder)
    {
        builder.comment("General settings").push("general");

        final ForgeConfigSpec.IntValue cMaxLives = builder.comment("Maximum lives a player can have before being \"banned\".")
            .translation("config.limitedlives.prop.maxLives.desc")
            .defineInRange("maxLives", 20, 1, Integer.MAX_VALUE);
        maxLives = new ConfigWrapper<>(cMaxLives::get, cMaxLives::set, cMaxLives::save);

        final ForgeConfigSpec.EnumValue<LimitedLives.BanType> cBanType = builder.comment("Ban type once the player dies too many times.\nAccepts: SPECTATOR, BAN")
            .translation("config.limitedlives.prop.banType.desc")
            .defineEnum("banType", LimitedLives.BanType.SPECTATOR);
        banType = new ConfigWrapper<>(cBanType::get, cBanType::set, cBanType::save);

        final ForgeConfigSpec.IntValue cBanTime = builder.comment("Length of time the player is banned (in seconds). Set to 0 to permaban.")
            .translation("config.limitedlives.prop.banTime.desc")
            .defineInRange("banTime", 5 * 60, 0, Integer.MAX_VALUE);
        banDuration = new ConfigWrapper<>(cBanTime::get, cBanTime::set, cBanTime::save);

        final ForgeConfigSpec.DoubleValue cHealthAdjust = builder.comment("How much health to change per death.")
            .translation("config.limitedlives.prop.healthAdjust.desc")
            .defineInRange("healthAdjust", -1D, -20D, 20D);
        healthAdjust = new ConfigWrapper<>(cHealthAdjust::get, cHealthAdjust::set, cHealthAdjust::save);

        final ForgeConfigSpec.DoubleValue cMaxHealthReduction = builder.comment("What's the maximum health reduction allowed before it caps out? Set to 0 to disable cap.")
            .translation("config.limitedlives.prop.maxHealthReduction.desc")
            .defineInRange("maxHealthReduction", 0D, -500D, 0D);
        maxHealthReduction = new ConfigWrapper<>(cMaxHealthReduction::get, cMaxHealthReduction::set, cMaxHealthReduction::save);

        final ForgeConfigSpec.BooleanValue cAnnounceOnRespawn = builder.comment("Should we announce the player their remaining lives on respawn?")
            .translation("config.limitedlives.prop.announceOnRespawn.desc")
            .define("announceOnRespawn", true);
        announceOnRespawn = new ConfigWrapper<>(cAnnounceOnRespawn::get, cAnnounceOnRespawn::set, cAnnounceOnRespawn::save);

        builder.pop();
    }
}
