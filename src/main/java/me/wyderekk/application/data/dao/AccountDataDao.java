package me.wyderekk.application.data.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.wyderekk.application.data.datatypes.AccountData;

public class AccountDataDao {

    public static String toJsonString(AccountData accountData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(accountData);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static AccountData fromJsonString(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonString, AccountData.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }


}
