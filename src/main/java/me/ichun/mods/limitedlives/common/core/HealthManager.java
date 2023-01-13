package me.ichun.mods.limitedlives.common.core;

import me.ichun.mods.limitedlives.common.LimitedLives;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class HealthManager {

    private static final int MAX_DEFAULT_HEALTH = 20;

    /**
     * Updates the player's max health based on the number of deaths they have.
     * @param player player we are updating the health of
     * @param deaths number of deaths the player has
     */
    public static void updatePlayerHealth(ServerPlayer player, int deaths) {
        int newHealth = calculateNewHealth(deaths);
        AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        
        if (healthAttribute != null) {
            healthAttribute.setBaseValue(Math.max(newHealth, 1D));
        }

        if (player.getHealth() > newHealth) {
            player.setHealth(newHealth);
        }
    }

    private static int calculateNewHealth(int deaths) {
        int maxLives = LimitedLives.config.maxLives.get();
        int totalPlayerLivesLeft = maxLives - deaths;

        double healthPerLife = (double) MAX_DEFAULT_HEALTH / (double) maxLives;

        return (int) (totalPlayerLivesLeft * healthPerLife);
    }

    private HealthManager() {
    }

}
