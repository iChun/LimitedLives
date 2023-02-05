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
    @ConfigEntries
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

        @ConfigEntry(comment = "Maximum lives a player can have before being \"banned\".")
        @ConfigEntry.BoundedInteger(min = 1)
        public int maxLives = 20;

        @ConfigEntry(comment = "Ban type once the player dies too many times.\nAccepts: SPECTATOR, BAN")
        public LimitedLives.BanType banType = LimitedLives.BanType.SPECTATOR;

        @ConfigEntry(comment = "Length of time the player is banned (in seconds). Set to 0 to permaban.")
        @ConfigEntry.BoundedInteger(min = 0)
        public int banTime = 5 * 60;

        @ConfigEntry(comment = "How much health to change per death.")
        @ConfigEntry.BoundedDouble(min = -20D, max = 20D)
        public double healthAdjust = -1D;

        @ConfigEntry(comment = "What's the maximum health reduction allowed before it caps out? Set to 0 to disable cap.")
        @ConfigEntry.BoundedDouble(min = -500D, max = 0D)
        public double maxHealthReduction = 0D;

        @ConfigEntry(comment = "Should we announce the player their remaining lives on respawn?")
        public boolean announceOnRespawn = true;
    }
}