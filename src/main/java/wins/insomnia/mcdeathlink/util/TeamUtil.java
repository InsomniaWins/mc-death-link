package wins.insomnia.mcdeathlink.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nullable;
import java.util.HashMap;

public class TeamUtil {

    private static HashMap<Player, HashMap<String, Object>> playerTeamVariables = new HashMap<>();

    public static void killAllPlayersOnTeam(Team team, Player playerException) {

        for (Player player : Bukkit.getOnlinePlayers()) {

            Team playerTeam = player.getScoreboard().getEntityTeam(player);

            if (playerTeam.equals(team) && !player.equals(playerException)) {

                setPlayerTeamVariable(player, "deathShouldCauseTeamDeath", false);
                setPlayerTeamVariable(player, "playerThatCausedTeamDeath", playerException);
                player.setHealth(0.0);

            }

        }

    }

    public static void setDefaultTeamVariablesForPlayer(Player player) {
        playerTeamVariables.put(player, new HashMap<>());
        playerTeamVariables.get(player).put("deathShouldCauseTeamDeath", true);
        playerTeamVariables.get(player).put("playerThatCausedTeamDeath", null);
    }

    public static void removePlayerFromTeamVariables(Player player) {
        playerTeamVariables.remove(player);
    }

    public static void setPlayerTeamVariable(Player player, String variableName, Object value) {
        if (!playerTeamVariables.containsKey(player)) {
            playerTeamVariables.put(player, new HashMap<>());
        }

        playerTeamVariables.get(player).put(variableName, value);
    }

    @Nullable
    public static Object getPlayerTeamVariable(Player player, String variableName) {

        if (!playerTeamVariables.containsKey(player)) {
            return null;
        }

        return playerTeamVariables.get(player).get(variableName);
    }

}
