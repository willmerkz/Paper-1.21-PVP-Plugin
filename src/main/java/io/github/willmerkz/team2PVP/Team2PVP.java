package io.github.willmerkz.team2PVP;

import io.github.willmerkz.team2PVP.commands.TournamentCommand;
import io.github.willmerkz.team2PVP.listener.KillListener;
import io.github.willmerkz.team2PVP.tournament.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Team2PVP extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        new Tournament();

        Bukkit.getPluginManager().registerEvents(new KillListener(), this);

        getCommand("tournament").setExecutor(new TournamentCommand(getConfig().getString("tournament-start-permission")));
    }

    @Override
    public void onDisable() {

    }
}
