package me.ichun.mods.limitedlives.common.core;

import me.ichun.mods.limitedlives.common.LimitedLives;
import me.ichun.mods.limitedlives.common.command.LimitedLivesCommand;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.server.command.TextComponentHelper;

import java.util.Date;

public class EventHandler
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerDeath(LivingDeathEvent event)
    {
        if(!event.getEntityLiving().getCommandSenderWorld().isClientSide && event.getEntityLiving() instanceof Player && !(event.getEntityLiving() instanceof FakePlayer))
        {
            ServerPlayer player = (ServerPlayer)event.getEntityLiving();
            if(player.gameMode.getGameModeForPlayer() == GameType.CREATIVE || player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR)
            {
                return;
            }
            CompoundTag tag = EntityHelper.getPlayerPersistentData(player, "LimitedLivesSave");
            int prevDeaths = tag.getInt("deathCount");
            int liveCount = tag.getInt("maxLives");
            if(liveCount == 0)
            {
                liveCount = LimitedLives.config.maxLives.get();
            }
            tag.putDouble("healthOffset", event.getEntityLiving().getAttribute(Attributes.MAX_HEALTH).getBaseValue() - (20D - (20D * prevDeaths / (double)liveCount)));
            tag.putInt("deathCount", prevDeaths + 1);
            tag.putInt("maxLives", LimitedLives.config.maxLives.get());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        CompoundTag tag = EntityHelper.getPlayerPersistentData(event.getPlayer(), "LimitedLivesSave");
        int deaths = tag.getInt("deathCount");
        if(deaths >= LimitedLives.config.maxLives.get())
        {
            //do ban
            ServerPlayer player = (ServerPlayer)event.getPlayer();
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if(LimitedLives.config.banType.get() == LimitedLives.BanType.SPECTATOR || server.isSingleplayer() && server.getSingleplayerName().equals(player.getName().getContents()))
            {
                tag.putInt("gameMode", player.gameMode.getGameModeForPlayer().getId());
                tag.putLong("banTime", System.currentTimeMillis());
                player.setGameMode(GameType.SPECTATOR);
                player.fallDistance = 0.0F;
                player.displayClientMessage(LimitedLives.config.banTime.get() == 0 ? TextComponentHelper.createComponentTranslation(player, "limitedlives.spectateForcePerma") : TextComponentHelper.createComponentTranslation(player, "limitedlives.spectateForce", LimitedLives.config.banTime.get()), false);
            }
            else
            {
                UserBanListEntry userlistbansentry = new UserBanListEntry(player.getGameProfile(), null, LimitedLives.MOD_NAME, LimitedLives.config.banTime.get() == 0 ? null : new Date(System.currentTimeMillis() + (LimitedLives.config.banTime.get() * 1000L)), TextComponentHelper.createComponentTranslation(player, "limitedlives.banReason").toString());
                server.getPlayerList().getBans().add(userlistbansentry);
                player.connection.disconnect(TextComponentHelper.createComponentTranslation(player, "limitedlives.banKickReason"));
            }
        }
        else if(LimitedLives.config.healthAdjust.get())
        {
            double nextHealth = Math.max(20 - (deaths / (double)LimitedLives.config.maxLives.get() * 20D) + tag.getDouble("healthOffset"), 1D);
            event.getPlayer().getAttribute(Attributes.MAX_HEALTH).setBaseValue(nextHealth);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.side.isServer() && event.phase == TickEvent.Phase.END && event.player.tickCount % 20 == 0)
        {
            ServerPlayer player = (ServerPlayer)event.player;
            CompoundTag tag = EntityHelper.getPlayerPersistentData(player, "LimitedLivesSave");
            int deaths = tag.getInt("deathCount");
            if(deaths >= LimitedLives.config.maxLives.get() && LimitedLives.config.banTime.get() > 0 && player.isAlive())
            {
                long banTime = tag.getLong("banTime");
                if((banTime + (LimitedLives.config.banTime.get() * 1000L) - System.currentTimeMillis()) % FIVE_MINS_IN_MS > (banTime + 1000L + (LimitedLives.config.banTime.get() * 1000L) - System.currentTimeMillis()) % FIVE_MINS_IN_MS && banTime + 1000L + (LimitedLives.config.banTime.get() * 1000L) - System.currentTimeMillis() > FIVE_MINS_IN_MS && player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR)
                {
                    player.displayClientMessage(TextComponentHelper.createComponentTranslation(player, "limitedlives.respawnTimeLeft", (int)Math.ceil((banTime + (LimitedLives.config.banTime.get() * 1000L) - System.currentTimeMillis()) / (float)FIVE_MINS_IN_MS * 5F)), false);
                }
                if((new Date(banTime + (LimitedLives.config.banTime.get() * 1000L))).before(new Date()) || player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) //later is to say, player was pardoned by an op.
                {
                    //time to "unban"
                    boolean respawn = false;
                    if(player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR)
                    {
                        respawn = true;
                        if(LimitedLives.config.healthAdjust.get())
                        {
                            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20 + tag.getDouble("healthOffset"));
                        }
                        player.gameMode.changeGameModeForPlayer(GameType.byId(tag.getInt("gameMode")));
                    }
                    tag.remove("deathCount");
                    tag.remove("maxLives");
                    tag.remove("gameMode");
                    tag.remove("banTime");
                    if(respawn)
                    {
                        player.connection.player = ServerLifecycleHooks.getCurrentServer().getPlayerList().respawn(player, false); // recreatePlayerEntity
                        if(LimitedLives.config.healthAdjust.get())
                        {
                            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20 + tag.getDouble("healthOffset"));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event)
    {
        LimitedLivesCommand.register(event.getDispatcher());
    }

    public static final int FIVE_MINS_IN_MS = 5 * 60 * 1000;
}
