package me.ichun.mods.limitedlives.common.core;

import com.mojang.brigadier.CommandDispatcher;
import me.ichun.mods.ichunutil.common.entity.EntityHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.limitedlives.api.IApi;
import me.ichun.mods.limitedlives.common.LimitedLives;
import me.ichun.mods.limitedlives.common.command.CommandLimitedLives;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

public abstract class EventHandlerServer
    implements IApi
{
    public static final String LL_PERSISTED_TAG = "LimitedLivesSave";

    public static final UUID HEALTH_MODIFIER_UUID = Mth.createInsecureUUID(RandomSource.create("Limited Lives Attribute Modifier ID".hashCode() * 57659L));

    public EventHandlerServer()
    {
        iChunUtil.eS().registerPlayerTickEndListener(player -> {
            if(!player.level().isClientSide() && player.tickCount % 20 == 0)
            {
                onPlayerTickEnd(player);
            }
        });
    }

    public void onLivingDeath(LivingEntity living) //check for player death
    {
        if(!living.getCommandSenderWorld().isClientSide && living instanceof ServerPlayer player && !(EntityHelper.isFakePlayer(player)))
        {
            if(player.gameMode.getGameModeForPlayer() == GameType.CREATIVE || player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR)
            {
                return;
            }
            CompoundTag tag = EntityHelper.getPlayerPersistentData(player, LL_PERSISTED_TAG);
            tag.putInt("deathCount", tag.getInt("deathCount") + 1); //Save the death count
            tag.putLong("lastDeath", System.currentTimeMillis());
        }
    }

    public void onPlayerRespawn(ServerPlayer player, boolean endConquered)
    {
        if(endConquered)
        {
            return;
        }
        if(player.gameMode.getGameModeForPlayer() == GameType.CREATIVE || player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR)
        {
            return;
        }

        CompoundTag tag = EntityHelper.getPlayerPersistentData(player, LL_PERSISTED_TAG);
        int deaths = tag.getInt("deathCount");
        if(deaths >= LimitedLives.config.maxLives)
        {
            AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
            attribute.removePermanentModifier(HEALTH_MODIFIER_UUID); //Remove it, change only if config is true.

            //do ban
            MinecraftServer server = player.getServer();
            if(LimitedLives.config.banType == LimitedLives.BanType.SPECTATOR || server.isSingleplayer() && server.getSingleplayerProfile().getName().equals(player.getName().getString()))
            {
                tag.putInt("gameMode", player.gameMode.getGameModeForPlayer().getId());
                tag.putLong("timeBanned", System.currentTimeMillis());
                player.setGameMode(GameType.SPECTATOR);
                player.fallDistance = 0.0F;
                player.displayClientMessage(LimitedLives.config.banDuration == 0 ? Component.translatable("limitedlives.spectateForcePerma") : Component.translatable("limitedlives.spectateForce", LimitedLives.config.banDuration), false);
            }
            else
            {
                UserBanListEntry userlistbansentry = new UserBanListEntry(player.getGameProfile(), null, LimitedLives.MOD_NAME, LimitedLives.config.banDuration == 0 ? null : new Date(System.currentTimeMillis() + (LimitedLives.config.banDuration * 1000L)), Component.translatable("limitedlives.banReason").getString());
                server.getPlayerList().getBans().add(userlistbansentry);
                player.connection.disconnect(Component.translatable("limitedlives.banKickReason"));
            }
        }
        else
        {
            setPlayerDeaths(player, deaths, true);

            if(LimitedLives.config.announceOnRespawn)
            {
                player.displayClientMessage(Component.translatable("limitedlives.livesLeft", LimitedLives.config.maxLives - deaths), false);
            }
        }
    }

    public void onPlayerTickEnd(Player playerUnsided)
    {
        if(playerUnsided instanceof ServerPlayer player)
        {
            CompoundTag tag = EntityHelper.getPlayerPersistentData(player, LL_PERSISTED_TAG);
            int deaths = tag.getInt("deathCount");
            if(LimitedLives.config.timeToNewLife > 0 && tag.contains("lastDeath") && deaths > 0) //calculate if it's time to give a new life
            {
                long timeOfLastDeath = tag.getLong("lastDeath");
                long timeSinceLastDeath = System.currentTimeMillis() - timeOfLastDeath;
                int timeToNewLifeMs = LimitedLives.config.timeToNewLife * 1000;
                if(timeSinceLastDeath >= timeToNewLifeMs)
                {
                    int livesToGive = Mth.clamp((int)Math.floor((double)timeSinceLastDeath / timeToNewLifeMs), 0, LimitedLives.config.maxLives - deaths);

                    deaths = Mth.clamp(deaths - livesToGive, 0, Integer.MAX_VALUE);
                    tag.putInt("deathCount", deaths);
                    tag.putLong("lastDeath", System.currentTimeMillis());

                    player.displayClientMessage(Component.translatable("limitedlives.livesRegained", livesToGive, LimitedLives.config.maxLives - deaths), false);
                }
            }

            if(deaths >= LimitedLives.config.maxLives && LimitedLives.config.banDuration > 0 && player.isAlive()) //is "banned, config has ban duration > 0 (not permaban), player is alive
            {
                long timeBanned = tag.getLong("timeBanned");
                long banDurationMs = (LimitedLives.config.banDuration * 1000L);
                long timeBanDone = System.currentTimeMillis() - timeBanned; // in MS
                long timeBanLeft = banDurationMs - timeBanDone;

                int announceTimeFrequency = LimitedLives.config.timeRemainingMessageFrequency * 60 * 1000;
                //if timeBanLeft mod 5 mins is more than timeBanLeft + 1 second, it just hit 5 mins round, send message if timeBanLeft + 1s is > 5 mins still.
                if(announceTimeFrequency > 0 && timeBanLeft % announceTimeFrequency > (timeBanLeft + 1000L) % announceTimeFrequency && (timeBanLeft + 1000L) > announceTimeFrequency && player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR)
                {
                    player.displayClientMessage(Component.translatable("limitedlives.respawnTimeLeft", (int)Math.ceil(timeBanLeft / 60000F)), false);
                }
                if((new Date(timeBanned + banDurationMs)).before(new Date()) || player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) //later is to say, player was pardoned by an op.
                {
                    //time to "unban"
                    pardon(player, tag);
                }
            }
        }
    }

    public void pardon(ServerPlayer player, CompoundTag tag)
    {
        boolean respawn = false;
        if(player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR)
        {
            respawn = true;

            player.gameMode.changeGameModeForPlayer(GameType.byId(tag.getInt("gameMode")));

            AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
            attribute.removePermanentModifier(HEALTH_MODIFIER_UUID);
        }

        tag.remove("deathCount");
        tag.remove("lastDeath");
        tag.remove("gameMode");
        tag.remove("timeBanned");
        if(respawn)
        {
            player.connection.player = player.getServer().getPlayerList().respawn(player, false); // recreatePlayerEntity
            player.displayClientMessage(Component.translatable("limitedlives.respawned"), false);
        }
    }

    public void onRegisterCommands(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        CommandLimitedLives.register(dispatcher);
    }

    @Override
    public void setPlayerDeaths(@NotNull ServerPlayer player, int deaths, boolean resetHealth)
    {
        CompoundTag tag = EntityHelper.getPlayerPersistentData(player, EventHandlerServer.LL_PERSISTED_TAG);
        tag.putInt("deathCount", deaths);
        tag.putLong("lastDeath", System.currentTimeMillis());

        if(LimitedLives.config.healthAdjust != 0D)
        {
            double healthRatio = player.getHealth() / player.getMaxHealth();

            AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
            attribute.removePermanentModifier(EventHandlerServer.HEALTH_MODIFIER_UUID); //Remove it, change only if config is true.

            double healthOffset = LimitedLives.config.healthAdjust * deaths;

            if(LimitedLives.config.maxHealthReduction < 0D && healthOffset < LimitedLives.config.maxHealthReduction)
            {
                healthOffset = LimitedLives.config.maxHealthReduction;
            }

            attribute.addPermanentModifier(new AttributeModifier(EventHandlerServer.HEALTH_MODIFIER_UUID, "LimitedLivesMaxHealthModifier", healthOffset, AttributeModifier.Operation.ADD_VALUE));

            if(resetHealth)
            {
                player.setHealth((float)attribute.getValue());
            }
            else
            {
                double targetHealth = healthRatio * player.getMaxHealth();

                player.setHealth((float)targetHealth);
            }
        }
    }

    @Override
    public int getPlayerDeaths(@NotNull ServerPlayer player)
    {
        CompoundTag tag = EntityHelper.getPlayerPersistentData(player, EventHandlerServer.LL_PERSISTED_TAG);
        return tag.getInt("deathCount");
    }
}
