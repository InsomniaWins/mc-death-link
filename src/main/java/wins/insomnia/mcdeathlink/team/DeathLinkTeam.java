package wins.insomnia.mcdeathlink.team;

import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;
import wins.insomnia.mcdeathlink.util.TeamUtil;

import java.util.ArrayList;
import java.util.UUID;

public class DeathLinkTeam {


	public static final int ADD_ERR_PLAYER_ALREADY_IN_A_TEAM = 1;


	public static final int REMOVE_ERR_PLAYER_NOT_IN_A_TEAM = 1;
	public static final int REMOVE_ERR_PLAYER_IN_A_DIFFERENT_TEAM = 2;


	private final ArrayList<UUID> MEMBERS = new ArrayList<>();
	private String name = "Unnamed Team";


	public DeathLinkTeam(String name) {
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;

		for (UUID member : MEMBERS) {
			TeamUtil.setPlayerTeamVariable(member, "death_link_team", name);
		}

		TeamUtil.saveTeam(this);

	}

	public String getName() {
		return name;
	}

	public boolean isPlayerMember(Player player) {
		return MEMBERS.contains(player.getUniqueId());
	}

	public int addMember(UUID playerId) {
		if (TeamUtil.getPlayerTeamVariable(playerId, "death_link_team") != null) {
			return ADD_ERR_PLAYER_ALREADY_IN_A_TEAM;
		}

		MEMBERS.add(playerId);
		TeamUtil.setPlayerTeamVariable(playerId, "death_link_team", getName());

		TeamUtil.saveTeam(this);

		return 0;
	}

	public int addMember(Player player) {
		return addMember(player.getUniqueId());
	}

	public int removeMember(UUID playerId) {

		String playerDeathLinkTeam = (String) TeamUtil.getPlayerTeamVariable(playerId, "death_link_team");

		if (playerDeathLinkTeam == null) return REMOVE_ERR_PLAYER_NOT_IN_A_TEAM;

		if (!playerDeathLinkTeam.equals(getName())) return REMOVE_ERR_PLAYER_IN_A_DIFFERENT_TEAM;

		MEMBERS.remove(playerId);
		TeamUtil.removePlayerTeamVariable(playerId, "death_link_team");

		TeamUtil.saveTeam(this);

		return 0;
	}

	public int removeMember(Player player) {
		return removeMember(player.getUniqueId());
	}

	public void clearMembers() {

		for (UUID memberId : getMembers()) {
			TeamUtil.removePlayerTeamVariable(memberId, "death_link_team");
		}

		MEMBERS.clear();

		TeamUtil.saveTeam(this);
	}

	public ArrayList<UUID> getMembers() {
		return new ArrayList<>(MEMBERS);
	}

}

