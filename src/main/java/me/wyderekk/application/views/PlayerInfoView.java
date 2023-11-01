package me.wyderekk.application.views;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import me.wyderekk.application.data.database.SQLite;
import me.wyderekk.application.data.datatypes.AccountData;
import me.wyderekk.application.data.datatypes.SummonerName;
import me.wyderekk.application.data.datatypes.enums.Badge;
import me.wyderekk.application.data.datatypes.enums.SortBy;
import me.wyderekk.application.data.util.AccountDataUtil;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Route("player")
@RouteAlias("player/*")
@PageTitle("Player Info")
@CssImport("./styles/player-info.css")
public class PlayerInfoView extends HorizontalLayout implements HasUrlParameter<String> {

    private static final String LOL_CDN = "https://ddragon.leagueoflegends.com/cdn/13.21.1/img/profileicon/";

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        String decodedParameter = URLDecoder.decode(parameter, StandardCharsets.UTF_8);
        ArrayList<AccountData> accountDataArrayList = SQLite.getAccountData(decodedParameter);

        // profile card

        Div profileContainer = new Div();
        profileContainer.setClassName("profile-container");

        VerticalLayout profileLayout = new VerticalLayout();
        profileLayout.setWidth("auto");
        profileLayout.setClassName("profile-layout");

        // main card
        Div cardProfile = new Div();
        cardProfile.setClassName("card-profile");

        VerticalLayout cardProfileLayout = new VerticalLayout();
        cardProfileLayout.setClassName("card-profile-layout");

        AccountData firstAccount = accountDataArrayList.getFirst();

        Avatar profileCardImage = new Avatar("Profile Picture", LOL_CDN + firstAccount.avatarId() + ".png");
        profileCardImage.setClassName("profile-card-avatar");

        H1 profileCardName = new H1(firstAccount.owner());

        Div badges = new Div();
        badges.setClassName("badges");

        ArrayList<Badge> userBadges = SQLite.getBadges(firstAccount.owner().toLowerCase());

        if(!userBadges.isEmpty()) {
            userBadges.forEach(badge -> {

                Image badgeImage = new Image("frontend/img/badges/" + badge.name().toLowerCase() + ".svg", badge.name());
                badgeImage.setClassName("badge");

                Tooltip tooltip = Tooltip.forComponent(badgeImage);
                tooltip.setText(badge.name().substring(0, 1).toUpperCase() + badge.name().substring(1).toLowerCase());
                tooltip.setPosition(Tooltip.TooltipPosition.TOP);

                badges.add(badgeImage);
            });
        }

        Div roleKeypoint = new Div();
        roleKeypoint.setClassName("role-keypoint");

        Image roleImage = new Image("frontend/roles/" + firstAccount.position().getName().toLowerCase() + ".svg", firstAccount.position().getName());
        roleImage.setClassName("profile-role-image");

        Span roleText = new Span();
        roleText.setText(firstAccount.position().getName());

        roleKeypoint.add(roleImage, roleText);
        cardProfileLayout.add(profileCardImage, profileCardName, badges, roleKeypoint);
        cardProfile.add(cardProfileLayout);

        // rankings card

        Div rankingsCard = new Div();
        rankingsCard.setClassName("rankings-card");

        VerticalLayout rankingsCardLayout = new VerticalLayout();
        rankingsCardLayout.setClassName("rankings-card-layout");

        Div globalRanking = new Div();
        globalRanking.setClassName("ranking-keypoint");

        Span globalRankingText = new Span();
        globalRankingText.setText("Global");
        globalRankingText.setId("bold");

        Span globalRankingNumber = new Span();
        globalRankingNumber.setId("bold");

        SQLite.getSortedAccountData(SortBy.CURRENT_RANK).stream()
                .filter(accountData -> accountData.owner().equals(firstAccount.owner()))
                .findFirst()
                .ifPresent(accountData -> globalRankingNumber.setText("#" + (SQLite.getSortedAccountData(SortBy.CURRENT_RANK).indexOf(accountData) + 1)));

        globalRanking.add(globalRankingText, globalRankingNumber);

        Div positionRanking = new Div();
        positionRanking.setClassName("ranking-keypoint");

        Span positionRankingText = new Span();
        positionRankingText.setText("Position");
        positionRankingText.setId("bold");

        Span positionRankingNumber = new Span();
        positionRankingNumber.setId("bold");

        // gets all accounts with the same position as the first account
        List<AccountData> positionList = SQLite.getSortedAccountData(SortBy.CURRENT_RANK).stream()
                .filter(accountData -> accountData.position().equals(firstAccount.position()))
                .toList();

        // gets the position of the first account in the list
        positionList.stream()
                .filter(accountData -> accountData.owner().equals(firstAccount.owner()))
                .findFirst()
                .ifPresent(accountData -> positionRankingNumber.setText("#" + (positionList.indexOf(accountData) + 1)));

        positionRanking.add(positionRankingText, positionRankingNumber);

        rankingsCardLayout.add(globalRanking, positionRanking);
        rankingsCard.add(rankingsCardLayout);

        profileLayout.add(cardProfile, rankingsCard);

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

        H4 lastSummonerNamesText = new H4("Last Summoner Names");
        lastSummonerNamesText.setId("heading");

        VirtualList<SummonerName> lastSummonerNames = new VirtualList<>();
        lastSummonerNames.setClassName("last-summoner-names");
        lastSummonerNames.setItems(accountData.summonerNames());
        lastSummonerNames.setRenderer(new ComponentRenderer<>(summonerName -> {
            HorizontalLayout summonerNameCard = new HorizontalLayout();
            summonerNameCard.setClassName("summoner-name-card");

            Span name = new Span(summonerName.name());
            name.setId("bold");

            Span createdAt = new Span(Instant.ofEpochSecond(summonerName.createdAt())
                    .atZone(ZoneId.of("UTC"))
                    .format(DateTimeFormatter.ofPattern("d/M")));
            createdAt.setId("time");

            summonerNameCard.add(name, createdAt);
            return summonerNameCard;
        }));

        accountDataArrayList.forEach(profile -> {
            Div div = new Div();
            div.setText(profile.summonerNames().getFirst().name());
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

                // updates rank cards and last summoner names
                currentRank.removeAll();
                createRankCard(currentRank, accountDataArrayList.get(selectedAccount.get()));

                peakRank.removeAll();
                createPeakCard(peakRank, accountDataArrayList.get(selectedAccount.get()));

                lastSummonerNames.setItems(accountDataArrayList.get(selectedAccount.get()).summonerNames());
            });

            // selects first account by default
            if(accountDataArrayList.indexOf(profile) == 0) {
                div.getElement().setAttribute("data-selected", "true");
                div.addClassName("selected");
            }

            accountsContainer.add(div);
        });

        accountInfoLayout.add(currentRankText, currentRank, peakRankText, peakRank, lastSummonerNamesText, lastSummonerNames);

        infoContainer.add(accountsContainer, accountInfoLayout);

        profileContainer.add(profileLayout, infoContainer);

        add(profileContainer);
    }

    public void createRankCard(Div div, AccountData accountData) {

        Div background = new Div();
        background.setClassName("background");
        background.getStyle().setBackground("url(frontend/img/" + accountData.rank().tier().getName().toLowerCase() + ".webp) center/cover no-repeat");

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
        background.getStyle().setBackground("url(frontend/img/" + accountData.peak().tier().getName().toLowerCase() + ".webp) center/cover no-repeat");

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