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
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.kozakatv.TwitchChatClient.Utils.isException;
import static com.github.kozakatv.TwitchChatClient.Utils.readConfig;

@Slf4j
@SpringBootApplication
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

    public static void event() {
        eventManager.autoDiscovery();

        eventManager.onEvent(ChannelMessageEvent.class, event -> {
            if (isException(event.getMessage(), exceptions)) {
                return;
            }

            log.info("[" + event.getChannel().getName() + "] " + event.getUser().getName() + ": " + event.getMessage());

            AtomicReference<String> subBadge = getSubBadge(event);

            if (!event.getMessage().startsWith("!jam") && !event.getMessage().startsWith("pepeJAM")) {
                GUIConsole.append(subBadge + event.getUser().getName() + ": " + event.getMessage() + "\n");
            }

        });
    }

    @NotNull
    private static AtomicReference<String> getSubBadge(ChannelMessageEvent event) {

        SubscriptionList subs = getSubList();

        if (subs == null) {
            return new AtomicReference<>("");
        }

        AtomicReference<String> subBadge = new AtomicReference<>("");
        subs.getSubscriptions().forEach(subscription -> {
            log.info("Subscriber: " + subscription);
            if (subscription.getUserId().equals(event.getUser().getId())) {
                subBadge.set("[T" + subTier(subscription) / 1000 + "] ");
            }
        });
        return subBadge;
    }

    private static SubscriptionList getSubList() {
        try {
            // TODO
//            return twitchClient.getHelix().getSubscriptions(config.getAccessTokenSubscriptions(), config.getBroadcasterId(), config.getAfter(), config.getBefore(), config.getLimit()).execute();
//            return twitchClient.getHelix().getSubscriptionsByUser(config.getAccessTokenSubscriptions(), config.getBroadcasterId(), Arrays.asList("kozaka")).execute();
            return null;
        } catch (Exception e) {
            log.error("Problem getting subs! msg: " + e.getMessage(), e);
        }
        return null;
    }

    private static int subTier(Subscription subscription) {
        return Integer.parseInt(subscription.getTier());
    }

}
