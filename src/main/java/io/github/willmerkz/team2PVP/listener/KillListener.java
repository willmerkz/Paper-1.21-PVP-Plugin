package io.github.willmerkz.team2PVP.listener;

import io.github.willmerkz.team2PVP.tournament.Tournament;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getPlayer();

        if (!Tournament.instance.contains(player)) return;

        Tournament.instance.killPlayer(player);
    }

}
