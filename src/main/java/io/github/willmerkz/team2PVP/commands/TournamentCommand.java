package io.github.willmerkz.team2PVP.commands;

import io.github.willmerkz.team2PVP.listener.GeneralListener;
import io.github.willmerkz.team2PVP.tournament.Tournament;
import io.github.willmerkz.team2PVP.tournament.state.GameState;
import io.github.willmerkz.team2PVP.utils.ChatUtil;
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
    private final String startPermission, stopPermission, tournamentHasntStarted, alreadyStarted, joined, started;
// tournament permissions
    public TournamentCommand(String startPermission, String stopPermission, String tournamentHasntStarted, String alreadyStarted, String joined, String started) {
        this.startPermission = startPermission;
        this.stopPermission = stopPermission;
        this.tournamentHasntStarted = tournamentHasntStarted;
        this.alreadyStarted = alreadyStarted;
        this.joined = joined;
        this.started = started;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player player)) return true;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("join")) {
                if (Tournament.instance.getGameState() == GameState.DISABLED) {
                    player.sendMessage(
                            ChatUtil.color(
                                    tournamentHasntStarted
                            )
                    );
                    return true;
                }

                if (Tournament.instance.getGameState() != GameState.WAITING) {
                    player.sendMessage(
                            ChatUtil.color(
                                    alreadyStarted
                            )
                    );
                    return true;
                }

                if (Tournament.instance.contains(player)) return true;

                Tournament.instance.addPlayer(player);
                player.sendMessage(
                        ChatUtil.color(
                                joined
                        )
                );
                return true;
            }

            if (args[0].equalsIgnoreCase("stop")) {
                if (!player.hasPermission(stopPermission)) return true;

                Tournament.instance.getPlayers().forEach(target -> {
                    target.teleportAsync(
                            GeneralListener.spawn
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
                player.sendMessage(
                        ChatUtil.color(
                                started
                        )
                );
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
