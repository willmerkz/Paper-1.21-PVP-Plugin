package io.github.willmerkz.team2PVP.tournament;

import io.github.willmerkz.team2PVP.Team2PVP;
import io.github.willmerkz.team2PVP.pair.Pair;
import io.github.willmerkz.team2PVP.tournament.state.GameState;
import io.github.willmerkz.team2PVP.utils.ChatUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Tournament {
    private final List<Location> locations;
    public static Tournament instance;

    private GameState gameState;
    private final List<@NotNull Player> players;
    private final List<Pair<Player>> pairs;
    private final List<Player> alivePlayers;
    private int currentRound = 0;
    private boolean isEveningDuelRunning = false;

    public Tournament() {
        instance = this;
        gameState = GameState.DISABLED;
        players = new ArrayList<>();
        pairs = new ArrayList<>();
        locations = new ArrayList<>();
        alivePlayers = new ArrayList<>();

        World world = Bukkit.getWorld(Team2PVP.instance.getConfig().getString("world"));

        //define locations in pairs. each pair represents a 1v1 map. new maps can be added by adding new pairs.
        locations.addAll(
                Team2PVP.instance.getConfig().getStringList("locations").stream().map(str -> {
                    String[] split = str.split(";");

                    return new Location(world, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Float.parseFloat(split[3]), Float.parseFloat(split[4]));
                }).toList()
        );


        List<List<Location>> pairs = new ArrayList<>();

        for (int i = 0; i < locations.size(); i+=2) {
            List<Location> pair = new ArrayList<>();
            pair.add(locations.get(i));
            pair.add(locations.get(i+1));
            pairs.add(pair);
        }

        Collections.shuffle(pairs);

        locations.clear();

        for (List<Location> pair : pairs) {
            locations.addAll(pair);
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
// add players
    public void addPlayer(Player player) {
        this.players.add(player);
        this.alivePlayers.add(player);
    }
//remove player from map
    public void removePlayer(Player player) {
        this.alivePlayers.remove(player);
        this.players.remove(player);
    }

    public void killPlayer(Player player) {
        alivePlayers.remove(player);
        Pair<Player> pair = getPair(player);

        if (pair == null) return;

        pairs.remove(pair);

        pair = removeFromPair(pair, player);

        pairs.add(pair);
// announce player death
        Bukkit.broadcast(
                ChatUtil.color(
                        Team2PVP.instance.getConfig().getString("messages.broadcast-kill")
                                .replace("%player%", player.getName())
                )
        );

        if (isEveningDuelRunning || getLonelyPairs() == pairs.size()) {
            isEveningDuelRunning = false;
            start();
        }
    }

    public void start() {
        if (alivePlayers.isEmpty()) {
            Bukkit.getServer().shutdown();
            return;
        }
        if (alivePlayers.size() == 1) {
            Player winner = alivePlayers.getFirst();
            currentRound = 0;

            Bukkit.broadcast(
                    ChatUtil.color(
                            Team2PVP.instance.getConfig().getString("messages.winner")
                                    .replace("%winner%", winner.getName())
                    )
            );
            new Tournament();
            return;
        }

        if (!pairs.isEmpty()) {
            players.clear();
            for (Pair<Player> pair : pairs) {
                players.add(pair.getFirstNotNull());
            }

            Bukkit.broadcast(
                    ChatUtil.color(
                            Team2PVP.instance.getConfig().getString("messages.rounding")
                                    .replace("%round%", String.valueOf(currentRound))
                                    .replace("%winners%", players.stream().map(Player::getName).toList().toString())
                    )
            );
        }

        currentRound++;
// logic for odd number of players. hold an evening duel
        pairs.clear();
        setGameState(GameState.PLAYING);
        if (alivePlayers.size() > 1 && alivePlayers.size() % 2 != 0) {
            handleEveningDuel();
            return;
        }

        for (int i = 0; i < alivePlayers.size(); i+=2) {
            Player player1 = alivePlayers.get(i);
            Player player2 = alivePlayers.get(i+1);

            startDuel(player1, player2, i, i+1);
        }
    }
// matches and output
    public void startDuel(Player player1, Player player2, int locationId1, int locationId2) {
        Bukkit.broadcast(
                ChatUtil.color(
                        Team2PVP.instance.getConfig().getString("messages.vs")
                                .replace("%first%", player1.getName())
                                .replace("%second%", player2.getName())
                )
        );
        pairs.add(new Pair<>(player1, player2));
// teleport locations
        Location location1 = locations.get(locationId1);
        Location location2 = locations.get(locationId2);

        Bukkit.getScheduler().runTaskLater(Team2PVP.instance, () -> {
            player1.teleportAsync(location1);
            player2.teleportAsync(location2);
        }, 5);
// types of armors listed
        List<String> armorList = List.of(
                "DIAMOND",
                "NETHERITE",
                "IRON",
                "LEATHER",
                "CHAINMAIL",
                "GOLDEN"
        );

        List<Material> swordList = List.of(
                Material.DIAMOND_SWORD,
                Material.NETHERITE_SWORD,
                Material.IRON_SWORD
        );
// clear inventory
        player1.getInventory().clear();
        player2.getInventory().clear();

        Random random = new Random();

        int randomArmorNumber = random.nextInt(armorList.size());
        int randomSwordNumber = random.nextInt(swordList.size());

        String listResult = armorList.get(randomArmorNumber);

        // the goal with adventure mode is to prevent griefing, if these systems are already provided by the server
        // the following 2 lines can be omitted.
        player1.setGameMode(GameMode.ADVENTURE);
        player2.setGameMode(GameMode.ADVENTURE);

        // setting the random gear by fetching list result for each player. we do not change them a second time so that
        // matchups are even.
        player1.getInventory().setHelmet(new ItemStack(Material.getMaterial(listResult + "_HELMET")));
        player1.getInventory().setChestplate(new ItemStack(Material.getMaterial(listResult + "_CHESTPLATE")));
        player1.getInventory().setLeggings(new ItemStack(Material.getMaterial(listResult + "_LEGGINGS")));
        player1.getInventory().setBoots(new ItemStack(Material.getMaterial(listResult + "_BOOTS")));

        player2.getInventory().setHelmet(new ItemStack(Material.getMaterial(listResult + "_HELMET")));
        player2.getInventory().setChestplate(new ItemStack(Material.getMaterial(listResult + "_CHESTPLATE")));
        player2.getInventory().setLeggings(new ItemStack(Material.getMaterial(listResult + "_LEGGINGS")));
        player2.getInventory().setBoots(new ItemStack(Material.getMaterial(listResult + "_BOOTS")));

        //similar logic to armor, except for the players sword
        player1.getInventory().addItem(ItemStack.of(swordList.get(randomSwordNumber)));
        player2.getInventory().addItem(ItemStack.of(swordList.get(randomSwordNumber)));
    }

    public int getLonelyPairs() {
        int count = 0;
        for (Pair<Player> pair : pairs) {
            if (pair.getFirst() == null || pair.getSecond() == null) count++;
        }
        return count;
    }

    public Pair<Player> getPair(Player player) {
        for (Pair<Player> pair : pairs) {
            if (pair.getFirst().equals(player) || pair.getSecond().equals(player)) {
                return pair;
            }
        }
        return null;
    }

    public Pair<Player> removeFromPair(Pair<Player> pair, Player player) {
        if (pair == null) return null;

        if (pair.getFirst().equals(player)) {
            pair.setFirst(null);
        }

        if (pair.getSecond().equals(player)) {
            pair.setSecond(null);
        }

        return pair;
    }
// duel when odd number of players
    private void handleEveningDuel() {
        Player first = alivePlayers.getFirst();
        Player second = alivePlayers.get(1);

        Bukkit.broadcast(
                ChatUtil.color(
                        Team2PVP.instance.getConfig().getString("messages.broadcast-even-duel")
                )
        );

        isEveningDuelRunning = true;
        startDuel(first, second, 0, 1);
    }

    public boolean contains(Player player) {
        return alivePlayers.contains(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getOpponent(Player player) {
        Pair<Player> pair = getPair(player);

        if (pair == null) return null;

        if (pair.getFirst() == player) return pair.getSecond();

        return pair.getFirst();
    }

}
