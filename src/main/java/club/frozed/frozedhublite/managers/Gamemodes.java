package club.frozed.frozedhublite.managers;

import lombok.Getter;

import java.util.List;

@Getter
public class Gamemodes {

    private final String name;
    private final String item;
    private final int slot;
    private final String serverName;
    private final List<String> lore;

    public Gamemodes(String name, String item, int slot, String serverName, List<String> lore) {
        this.name = name;
        this.item = item;
        this.slot = slot;
        this.serverName = serverName;
        this.lore = lore;
    }
}
