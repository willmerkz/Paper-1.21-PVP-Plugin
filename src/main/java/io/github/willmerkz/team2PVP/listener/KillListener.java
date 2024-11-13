package io.github.willmerkz.team2PVP.listener;

import io.github.willmerkz.team2PVP.Team2PVP;
import io.github.willmerkz.team2PVP.tournament.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillListener implements Listener {
    private final static String locationString = Team2PVP.instance.getConfig().getString("spawn");
    public final static Location spawn = new Location(
            Bukkit.getWorld(Team2PVP.instance.getConfig().getString("world")),
            Double.parseDouble(locationString.split(";")[0]),
            Double.parseDouble(locationString.split(";")[1]),
            Double.parseDouble(locationString.split(";")[2]),
            Float.parseFloat(locationString.split(";")[3]),
            Float.parseFloat(locationString.split(";")[4])
    );

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getPlayer();

        if (!Tournament.instance.contains(player)) return;

        player.setHealth(player.getMaxHealth());
        Player damager = Tournament.instance.getOpponent(player);
        damager.setHealth(damager.getMaxHealth());

        Tournament.instance.killPlayer(player);
        player.teleportAsync(
                spawn
        );
        damager.teleportAsync(
                spawn
        );
    }

}
