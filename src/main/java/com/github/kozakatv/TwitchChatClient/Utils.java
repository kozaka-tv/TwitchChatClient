package com.github.kozakatv.TwitchChatClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Utils {

    private static final String CONFIG_FILE_NAME = "config.yaml";

    public static Config readConfig(ObjectMapper mapper) {
        try {
            return mapper.readValue(new File(CONFIG_FILE_NAME), Config.class);
        } catch (IOException e) {
            throw new RuntimeException("config file can not be found! configFile: " + CONFIG_FILE_NAME, e);
        }
    }
}
