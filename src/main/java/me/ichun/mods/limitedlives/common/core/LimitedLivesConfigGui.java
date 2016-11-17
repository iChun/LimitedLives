package me.ichun.mods.limitedlives.common.core;

import me.ichun.mods.limitedlives.common.LimitedLives;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;

public class LimitedLivesConfigGui extends GuiConfig
{
    public LimitedLivesConfigGui(GuiScreen parentScreen)
    {
        super(parentScreen,
                new ConfigElement(LimitedLives.config.getCategory("general")).getChildElements(),
                Reference.NAME, false, false, GuiConfig.getAbridgedConfigPath(LimitedLives.config.toString()));
    }
}
