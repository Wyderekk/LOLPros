package me.wyderekk.application.discord.cmd.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.Arrays;

public class TextCommandEvent {

    private JDA jda;

    private User user;

    private Guild guild;

    private Message message;

    private MessageChannelUnion channel;

    private String[] arguments;

    private MessageReceivedEvent messageReceivedEvent;

    /**
     * Constructor used to save the Data.
     *
     * @param jda                          the {@link JDA} Entity.
     * @param user                         the {@link User} Entity.
     * @param guild                        the {@link Guild} Entity.
     * @param message                      the {@link Message} Entity.
     * @param textChannel                  the {@link TextChannel} Entity.
     * @param arguments                    the {@link String[]} Entity.
     * @param messageReceivedEvent         the {@link MessageReceivedEvent} Entity.
     */
    public TextCommandEvent(MessageReceivedEvent messageReceivedEvent) {
        this.messageReceivedEvent = messageReceivedEvent;
        this.jda = messageReceivedEvent.getJDA();
        this.user = messageReceivedEvent.getAuthor();
        this.guild = messageReceivedEvent.getGuild();
        this.message = messageReceivedEvent.getMessage();
        this.channel = messageReceivedEvent.getChannel();
        this.arguments = Arrays.copyOfRange(message.getContentRaw().split("\\s+"), 1, message.getContentRaw().split("\\s+").length);
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

    public Message getMessage() {
        return message;
    }

    public MessageChannelUnion getChannel() {
        return channel;
    }

    public MessageReceivedEvent getMessageReceivedEvent() {
        return messageReceivedEvent;
    }

    public String[] getArguments() {
        return arguments;
    }

}