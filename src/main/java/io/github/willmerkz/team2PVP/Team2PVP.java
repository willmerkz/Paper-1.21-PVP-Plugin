package io.github.willmerkz.team2PVP;

import io.github.willmerkz.team2PVP.commands.TournamentCommand;
import io.github.willmerkz.team2PVP.listener.GeneralListener;
import io.github.willmerkz.team2PVP.listener.KillListener;
import io.github.willmerkz.team2PVP.tournament.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Team2PVP extends JavaPlugin {
    public static Team2PVP instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        new Tournament();

        Bukkit.getPluginManager().registerEvents(new GeneralListener(), this);
        Bukkit.getPluginManager().registerEvents(new KillListener(), this);
// refer to config for permissions
        TournamentCommand tournamentCMD = new TournamentCommand(
                getConfig().getString("tournament-start-permission"),
                getConfig().getString("tournament-stop-permission"),
                getConfig().getString("tournament-join-permission"),
                getConfig().getString("messages.tournament-hasnt-started"),
                getConfig().getString("messages.already-started"),
                getConfig().getString("messages.joined"),
                getConfig().getString("messages.started"),
                getConfig().getString("messages.notInGame"),
                getConfig().getString("messages.cantStart")
        );

        getCommand("tournament").setExecutor(tournamentCMD);
        getCommand("tournament").setTabCompleter(tournamentCMD);
    }

    @Override
    public void onDisable() {

    }
}
