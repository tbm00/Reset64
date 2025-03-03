package dev.tbm00.spigot.reset64.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dev.tbm00.spigot.reset64.Reset64;
import dev.tbm00.spigot.reset64.process.ResetProcess;

public class PlayerConnection implements Listener {
    private final Reset64 javaPlugin;

    public PlayerConnection(Reset64 javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    /**
     * Handles the player join event.
     * Triggers reset process if applicable.
     * (not applicable if already checked, reset, or newbie)
     *
     * @param event the PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new ResetProcess(javaPlugin, javaPlugin.getServer().getConsoleSender(), event.getPlayer());
    }
}