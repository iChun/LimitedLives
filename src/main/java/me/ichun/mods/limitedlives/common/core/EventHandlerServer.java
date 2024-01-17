package me.ichun.mods.limitedlives.common.core;

import com.mojang.brigadier.CommandDispatcher;
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
    public static final String PLAYER_PERSISTED_NBT_TAG = "PlayerPersisted"; //As per Forge.

    public static final String LL_PERSISTED_TAG = "LimitedLivesSave";

    public static final UUID HEALTH_MODIFIER_UUID = Mth.createInsecureUUID(RandomSource.create("Limited Lives Attribute Modifier ID".hashCode() * 57659L));

    public final EntityPersistentDataHandler persistentDataHandler;

    protected EventHandlerServer(EntityPersistentDataHandler persistentDataHandler)
    {
        this.persistentDataHandler = persistentDataHandler;
    }

    public void onLivingDeath(LivingEntity living) //check for player death
    {
        if(!living.getCommandSenderWorld().isClientSide && living instanceof ServerPlayer player && !(isFakePlayer(player)))
        {
            if(player.gameMode.getGameModeForPlayer() == GameType.CREATIVE || player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR)
            {
                return;
            }
            CompoundTag tag = getPlayerPersistentData(player, LL_PERSISTED_TAG);
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

        CompoundTag tag = getPlayerPersistentData(player, LL_PERSISTED_TAG);
        int deaths = tag.getInt("deathCount");
        if(deaths >= LimitedLives.config.maxLives.get())
        {
            AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
            attribute.removePermanentModifier(HEALTH_MODIFIER_UUID); //Remove it, change only if config is true.

            //do ban
            MinecraftServer server = player.getServer();
            if(LimitedLives.config.banType.get() == LimitedLives.BanType.SPECTATOR || server.isSingleplayer() && server.getSingleplayerProfile().getName().equals(player.getName().getString()))
            {
                tag.putInt("gameMode", player.gameMode.getGameModeForPlayer().getId());
                tag.putLong("timeBanned", System.currentTimeMillis());
                player.setGameMode(GameType.SPECTATOR);
                player.fallDistance = 0.0F;
                player.displayClientMessage(LimitedLives.config.banDuration.get() == 0 ? Component.translatable("limitedlives.spectateForcePerma") : Component.translatable("limitedlives.spectateForce", LimitedLives.config.banDuration.get()), false);
            }
            else
            {
                UserBanListEntry userlistbansentry = new UserBanListEntry(player.getGameProfile(), null, LimitedLives.MOD_NAME, LimitedLives.config.banDuration.get() == 0 ? null : new Date(System.currentTimeMillis() + (LimitedLives.config.banDuration.get() * 1000L)), Component.translatable("limitedlives.banReason").getString());
                server.getPlayerList().getBans().add(userlistbansentry);
                player.connection.disconnect(Component.translatable("limitedlives.banKickReason"));
            }
        }
        else
        {
            setPlayerDeaths(player, deaths, true);

            if(LimitedLives.config.announceOnRespawn.get())
            {
                player.displayClientMessage(Component.translatable("limitedlives.livesLeft", LimitedLives.config.maxLives.get() - deaths), false);
            }
        }
    }

    public abstract void firePlayerTickEndEvent(Player player);
    public void onPlayerTickEnd(Player playerUnsided)
    {
        if(playerUnsided instanceof ServerPlayer player)
        {
            CompoundTag tag = getPlayerPersistentData(player, LL_PERSISTED_TAG);
            int deaths = tag.getInt("deathCount");
            if(LimitedLives.config.timeToNewLife.get() > 0 && tag.contains("lastDeath") && deaths > 0) //calculate if it's time to give a new life
            {
                long timeOfLastDeath = tag.getLong("lastDeath");
                long timeSinceLastDeath = System.currentTimeMillis() - timeOfLastDeath;
                int timeToNewLifeMs = LimitedLives.config.timeToNewLife.get() * 1000;
                if(timeSinceLastDeath >= timeToNewLifeMs)
                {
                    int livesToGive = Mth.clamp((int)Math.floor((double)timeSinceLastDeath / timeToNewLifeMs), 0, LimitedLives.config.maxLives.get() - deaths);

                    deaths = Mth.clamp(deaths - livesToGive, 0, Integer.MAX_VALUE);
                    tag.putInt("deathCount", deaths);
                    tag.putLong("lastDeath", System.currentTimeMillis());

                    player.displayClientMessage(Component.translatable("limitedlives.livesRegained", livesToGive, LimitedLives.config.maxLives.get() - deaths), false);
                }
            }

            if(deaths >= LimitedLives.config.maxLives.get() && LimitedLives.config.banDuration.get() > 0 && player.isAlive()) //is "banned, config has ban duration > 0 (not permaban), player is alive
            {
                long timeBanned = tag.getLong("timeBanned");
                long banDurationMs = (LimitedLives.config.banDuration.get() * 1000L);
                long timeBanDone = System.currentTimeMillis() - timeBanned; // in MS
                long timeBanLeft = banDurationMs - timeBanDone;

                int announceTimeFrequency = LimitedLives.config.timeRemainingMessageFrequency.get() * 60 * 1000;
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

    public CompoundTag getPlayerPersistentData(Player player, String key)
    {
        CompoundTag playerPersisted = persistentDataHandler.getPersistentData(player).getCompound(PLAYER_PERSISTED_NBT_TAG);
        persistentDataHandler.getPersistentData(player).put(PLAYER_PERSISTED_NBT_TAG, playerPersisted);
        CompoundTag persistentTag = playerPersisted.getCompound(key);
        playerPersisted.put(key, persistentTag);
        return persistentTag;
    }

    public abstract boolean isFabricEnv();
    public abstract boolean isFakePlayer(ServerPlayer player);

    @Override
    public void setPlayerDeaths(@NotNull ServerPlayer player, int deaths, boolean resetHealth)
    {
        CompoundTag tag = LimitedLives.eventHandlerServer.getPlayerPersistentData(player, EventHandlerServer.LL_PERSISTED_TAG);
        tag.putInt("deathCount", deaths);
        tag.putLong("lastDeath", System.currentTimeMillis());

        if(LimitedLives.config.healthAdjust.get() != 0D)
        {
            double healthRatio = player.getHealth() / player.getMaxHealth();

            AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
            attribute.removePermanentModifier(EventHandlerServer.HEALTH_MODIFIER_UUID); //Remove it, change only if config is true.

            double healthOffset = LimitedLives.config.healthAdjust.get() * deaths;

            if(LimitedLives.config.maxHealthReduction.get() < 0D && healthOffset < LimitedLives.config.maxHealthReduction.get())
            {
                healthOffset = LimitedLives.config.maxHealthReduction.get();
            }

            attribute.addPermanentModifier(new AttributeModifier(EventHandlerServer.HEALTH_MODIFIER_UUID, "LimitedLivesMaxHealthModifier", healthOffset, AttributeModifier.Operation.ADDITION));

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
        CompoundTag tag = LimitedLives.eventHandlerServer.getPlayerPersistentData(player, EventHandlerServer.LL_PERSISTED_TAG);
        return tag.getInt("deathCount");
    }
}
