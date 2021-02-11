package club.frozed.frozedhublite.listeners;

import club.frozed.frozedhublite.FrozedHubLite;
import club.frozed.frozedhublite.provider.GamemodeSelectorProvider;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.HashMap;

public class HubListener implements Listener {

	final String lobbyWorldName = FrozedHubLite.getInstance().getConfig().getString("SETTINGS.LOBBY-WORLD-NAME");
	private final HashMap<Player, Integer> invisibleBat = new HashMap<>();

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		event.setJoinMessage(null);

		for (Player hiddenPlayer : Bukkit.getOnlinePlayers()) {
			player.hidePlayer(hiddenPlayer);
		}

		player.setAllowFlight(true);
		addInvisibleBat(player);

		player.teleport(Bukkit.getServer().getWorld(lobbyWorldName).getSpawnLocation());
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(FrozedHubLite.getInstance(), () -> new GamemodeSelectorProvider(player).open(player), 2L);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		event.setQuitMessage(null);
		removeInvisibleBat(player);
	}

	public void addInvisibleBat(Player player) {
		Location location = player.getLocation();
		WorldServer worldServer = ((CraftWorld) player.getLocation().getWorld()).getHandle();
		EntityBat bat = new EntityBat(worldServer);

		bat.setLocation(location.getX() + 0.5D, location.getY() + 2.0D, location.getZ() + 0.5D, 0.0F, 0.0F);
		bat.setHealth(bat.getMaxHealth());
		bat.setInvisible(true);
		bat.d(0);
		bat.setAsleep(true);
		bat.setAirTicks(10);
		bat.setSneaking(false);

		PlayerConnection playerConnection = (((CraftPlayer) player).getHandle()).playerConnection;
		PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(bat);
		PacketPlayOutAttachEntity attach = new PacketPlayOutAttachEntity(0, ((CraftPlayer) player).getHandle(), bat);

		playerConnection.sendPacket(packet);
		playerConnection.sendPacket(attach);

		this.invisibleBat.put(player, bat.getId());
	}

	public void removeInvisibleBat(Player player) {
		if (this.invisibleBat.get(player) != null) {
			PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(this.invisibleBat.get(player));
			PlayerConnection playerConnection = (((CraftPlayer) player).getHandle()).playerConnection;

			playerConnection.sendPacket(packet);

			this.invisibleBat.put(player, null);
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(FrozedHubLite.getInstance(), () -> new GamemodeSelectorProvider(player).open(player), 3L);
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockPlaceEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockInteract(PlayerInteractEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onHungerChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) event.setCancelled(true);
		if (event.getCause() == EntityDamageEvent.DamageCause.VOID)
			event.getEntity().teleport(Bukkit.getServer().getWorld(lobbyWorldName).getSpawnLocation());
	}

	@EventHandler
	public void onPluginLoad(PluginEnableEvent event) {
		World lobbyWorld = Bukkit.getServer().getWorld(lobbyWorldName);

		lobbyWorld.setGameRuleValue("doDaylightCycle", "false");
		lobbyWorld.setTime(6000);
		lobbyWorld.setStorm(false);
		lobbyWorld.setWeatherDuration(0);
		lobbyWorld.setAnimalSpawnLimit(0);
		lobbyWorld.setAmbientSpawnLimit(0);
		lobbyWorld.setMonsterSpawnLimit(0);
		lobbyWorld.setWaterAnimalSpawnLimit(0);
	}
}
