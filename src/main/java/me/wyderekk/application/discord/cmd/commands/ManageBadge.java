package me.wyderekk.application.discord.cmd.commands;

import me.wyderekk.application.data.PlayerList;
import me.wyderekk.application.data.database.SQLite;
import me.wyderekk.application.data.datatypes.enums.Badge;
import me.wyderekk.application.discord.cmd.CommandEvent;
import me.wyderekk.application.discord.cmd.interfaces.Command;
import me.wyderekk.application.discord.cmd.interfaces.ICommand;
import net.dv8tion.jda.api.Permission;

import java.util.ArrayList;

@Command(name = "badge", description = "Manage player badges.", permissions = Permission.ADMINISTRATOR, minArgs = 3, maxArgs = 3)
public class ManageBadge implements ICommand {

    @Override
    public void executeCommand(CommandEvent event) {
        String[] args = event.getArguments();
        if(getMinArgs() > args.length || getMaxArgs() < args.length) {
            event.getChannel().sendMessage("Usage: !badge <add/remove> <player> <badge>").queue();
        } else {
            switch (args[0]) {
                case "add" -> {
                    try {
                        Badge badge = Badge.valueOf(args[2].toUpperCase());
                        if(!PlayerList.INSTANCE.getPlayers().contains(args[1].toLowerCase())) {
                            event.getChannel().sendMessage("Player not found.").queue();
                            return;
                        }
                        SQLite.saveBadges(args[1], badge);
                        event.getChannel().sendMessage("Added badge: " + badge.name() + " for " + args[1]).queue();
                    } catch (IllegalArgumentException e) {
                        event.getChannel().sendMessage("Invalid badge.").queue();
                    }
                }

                case "remove" -> {
                    try {
                        Badge badge = Badge.valueOf(args[2].toUpperCase());
                        if(!PlayerList.INSTANCE.getPlayers().contains(args[1].toLowerCase())) {
                            event.getChannel().sendMessage("Player not found.").queue();
                            return;
                        }
                        ArrayList<Badge> badges = SQLite.getBadges(args[1]);
                        if(!badges.contains(badge)) {
                            event.getChannel().sendMessage("Player does not have this badge.").queue();
                            return;
                        }
                        badges.remove(badge);
                        SQLite.saveBadges(args[1], badges);
                        event.getChannel().sendMessage("Removed badge: " + badge.name() + " for " + args[1]).queue();
                    } catch (IllegalArgumentException e) {
                        event.getChannel().sendMessage("Invalid badge.").queue();
                    }
                }
            }
        }
    }
}
