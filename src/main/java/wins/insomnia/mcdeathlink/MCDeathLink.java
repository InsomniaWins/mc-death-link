package wins.insomnia.mcdeathlink;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import wins.insomnia.mcdeathlink.eventlisteners.PlayerEvents;
import wins.insomnia.mcdeathlink.util.TeamUtil;

public class MCDeathLink extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {

        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(this, this);

        PlayerEvents playerEventsListenerInstance = new PlayerEvents();
        Bukkit.getPluginManager().registerEvents(playerEventsListenerInstance, this);

        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            PluginCommands.register(event.registrar());
        });


        TeamUtil.loadTeams();

    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    public static MCDeathLink getInstance() {
        return getPlugin(MCDeathLink.class);
    }
}