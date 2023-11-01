package me.wyderekk.application.api;

import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.common.Region;
import com.merakianalytics.orianna.types.core.league.LeagueEntry;
import com.merakianalytics.orianna.types.core.match.Match;
import com.merakianalytics.orianna.types.core.match.MatchHistory;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import me.wyderekk.application.data.datatypes.AccountData;
import me.wyderekk.application.data.datatypes.Rank;
import me.wyderekk.application.data.datatypes.SummonerName;
import me.wyderekk.application.data.datatypes.enums.Division;
import me.wyderekk.application.data.datatypes.enums.Position;
import me.wyderekk.application.data.datatypes.enums.Tier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;
import java.util.*;

public class RiotAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(RiotAPI.class);

    static {
        Orianna.setRiotAPIKey(System.getenv("RIOT_API_KEY"));
        Orianna.setDefaultRegion(Region.EUROPE_WEST);
    }

    public static AccountData getAccountData(String owner, String query, boolean usePuuid) {
        Summoner summoner = usePuuid ? Orianna.summonerWithPuuid(query).get() : Orianna.summonerNamed(query).get();
        LeagueEntry leagueEntry = summoner.getLeaguePosition(Queue.RANKED_SOLO);

        Rank rank = new Rank(
                Tier.valueOf(leagueEntry.getTier().name()),
                Division.valueOf(leagueEntry.getDivision().name()),
                leagueEntry.getLeaguePoints(),
                leagueEntry.getWins(),
                leagueEntry.getLosses(),
                leagueEntry.getWins() + leagueEntry.getLosses(),
                Instant.now().getEpochSecond());

        String summonerName = usePuuid ? summoner.getName() : query;

        LOGGER.info("[ {} ] Account data fetched successfully.", owner);
        return new AccountData(
                owner,
                summoner.getPuuid(),
                new ArrayList<>(List.of(new SummonerName(summonerName, summoner.getUpdated().getMillis()))),
                getMostPlayedRole(summoner),
                summoner.getProfileIcon().getId(),
                rank,
                null);
    }

    private static Position getMostPlayedRole(Summoner summoner) {
        MatchHistory matchHistory = summoner.matchHistory().withQueues(Queue.RANKED_SOLO).get();

        Map<Position, Integer> roleCount = new HashMap<>();

        for(Match match : matchHistory) {
            Position role = Position.getPosition(match.getParticipants().find(summoner).getLane().name());
            if (role != null) {
                roleCount.put(role, roleCount.getOrDefault(role, 0) + 1);
            }
        }

        Position mostPlayedRole = null;
        int maxCount = -1;
        for(Map.Entry<Position, Integer> entry : roleCount.entrySet()) {
            if(entry.getValue() > maxCount) {
                mostPlayedRole = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        return mostPlayedRole;
    }

}
