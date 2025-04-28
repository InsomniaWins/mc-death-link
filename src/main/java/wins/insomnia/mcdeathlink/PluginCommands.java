package wins.insomnia.mcdeathlink;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import wins.insomnia.mcdeathlink.team.DeathLinkTeam;
import wins.insomnia.mcdeathlink.util.PlayerUtil;
import wins.insomnia.mcdeathlink.util.TeamUtil;

import java.util.List;
import java.util.UUID;

public class PluginCommands {


	public static void register(Commands commands) {

		commands.register(
				Commands.literal("dlteam")
						.then(Commands.literal("list")
								.executes(ctx -> {

									StringBuilder teamsString = new StringBuilder(" - Death Link Teams - \n");


									List<DeathLinkTeam> deathLinkTeams = TeamUtil.getDeathLinkTeams();
									for (DeathLinkTeam team : deathLinkTeams) {

										teamsString.append(team.getName()).append(" {");

										boolean printComma = false;

										for (UUID playerUuid : team.getMembers()) {

											if (printComma) teamsString.append(", ");
											if (!printComma) printComma = true;

											OfflinePlayer player = ctx.getSource().getSender().getServer().getOfflinePlayer(playerUuid);

											if (player.getName() == null || player.getName().equals("null")) {
												teamsString.append(PlayerUtil.getUsernameFromUUID(playerUuid));
												continue;
											}

											teamsString.append(player.getName());

										}

										teamsString.append("}\n");
									}

									ctx.getSource().getSender().sendMessage(teamsString.toString());

									return 0;
								})
						)
						.then(Commands.literal("join")
								.then(Commands.argument("team name", StringArgumentType.string())
										.then(Commands.argument("player name", StringArgumentType.string())
												.executes(ctx -> {

													String teamName = StringArgumentType.getString(ctx, "team name");
													String playerName = StringArgumentType.getString(ctx, "player name");
													OfflinePlayer offlinePlayer = ctx.getSource().getSender().getServer().getOfflinePlayer(playerName);

													if (offlinePlayer == null) {
														ctx.getSource().getSender().sendMessage("Player does not exist!");
														return 1;
													}

													UUID playerId = offlinePlayer.getUniqueId();

													DeathLinkTeam team = TeamUtil.getDeathLinkTeam(teamName);

													if (team == null) {
														ctx.getSource().getSender().sendMessage("Team does not exist!");
														return 1;
													}

													int result = team.addMember(playerId);

													switch (result) {
														case DeathLinkTeam.ADD_ERR_PLAYER_ALREADY_IN_A_TEAM -> {

															ctx.getSource().getSender().sendMessage("Player is already in a team! Do /dlteam leave <player>");

															return 1;
														}
														default -> {

															ctx.getSource().getSender().sendMessage("Player joined team.");

															return Command.SINGLE_SUCCESS;
														}
													}
												})
										)
								)
						)
						.then(Commands.literal("leave")
								.then(Commands.argument("player name", StringArgumentType.string())
										.executes(ctx -> {

											String playerName = StringArgumentType.getString(ctx, "player name");
											OfflinePlayer offlinePlayer = ctx.getSource().getSender().getServer().getOfflinePlayer(playerName);

											if (offlinePlayer == null) {
												ctx.getSource().getSender().sendMessage("Player does not exist!");
												return 1;
											}

											UUID playerId = offlinePlayer.getUniqueId();


											DeathLinkTeam team = TeamUtil.getPlayerDeathLinkTeam(playerId);

											if (team == null) {
												ctx.getSource().getSender().sendMessage("Player is not in a team!");
												return 1;
											}

											int result = team.removeMember(playerId);

											switch (result) {
												case DeathLinkTeam.REMOVE_ERR_PLAYER_IN_A_DIFFERENT_TEAM -> {

													ctx.getSource().getSender().sendMessage("Player is in a different team!");

													return 2;
												}

												case DeathLinkTeam.REMOVE_ERR_PLAYER_NOT_IN_A_TEAM -> {

													ctx.getSource().getSender().sendMessage("Player is not in a team!");

													return 3;
												}

												default -> {

													ctx.getSource().getSender().sendMessage("Player removed from team.");

													return Command.SINGLE_SUCCESS;
												}
											}
										})
								)
						)
						.then(Commands.literal("add")
								.then(Commands.argument("team name", StringArgumentType.string())
										.executes(ctx -> {

											String teamName = StringArgumentType.getString(ctx, "team name");
											int result = TeamUtil.createDeathLinkTeam(teamName);

											switch (result) {
												case 1 -> {
													ctx.getSource().getSender().sendMessage("Team already exists!");
													return 1;
												}
												default -> {
													ctx.getSource().getSender().sendMessage("Added team: " + teamName + "!");
													return Command.SINGLE_SUCCESS;
												}
											}
										}
										)
								)
						)
						.then(Commands.literal("remove")
								.then(Commands.argument("team name", StringArgumentType.string())
										.executes(ctx -> {

											String teamName = StringArgumentType.getString(ctx, "team name");
											int result = TeamUtil.removeDeathLinkTeam(teamName);

											switch (result) {
												case 1 -> {
													ctx.getSource().getSender().sendMessage("Team does not exists!");
													return 1;
												}
												default -> {
													ctx.getSource().getSender().sendMessage("Removed team: " + teamName + "!");
													return Command.SINGLE_SUCCESS;
												}
											}
										})
								)
						)
						.build(),
				"Manages death-link teams"
		);


		commands.register(Commands.literal("locateteam")
				.executes(ctx -> {


					if (!MCDeathLink.getInstance().getConfig().getBoolean("players_can_locate_teammates")) {
						return 0;
					}



					CommandSender sender = ctx.getSource().getSender();
					if (sender instanceof Player playerSender) {


						DeathLinkTeam playerDeathLinkTeam = TeamUtil.getPlayerDeathLinkTeam(playerSender);

						if (playerDeathLinkTeam != null) {

							playerSender.sendMessage(Component.text("This command currently does not work with death-link teams. Use minecraft teams instead."));

							return 1;

						}



						Team senderTeam = playerSender.getScoreboard().getEntityTeam(playerSender);

						// player not in team, no members to locate
						if (senderTeam == null) return Command.SINGLE_SUCCESS;



						for (Player player : Bukkit.getOnlinePlayers()) {

							Team playerTeam = player.getScoreboard().getEntityTeam(player);

							if (playerTeam == null) continue;

							if (playerTeam.equals(senderTeam)) {

								World.Environment playerDimension = player.getWorld().getEnvironment();

								String dimensionName = "The Overworld";
								Style dimensionStyle = Style.style(TextColor.color(85, 255, 85));
								if (playerDimension == World.Environment.NETHER) {
									dimensionName = "The Nether";
									Style.style(TextColor.color(255, 85, 85));
								}
								if (playerDimension == World.Environment.THE_END) {
									dimensionName = "The End";
									Style.style(TextColor.color(170, 170, 170));
								}

								Location playerLocation = player.getLocation();

								String positionString = "<" +
										(int) playerLocation.x() +
										", " +
										(int) playerLocation.y() +
										", " +
										(int) playerLocation.z() +
										">";

								Component message = player.name()
										.append(Component.text(" is in "))
										.append(Component.text(dimensionName, dimensionStyle))
										.append(Component.text(" at position "))
										.append(Component.text(positionString, Style.style(TextColor.color(255, 255, 85))));
								playerSender.sendMessage(message);

							}

						}


						return Command.SINGLE_SUCCESS;
					}

					return 0;
				}
			).build(),
			"locates fellow teammates"
		);
	}

	private static void locateTeam(Player player) {

	}

}
