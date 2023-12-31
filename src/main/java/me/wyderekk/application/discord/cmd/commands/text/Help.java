package me.wyderekk.application.discord.cmd.commands.text;

import me.wyderekk.application.discord.cmd.events.TextCommandEvent;
import me.wyderekk.application.discord.cmd.CommandManager;
import me.wyderekk.application.discord.cmd.interfaces.Command;
import me.wyderekk.application.discord.cmd.interfaces.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import java.awt.*;

@Command(name = "help", description = "Displays list of commands.")
public class Help implements ICommand {

    @Override
    public void executeTextCommand(TextCommandEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(new Color(125, 60, 255));
        embedBuilder.setTitle("Commands: ");
        for (ICommand command :  CommandManager.getInstance().getTextCommands()) {
            if(!command.isHidden()) {
                embedBuilder.addField(command.getCommandName(), command.getCommandDescription(), true);
            }
        }
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
