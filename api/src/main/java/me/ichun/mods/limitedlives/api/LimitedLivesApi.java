package me.ichun.mods.limitedlives.api;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class LimitedLivesApi
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static IApi apiImpl = new IApi(){};

    /**
     * Get the IApi implementation for LimitedLives.
     * @return returns the IApi implementation from LimitedLives. Will not be an anonymous class if LimitedLives has loaded
     */
    public static IApi getApiImpl()
    {
        return apiImpl;
    }

    /**
     * Sets the IApi implementation for LimitedLives.
     * For use of LimitedLives, so please don't actually use this.
     * @param apiImpl API implementation to set.
     */
    public static void setApiImpl(IApi apiImpl)
    {
        LimitedLivesApi.apiImpl = apiImpl;
    }

    public static Logger getLogger()
    {
        return LOGGER;
    }

}
