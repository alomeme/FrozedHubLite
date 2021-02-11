package club.frozed.frozedhublite;

import club.frozed.frozedhublite.listeners.HubListener;
import club.frozed.frozedhublite.managers.GamemodesManager;
import club.frozed.frozedhublite.utils.menu.MenuListener;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

@Getter
public class FrozedHubLite extends JavaPlugin implements PluginMessageListener, Listener {

	@Getter private static FrozedHubLite instance;

	public Map<String, Integer> playerCount;
	@Getter private GamemodesManager gamemodesManager;

	@Override
	public void onEnable() {
		instance = this;

		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			Bukkit.getPluginManager().registerEvents(this, this);
		} else {
			throw new RuntimeException("Could not find PlaceholderAPI!! You need PlaceholderAPI in order to make player counts update.");
		}

		this.saveDefaultConfig();

		gamemodesManager = new GamemodesManager();
		gamemodesManager.loadServers();

		Bukkit.getPluginManager().registerEvents(new HubListener(), this);
		Bukkit.getPluginManager().registerEvents(new MenuListener(), this);

		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

		this.updateServerCount();
	}

	private void updateServerCount() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (Bukkit.getOnlinePlayers().size() > 0) {
					getCount(Bukkit.getOnlinePlayers().iterator().next(), null);
				}
			}
		}.runTaskTimerAsynchronously(this, 20L, 20L);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}
		try {
			if (message.length == 0) return;
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subChannel = in.readUTF();
			if (subChannel.equals("PlayerCount")) {
				String server = in.readUTF();
				int playerCount = in.readInt();
				this.playerCount.put(server, playerCount);
			}
		} catch (Exception ignored) {
		}
	}

	public void getCount(Player player, String server) {
		if (server == null) server = "ALL";
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PlayerCount");
		out.writeUTF(server);
		player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
	}
}
