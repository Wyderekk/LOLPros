package me.wyderekk.application.discord.cmd.commands.slash;

import me.wyderekk.application.data.database.SQLite;
import me.wyderekk.application.data.datatypes.AccountData;
import me.wyderekk.application.data.util.AccountDataUtil;
import me.wyderekk.application.discord.cmd.events.SlashCommandEvent;
import me.wyderekk.application.discord.cmd.interfaces.Command;
import me.wyderekk.application.discord.cmd.interfaces.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Command(name = "player", description = "Displays player information.")
public class Player implements ICommand {

    @Override
    public void executeSlashCommand(SlashCommandEvent event) {
        String player = event.getSlashCommandInteractionEvent().getOption("player").getAsString();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(new Color(125, 60, 255));
        ArrayList<AccountData> accountData = SQLite.getAccountData(player);
        if(accountData.isEmpty()) {
            embedBuilder.setTitle("No player found with the name: " + player);
            event.getSlashCommandInteractionEvent().replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
        } else {
            AccountData highestAccount = accountData.getFirst();
            embedBuilder.setThumbnail("https://ddragon.leagueoflegends.com/cdn/13.21.1/img/profileicon/" + highestAccount.avatarId() + ".png");
            embedBuilder.setTitle(accountData.getFirst().owner());
            embedBuilder.addField("Position: ", highestAccount.position().getName(), false);
            embedBuilder.addField("Rank: ", AccountDataUtil.parse(highestAccount.rank().tier(), highestAccount.rank().division(), highestAccount.rank().lp()), false);
            embedBuilder.addField("Peak: ", AccountDataUtil.parse(highestAccount.peak().tier(), highestAccount.peak().division(), highestAccount.peak().lp()), false);
            embedBuilder.addField("Accounts: (" + accountData.size() + ")",
                    accountData.stream()
                            .map(account -> account.summonerNames().getFirst().name())
                            .collect(Collectors.joining(", ")),
                    false);
            event.getSlashCommandInteractionEvent().replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
        }
    }

    @Override
    public List<OptionData> getCommandOptions() {
        OptionData optionData = new OptionData(OptionType.STRING, "player", "Player name", true);
        return List.of(optionData);
    }
}
