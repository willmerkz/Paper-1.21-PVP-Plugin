package io.github.willmerkz.team2PVP.commands;

import io.github.willmerkz.team2PVP.tournament.Tournament;
import io.github.willmerkz.team2PVP.tournament.state.GameState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TournamentCommand implements CommandExecutor {
    private final String startPermission;

    public TournamentCommand(String startPermission) {
        this.startPermission = startPermission;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player player)) return true;

        if (args.length == 1 && args[0].equalsIgnoreCase("join")) {
            if (Tournament.instance.getGameState() != GameState.WAITING) {
                player.sendMessage("The tournament has already started!");
                return true;
            }

            Tournament.instance.addPlayer(player);
            player.sendMessage("You have successfully joined the tournament!");
            return true;
        }

        if (!player.hasPermission(startPermission)) return true;

        if (Tournament.instance.getGameState() == GameState.WAITING) {
            Tournament.instance.start();
            return true;
        }

        Tournament.instance.setGameState(GameState.WAITING);
        player.sendMessage("Tournament has started!");

        return true;
    }

}
