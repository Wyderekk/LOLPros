package me.wyderekk.application.views;

import com.vaadin.flow.component.Component;
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
import me.wyderekk.application.data.datatypes.Rank;
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
    private static final String RANK_CARD_CLASS = "rank-card";
    private static final String BOLD_CLASS = "bold";
    private static final String INFO_CLASS = "info";

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        String decodedParameter = URLDecoder.decode(parameter, StandardCharsets.UTF_8);
        List<AccountData> dataArrayList = SQLite.getAccountData(decodedParameter);
        AccountData firstAccount = dataArrayList.getFirst();

        // profile card

        Div profileContainer = createProfileContainer(firstAccount);

        // info

        Div infoContainer = new Div();
        infoContainer.setClassName("info-container");

        HorizontalLayout accountsContainer = new HorizontalLayout();
        accountsContainer.setClassName("accounts-container");

        AtomicInteger selectedAccount = new AtomicInteger(0);
        AccountData accountData = dataArrayList.get(selectedAccount.get());

        VerticalLayout accountInfoLayout = new VerticalLayout();
        accountInfoLayout.setClassName("account-info-layout");

        // rank cards

        H4 currentRankText = new H4("Current Rank");
        currentRankText.setId("heading");

        Div currentRank = new Div();
        currentRank.setClassName("rank-card");
        createRankCard(currentRank, accountData, false);

        H4 peakRankText = new H4("Peak Rank");
        peakRankText.setId("heading");

        Div peakRank = new Div();
        peakRank.setClassName("rank-card");
        createRankCard(peakRank, accountData, true);

        H4 lastSummonerNamesText = new H4("Last Summoner Names");
        lastSummonerNamesText.setId("heading");

        // last summonerNames

        VirtualList<SummonerName> summonerNameHistory = createSummonerNamesList(accountData, dataArrayList, accountsContainer, selectedAccount, currentRank, peakRank);

        accountInfoLayout.add(currentRankText, currentRank, peakRankText, peakRank, lastSummonerNamesText, summonerNameHistory);

        infoContainer.add(accountsContainer, accountInfoLayout);

        profileContainer.add(infoContainer);

        add(profileContainer);
    }

    private Div createProfileContainer(AccountData firstAccount) {
        Div profileContainer = new Div();
        profileContainer.setClassName("profile-container");

        VerticalLayout profileLayout = createLayout();
        profileContainer.add(profileLayout);

        Div cardProfile = createCardProfile(firstAccount);
        Div rankingsCard = createRankingsCard(firstAccount);

        profileLayout.add(cardProfile, rankingsCard);

        return profileContainer;
    }
    private VerticalLayout createLayout() {
        VerticalLayout profileLayout = new VerticalLayout();
        profileLayout.setWidth("auto");
        profileLayout.setClassName("profile-layout");
        return profileLayout;
    }

    private Div createRankingsCard(AccountData firstAccount) {
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

        // Get all accounts with the same position as the first account
        List<AccountData> positionList = SQLite.getSortedAccountData(SortBy.CURRENT_RANK).stream()
                .filter(accountData -> accountData.position().equals(firstAccount.position()))
                .toList();

        // Get ranking position of the first account in the list
        positionList.stream()
                .filter(accountData -> accountData.owner().equals(firstAccount.owner()))
                .findFirst()
                .ifPresent(accountData -> positionRankingNumber.setText("#" + (positionList.indexOf(accountData) + 1)));

        positionRanking.add(positionRankingText, positionRankingNumber);

        rankingsCardLayout.add(globalRanking, positionRanking);
        rankingsCard.add(rankingsCardLayout);

        return rankingsCard;
    }

    private VirtualList<SummonerName> createSummonerNamesList(AccountData accountData, List<AccountData> dataArrayList, HorizontalLayout accountsContainer, AtomicInteger selectedAccount, Div currentRank, Div peakRank) {
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

        dataArrayList.forEach(profile -> {
            Div div = new Div();
            div.setText(profile.summonerNames().getFirst().name());
            div.setClassName("account");
            div.getElement().setAttribute("data-selected", "false");
            div.addClickListener(e -> {
                accountsContainer.getChildren().forEach(child -> {
                    if (child instanceof Div childDiv) {
                        childDiv.getElement().setAttribute("data-selected", "false");
                        childDiv.removeClassName("selected");
                    }
                });
                div.getElement().setAttribute("data-selected", "true");
                div.addClassName("selected");

                selectedAccount.set(dataArrayList.indexOf(profile));

                // Update each rank card with the new data
                currentRank.removeAll();
                createRankCard(currentRank, dataArrayList.get(selectedAccount.get()), false);

                peakRank.removeAll();
                createRankCard(peakRank, dataArrayList.get(selectedAccount.get()), true);

                lastSummonerNames.setItems(dataArrayList.get(selectedAccount.get()).summonerNames());
            });

            // selects first account by default
            if(dataArrayList.indexOf(profile) == 0) {
                div.getElement().setAttribute("data-selected", "true");
                div.addClassName("selected");
            }

            accountsContainer.add(div);
        });

        return lastSummonerNames;
    }


    private Div createBadgesDiv(AccountData firstAccount) {
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

        return badges;
    }

    private Div createCardProfile(AccountData firstAccount) {
        Div cardProfile = new Div();
        cardProfile.setClassName("card-profile");

        VerticalLayout cardProfileLayout = new VerticalLayout();
        cardProfileLayout.setClassName("card-profile-layout");

        Avatar profileCardImage = new Avatar("Profile Picture", LOL_CDN + firstAccount.avatarId() + ".png");
        profileCardImage.setClassName("profile-card-avatar");

        H1 profileCardName = new H1(firstAccount.owner());

        Div badges = createBadgesDiv(firstAccount);

        Div roleKeypoint = new Div();
        roleKeypoint.setClassName("role-keypoint");

        Image roleImage = new Image("frontend/roles/" + firstAccount.position().getName().toLowerCase() + ".svg", firstAccount.position().getName());
        roleImage.setClassName("profile-role-image");

        Span roleText = new Span();
        roleText.setText(firstAccount.position().getName());

        roleKeypoint.add(roleImage, roleText);
        cardProfileLayout.add(profileCardImage, profileCardName, badges, roleKeypoint);
        cardProfile.add(cardProfileLayout);

        return cardProfile;
    }


    private void createRankCard(Div div, AccountData accountData, boolean isPeak) {
        Rank rank = isPeak ? accountData.peak() : accountData.rank();

        div.setClassName(RANK_CARD_CLASS);
        div.add(createBackgroundDiv(rank.tier().getName().toLowerCase()));
        div.add(createRankInfoDiv(rank));
    }

    private Div createBackgroundDiv(String tierName) {
        Div background = new Div();
        background.setClassName("background");
        background.getStyle().setBackground("url(frontend/img/" + tierName + ".webp) center/cover no-repeat");
        return background;
    }

    private Div createRankInfoDiv(Rank rank) {
        Div info = new Div();
        info.setClassName(INFO_CLASS);

        String tierText = rank.tier().getName().equals("Unranked")
                ? rank.tier().getName()
                : rank.tier().getName() + " " + rank.division().ordinal();

        info.add(createBoldParagraph(tierText),
                createBoldParagraph(rank.lp() + " LP"),
                new Paragraph(rank.wins() + "/" + rank.loses() + " (" + AccountDataUtil.getRoundedWinrateAsInt(rank.winrate()) + "%)"),
                createDateParagraph(rank.createdAt()));

        return info;
    }

    private Paragraph createBoldParagraph(String text) {
        Paragraph paragraph = new Paragraph(text);
        paragraph.setId(BOLD_CLASS);
        return paragraph;
    }

    private Paragraph createDateParagraph(long epochSecond) {
        Paragraph paragraph = new Paragraph(Instant.ofEpochSecond(epochSecond)
                .atZone(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("d/M")));
        paragraph.setId("time");
        return paragraph;
    }

}