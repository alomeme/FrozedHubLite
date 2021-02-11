package club.frozed.frozedhublite.provider;

import club.frozed.frozedhublite.FrozedHubLite;
import club.frozed.frozedhublite.managers.Gamemodes;
import club.frozed.frozedhublite.utils.CC;
import club.frozed.frozedhublite.utils.ItemBuilder;
import club.frozed.frozedhublite.utils.menu.type.ChestMenu;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class GamemodeSelectorProvider extends ChestMenu {

	private final FrozedHubLite plugin = FrozedHubLite.getInstance();
	@Getter private final Player player;
	private BukkitTask runnable;

	public GamemodeSelectorProvider(Player player) {
		super(CC.translate(
				FrozedHubLite.getInstance().getConfig().getString("SETTINGS.SELECTOR-TITLE")),
				FrozedHubLite.getInstance().getConfig().getInt("SETTINGS.SELECTOR-SIZE")
		);

		this.player = player;
		update();
	}

	private void update() {
		this.runnable = new BukkitRunnable() {
			@Override
			public void run() {
				for (Gamemodes gamemode : FrozedHubLite.getInstance().getGamemodesManager().getGamemodes()) {
					getInventory().setItem(gamemode.getSlot(), new ItemBuilder(Material.valueOf(gamemode.getItem()))
							.name(plugin.getConfig().getString("SETTINGS.GAMEMODE-NAME-COLOR") + gamemode.getName())
							.lore(PlaceholderAPI.setPlaceholders(player, gamemode.getLore()))
							.build());
				}
				getInventory().setItem(plugin.getConfig().getInt("LEAVE-NETWORK.SLOT"), new ItemBuilder(
						Material.valueOf(plugin.getConfig().getString("LEAVE-NETWORK.ITEM")))
						.name(CC.translate(plugin.getConfig().getString("LEAVE-NETWORK.NAME")))
						.lore(plugin.getConfig().getStringList("LEAVE-NETWORK.LORE"))
						.build());
			}
		}.runTaskTimerAsynchronously(plugin, 0, plugin.getConfig().getInt("SELECTOR-UPDATE-TIME"));
	}

	public void onInventoryClick(InventoryClickEvent event) {
		Inventory clickedInventory = event.getClickedInventory();
		Inventory topInventory = event.getView().getTopInventory();

		if (!topInventory.equals(this.getInventory())) {
			return;
		}
		if (topInventory.equals(clickedInventory)) {
			event.setCancelled(true);
			ItemStack item = event.getCurrentItem();
			if (item == null || item.getType() == Material.AIR || item.getType() == Material.STAINED_GLASS_PANE) {
				return;
			}

			Gamemodes gamemodes = FrozedHubLite.getInstance().getGamemodesManager().getGamemodeByName(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
			if (event.getSlot() == plugin.getConfig().getInt("LEAVE-NETWORK.SLOT")) {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("KickPlayer");
				out.writeUTF(player.getName());
				out.writeUTF(CC.translate(plugin.getConfig().getString("LEAVE-NETWORK.EXIT-MESSAGE")));
				player.sendPluginMessage((plugin), "BungeeCord", out.toByteArray());
			} else {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF(gamemodes.getServerName());
				player.sendPluginMessage((plugin), "BungeeCord", out.toByteArray());
				player.sendMessage(CC.translate("&aConnecting to... &o" + gamemodes.getName()));
			}
		} else if ((!topInventory.equals(clickedInventory) && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) || event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
			event.setCancelled(true);
		}
	}
}
