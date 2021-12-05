package com.github.kozakatv.TwitchChatClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.json.JSONArray;

import java.io.File;

public class Utils {

    private static final String CONFIG_FILE_NAME = "config.yaml";

    @SneakyThrows
    public static Config readConfig(ObjectMapper mapper) {
        return mapper.readValue(new File(CONFIG_FILE_NAME), Config.class);
    }

    public static boolean isException(@NonNull String msg, JSONArray exceptions) {
        for (Object e : exceptions) {
            if (msg.startsWith(String.valueOf(e))) {
                return true;
            }
        }
        return false;
    }


}
