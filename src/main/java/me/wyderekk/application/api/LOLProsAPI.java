package me.wyderekk.application.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import me.wyderekk.application.data.datatypes.AccountData;
import me.wyderekk.application.data.datatypes.Rank;
import me.wyderekk.application.data.datatypes.SummonerName;
import me.wyderekk.application.data.datatypes.enums.Division;
import me.wyderekk.application.data.datatypes.enums.Position;
import me.wyderekk.application.data.datatypes.enums.Tier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class LOLProsAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(LOLProsAPI.class);
    private static final OkHttpClient client = new OkHttpClient();
    private static final String LOLPROS_API_URL = "https://api.lolpros.gg/es/profiles/";

    public static String getPlayer(String name) {
        String url = LOLPROS_API_URL + name;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                String responseBodyString = responseBody.string();
                responseBody.close();
                return responseBodyString;
            } else {
                LOGGER.error("[ {} ] Request failed with HTTP error code: {}", name, response.code());
            }
        } catch (Exception e) {
            LOGGER.error("[ {} ] Request failed with exception: {}", name, e.getMessage());
        }
        return null;
    }

    public static ArrayList<AccountData> getAccountData(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<AccountData> accountDataArrayList = new ArrayList<>();

        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            String owner = jsonNode.path("name").asText();

            JsonNode leaguePlayerNode = jsonNode.path("league_player");
            Position position = Position.getPosition(leaguePlayerNode.path("position").asText());

            JsonNode accountsNode = leaguePlayerNode.path("accounts");

            for (int i = 0; i < accountsNode.size() ; i++) {

                JsonNode summonerNames = accountsNode.path(i).path("summoner_names");
                ArrayList<SummonerName> summonerNameArrayList = new ArrayList<>();
                for (int j = 0; j < summonerNames.size(); j++) {
                    summonerNameArrayList.add(new SummonerName(
                            summonerNames.path(j).path("name").asText(),
                            ZonedDateTime.parse(summonerNames.path(j).path("created_at").asText()).toEpochSecond()
                    ));
                }

                int avatarId = accountsNode.path(i).path("profile_icon_id").asInt();
                String id = accountsNode.path(i).path("uuid").asText();

                JsonNode rankNode = accountsNode.path(i).path("rank");

                Rank rank = getRank(rankNode);

                JsonNode peakNode = accountsNode.path(i).path("peak");

                Rank peak = getRank(peakNode);

                AccountData accountData = new AccountData(owner, id, summonerNameArrayList, position, avatarId, rank, peak);
                accountDataArrayList.add(accountData);
                LOGGER.info("[ {} ] Account data fetched successfully.", owner);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to get account data", e);
        }
        return accountDataArrayList;
    }

    private static Rank getRank(JsonNode rankNode) {
        return new Rank(
                Tier.getByName(rankNode.path("tier").asText()),
                Division.getByValue(rankNode.path("division").asInt()),
                rankNode.path("league_points").asInt(),
                rankNode.path("wins").asInt(),
                rankNode.path("losses").asInt(),
                rankNode.path("wins").asInt() != 0 && rankNode.path("losses").asInt() != 0 ?
                        (double) rankNode.path("wins").asInt() / (rankNode.path("wins").asInt() + rankNode.path("losses").asInt()) : 0,
                ZonedDateTime.parse(rankNode.path("created_at").asText()).toEpochSecond()
        );
    }

}
