package me.ichun.mods.limitedlives.common.core;

import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import me.ichun.mods.limitedlives.common.LimitedLives;
import org.jetbrains.annotations.NotNull;

public class Config extends ConfigBase
{
    @Prop(min = 1)
    public int maxLives = 20;

    public LimitedLives.BanType banType = LimitedLives.BanType.SPECTATOR;

    @Prop(min = 0)
    public int banDuration = 5 * 60;

    @Prop(min = 0)
    public int timeToNewLife = 0;

    @Prop(min = 0)
    public int timeRemainingMessageFrequency = 5;

    @Prop(min = -20D, max = 20D)
    public double healthAdjust = -1D;

    @Prop(min = -500D, max = 0D)
    public double maxHealthReduction = 0D;

    public boolean announceOnRespawn = true;

    @NotNull
    @Override
    public String getModId()
    {
        return LimitedLives.MOD_ID;
    }

    @NotNull
    @Override
    public String getConfigName()
    {
        return LimitedLives.MOD_NAME;
    }
}
