package wins.insomnia.mcdeathlink;

import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class LocateTeamCommand {

	public static void register(Commands commands) {
		commands.register(
				Commands.literal("locateteam")
						.executes(ctx -> {


							if (!MCDeathLink.getInstance().getConfig().getBoolean("players_can_locate_teammates")) {
								return 0;
							}



							CommandSender sender = ctx.getSource().getSender();
							if (sender instanceof Player playerSender) {

								Team senderTeam = playerSender.getScoreboard().getEntityTeam(playerSender);

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
						})
						.build(),
				"locates fellow teammates",
				List.of("an-alias")
		);
	}

}
