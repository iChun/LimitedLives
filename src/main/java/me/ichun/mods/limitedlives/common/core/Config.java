package me.ichun.mods.limitedlives.common.core;

import me.ichun.mods.limitedlives.common.LimitedLives;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Config
{
    public ConfigWrapper<Integer> maxLives;
    public ConfigWrapper<LimitedLives.BanType> banType;
    public ConfigWrapper<Integer> banTime;
    public ConfigWrapper<Double> healthAdjust;
    public ConfigWrapper<Double> maxHealthReduction;
    public ConfigWrapper<Boolean> announceOnRespawn;

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
