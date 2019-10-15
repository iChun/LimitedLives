package me.ichun.mods.limitedlives.common;

import me.ichun.mods.limitedlives.common.core.EventHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LimitedLives.MOD_ID)
public class LimitedLives
{
    public static final String MOD_ID = "limitedlives";
    public static final String MOD_NAME = "Limiged Lives";

    public static final Logger LOGGER = LogManager.getLogger();

    public static Config config;

    public LimitedLives()
    {
        //build the config
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();

        config = new Config(configBuilder);

        //register the config. This loads the config for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configBuilder.build());

        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public enum BanType
    {
        SPECTATOR,
        BAN
    }

    public class Config
    {
        public final ForgeConfigSpec.IntValue maxLives;
        public final ForgeConfigSpec.EnumValue<BanType> banType;
        public final ForgeConfigSpec.IntValue banTime;
        public final ForgeConfigSpec.BooleanValue healthAdjust;

        public Config(ForgeConfigSpec.Builder builder)
        {
            builder.comment("General settings").push("general");

            maxLives = builder.comment("Maximum lives a player can have before being \"banned\".")
                    .translation("config.limitedlives.prop.maxLives.comment")
                    .defineInRange("maxLives", 20, 1, Integer.MAX_VALUE);
            banType = builder.comment("Ban type once the player dies too many times.")
                    .translation("config.limitedlives.prop.banType.comment")
                    .defineEnum("banType", BanType.SPECTATOR);
            banTime = builder.comment("Length of time the player is banned (in seconds). Set to 0 to permaban.")
                    .translation("config.limitedlives.prop.banTime.comment")
                    .defineInRange("banTime", 5 * 60, 0, Integer.MAX_VALUE);
            healthAdjust = builder.comment("Adjust health of the player?")
                    .translation("config.limitedlives.prop.healthAdjust.comment")
                    .define("healthAdjust", true);

            builder.pop();
        }
    }
}