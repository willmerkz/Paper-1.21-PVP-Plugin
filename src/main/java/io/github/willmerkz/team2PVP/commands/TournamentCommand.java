package io.github.willmerkz.team2PVP.commands;

import io.github.willmerkz.team2PVP.tournament.Tournament;
import io.github.willmerkz.team2PVP.tournament.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TournamentCommand implements CommandExecutor, TabCompleter {
    private final String startPermission, stopPermission;

    public TournamentCommand(String startPermission, String stopPermission) {
        this.startPermission = startPermission;
        this.stopPermission = stopPermission;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player player)) return true;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("join")) {
                if (Tournament.instance.getGameState() == GameState.DISABLED) {
                    player.sendMessage("The tournament hasn't started yet!");
                    return true;
                }

                if (Tournament.instance.getGameState() != GameState.WAITING) {
                    player.sendMessage("The tournament has already started!");
                    return true;
                }

                if (Tournament.instance.contains(player)) return true;

                Tournament.instance.addPlayer(player);
                player.sendMessage("You have successfully joined the tournament!");
                return true;
            }

            if (args[0].equalsIgnoreCase("stop")) {
                if (!player.hasPermission(stopPermission)) return true;

                Tournament.instance.getPlayers().forEach(target -> {
                    target.teleport(
                            new Location(
                                    Bukkit.getWorld("world"),
                                    119.50,
                                    -41,
                                    -27.50,
                                    90,
                                    0
                            )
                    );
                    target.getInventory().clear();
                });

                new Tournament();
                return true;
            }

            if (args[0].equalsIgnoreCase("start")) {
                if (!player.hasPermission(startPermission)) return true;

                if (Tournament.instance.getGameState() == GameState.WAITING) {
                    Tournament.instance.start();
                    return true;
                }

                Tournament.instance.setGameState(GameState.WAITING);
                player.sendMessage("Tournament has started!");
                return true;
            }
            return true;
        }

        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of("join", "start", "stop");
    }
}
