package me.wyderekk.application.discord.listeners;

import me.wyderekk.application.discord.cmd.events.SlashCommandEvent;
import me.wyderekk.application.discord.cmd.events.TextCommandEvent;
import me.wyderekk.application.discord.cmd.CommandManager;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageListener extends ListenerAdapter {

    private static final String PREFIX = ">";

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().replaceFirst(PREFIX, "").split("\\s+");

        if(!args[0].startsWith(PREFIX)) return;

        if(!(event.getChannel() instanceof GuildMessageChannel)) return;

        CommandManager.getInstance().getTextCommands().stream()
                .filter(command -> command.getCommandName().equalsIgnoreCase(args[0])
                        && event.getMember().hasPermission(command.getCommandPermissions()))
                .forEach(command -> command.executeTextCommand(new TextCommandEvent(event)));
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String[] args = event.getFullCommandName().split("\\s+");

        CommandManager.getInstance().getSlashCommands().stream()
                .filter(command -> command.getCommandName().equalsIgnoreCase(args[0])
                        && event.getMember().hasPermission(command.getCommandPermissions()))
                .forEach(command -> command.executeSlashCommand(new SlashCommandEvent(event)));
    }
}
