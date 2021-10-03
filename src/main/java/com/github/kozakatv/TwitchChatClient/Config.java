package com.github.kozakatv.TwitchChatClient;

import lombok.Data;

@Data
public class Config {
    private String oauthToken;
    private String channelName;

    private String accessTokenSubscriptions;
    private String broadcasterId;
    private String after;
    private String before;
    private Integer limit;
}
