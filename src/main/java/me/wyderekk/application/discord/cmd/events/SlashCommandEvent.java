package me.wyderekk.application.discord.cmd.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import java.util.Arrays;

public class SlashCommandEvent {

    private JDA jda;

    private User user;

    private Guild guild;

    private String message;

    private MessageChannelUnion channel;

    private String[] arguments;

    private SlashCommandInteractionEvent slashCommandInteractionEvent;

    /**
     * Constructor used to save the Data.
     *
     * @param jda                          the {@link JDA} Entity.
     * @param user                         the {@link User} Entity.
     * @param guild                        the {@link Guild} Entity.
     * @param message                      the {@link String} Entity.
     * @param textChannel                  the {@link TextChannel} Entity.
     * @param arguments                    the {@link String[]} Entity.
     * @param slashCommandInteractionEvent the {@link SlashCommandInteractionEvent} Entity.
     */
    public SlashCommandEvent(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        this.slashCommandInteractionEvent = slashCommandInteractionEvent;
        this.jda = slashCommandInteractionEvent.getJDA();
        this.user = slashCommandInteractionEvent.getUser();
        this.guild = slashCommandInteractionEvent.getGuild();
        this.message = slashCommandInteractionEvent.getFullCommandName();
        this.channel = slashCommandInteractionEvent.getChannel();
        this.arguments = Arrays.copyOfRange(message.split("\\s+"), 1, message.split("\\s+").length);
    }

    public JDA getJDA() {
        return jda;
    }

    public User getUser() {
        return user;
    }

    public Guild getGuild() {
        return guild;
    }

    public String getMessage() {
        return message;
    }

    public MessageChannelUnion getChannel() {
        return channel;
    }

    public String[] getArguments() {
        return arguments;
    }

    public SlashCommandInteractionEvent getSlashCommandInteractionEvent() {
        return slashCommandInteractionEvent;
    }

}
