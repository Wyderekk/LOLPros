package me.wyderekk.application.discord.cmd.commands;

import me.wyderekk.application.data.PlayerList;
import me.wyderekk.application.data.database.SQLite;
import me.wyderekk.application.data.datatypes.enums.Badge;
import me.wyderekk.application.discord.cmd.CommandEvent;
import me.wyderekk.application.discord.cmd.interfaces.Command;
import me.wyderekk.application.discord.cmd.interfaces.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import java.awt.*;
import java.util.ArrayList;

@Command(name = "badge", description = "Manage player badges.", permissions = Permission.ADMINISTRATOR, minArgs = 3, maxArgs = 3)
public class ManageBadge implements ICommand {

    @Override
    public void executeCommand(CommandEvent event) {
        String[] args = event.getArguments();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(new Color(125, 60, 255));
        if(getMinArgs() > args.length || getMaxArgs() < args.length) {
            embedBuilder.setTitle("Usage: !badge <add/remove> <player> <badge>");
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        } else {
            switch (args[0]) {
                case "add" -> {
                    try {
                        Badge badge = Badge.valueOf(args[2].toUpperCase());
                        if(!PlayerList.INSTANCE.getPlayers().contains(args[1].toLowerCase())) {
                            embedBuilder.setTitle("Player not found.");
                            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                            return;
                        }
                        SQLite.saveBadges(args[1], badge);
                        embedBuilder.setTitle("Added badge: " + badge.name() + " for " + args[1]);
                        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                    } catch (IllegalArgumentException e) {
                        embedBuilder.setTitle("Invalid badge.");
                        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                    }
                }

                case "remove" -> {
                    try {
                        Badge badge = Badge.valueOf(args[2].toUpperCase());
                        if(!PlayerList.INSTANCE.getPlayers().contains(args[1].toLowerCase())) {
                            embedBuilder.setTitle("Player not found.");
                            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                            return;
                        }
                        ArrayList<Badge> badges = SQLite.getBadges(args[1]);
                        if(!badges.contains(badge)) {
                            embedBuilder.setTitle("Player does not have this badge.");
                            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                            return;
                        }
                        badges.remove(badge);
                        SQLite.saveBadges(args[1], badges);
                        embedBuilder.setTitle("Removed badge: " + badge.name() + " for " + args[1]);
                        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                    } catch (IllegalArgumentException e) {
                        embedBuilder.setTitle("Invalid badge.");
                        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                    }
                }
            }
        }
    }
}
