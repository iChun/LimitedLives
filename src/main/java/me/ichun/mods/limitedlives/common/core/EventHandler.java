package me.ichun.mods.limitedlives.common.core;

import me.ichun.mods.limitedlives.common.LimitedLives;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.GameType;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Date;

public class EventHandler
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerDeath(LivingDeathEvent event)
    {
        if(!event.getEntityLiving().worldObj.isRemote && event.getEntityLiving() instanceof EntityPlayer && !(event.getEntityLiving() instanceof FakePlayer))
        {
            EntityPlayerMP player = (EntityPlayerMP)event.getEntityLiving();
            if(player.interactionManager.getGameType() == GameType.CREATIVE || player.interactionManager.getGameType() == GameType.SPECTATOR)
            {
                return;
            }
            NBTTagCompound tag = EntityHelper.getPlayerPersistentData(player, "LimitedLivesSave");
            int prevDeaths = tag.getInteger("deathCount");
            int liveCount = tag.getInteger("maxLives");
            if(liveCount == 0)
            {
                liveCount = LimitedLives.maxLives;
            }
            tag.setDouble("healthOffset", event.getEntityLiving().getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() - (20D - (20D * prevDeaths / (double)liveCount)));
            tag.setInteger("deathCount", prevDeaths + 1);
            tag.setInteger("maxLives", LimitedLives.maxLives);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        NBTTagCompound tag = EntityHelper.getPlayerPersistentData(event.player, "LimitedLivesSave");
        int deaths = tag.getInteger("deathCount");
        if(deaths >= LimitedLives.maxLives)
        {
            //do ban
            EntityPlayerMP player = (EntityPlayerMP)event.player;
            MinecraftServer minecraftserver = FMLCommonHandler.instance().getMinecraftServerInstance();
            if(LimitedLives.banType == 1 || !minecraftserver.isDedicatedServer() && minecraftserver.getServerOwner() != null && player.getName().equals(minecraftserver.getServerOwner()))
            {
                tag.setInteger("gameMode", player.interactionManager.getGameType().getID());
                tag.setLong("banTime", System.currentTimeMillis());
                player.setGameType(GameType.SPECTATOR);
                player.fallDistance = 0.0F;
                player.addChatMessage(LimitedLives.banTime == 0 ? new TextComponentTranslation("limitedlives.spectateForcePerma") : new TextComponentTranslation("limitedlives.spectateForce", LimitedLives.banTime) );
            }
            else
            {
                UserListBansEntry userlistbansentry = new UserListBansEntry(player.getGameProfile(), null, Reference.NAME, LimitedLives.banTime == 0 ? null : new Date(System.currentTimeMillis() + (LimitedLives.banTime * 1000L)), I18n.translateToLocal("limitedlives.banReason"));
                minecraftserver.getPlayerList().getBannedPlayers().addEntry(userlistbansentry);
                player.connection.kickPlayerFromServer(I18n.translateToLocal("limitedlives.banKickReason"));
            }
        }
        else if(LimitedLives.healthAdjust == 1)
        {
            double nextHealth = Math.max(20 - (deaths / (double)LimitedLives.maxLives * 20D) + tag.getDouble("healthOffset"), 1D);
            event.player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(nextHealth);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.side.isServer() && event.phase == TickEvent.Phase.END && event.player.ticksExisted % 20 == 0)
        {
            EntityPlayerMP player = (EntityPlayerMP)event.player;
            NBTTagCompound tag = EntityHelper.getPlayerPersistentData(player, "LimitedLivesSave");
            int deaths = tag.getInteger("deathCount");
            if(deaths >= LimitedLives.maxLives && LimitedLives.banTime > 0 && player.isEntityAlive())
            {
                long banTime = tag.getLong("banTime");
                if((banTime + (LimitedLives.banTime * 1000L) - System.currentTimeMillis()) % FIVE_MINS_IN_MS > (banTime + 1000L + (LimitedLives.banTime * 1000L) - System.currentTimeMillis()) % FIVE_MINS_IN_MS && banTime + 1000L + (LimitedLives.banTime * 1000L) - System.currentTimeMillis() > FIVE_MINS_IN_MS && player.interactionManager.getGameType() == GameType.SPECTATOR)
                {
                    player.addChatMessage(new TextComponentTranslation("limitedlives.respawnTimeLeft", (int)Math.ceil((banTime + (LimitedLives.banTime * 1000L) - System.currentTimeMillis()) / (float)FIVE_MINS_IN_MS * 5F)));
                }
                if((new Date(banTime + (LimitedLives.banTime * 1000L))).before(new Date()) || player.interactionManager.getGameType() != GameType.SPECTATOR) //later is to say, player was pardoned by an op.
                {
                    //time to "unban"
                    boolean respawn = false;
                    if(player.interactionManager.getGameType() == GameType.SPECTATOR)
                    {
                        respawn = true;
                        if(LimitedLives.healthAdjust == 1)
                        {
                            player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20 + tag.getDouble("healthOffset"));
                        }
                        player.interactionManager.setGameType(GameType.getByID(tag.getInteger("gameMode")));
                    }
                    tag.removeTag("deathCount");
                    tag.removeTag("maxLives");
                    tag.removeTag("gameMode");
                    tag.removeTag("banTime");
                    if(respawn)
                    {
                        player.connection.playerEntity = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().recreatePlayerEntity(player, player.dimension, false);
                        if(LimitedLives.healthAdjust == 1)
                        {
                            player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20 + tag.getDouble("healthOffset"));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onChatEvent(ServerChatEvent event)
    {
        if(event.getMessage().toLowerCase().toLowerCase().startsWith("!ll") || event.getMessage().toLowerCase().toLowerCase().startsWith("!limitedlives"))
        {
            NBTTagCompound tag = EntityHelper.getPlayerPersistentData(event.getPlayer(), "LimitedLivesSave");
            int deaths = tag.getInteger("deathCount");
            if(deaths >= LimitedLives.maxLives && LimitedLives.banTime > 0)
            {
                int time = (int)Math.ceil((tag.getLong("banTime") + 1000L + (LimitedLives.banTime * 1000L) - System.currentTimeMillis()) / (float)(FIVE_MINS_IN_MS / 5F));
                event.getPlayer().addChatMessage(new TextComponentTranslation(time == 1 ? "limitedlives.respawnTimeLeftSingle" : "limitedlives.respawnTimeLeft", time));
            }
            else
            {
                event.getPlayer().addChatMessage(new TextComponentTranslation(LimitedLives.maxLives - deaths == 1 ? "limitedlives.livesLeftSingle" : "limitedlives.livesLeft", LimitedLives.maxLives - deaths));
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if(event.getModID().equals(Reference.NAME))
        {
            LimitedLives.syncConfig();
        }
    }

    public static final int FIVE_MINS_IN_MS = 5 * 60 * 1000;
}
