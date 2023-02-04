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
                .then(Commands.literal("set")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("deaths", IntegerArgumentType.integer(0))
                            .executes((source) -> {
                                ServerPlayer player = EntityArgument.getPlayer(source, "player");
                                int deaths = IntegerArgumentType.getInteger(source, "deaths");

                                LimitedLives.eventHandlerServer.setPlayerDeaths(player, deaths, false);

                                source.getSource().sendSuccess(Component.translatable("limitedlives.setDeaths", player.getName().getString(), deaths), true);

                                return deaths;
                            })
                        )
                    )
                )
            );

        //register alias.
        dispatcher.register(Commands.literal("limitedlives")
            .redirect(command));
    }

    private static void informLivesLeft(CommandContext<CommandSourceStack> source)
    {
        if(source.getSource().getEntity() instanceof ServerPlayer player)
        {
            CompoundTag tag = LimitedLives.eventHandlerServer.getPlayerPersistentData(player, EventHandlerServer.LL_PERSISTED_TAG);
            int deaths = tag.getInt("deathCount");
            if(deaths >= LimitedLives.config.maxLives.get() && LimitedLives.config.banTime.get() > 0)
            {
                int time = (int)Math.ceil((tag.getLong("banTime") + 1000L + (LimitedLives.config.banTime.get() * 1000L) - System.currentTimeMillis()) / (LimitedLives.eventHandlerServer.FIVE_MINS_IN_MS / 5F));
                source.getSource().sendSuccess(Component.translatable("limitedlives.respawnTimeLeft", time), false);
            }
            else
            {
                source.getSource().sendSuccess(Component.translatable("limitedlives.livesLeft", LimitedLives.config.maxLives.get() - deaths), false);
            }
        }
    }
}
