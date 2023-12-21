package me.wyderekk.application.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.charts.model.Responsive;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import me.wyderekk.application.data.datatypes.AccountData;
import me.wyderekk.application.data.datatypes.enums.Position;
import me.wyderekk.application.data.datatypes.enums.SortBy;
import me.wyderekk.application.data.database.SQLite;
import me.wyderekk.application.data.util.AccountDataUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PageTitle("Ladder")
@Route("ladder")
@CssImport("./styles/ladder-view.css")
public class LadderView extends VerticalLayout {

    private static final String LOL_CDN = "https://ddragon.leagueoflegends.com/cdn/13.21.1/img/profileicon/";

    public LadderView() {
        initializeUI();
    }

    private void initializeUI() {
        VirtualList<AccountData> accountDataVirtualList = createAccountDataVirtualList();

        // Create the UI components for sorting and filtering
        ComboBox<String> sortByComboBox = createSortByComboBox();
        ComboBox<String> positionComboBox = createPositionComboBox();

        // Add value change listeners to update the list based on selections
        addValueChangeListeners(positionComboBox, sortByComboBox, accountDataVirtualList);

        Div card = createHeaderLayout(sortByComboBox, positionComboBox);

        // Setup the main container with the list
        Div container = new Div(accountDataVirtualList);
        container.setClassName("list-container");

        // Configure the main layout properties
        setHeightFull();
        setWidthFull();
        setAlignItems(Alignment.CENTER);

        add(card, container);
    }

    private VirtualList<AccountData> createAccountDataVirtualList() {
        ArrayList<AccountData> accountDataArrayList = SQLite.getSortedAccountData(SortBy.CURRENT_RANK);
        VirtualList<AccountData> accountDataVirtualList = new VirtualList<>();
        accountDataVirtualList.setDataProvider(DataProvider.ofCollection(accountDataArrayList));
        accountDataVirtualList.setRenderer(accountDataComponentRenderer);
        accountDataVirtualList.setHeightFull();
        return accountDataVirtualList;
    }

    private ComboBox<String> createSortByComboBox() {
        ComboBox<String> sortByComboBox = new ComboBox<>();
        List<String> sortByList = Arrays.stream(SortBy.values())
                .map(SortBy::getName)
                .collect(Collectors.toList());
        sortByComboBox.setItems(sortByList);
        sortByComboBox.setValue(SortBy.CURRENT_RANK.getName());
        sortByComboBox.getElement().getStyle().set("margin-right", "10px");
        return sortByComboBox;
    }

    private ComboBox<String> createPositionComboBox() {
        ComboBox<String> positionComboBox = new ComboBox<>();
        List<String> positionList = Stream.concat(Stream.of("All"), Arrays.stream(Position.values())
                        .map(Position::getName))
                .collect(Collectors.toList());
        positionComboBox.setItems(positionList);
        positionComboBox.setValue("All");
        positionComboBox.getElement().getStyle().set("margin-left", "10px");
        return positionComboBox;
    }

    private void addValueChangeListeners(ComboBox<String> positionComboBox, ComboBox<String> sortByComboBox,
                                         VirtualList<AccountData> accountDataVirtualList) {
        positionComboBox.addValueChangeListener(
                e -> updateAccountDataList(positionComboBox, sortByComboBox, accountDataVirtualList));
        sortByComboBox.addValueChangeListener(
                e -> updateAccountDataList(positionComboBox, sortByComboBox, accountDataVirtualList));
    }

    private Div createHeaderLayout(ComboBox<String> sortByComboBox, ComboBox<String> positionComboBox) {
        Div card = new Div();
        card.setClassName("header");
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setClassName("header-layout");
        headerLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        headerLayout.add(sortByComboBox, new Span(" "), positionComboBox);
        card.add(headerLayout);
        return card;
    }

    private ComponentRenderer<HorizontalLayout, AccountData> accountDataComponentRenderer = new ComponentRenderer<>(
            accountData -> {
                HorizontalLayout cardLayout = new HorizontalLayout();
                cardLayout.setClassName("ladder-entry");
                cardLayout.setMargin(true);

                Avatar avatar = createAvatar(accountData);

                VerticalLayout infoLayout = createInfoLayout(accountData);

                cardLayout.add(avatar, infoLayout);
                return cardLayout;
            });

    private Avatar createAvatar(AccountData accountData) {
        Avatar avatar = new Avatar(accountData.summonerNames().getFirst().name(),
                LOL_CDN + accountData.avatarId() + ".png");
        avatar.setHeight("64px");
        avatar.setWidth("64px");
        avatar.getStyle().set("margin-left", "0.5em");
        avatar.getStyle().set("margin-top", "0.5em");
        return avatar;
    }

    private VerticalLayout createInfoLayout(AccountData accountData) {
        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setSpacing(false);
        infoLayout.setPadding(false);
        infoLayout.getElement().appendChild(
                ElementFactory.createStrong(String.format("%s (%s)", accountData.summonerNames().getFirst().name(), accountData.owner())));

        infoLayout.add(new Div(
                new Text(accountData.position().getName())));

        if (accountData.rank().winrate() != 0) {
            infoLayout.add(new Div(
                    new Text(AccountDataUtil.getRoundedWinrate(accountData.rank().winrate()) + " %")));
        }

        VerticalLayout contactLayout = createContactLayout(accountData);

        infoLayout.add(new Details("More Information", contactLayout));

        return infoLayout;
    }

    private VerticalLayout createContactLayout(AccountData accountData) {
        VerticalLayout contactLayout = new VerticalLayout();
        contactLayout.setSpacing(false);
        contactLayout.setPadding(false);
        contactLayout.add(new Div(new Text("Current - " + AccountDataUtil.parse(accountData.rank().tier(), accountData.rank().division(), accountData.rank().lp()))));
        contactLayout.add(new Div(new Text("Peak - " + AccountDataUtil.parse(accountData.peak().tier(), accountData.peak().division(), accountData.peak().lp()))));
        return contactLayout;
    }

    private void updateAccountDataList(ComboBox<String> positionComboBox, ComboBox<String> sortByComboBox, VirtualList<AccountData> accountDataVirtualList) {
        String selectedPosition = positionComboBox.getValue();
        String selectedSortBy = sortByComboBox.getValue();

        if (selectedPosition.equals("All")) {
            accountDataVirtualList.setItems(SQLite.getSortedAccountData(SortBy.getByName(selectedSortBy)));
        } else {
            accountDataVirtualList.setItems(SQLite.getSortedAccountData(SortBy.getByName(selectedSortBy)).stream()
                    .filter(accountData -> accountData.position().getName().equals(selectedPosition))
                    .toList());
        }
    }
}