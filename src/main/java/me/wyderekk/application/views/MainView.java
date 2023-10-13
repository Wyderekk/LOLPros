package me.wyderekk.application.views;

import com.helger.commons.string.util.LevenshteinDistance;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import me.wyderekk.application.data.PlayerList;
import me.wyderekk.application.data.datatypes.AccountData;
import me.wyderekk.application.data.datatypes.enums.SortBy;
import me.wyderekk.application.data.database.SQLite;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;

@PageTitle("Main")
@Route("")
@CssImport("./styles/main-view.css")
public class MainView extends VerticalLayout {

    public MainView() {
        initializeUI();
    }

    private void initializeUI() {
        Div cardContainer = new Div();
        cardContainer.addClassName("card-container");

        Div leaderboardCard = new Div();
        leaderboardCard.addClassName("leaderboard-card");

        Div searchCard = new Div();
        searchCard.addClassName("search-card");

        TextField searchField = new TextField();
        searchField.setPlaceholder("Username");
        searchField.setWidth("50%");

        Div headerCard = new Div();
        headerCard.addClassName("header-card");

        H4 topText = new H4("Top 10");

        Html completeLadderText = new Html("<a href=\"/ladder\">Complete ladder</a>");
        completeLadderText.setClassName("header-complete-ladder");

        Button search = new Button("Search");

        search.addClickShortcut(Key.ENTER);
        search.addClickListener(event -> searchForPlayer(searchField));

        ArrayList<AccountData> leaderboard = SQLite.getSortedAccountData(SortBy.CURRENT_RANK);

        ArrayList<Div> topSummoners = leaderboard.stream()
                .limit(10)
                .map(this::createSummonerDiv)
                .collect(Collectors.toCollection(ArrayList::new));

        searchCard.add(searchField, search);
        headerCard.add(topText, completeLadderText);

        topSummoners.forEach(leaderboardCard::add);

        cardContainer.add(headerCard, searchCard, leaderboardCard);

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        add(cardContainer);
    }

    private Div createSummonerDiv(AccountData accountData) {
        Div summoner = new Div();
        summoner.setClassName("leaderboard-summoner");

        Paragraph name = new Paragraph(accountData.owner());
        name.addClickListener(event -> UI.getCurrent().navigate("/player/" + accountData.owner()));

        Div role = new Div();
        role.setClassName("leaderboard-summoner-role");

        Image roleImage = new Image("frontend/roles/" + accountData.position().getName().toLowerCase() + ".svg", accountData.position().getName());
        role.add(roleImage);

        Div rank = new Div();
        rank.setClassName("leaderboard-summoner-rank");

        Paragraph rankText = new Paragraph(String.valueOf(accountData.rank().lp()));
        rankText.setClassName("leaderboard-summoner-rank-text");

        Image rankImage = new Image("frontend/icons/" + accountData.rank().tier().getName().toLowerCase() + ".svg", String.valueOf(accountData.rank().division().ordinal()));
        rank.add(rankImage, rankText);

        Tooltip tooltip = Tooltip.forComponent(rankImage);
        tooltip.setPosition(Tooltip.TooltipPosition.TOP);
        tooltip.setText(accountData.rank().tier().getName() + " " + accountData.rank().division().ordinal() + " " + accountData.rank().lp() + " LP");

        summoner.add(name, rank, role);

        return summoner;
    }

    private void searchForPlayer(TextField searchField) {
        String searchText = searchField.getValue();
        if (searchText.isEmpty()) {
            searchField.setInvalid(true);
        } else {
            ArrayList<String> players = PlayerList.INSTANCE.getPlayers();
            String closestMatch = findClosestMatch(searchText, players);

            if (closestMatch != null) {
                String encodedSearchText = URLEncoder.encode(closestMatch, StandardCharsets.UTF_8);
                UI.getCurrent().navigate("/player/" + encodedSearchText);
            } else {
                Notification.show("Player not found in streamer database", 3000, Notification.Position.BOTTOM_START);
            }
        }
    }

    private String findClosestMatch(String searchText, ArrayList<String> players) {
        int minDistance = Integer.MAX_VALUE;
        String closestMatch = null;

        for (String player : players) {
            int distance = LevenshteinDistance.getDistance(searchText, player);
            if (distance < minDistance) {
                minDistance = distance;
                closestMatch = player;
            }
        }

        int threshold = 2;
        if (minDistance <= threshold) {
            return closestMatch;
        } else {
            return null;
        }
    }
}