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
import java.util.stream.Stream;

@PageTitle("Ladder")
@Route("ladder")
@CssImport("./styles/ladder-view.css")
public class LadderView extends VerticalLayout {

    private final VirtualList<AccountData> accountDataVirtualList;
    private static final String LOL_CDN = "https://ddragon.leagueoflegends.com/cdn/13.19.1/img/profileicon/";

    public LadderView() {

        ArrayList<AccountData> accountDataArrayList = SQLite.getSortedAccountData(SortBy.CURRENT_RANK);

        accountDataVirtualList = new VirtualList<>();
        accountDataVirtualList.setDataProvider(DataProvider.ofCollection(accountDataArrayList));
        accountDataVirtualList.setRenderer(accountDataComponentRenderer);
        accountDataVirtualList.setHeightFull();

        initializeUI();
    }

    private void initializeUI() {
        setHeightFull();
        setWidthFull();
        setAlignItems(Alignment.CENTER);

        Div card = createHeaderCard();

        Div container = new Div(accountDataVirtualList);
        container.setClassName("list-container");

        add(card, container);
    }

    private Div createHeaderCard() {
        Div card = new Div();
        card.setClassName("header");

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setClassName("header-layout");
        headerLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        ComboBox<String> sortByComboBox = new ComboBox<>();
        List<String> sortByList = new ArrayList<>(Arrays.stream(SortBy.values()).map(SortBy::getName).toList());

        sortByComboBox.setItems(sortByList);
        sortByComboBox.setValue(SortBy.CURRENT_RANK.getName());
        sortByComboBox.getElement().getStyle().set("margin-right", "10px");

        ComboBox<String> positionComboBox = new ComboBox<>();
        List<String> positionList = new ArrayList<>(Stream.concat(Stream.of("All"), Arrays.stream(Position.values()).map(Position::getName)).toList());
        positionComboBox.setItems(positionList);
        positionComboBox.setValue("All");
        positionComboBox.getElement().getStyle().set("margin-left", "10px");

        positionComboBox.addValueChangeListener(valueChanged -> updateAccountDataList(positionComboBox, sortByComboBox));
        sortByComboBox.addValueChangeListener(valueChanged -> updateAccountDataList(positionComboBox, sortByComboBox));


        headerLayout.add(sortByComboBox, new Span(" "), positionComboBox);

        card.add(headerLayout);

        return card;
    }

    private final ComponentRenderer<HorizontalLayout, AccountData> accountDataComponentRenderer = new ComponentRenderer<>(
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
        Avatar avatar = new Avatar(accountData.accountName(),
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
                ElementFactory.createStrong(accountData.owner()));

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

    private void updateAccountDataList(ComboBox<String> positionComboBox, ComboBox<String> sortByComboBox) {
        String selectedPosition = positionComboBox.getValue();
        String selectedSortBy = sortByComboBox.getValue();

        if (selectedPosition.equals("All")) {
            accountDataVirtualList.setItems(SQLite.getSortedAccountData(SortBy.getByName(selectedSortBy)));
        } else {
            accountDataVirtualList.setItems(SQLite.getSortedAccountData(SortBy.getByName(selectedSortBy)).stream()
                    .filter(accountData -> accountData.position().getName().equals(selectedPosition)).toList());
        }
    }
}