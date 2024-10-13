package wins.insomnia.mcdeathlink.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import javax.annotation.Nullable;
import java.util.HashMap;

public class TeamUtil {

    // hashmap holding variables per player to help plugin determine things like if their death should kill the entire team
    private static final HashMap<Player, HashMap<String, Object>> PLAYER_TEAM_VARIABLES = new HashMap<>();



    public static void killAllPlayersOnTeam(Team team, Player playerException) {

        for (Player player : Bukkit.getOnlinePlayers()) {

            Team playerTeam = player.getScoreboard().getEntityTeam(player);

            if (playerTeam == null) continue;

            if (playerTeam.equals(team) && !player.equals(playerException)) {

                setPlayerTeamVariable(player, "deathShouldCauseTeamDeath", false);
                setPlayerTeamVariable(player, "playerThatCausedTeamDeath", playerException);
                player.setHealth(0.0);

            }

        }

    }



    public static void setDefaultTeamVariablesForPlayer(Player player) {
        PLAYER_TEAM_VARIABLES.put(player, new HashMap<>());
        PLAYER_TEAM_VARIABLES.get(player).put("deathShouldCauseTeamDeath", true);
        PLAYER_TEAM_VARIABLES.get(player).put("playerThatCausedTeamDeath", null);
    }



    public static void removePlayerFromTeamVariables(Player player) {
        PLAYER_TEAM_VARIABLES.remove(player);
    }



    public static void setPlayerTeamVariable(Player player, String variableName, Object value) {
        if (!PLAYER_TEAM_VARIABLES.containsKey(player)) {
            PLAYER_TEAM_VARIABLES.put(player, new HashMap<>());
        }

        PLAYER_TEAM_VARIABLES.get(player).put(variableName, value);
    }



    @Nullable
    public static Object getPlayerTeamVariable(Player player, String variableName) {

        if (!PLAYER_TEAM_VARIABLES.containsKey(player)) {
            return null;
        }

        return PLAYER_TEAM_VARIABLES.get(player).get(variableName);
    }

}
