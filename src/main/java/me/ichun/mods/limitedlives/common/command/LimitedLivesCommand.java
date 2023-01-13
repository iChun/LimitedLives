package me.ichun.mods.limitedlives.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.ichun.mods.limitedlives.common.LimitedLives;
import me.ichun.mods.limitedlives.common.core.EntityHelper;
import me.ichun.mods.limitedlives.common.core.HealthManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.command.TextComponentHelper;

import static me.ichun.mods.limitedlives.common.core.EventHandler.FIVE_MINS_IN_MS;

public class LimitedLivesCommand
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

                                CompoundTag tag = EntityHelper.getPlayerPersistentData(player, "LimitedLivesSave");
                                tag.putInt("deathCount", deaths);
                                HealthManager.updatePlayerHealth(player, deaths);

                                source.getSource().sendSuccess(TextComponentHelper.createComponentTranslation(source.getSource().getEntity(), "limitedlives.setDeaths", player.getName().getContents(), deaths), true);

                                return deaths;
                            }))))
            );

        //register alias.
        dispatcher.register(Commands.literal("limitedlives")
            .executes((source) -> {
                informLivesLeft(source);
                return 0;
            })
            .redirect(command));
    }

    private static void informLivesLeft(CommandContext<CommandSourceStack> source)
    {
        if(source.getSource().getEntity() instanceof ServerPlayer)
        {
            ServerPlayer player = (ServerPlayer)source.getSource().getEntity();
            CompoundTag tag = EntityHelper.getPlayerPersistentData(player, "LimitedLivesSave");
            int deaths = tag.getInt("deathCount");
            if(deaths >= LimitedLives.config.maxLives.get() && LimitedLives.config.banTime.get() > 0)
            {
                int time = (int)Math.ceil((tag.getLong("banTime") + 1000L + (LimitedLives.config.banTime.get() * 1000L) - System.currentTimeMillis()) / (FIVE_MINS_IN_MS / 5F));
                source.getSource().sendSuccess(TextComponentHelper.createComponentTranslation(player, time == 1 ? "limitedlives.respawnTimeLeftSingle" : "limitedlives.respawnTimeLeft", time), false);
            }
            else
            {
                source.getSource().sendSuccess(TextComponentHelper.createComponentTranslation(player, (LimitedLives.config.maxLives.get() - deaths) == 1 ? "limitedlives.livesLeftSingle" : "limitedlives.livesLeft", LimitedLives.config.maxLives.get() - deaths), false);
            }
        }
    }
}
