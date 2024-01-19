package me.ichun.mods.limitedlives.common.core;

import me.ichun.mods.limitedlives.common.LimitedLives;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Config
{
    public ConfigWrapper<Integer> maxLives;
    public ConfigWrapper<LimitedLives.BanType> banType;
    public ConfigWrapper<Integer> banDuration;
    public ConfigWrapper<Integer> timeToNewLife;
    public ConfigWrapper<Integer> timeRemainingMessageFrequency;
    public ConfigWrapper<Double> healthAdjust;
    public ConfigWrapper<Double> maxHealthReduction;
    public ConfigWrapper<Boolean> announceOnRespawn;

    protected static class Reference
    {
        public static final String MAX_LIVES_COMMENT = "Maximum lives a player can have before being \"banned\".";
        public static final String BAN_TYPE_COMMENT = "Ban type once the player dies too many times.\nAccepts: SPECTATOR, BAN";
        public static final String BAN_DURATION_COMMENT = "Length of time (in seconds) the player is banned. Set to 0 to permaban.";
        public static final String TIME_TO_NEW_LIFE_COMMENT = "Length of time (in seconds) since last death for the player to regain a life. Set to 0 to disable.";
        public static final String TIME_REMAINING_MESSAGE_FREQUENCY_COMMENT = "Length of time (in minutes) between messages announcing time remaining of ban. Set to 0 to disable.";
        public static final String HEALTH_ADJUST_COMMENT = "How much health to change per death.";
        public static final String MAX_HEALTH_REDUCTION_COMMENT = "What's the maximum health reduction allowed before it caps out? Set to 0 to disable cap.";
        public static final String ANNOUNCE_ON_RESPAWN_COMMENT = "Should we announce the player their remaining lives on respawn?";
    }

    public static class ConfigWrapper<T>
    {
        public final Supplier<T> getter;
        public final Consumer<T> setter;
        public final Runnable saver;

        public ConfigWrapper(Supplier<T> getter, Consumer<T> setter) {
            this.getter = getter;
            this.setter = setter;
            this.saver = null;
        }

        public ConfigWrapper(Supplier<T> getter, Consumer<T> setter, Runnable saver) {
            this.getter = getter;
            this.setter = setter;
            this.saver = saver;
        }

        public T get()
        {
            return getter.get();
        }

        public void set(T obj)
        {
            setter.accept(obj);
        }

        public void save()
        {
            if(saver != null)
            {
                saver.run();
            }
        }
    }
}
