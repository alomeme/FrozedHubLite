package club.frozed.frozedhublite.utils.menu.type;

import club.frozed.frozedhublite.utils.menu.Menu;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.ParameterizedType;

public abstract class ChestMenu implements Menu {

	protected final JavaPlugin plugin;
	@Getter private final Inventory inventory;

	public ChestMenu(String title, int rows) {
		this.plugin = JavaPlugin.getPlugin((Class) (((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]));
		this.inventory = this.plugin.getServer().createInventory(this, rows, title);
	}
}
