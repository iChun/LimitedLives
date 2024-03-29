package me.ichun.mods.limitedlives.loader.forge;

import me.ichun.mods.limitedlives.common.LimitedLives;
import me.ichun.mods.limitedlives.common.core.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigForge extends Config
{
    public ConfigForge(ForgeConfigSpec.Builder builder)
    {
        builder.comment("General settings").push("general");

        final ForgeConfigSpec.IntValue cMaxLives = builder.comment(Reference.MAX_LIVES_COMMENT)
            .translation("config.limitedlives.prop.maxLives.desc")
            .defineInRange("maxLives", 20, 1, Integer.MAX_VALUE);
        maxLives = new ConfigWrapper<>(cMaxLives::get, cMaxLives::set, cMaxLives::save);

        final ForgeConfigSpec.EnumValue<LimitedLives.BanType> cBanType = builder.comment(Reference.BAN_TYPE_COMMENT)
            .translation("config.limitedlives.prop.banType.desc")
            .defineEnum("banType", LimitedLives.BanType.SPECTATOR);
        banType = new ConfigWrapper<>(cBanType::get, cBanType::set, cBanType::save);

        final ForgeConfigSpec.IntValue cBanDuration = builder.comment(Reference.BAN_DURATION_COMMENT)
            .translation("config.limitedlives.prop.banDuration.desc")
            .defineInRange("banDuration", 5 * 60, 0, Integer.MAX_VALUE);
        banDuration = new ConfigWrapper<>(cBanDuration::get, cBanDuration::set, cBanDuration::save);

        final ForgeConfigSpec.IntValue cTimeToNewLife = builder.comment(Reference.TIME_TO_NEW_LIFE_COMMENT)
            .translation("config.limitedlives.prop.timeToNewLife.desc")
            .defineInRange("timeToNewLife", 0, 0, Integer.MAX_VALUE);
        timeToNewLife = new ConfigWrapper<>(cTimeToNewLife::get, cTimeToNewLife::set, cTimeToNewLife::save);

        final ForgeConfigSpec.IntValue cTimeRemainingMessageFrequency = builder.comment(Reference.TIME_REMAINING_MESSAGE_FREQUENCY_COMMENT)
            .translation("config.limitedlives.prop.timeRemainingMessageFrequency.desc")
            .defineInRange("timeRemainingMessageFrequency", 5, 0, Integer.MAX_VALUE);
        timeRemainingMessageFrequency = new ConfigWrapper<>(cTimeRemainingMessageFrequency::get, cTimeRemainingMessageFrequency::set, cTimeRemainingMessageFrequency::save);

        final ForgeConfigSpec.DoubleValue cHealthAdjust = builder.comment(Reference.HEALTH_ADJUST_COMMENT)
            .translation("config.limitedlives.prop.healthAdjust.desc")
            .defineInRange("healthAdjust", -1D, -20D, 20D);
        healthAdjust = new ConfigWrapper<>(cHealthAdjust::get, cHealthAdjust::set, cHealthAdjust::save);

        final ForgeConfigSpec.DoubleValue cMaxHealthReduction = builder.comment(Reference.MAX_HEALTH_REDUCTION_COMMENT)
            .translation("config.limitedlives.prop.maxHealthReduction.desc")
            .defineInRange("maxHealthReduction", 0D, -500D, 0D);
        maxHealthReduction = new ConfigWrapper<>(cMaxHealthReduction::get, cMaxHealthReduction::set, cMaxHealthReduction::save);

        final ForgeConfigSpec.BooleanValue cAnnounceOnRespawn = builder.comment(Reference.ANNOUNCE_ON_RESPAWN_COMMENT)
            .translation("config.limitedlives.prop.announceOnRespawn.desc")
            .define("announceOnRespawn", true);
        announceOnRespawn = new ConfigWrapper<>(cAnnounceOnRespawn::get, cAnnounceOnRespawn::set, cAnnounceOnRespawn::save);

        builder.pop();
    }
}
