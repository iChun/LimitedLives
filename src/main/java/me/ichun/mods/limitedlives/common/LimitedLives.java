package me.ichun.mods.limitedlives.common;

import com.mojang.logging.LogUtils;
import me.ichun.mods.limitedlives.api.LimitedLivesApi;
import me.ichun.mods.limitedlives.common.core.Config;
import me.ichun.mods.limitedlives.common.core.EventHandlerServer;
import org.slf4j.Logger;

public abstract class LimitedLives
{
    public static final String MOD_ID = "limitedlives";
    public static final String MOD_NAME = "Limited Lives";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static LimitedLives modProxy;

    public static Config config;

    public static EventHandlerServer eventHandlerServer;

    public enum BanType
    {
        SPECTATOR,
        BAN
    }

    public static void setEventHandlerServer(EventHandlerServer handler)
    {
        eventHandlerServer = handler;
        LimitedLivesApi.setApiImpl(handler);
    }
}
