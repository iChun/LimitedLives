package me.ichun.mods.limitedlives.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.ichun.mods.limitedlives.common.LimitedLives;
import me.ichun.mods.limitedlives.common.core.EventHandlerServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

import java.util.Date;

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

                                sendCommandOutput(source, player, Component.translatable("limitedlives.setDeaths", player.getName().getString(), deaths));

                                return 1;
                            })
                        )
                    )
                )
                .then(Commands.literal("pardon").requires((p) -> p.hasPermission(2))
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes((source) -> {
                            ServerPlayer player = EntityArgument.getPlayer(source, "player");

                            CompoundTag tag = LimitedLives.eventHandlerServer.getPlayerPersistentData(player, EventHandlerServer.LL_PERSISTED_TAG);
                            int deaths = tag.getInt("deathCount");
                            if(deaths >= LimitedLives.config.maxLives.get() && LimitedLives.config.banDuration.get() > 0 && player.isAlive()) //is "banned, config has ban > 0 (not permaban), player is alive
                            {
                                sendCommandOutput(source, player, Component.translatable("limitedlives.pardoned", player.getName().getString()));

                                LimitedLives.eventHandlerServer.pardon(player, tag);
                            }
                            else
                            {
                                sendCommandOutput(source, player, Component.translatable("limitedlives.notBanned", player.getName().getString()));
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
        source.getSource().sendSuccess(() -> outcomeText, true);

        if(player != source.getSource().getPlayer())
        {
            player.sendSystemMessage(outcomeText);
        }
    }


    private static void informLivesLeft(CommandContext<CommandSourceStack> source)
    {
        if(source.getSource().getEntity() instanceof ServerPlayer player)
        {
            CompoundTag tag = LimitedLives.eventHandlerServer.getPlayerPersistentData(player, EventHandlerServer.LL_PERSISTED_TAG);
            int deaths = tag.getInt("deathCount");
            if(deaths >= LimitedLives.config.maxLives.get() && LimitedLives.config.banDuration.get() > 0)
            {
                long timeBanned = tag.getLong("timeBanned");
                long banDurationMs = (LimitedLives.config.banDuration.get() * 1000L);
                long timeBanDone = System.currentTimeMillis() - timeBanned; // in MS
                long timeBanLeft = banDurationMs - timeBanDone;

                source.getSource().sendSuccess(() -> Component.translatable("limitedlives.respawnTimeLeft", (int)Math.ceil(timeBanLeft / 60000F)), false);
            }
            else
            {
                source.getSource().sendSuccess(() -> Component.translatable("limitedlives.livesLeft", LimitedLives.config.maxLives.get() - deaths), false);
            }
        }
    }
}
