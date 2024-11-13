package io.github.willmerkz.team2PVP.commands;

import io.github.willmerkz.team2PVP.Team2PVP;
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
// pairing logic
public class TournamentCommand implements CommandExecutor, TabCompleter {
    private final static String locationString = Team2PVP.instance.getConfig().getString("spawn");
    public final static Location spawn = new Location(
            Bukkit.getWorld(Team2PVP.instance.getConfig().getString("world")),
            Double.parseDouble(locationString.split(";")[0]),
            Double.parseDouble(locationString.split(";")[1]),
            Double.parseDouble(locationString.split(";")[2]),
            Float.parseFloat(locationString.split(";")[3]),
            Float.parseFloat(locationString.split(";")[4])
    );
    private final String startPermission, stopPermission, joinPermission, tournamentHasntStarted, alreadyStarted, joined, started, notInGameMessage, cantStart;
// tournament permissions and states
    public TournamentCommand(String startPermission, String stopPermission, String joinPermission, String tournamentHasntStarted, String alreadyStarted, String joined, String started, String notInGameMessage, String cantStart) {
        this.startPermission = startPermission;
        this.stopPermission = stopPermission;
        this.joinPermission = joinPermission;
        this.tournamentHasntStarted = tournamentHasntStarted;
        this.alreadyStarted = alreadyStarted;
        this.joined = joined;
        this.started = started;
        this.notInGameMessage = notInGameMessage;
        this.cantStart = cantStart;
    }
// allow player to join tournament, assuming they have the perm. if no tournament, notify them
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player player)) return true;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("join")) {
                if (!player.hasPermission(joinPermission)) {
                    return true;
                }
                if (Tournament.instance.getGameState() == GameState.DISABLED) {
                    player.sendMessage(
                            ChatUtil.color(
                                    tournamentHasntStarted
                            )
                    );
                    return true;
                }
// if tournament has already started...
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
// stops the tourney
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
// starts the tourney
            if (args[0].equalsIgnoreCase("start")) {
                if (!player.hasPermission(startPermission)) return true;

                if (Tournament.instance.getGameState() == GameState.WAITING) {
                    if (Tournament.instance.getPlayers().size() <= 1) {
                        player.sendMessage(ChatUtil.color(cantStart));
                        return true;
                    }

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
// leave the tournament
            if (args[0].equalsIgnoreCase("leave")) {
                if (!Tournament.instance.contains(player)) {
                    player.sendMessage(ChatUtil.color(notInGameMessage));
                    return true;
                }
// when player dies, return to lobby
                if (Tournament.instance.getGameState() == GameState.PLAYING) {
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
                    return true;
                }

                Tournament.instance.removePlayer(player);
                Tournament.instance.removePlayer(player);
                return true;
            }
            return true;
        }

        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!commandSender.hasPermission("tournament.admin")) {
            return List.of("join", "leave");
        }
        return List.of("join", "start", "stop", "leave");
    }
}
