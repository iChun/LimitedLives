package me.ichun.mods.limitedlives.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.ichun.mods.ichunutil.common.entity.EntityHelper;
import me.ichun.mods.limitedlives.common.LimitedLives;
import me.ichun.mods.limitedlives.common.core.EventHandlerServer;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class CommandLimitedLives
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralCommandNode<CommandSourceStack> command =
            dispatcher.register(Commands.literal("ll")
                .executes((source) -> {
                    informLivesLeft(source);
                    return 0;
                })
                .then(Commands.literal("set").requires((p) -> p.hasPermission(2))
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("deaths", IntegerArgumentType.integer(0))
                            .executes((source) -> {
                                ServerPlayer player = EntityArgument.getPlayer(source, "player");
                                int deaths = IntegerArgumentType.getInteger(source, "deaths");

                                LimitedLives.eventHandlerServer.setPlayerDeaths(player, deaths, false);

                                sendCommandOutput(source, player, new TranslatableComponent("limitedlives.setDeaths", player.getName().getString(), deaths));

                                return 1;
                            })
                        )
                    )
                )
                .then(Commands.literal("add").requires((p) -> p.hasPermission(2))
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("deaths", IntegerArgumentType.integer())
                            .executes((source) -> {
                                ServerPlayer player = EntityArgument.getPlayer(source, "player");
                                int deathsArg = IntegerArgumentType.getInteger(source, "deaths");

                                CompoundTag tag = EntityHelper.getPlayerPersistentData(player, EventHandlerServer.LL_PERSISTED_TAG);
                                int deaths = tag.getInt("deathCount");

                                LimitedLives.eventHandlerServer.setPlayerDeaths(player, deaths + deathsArg, false);

                                sendCommandOutput(source, player, new TranslatableComponent("limitedlives.addDeaths", deathsArg, player.getName().getString(), deaths + deathsArg));

                                return 1;
                            })
                        )
                    )
                )
                .then(Commands.literal("pardon").requires((p) -> p.hasPermission(2))
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes((source) -> {
                            ServerPlayer player = EntityArgument.getPlayer(source, "player");

                            CompoundTag tag = EntityHelper.getPlayerPersistentData(player, EventHandlerServer.LL_PERSISTED_TAG);
                            int deaths = tag.getInt("deathCount");
                            if(deaths >= LimitedLives.config.maxLives && LimitedLives.config.banDuration > 0 && player.isAlive()) //is "banned, config has ban duration > 0 (not permaban), player is alive
                            {
                                sendCommandOutput(source, player, new TranslatableComponent("limitedlives.pardoned", player.getName().getString()));

                                LimitedLives.eventHandlerServer.pardon(player, tag);
                            }
                            else
                            {
                                sendCommandOutput(source, player, new TranslatableComponent("limitedlives.notBanned", player.getName().getString()));
                            }

                            return 1;
                        })
                    )
                )
            );

        //register alias.
        dispatcher.register(Commands.literal("limitedlives")
            .redirect(command));
    }

    private static void sendCommandOutput(CommandContext<CommandSourceStack> source, ServerPlayer player, MutableComponent outcomeText)
    {
        source.getSource().sendSuccess(outcomeText, true);

        if(player != source.getSource().getEntity())
        {
            player.sendMessage(outcomeText, Util.NIL_UUID);
        }
    }


    private static void informLivesLeft(CommandContext<CommandSourceStack> source)
    {
        if(source.getSource().getEntity() instanceof ServerPlayer player)
        {
            CompoundTag tag = EntityHelper.getPlayerPersistentData(player, EventHandlerServer.LL_PERSISTED_TAG);
            int deaths = tag.getInt("deathCount");
            if(deaths >= LimitedLives.config.maxLives && LimitedLives.config.banDuration > 0)
            {
                long timeBanned = tag.getLong("timeBanned");
                long banDurationMs = (LimitedLives.config.banDuration * 1000L);
                long timeBanDone = System.currentTimeMillis() - timeBanned; // in MS
                long timeBanLeft = banDurationMs - timeBanDone;

                source.getSource().sendSuccess(new TranslatableComponent("limitedlives.respawnTimeLeft", (int)Math.ceil(timeBanLeft / 60000F)), false);
            }
            else
            {
                source.getSource().sendSuccess(new TranslatableComponent("limitedlives.livesLeft", LimitedLives.config.maxLives - deaths), false);
            }
        }
    }
}
