package wins.insomnia.mcdeathlink;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import wins.insomnia.mcdeathlink.eventlisteners.PlayerEvents;

public class MCDeathLink extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        PlayerEvents playerEventsListenerInstance = new PlayerEvents();
        Bukkit.getPluginManager().registerEvents(playerEventsListenerInstance, this);
    }
}