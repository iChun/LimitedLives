package me.ichun.mods.limitedlives.loader.fabric;

import me.ichun.mods.limitedlives.common.LimitedLives;
import me.ichun.mods.limitedlives.common.core.Config;
import me.lortseam.completeconfig.api.ConfigContainer;
import me.lortseam.completeconfig.api.ConfigEntries;
import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.api.ConfigGroup;

public class ConfigFabric extends Config
        implements ConfigContainer
{
    public static General GENERAL = null;

    public me.lortseam.completeconfig.data.Config configInstance;

    public ConfigFabric()
    {
        maxLives = new ConfigWrapper<>(() -> GENERAL.maxLives, v -> GENERAL.maxLives = v);
        banType = new ConfigWrapper<>(() -> GENERAL.banType, v -> GENERAL.banType = v);
        banDuration = new ConfigWrapper<>(() -> GENERAL.banDuration, v -> GENERAL.banDuration = v);
        timeToNewLife = new ConfigWrapper<>(() -> GENERAL.timeToNewLife, v -> GENERAL.timeToNewLife = v);
        timeRemainingMessageFrequency = new ConfigWrapper<>(() -> GENERAL.timeRemainingMessageFrequency, v -> GENERAL.timeRemainingMessageFrequency = v);
        healthAdjust = new ConfigWrapper<>(() -> GENERAL.healthAdjust, v -> GENERAL.healthAdjust = v);
        maxHealthReduction = new ConfigWrapper<>(() -> GENERAL.maxHealthReduction, v -> GENERAL.maxHealthReduction = v);
        announceOnRespawn = new ConfigWrapper<>(() -> GENERAL.announceOnRespawn, v -> GENERAL.announceOnRespawn = v);
    }

    @ConfigContainer.Transitive
    @ConfigEntries(includeAll = true)
    public static class General implements ConfigGroup
    {
        public General()
        {
            GENERAL = this;
        }

        @Override
        public String getComment()
        {
            return "General configs that don't fit any other category.";
        }

        @ConfigEntry(nameKey = "prop.maxLives.name", descriptionKey = "prop.maxLives.desc", comment = Reference.MAX_LIVES_COMMENT)
        @ConfigEntry.BoundedInteger(min = 1)
        public int maxLives = 20;

        @ConfigEntry(nameKey = "prop.banType.name", descriptionKey = "prop.banType.desc", comment = Reference.BAN_TYPE_COMMENT)
        public LimitedLives.BanType banType = LimitedLives.BanType.SPECTATOR;

        @ConfigEntry(nameKey = "prop.banDuration.name", descriptionKey = "prop.banDuration.desc", comment = Reference.BAN_DURATION_COMMENT)
        @ConfigEntry.BoundedInteger(min = 0)
        public int banDuration = 5 * 60;

        @ConfigEntry(nameKey = "prop.timeToNewLife.name", descriptionKey = "prop.timeToNewLife.desc", comment = Reference.TIME_TO_NEW_LIFE_COMMENT)
        @ConfigEntry.BoundedInteger(min = 0)
        public int timeToNewLife = 0;

        @ConfigEntry(nameKey = "prop.timeRemainingMessageFrequency.name", descriptionKey = "prop.timeRemainingMessageFrequency.desc", comment = Reference.TIME_REMAINING_MESSAGE_FREQUENCY_COMMENT)
        @ConfigEntry.BoundedInteger(min = 0)
        public int timeRemainingMessageFrequency = 5;

        @ConfigEntry(nameKey = "prop.healthAdjust.name", descriptionKey = "prop.healthAdjust.desc", comment = Reference.HEALTH_ADJUST_COMMENT)
        @ConfigEntry.BoundedDouble(min = -20D, max = 20D)
        public double healthAdjust = -1D;

        @ConfigEntry(nameKey = "prop.maxHealthReduction.name", descriptionKey = "prop.maxHealthReduction.desc", comment = Reference.MAX_HEALTH_REDUCTION_COMMENT)
        @ConfigEntry.BoundedDouble(min = -500D, max = 0D)
        public double maxHealthReduction = 0D;

        @ConfigEntry(nameKey = "prop.announceOnRespawn.name", descriptionKey = "prop.announceOnRespawn.desc", comment = Reference.ANNOUNCE_ON_RESPAWN_COMMENT)
        public boolean announceOnRespawn = true;
    }
}