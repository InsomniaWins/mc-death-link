package wins.insomnia.mcdeathlink.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import wins.insomnia.mcdeathlink.MCDeathLink;
import wins.insomnia.mcdeathlink.team.DeathLinkTeam;

import javax.annotation.Nullable;
import java.util.*;

public class TeamUtil {

    // map of death-link teams used rather than vanilla teams
    // used if admin does not want to use vanilla team command
    private static final HashMap<String, DeathLinkTeam> DEATH_LINK_TEAMS = new HashMap<>();



    // hashmap holding variables per player to help plugin determine things like if their death should kill the entire team
    private static final HashMap<UUID, HashMap<String, Object>> PLAYER_TEAM_VARIABLES = new HashMap<>();


    public static void killAllPlayersOnTeam(DeathLinkTeam team, Player playerException) {

        for (UUID uuid : team.getMembers()) {
            if (uuid.equals(playerException.getUniqueId())) {
                continue;
            }

            Player player = playerException.getServer().getPlayer(uuid);

            if (player == null) continue;

            setPlayerTeamVariable(player, "deathShouldCauseTeamDeath", false);
            setPlayerTeamVariable(player, "playerThatCausedTeamDeath", playerException);
            player.setHealth(0.0);
        }

    }


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
        setDefaultTeamVariablesForPlayer(player.getUniqueId());
    }


    public static void setDefaultTeamVariablesForPlayer(UUID playerId) {
        PLAYER_TEAM_VARIABLES.put(playerId, new HashMap<>());
        PLAYER_TEAM_VARIABLES.get(playerId).put("deathShouldCauseTeamDeath", true);
        PLAYER_TEAM_VARIABLES.get(playerId).put("playerThatCausedTeamDeath", null);
    }


    public static void removePlayerFromTeamVariables(Player player) {
        removePlayerFromTeamVariables(player.getUniqueId());
    }

    public static void removePlayerFromTeamVariables(UUID playerId) {
        PLAYER_TEAM_VARIABLES.remove(playerId);
    }


    public static void removePlayerTeamVariable(Player player, String variableName) {
        removePlayerTeamVariable(player.getUniqueId(), variableName);
    }

    public static void removePlayerTeamVariable(UUID playerId, String variableName) {

        if (!PLAYER_TEAM_VARIABLES.containsKey(playerId)) return;

        PLAYER_TEAM_VARIABLES.get(playerId).remove(variableName);

    }

    public static void setPlayerTeamVariable(Player player, String variableName, Object value) {
        setPlayerTeamVariable(player.getUniqueId(), variableName, value);
    }


    public static void setPlayerTeamVariable(UUID playerId, String variableName, Object value) {

        if (!PLAYER_TEAM_VARIABLES.containsKey(playerId)) {
            PLAYER_TEAM_VARIABLES.put(playerId, new HashMap<>());
        }

        PLAYER_TEAM_VARIABLES.get(playerId).put(variableName, value);
    }



    @Nullable
    public static Object getPlayerTeamVariable(Player player, String variableName) {
        return getPlayerTeamVariable(player.getUniqueId(), variableName);
    }

    @Nullable
    public static Object getPlayerTeamVariable(UUID playerId, String variableName) {

        if (!PLAYER_TEAM_VARIABLES.containsKey(playerId)) {
            return null;
        }

        return PLAYER_TEAM_VARIABLES.get(playerId).get(variableName);
    }


    public static int createDeathLinkTeam(String teamName) {

        if (getDeathLinkTeam(teamName) != null) {
            return 1;
        }

        DeathLinkTeam team = new DeathLinkTeam(teamName);
        DEATH_LINK_TEAMS.put(team.getName(), team);

        saveTeam(team);

        return 0;
    }


    public static void loadTeams() {

        DEATH_LINK_TEAMS.clear();

        FileConfiguration config = MCDeathLink.getInstance().getConfig();


        ConfigurationSection teams = (ConfigurationSection) config.get("teams");

        if (teams == null) return;

        Set<String> teamNames = teams.getKeys(false);

        if (teamNames.isEmpty()) return;

        for (String teamName : teamNames) {

            ConfigurationSection teamInfo = (ConfigurationSection) teams.get(teamName);

            DeathLinkTeam team = new DeathLinkTeam(teamName);

            List<String> members = (List<String>) teamInfo.getList("members");

            for (String memberId : members) {
                team.addMember(UUID.fromString(memberId));
            }

            DEATH_LINK_TEAMS.put(teamName, team);

        }



    }


    public static void saveTeam(DeathLinkTeam deathLinkTeam) {

        FileConfiguration config = MCDeathLink.getInstance().getConfig();

        ConfigurationSection teamsSection = (ConfigurationSection) config.get("teams", null);

        if (teamsSection == null) {

            MemoryConfiguration teams = new MemoryConfiguration();

            MemoryConfiguration team = new MemoryConfiguration();

            List<String> memberList = new ArrayList<>();
            for (UUID id : deathLinkTeam.getMembers()) {
                memberList.add(id.toString());
            }

            team.set("members", memberList);
            teams.set(deathLinkTeam.getName(), team);

            config.set("teams", teams);
            MCDeathLink.getInstance().saveConfig();

            return;

        }



        MemorySection teamInfoSection = (MemorySection) teamsSection.get(deathLinkTeam.getName());

        if (teamInfoSection == null || teamInfoSection.getKeys(false).isEmpty()) {

            MemoryConfiguration team = new MemoryConfiguration();

            List<String> memberList = new ArrayList<>();
            for (UUID id : deathLinkTeam.getMembers()) {
                memberList.add(id.toString());
            }

            team.set("members", memberList);
            teamsSection.set(deathLinkTeam.getName(), team);

            return;

        }




        List<String> memberList = new ArrayList<>();
        for (UUID id : deathLinkTeam.getMembers()) {
            memberList.add(id.toString());
        }

        teamInfoSection.set("members", memberList);
        teamsSection.set(deathLinkTeam.getName(), teamInfoSection);

        MCDeathLink.getInstance().saveConfig();
        MCDeathLink.getInstance().reloadConfig();
    }





    public static DeathLinkTeam getDeathLinkTeam(String teamName) {
        return DEATH_LINK_TEAMS.get(teamName);
    }

    public static int removeDeathLinkTeam(String teamName) {

        if (getDeathLinkTeam(teamName) == null) {
            return 1;
        }

        DEATH_LINK_TEAMS.remove(teamName);



        FileConfiguration config = MCDeathLink.getInstance().getConfig();

        HashMap<String, Object> teams = (HashMap<String, Object>) config.get("teams", new HashMap<String, Object>());

        teams.remove(teamName);

        config.set("teams", teams);
        MCDeathLink.getInstance().saveConfig();



        return 0;
    }

    public static DeathLinkTeam getPlayerDeathLinkTeam(Player player) {

        return getPlayerDeathLinkTeam(player.getUniqueId());

    }

    public static DeathLinkTeam getPlayerDeathLinkTeam(UUID playerId) {

        String playerDeathLinkTeamId = (String) getPlayerTeamVariable(playerId, "death_link_team");

        if (playerDeathLinkTeamId == null) return null;

        return DEATH_LINK_TEAMS.get(playerDeathLinkTeamId);

    }

    public static List<DeathLinkTeam> getDeathLinkTeams() {
        return DEATH_LINK_TEAMS.values().stream().toList();
    }
}
