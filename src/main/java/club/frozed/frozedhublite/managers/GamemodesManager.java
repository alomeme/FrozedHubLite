package club.frozed.frozedhublite.managers;

import club.frozed.frozedhublite.FrozedHubLite;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class GamemodesManager {

    private final List<Gamemodes> gamemodesList;
    private final FileConfiguration plugin = FrozedHubLite.getInstance().getConfig();

    public Gamemodes getGamemodeByName(String name) {
        return this.gamemodesList.stream().filter(gamemodes -> gamemodes.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public GamemodesManager() {
        this.gamemodesList = new ArrayList<>();
    }

    public void loadServers() {
        for (String name : FrozedHubLite.getInstance().getConfig().getConfigurationSection("GAMEMODES").getKeys(false)) {
            String item = plugin.getString("GAMEMODES." + name + ".ITEM");
            int slot = plugin.getInt("GAMEMODES." + name + ".SLOT");
            String serverName = plugin.getString("GAMEMODES." + name + ".SERVER-NAME");
            List<String> lore = plugin.getStringList("GAMEMODES." + name + ".LORE");

            Gamemodes gamemode = new Gamemodes(name, item, slot, serverName, lore);

            gamemodesList.add(gamemode);
        }
    }

    public List<Gamemodes> getGamemodes() {
        return gamemodesList;
    }

    public Gamemodes getServerName(String name) {
        for (Gamemodes gamemodes : FrozedHubLite.getInstance().getGamemodesManager().getGamemodes()) {
            if (gamemodes.getName().equals(name)) {
                return gamemodes;
            }
        }
        return null;
    }
}
