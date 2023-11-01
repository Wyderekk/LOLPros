package me.wyderekk.application.data.datatypes.comparators;

import me.wyderekk.application.data.datatypes.AccountData;
import me.wyderekk.application.data.datatypes.enums.SortBy;
import java.util.Comparator;

public class AccountDataComparator {

    public static Comparator<AccountData> getComparator(SortBy sortBy) {
        return switch(sortBy.name()) {
            case "CURRENT_RANK" -> sortByCurrentRank();
            case "PEAK" -> sortByPeak();
            case "WINRATE" -> sortByWinrate();
            case "GAMES" -> sortByGames();
            default -> throw new IllegalStateException("Unexpected value: " + sortBy.name());
        };
    }

    private static Comparator<AccountData> sortByCurrentRank() {
        return (o1, o2) -> {
            int tierComparison = Integer.compare(o2.rank().tier().ordinal(), o1.rank().tier().ordinal());
            if (tierComparison != 0) {
                return tierComparison;
            }
            int divisionComparison = Integer.compare(o1.rank().division().ordinal(), o2.rank().division().ordinal());
            if (divisionComparison != 0) {
                return divisionComparison;
            }
            return Integer.compare(o2.rank().lp(), o1.rank().lp());
        };
    }

    private static Comparator<AccountData> sortByPeak() {
        return (o1, o2) -> {
            int tierComparison = Integer.compare(o2.peak().tier().ordinal(), o1.peak().tier().ordinal());
            if (tierComparison != 0) {
                return tierComparison;
            }
            int divisionComparison = Integer.compare(o1.peak().division().ordinal(), o2.peak().division().ordinal());
            if (divisionComparison != 0) {
                return divisionComparison;
            }
            return Integer.compare(o2.peak().lp(), o1.peak().lp());
        };
    }

    private static Comparator<AccountData> sortByWinrate() {
        return (o1, o2) -> Double.compare(o2.rank().winrate(), o1.rank().winrate());
    }

    private static Comparator<AccountData> sortByGames() {
        return (o1, o2) -> Integer.compare(o2.rank().wins() + o2.rank().loses(), o1.rank().wins() + o1.rank().loses());
    }
}
