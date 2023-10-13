package me.wyderekk.application.views;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

    private Div cardContainer;
    private Div searchCard;
    private Div leaderboardCard;
    private Div headerCard;
    private TextField searchField;
    private Button search;
    private H4 topText;
    private Html completeLadderText;
    private ArrayList<Div> topSummoners = new ArrayList<>();

    public MainView() {
        initializeUI();
        setupEventListeners();
        buildLayout();
    }

    private void initializeUI() {
        cardContainer = new Div();
        cardContainer.addClassName("card-container");

        leaderboardCard = new Div();
        leaderboardCard.addClassName("leaderboard-card");

        searchCard = new Div();
        searchCard.addClassName("search-card");

        searchField = new TextField();
        searchField.setPlaceholder("Username");
        searchField.setWidth("50%");

        headerCard = new Div();
        headerCard.addClassName("header-card");

        topText = new H4("Top 10");

        completeLadderText = new Html("<a href=\"/ladder\">Complete ladder</a>");
        completeLadderText.setClassName("header-complete-ladder");

        search = new Button("Search");

        search.addClickShortcut(Key.ENTER);

        ArrayList<AccountData> leaderboard = SQLite.getSortedAccountData(SortBy.CURRENT_RANK);

        topSummoners = leaderboard.stream()
                .limit(10)
                .map(this::createSummonerDiv)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void setupEventListeners() {
        search.addClickListener(event -> searchForPlayer());
    }

    private void buildLayout() {
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
        name.setClassName("leaderboard-summoner-name");
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

        summoner.add(name, rank, role);

        return summoner;
    }

    private void searchForPlayer() {
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
            int distance = calculateLevenshteinDistance(searchText, player);
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

    private int calculateLevenshteinDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        int[] costs = new int[s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else if (j > 0) {
                    int newValue = costs[j - 1];
                    if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                        newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                    }
                    costs[j - 1] = lastValue;
                    lastValue = newValue;
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }
}