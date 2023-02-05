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

    public ConfigFabric()
    {
        maxLives = new ConfigWrapper<>(() -> GENERAL.maxLives, v -> GENERAL.maxLives = v);
        banType = new ConfigWrapper<>(() -> GENERAL.banType, v -> GENERAL.banType = v);
        banTime = new ConfigWrapper<>(() -> GENERAL.banTime, v -> GENERAL.banTime = v);
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

        @ConfigEntry(nameKey = "config.limitedlives.prop.maxLives.name", descriptionKey = "config.limitedlives.prop.maxLives.desc", comment = "Maximum lives a player can have before being \"banned\".")
        @ConfigEntry.BoundedInteger(min = 1)
        public int maxLives = 20;

        @ConfigEntry(nameKey = "config.limitedlives.prop.banType.name", descriptionKey = "config.limitedlives.prop.banType.desc", comment = "Ban type once the player dies too many times.\nAccepts: SPECTATOR, BAN")
        public LimitedLives.BanType banType = LimitedLives.BanType.SPECTATOR;

        @ConfigEntry(nameKey = "config.limitedlives.prop.banTime.name", descriptionKey = "config.limitedlives.prop.banTime.desc", comment = "Length of time the player is banned (in seconds). Set to 0 to permaban.")
        @ConfigEntry.BoundedInteger(min = 0)
        public int banTime = 5 * 60;

        @ConfigEntry(nameKey = "config.limitedlives.prop.healthAdjust.name", descriptionKey = "config.limitedlives.prop.healthAdjust.desc", comment = "How much health to change per death.")
        @ConfigEntry.BoundedDouble(min = -20D, max = 20D)
        public double healthAdjust = -1D;

        @ConfigEntry(nameKey = "config.limitedlives.prop.maxHealthReduction.name", descriptionKey = "config.limitedlives.prop.maxHealthReduction.desc", comment = "What's the maximum health reduction allowed before it caps out? Set to 0 to disable cap.")
        @ConfigEntry.BoundedDouble(min = -500D, max = 0D)
        public double maxHealthReduction = 0D;

        @ConfigEntry(nameKey = "config.limitedlives.prop.announceOnRespawn.name", descriptionKey = "config.limitedlives.prop.announceOnRespawn.desc", comment = "Should we announce the player their remaining lives on respawn?")
        public boolean announceOnRespawn = true;
    }
}