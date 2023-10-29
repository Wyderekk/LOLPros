package me.wyderekk.application.discord.listeners;

import me.wyderekk.application.discord.cmd.CommandEvent;
import me.wyderekk.application.discord.cmd.CommandManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageListener extends ListenerAdapter {

    private static final String PREFIX = "!";

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(!args[0].startsWith(PREFIX)) return;

        CommandManager.INSTANCE.getCommands().stream()
                .filter(command -> command.getCommandName().equalsIgnoreCase(args[0].replaceFirst(PREFIX, "")))
                .forEach(command -> command.executeCommand(new CommandEvent(event)));
    }
}
