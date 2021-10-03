package com.github.kozakatv.TwitchChatClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.helix.domain.Subscription;
import com.github.twitch4j.helix.domain.SubscriptionList;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;

import java.util.concurrent.atomic.AtomicReference;

import static com.github.kozakatv.TwitchChatClient.Utils.readConfig;

@Slf4j
public class TwitchChatClient {

    static EventManager eventManager = new EventManager();
    static TwitchClient twitchClient;
    static JSONArray exceptions = new JSONArray("[]");

    private static final Config config;

    static {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        config = readConfig(mapper);
    }

    public static void main(String[] args) {
        event();
        twitchClient = TwitchClientBuilder.builder()
                .withEventManager(eventManager)
                .withDefaultAuthToken(new OAuth2Credential("twitch", config.getOauthToken()))
                .withEnableHelix(true)
                .withEnableChat(true)
                .build();
        twitchClient.getChat().joinChannel(config.getChannelName());
        Thread guitar = new Thread(() -> {
            GUIConsole.run("KOZAKA Chat Reader", 500, 900, "icon.png");
            GUIConsole.append("Chat Reader Loaded!\n");
        });
        guitar.start();
    }

    public static boolean isException(@NonNull String msg) {
        for (Object v : exceptions) {
            if (msg.startsWith(String.valueOf(v)))
                return true;
        }

        return false;
    }

    public static void event() {
        eventManager.autoDiscovery();

        eventManager.onEvent(ChannelMessageEvent.class, event -> {
            if (isException(event.getMessage())) return;

            log.info("[" + event.getChannel().getName() + "] " + event.getUser().getName() + ": " + event.getMessage());

            SubscriptionList subs = twitchClient.getHelix().getSubscriptions(config.getAuthToken(), config.getBroadcasterId(), config.getAfter(), config.getBefore(), config.getLimit()).execute();
            AtomicReference<String> subBadge = new AtomicReference<>("");
            subs.getSubscriptions().forEach(subscription -> {
                log.info("Subscriber: " + subscription);
                if (subscription.getUserId().equals(event.getUser().getId())) {
                    subBadge.set("[Tier-" + subTier(subscription) / 1000 + "] ");
                }
            });

            GUIConsole.append(subBadge + event.getUser().getName() + ": " + event.getMessage() + "\n");

        });
    }

    private static int subTier(Subscription subscription) {
        return Integer.parseInt(subscription.getTier());
    }

}
