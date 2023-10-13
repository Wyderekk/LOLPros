package me.wyderekk.application.views;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import me.wyderekk.application.data.database.SQLite;
import me.wyderekk.application.data.datatypes.AccountData;
import me.wyderekk.application.data.util.AccountDataUtil;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Route("player")
@RouteAlias("player/*")
@PageTitle("Player Info")
@CssImport("./styles/player-info.css")
public class PlayerInfoView extends HorizontalLayout implements HasUrlParameter<String> {

    private static final String LOL_CDN = "https://ddragon.leagueoflegends.com/cdn/13.19.1/img/profileicon/";

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        String decodedParameter = URLDecoder.decode(parameter, StandardCharsets.UTF_8);
        ArrayList<AccountData> accountDataArrayList = SQLite.getAccountData(decodedParameter);

        // profile card

        Div profileContainer = new Div();
        profileContainer.setClassName("profile-container");

        Div cardProfile = new Div();
        cardProfile.setClassName("card-profile");

        VerticalLayout cardProfileLayout = new VerticalLayout();
        cardProfileLayout.setClassName("card-profile-layout");

        Image profileCardImage = new Image(LOL_CDN + accountDataArrayList.get(0).avatarId() + ".png", "Profile Picture");
        H1 profileCardName = new H1(accountDataArrayList.get(0).owner());

        cardProfileLayout.add(profileCardImage, profileCardName);
        cardProfile.add(cardProfileLayout);

        // info

        Div infoContainer = new Div();
        infoContainer.setClassName("info-container");

        HorizontalLayout accountsContainer = new HorizontalLayout();
        accountsContainer.setClassName("accounts-container");

        AtomicInteger selectedAccount = new AtomicInteger(0);
        AccountData accountData = accountDataArrayList.get(selectedAccount.get());

        VerticalLayout accountInfoLayout = new VerticalLayout();
        accountInfoLayout.setClassName("account-info-layout");
        
        // rank cards

        H4 currentRankText = new H4("Current Rank");
        currentRankText.setId("heading");

        Div currentRank = new Div();
        currentRank.setClassName("rank-card");
        createRankCard(currentRank, accountData);

        H4 peakRankText = new H4("Peak Rank");
        peakRankText.setId("heading");

        Div peakRank = new Div();
        peakRank.setClassName("rank-card");
        createPeakCard(peakRank, accountData);

        accountDataArrayList.forEach(profile -> {
            Div div = new Div();
            div.setText(profile.accountName());
            div.setClassName("account");
            div.getElement().setAttribute("data-selected", "false");
            div.addClickListener(e -> {

                // unselects all accounts then selects the clicked one
                accountsContainer.getChildren().forEach(child -> {
                    if (child instanceof Div childDiv) {
                        childDiv.getElement().setAttribute("data-selected", "false");
                        childDiv.removeClassName("selected");
                    }
                });
                div.getElement().setAttribute("data-selected", "true");
                div.addClassName("selected");

                selectedAccount.set(accountDataArrayList.indexOf(profile));

                currentRank.removeAll();
                createRankCard(currentRank, accountDataArrayList.get(selectedAccount.get()));

                peakRank.removeAll();
                createPeakCard(peakRank, accountDataArrayList.get(selectedAccount.get()));

            });

            // selects first account by default
            if(accountDataArrayList.indexOf(profile) == 0) {
                div.getElement().setAttribute("data-selected", "true");
                div.addClassName("selected");
            }

            accountsContainer.add(div);
        });

        accountInfoLayout.add(currentRankText, currentRank, peakRankText, peakRank);

        infoContainer.add(accountsContainer, accountInfoLayout);

        profileContainer.add(cardProfile, infoContainer);

        add(profileContainer);
    }

    public void createRankCard(Div div, AccountData accountData) {

        Div background = new Div();
        background.setClassName("background");
        background.getStyle().setBackground("url(frontend/img/" + accountData.rank().tier().getName().toLowerCase() + ".png) center/cover no-repeat");

        Div info = new Div();
        info.setClassName("info");

        String tier = accountData.rank().tier().getName().equals("Unranked") ? accountData.rank().tier().getName() : accountData.rank().tier().getName() + " " + accountData.rank().division().ordinal();
        Paragraph currentTier = new Paragraph(tier);
        currentTier.setId("bold");
        Paragraph currentLP = new Paragraph(accountData.rank().lp() + " LP");
        currentLP.setId("bold");
        Paragraph currentWinrate = new Paragraph(accountData.rank().wins() + "/" + accountData.rank().loses() + " (" + AccountDataUtil.getRoundedWinrateAsInt(accountData.rank().winrate()) + "%)");
        Paragraph currentCreatedAt = new Paragraph(Instant.ofEpochSecond(accountData.rank().createdAt())
                .atZone(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("d/M")));
        currentCreatedAt.setId("time");

        info.add(currentTier, currentLP, currentWinrate, currentCreatedAt);

        div.add(background, info);
    }

    public void createPeakCard(Div div, AccountData accountData) {

        Div background = new Div();
        background.setClassName("background");
        background.getStyle().setBackground("url(frontend/img/" + accountData.peak().tier().getName().toLowerCase() + ".png) center/cover no-repeat");

        Div info = new Div();
        info.setClassName("info");

        String tier = accountData.peak().tier().getName().equals("Unranked") ? accountData.peak().tier().getName() : accountData.peak().tier().getName() + " " + accountData.peak().division().ordinal();
        Paragraph peakTier = new Paragraph(tier);
        peakTier.setId("bold");
        Paragraph peakLP = new Paragraph(accountData.peak().lp() + " LP");
        peakLP.setId("bold");
        Paragraph peakWinrate = new Paragraph(accountData.peak().wins() + "/" + accountData.peak().loses() + " (" + AccountDataUtil.getRoundedWinrateAsInt(accountData.peak().winrate()) + "%)");
        Paragraph peakCreatedAt = new Paragraph(Instant.ofEpochSecond(accountData.peak().createdAt())
                .atZone(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("d/M")));
        peakCreatedAt.setId("time");

        info.add(peakTier, peakLP, peakWinrate, peakCreatedAt);

        div.add(background, info);
    }

}