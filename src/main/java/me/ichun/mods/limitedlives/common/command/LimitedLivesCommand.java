package me.ichun.mods.limitedlives.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.ichun.mods.limitedlives.common.LimitedLives;
import me.ichun.mods.limitedlives.common.core.EntityHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.server.command.TextComponentHelper;

import static me.ichun.mods.limitedlives.common.core.EventHandler.FIVE_MINS_IN_MS;

public class LimitedLivesCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        LiteralCommandNode<CommandSource> command =
            dispatcher.register(Commands.literal("ll")
                .executes((source) -> {
                    informLivesLeft(source);
                    return 0;
                })
                .then(Commands.literal("set")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("deaths", IntegerArgumentType.integer(0))
                            .executes((source) -> {
                                ServerPlayerEntity player = EntityArgument.getPlayer(source, "targets");
                                int deaths = IntegerArgumentType.getInteger(source, "count");

                                CompoundNBT tag = EntityHelper.getPlayerPersistentData(player, "LimitedLivesSave");
                                tag.putInt("deathCount", deaths);

                                source.getSource().sendFeedback(TextComponentHelper.createComponentTranslation(source.getSource().getEntity(), "limitedlives.setDeaths", player.getName().getUnformattedComponentText(), deaths), true);

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

    private static void informLivesLeft(CommandContext<CommandSource> source)
    {
        if(source.getSource().getEntity() instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity player = (ServerPlayerEntity)source.getSource().getEntity();
            CompoundNBT tag = EntityHelper.getPlayerPersistentData(player, "LimitedLivesSave");
            int deaths = tag.getInt("deathCount");
            if(deaths >= LimitedLives.config.maxLives.get() && LimitedLives.config.banTime.get() > 0)
            {
                int time = (int)Math.ceil((tag.getLong("banTime") + 1000L + (LimitedLives.config.banTime.get() * 1000L) - System.currentTimeMillis()) / (FIVE_MINS_IN_MS / 5F));
                source.getSource().sendFeedback(TextComponentHelper.createComponentTranslation(player, time == 1 ? "limitedlives.respawnTimeLeftSingle" : "limitedlives.respawnTimeLeft", time), false);
            }
            else
            {
                source.getSource().sendFeedback(TextComponentHelper.createComponentTranslation(player, (LimitedLives.config.maxLives.get() - deaths) == 1 ? "limitedlives.livesLeftSingle" : "limitedlives.livesLeft", LimitedLives.config.maxLives.get() - deaths), false);
            }
        }
    }
}
