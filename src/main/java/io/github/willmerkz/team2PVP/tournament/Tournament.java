package io.github.willmerkz.team2PVP.tournament;

import io.github.willmerkz.team2PVP.pair.Pair;
import io.github.willmerkz.team2PVP.tournament.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Tournament {
    private final List<Location> locations;
    public static Tournament instance;

    private GameState gameState;
    private final List<@NotNull Player> players;
    private final List<Pair<Player, Player>> pairs;

    public Tournament() {
        instance = this;
        gameState = GameState.DISABLED;
        players = new ArrayList<>();
        pairs = new ArrayList<>();
        locations = new ArrayList<>();

        World world = Bukkit.getWorld("world");

        locations.add(
                new Location(
                        world,
                        37.50,
                        -41,
                        6.50,
                        -45,
                        0
                )
        );

        locations.add(
                new Location(
                        world,
                        53.50,
                        -41,
                        22.50,
                        135,
                        0
                )
        );
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public void killPlayer(Player player) {
        Pair<Player, Player> pair = getPair(player);

        if (pair == null) return;

        pairs.remove(pair);

        pair = removeFromPair(pair, player);

        pairs.add(pair);

        if (getLonelyPairs() == pairs.size()) {
            start();
        }
    }

    public void start() {
        if (pairs.size() == 1) {
            Player winner = getNonNull(pairs.get(0));

            Bukkit.broadcastMessage("The winner is: " + winner.getName());
            return;
        }

        if (!pairs.isEmpty()) {
            players.clear();
            for (Pair<Player, Player> pair : pairs) {
                players.add(getNonNull(pair));
            }
        }

        pairs.clear();
        setGameState(GameState.PLAYING);

        if (players.size() % 2 != 0) {
            removePlayer(players.get(players.size() - 1));
        }

        for (int i = 0; i < players.size(); i+=2) {
            Player player1 = players.get(i);
            Player player2 = players.get(i+1);

            startDuel(player1, player2, i, i+1);
        }
    }

    public void startDuel(Player player1, Player player2, int locationId1, int locationId2) {
        pairs.add(new Pair<>(player1, player2));

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

        player1.getInventory().clear();
        player2.getInventory().clear();

        Random random = new Random();

        int randomArmorNumber = random.nextInt(armorList.size());
        int randomSwordNumber = random.nextInt(swordList.size());

        String listResult = armorList.get(randomArmorNumber);

        player1.getInventory().setHelmet(new ItemStack(Material.getMaterial(listResult + "_HELMET")));
        player1.getInventory().setChestplate(new ItemStack(Material.getMaterial(listResult + "_CHESTPLATE")));
        player1.getInventory().setLeggings(new ItemStack(Material.getMaterial(listResult + "_LEGGINGS")));
        player1.getInventory().setBoots(new ItemStack(Material.getMaterial(listResult + "_BOOTS")));

        player2.getInventory().setHelmet(new ItemStack(Material.getMaterial(listResult + "_HELMET")));
        player2.getInventory().setChestplate(new ItemStack(Material.getMaterial(listResult + "_CHESTPLATE")));
        player2.getInventory().setLeggings(new ItemStack(Material.getMaterial(listResult + "_LEGGINGS")));
        player2.getInventory().setBoots(new ItemStack(Material.getMaterial(listResult + "_BOOTS")));

        player1.getInventory().addItem(ItemStack.of(swordList.get(randomSwordNumber)));
        player2.getInventory().addItem(ItemStack.of(swordList.get(randomSwordNumber)));

        Location location1 = locations.get(locationId1);
        Location location2 = locations.get(locationId2);

        player1.teleport(location1);
        player2.teleport(location2);
    }

    public Player getNonNull(Pair<Player, Player> pair) {
        if (pair.getFirst() != null) {
            return pair.getFirst();
        }

        return pair.getSecond();
    }

    public int getLonelyPairs() {
        int count = 0;
        for (Pair<Player, Player> pair : pairs) {
            if (pair.getFirst() == null || pair.getSecond() == null) count++;
        }
        return count;
    }

    public Pair<Player, Player> getPair(Player player) {
        for (Pair<Player, Player> pair : pairs) {
            if (pair.getFirst().equals(player) || pair.getSecond().equals(player)) {
                return pair;
            }
        }
        return null;
    }

    public Pair<Player, Player> removeFromPair(Pair<Player, Player> pair, Player player) {
        if (pair == null) return null;

        if (pair.getFirst().equals(player)) {
            pair.setFirst(null);
        }

        if (pair.getSecond().equals(player)) {
            pair.setSecond(null);
        }

        return pair;
    }

    public boolean contains(Player player) {
        return players.contains(player);
    }

}
