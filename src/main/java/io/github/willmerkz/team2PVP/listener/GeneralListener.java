package io.github.willmerkz.team2PVP.listener;

import io.github.willmerkz.team2PVP.Team2PVP;
import io.github.willmerkz.team2PVP.tournament.Tournament;
import io.github.willmerkz.team2PVP.tournament.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

//detect when player joins, so that we can teleport them to the PVP lobby
public class GeneralListener implements Listener {
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
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        player.teleportAsync(
                spawn
        );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (!Tournament.instance.contains(player)) return;

        player.setHealth(player.getMaxHealth());
        Player damager = Tournament.instance.getOpponent(player);
        damager.setHealth(damager.getMaxHealth());

        Tournament.instance.killPlayer(player);
        damager.teleportAsync(
                spawn
        );
    }

    //detect when a player dies. instead of providing death screen, we will heal them, set them as the loser, and
    // return them to the lobby
    @EventHandler
    public void onPlayerDeath(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player player) || !(e.getDamager() instanceof Player damager)) return;

        if (!Tournament.instance.contains(player)) return;

        if (player.getHealth() - e.getFinalDamage() > 0) return;
        e.setCancelled(true);

        player.setHealth(player.getMaxHealth());
        damager.setHealth(damager.getMaxHealth());

        Tournament.instance.killPlayer(player);
        player.teleportAsync(
                spawn
        );
        damager.teleportAsync(
                spawn
        );
    }

    // set max hunger to avoid having to eat
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    // detect sign click as an alternative to join the tournament, instead of using a chat command
    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;

        Block block = e.getClickedBlock();

        if (!block.getType().name().contains("WALL_SIGN")) return;

        e.setCancelled(true);

        e.getPlayer().performCommand("tournament join");
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();

        if (!player.hasPermission("tournament.admin") && Tournament.instance.getGameState() == GameState.PLAYING) {
            e.setCancelled(true);
        }
    }

}