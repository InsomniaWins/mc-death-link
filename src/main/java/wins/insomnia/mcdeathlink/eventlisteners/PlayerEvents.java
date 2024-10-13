package wins.insomnia.mcdeathlink.eventlisteners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Team;
import wins.insomnia.mcdeathlink.util.TeamUtil;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        TeamUtil.setDefaultTeamVariablesForPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        TeamUtil.removePlayerFromTeamVariables(event.getPlayer());
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getPlayer();
        Boolean deathShouldCauseTeamDeath = (Boolean) TeamUtil.getPlayerTeamVariable(player, "deathShouldCauseTeamDeath");

        if (deathShouldCauseTeamDeath != null && deathShouldCauseTeamDeath) {
            // get player team
            Team playerTeam = player.getScoreboard().getEntityTeam(player);

            // if player is not in team, return
            if (playerTeam == null) return;

            TeamUtil.killAllPlayersOnTeam(playerTeam, player);

        } else {

            Player playerCaused = ((Player) TeamUtil.getPlayerTeamVariable(player, "playerThatCausedTeamDeath"));
            if (playerCaused != null) {
                String playerCausedName = playerCaused.getName();
                Team playerTeam = player.getScoreboard().getEntityTeam(player);

                TextColor teamColor = playerTeam == null ? TextColor.color(255, 255, 255) : playerTeam.color();
                TextColor whiteColor = TextColor.color(255,255,255);

                Component deathMessage = Component.text(player.getName(), teamColor);
                deathMessage = deathMessage.append(Component.text(" died to ", whiteColor));
                deathMessage = deathMessage.append(Component.text(playerCausedName, teamColor));
                deathMessage = deathMessage.append(Component.text("'s incompetence", whiteColor));

                event.deathMessage(deathMessage);
            }


            TeamUtil.setPlayerTeamVariable(player, "deathShouldCauseTeamDeath", true);
            TeamUtil.setPlayerTeamVariable(player, "playerThatCausedTeamDeath", null);
        }
    }


}
