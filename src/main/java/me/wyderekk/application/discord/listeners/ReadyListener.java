package me.wyderekk.application.discord.listeners;

import me.wyderekk.application.discord.cmd.CommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        JDA jda = event.getJDA();
        CommandManager.getInstance().getSlashCommands().forEach(command -> {
            jda.upsertCommand(command.getCommandName(), command.getCommandDescription()).addOptions(command.getCommandOptions()).queue();
        });
    }
}
