package dat250.msd.FeedApp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import dat250.msd.FeedApp.model.Poll;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class Analytics {
    private final FeedAppService feedAppService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public Analytics(FeedAppService feedAppService) {
        this.feedAppService = feedAppService;
    }


    /**
     * When a poll is started it sends a REST POST request to Dweet.io
     * <a href="https://dweet.io/get/latest/dweet/for/feed-app-polls">Latest dweet</a>
     */
    public void startPoll(Long pollId) {
        Poll poll = feedAppService.getPollRepository().getPollById(pollId);

        createDweet(poll);
    }


    /**
     * When a poll is expired it sends a POST request to dweet.io & publishes a message to the MQTT messaging system.
     */
    public void endPoll(Long pollId) {
        Poll poll = feedAppService.getPollRepository().getPollById(pollId);

        createDweet(poll);
        MqttMessage(poll);
    }

    // Publish finished poll to MQTT messaging system
    private void MqttMessage(Poll poll) {
        // Create MQTT client
        // TODO extract variables (host + port) to application.properties?
        Mqtt5Client client = MqttClient.builder()
                .serverHost("localhost")
                .serverPort(1883)
                .useMqttVersion5()
                .build();
        try{
            // Connect to MQTT server
            client.toBlocking().connect();

            // Create message
            String pollJson = getPollInfoJson(poll);
            Mqtt5Publish publish = Mqtt5Publish.builder()
                    .topic("poll")
                    .payload(pollJson.getBytes())
                    .qos(MqttQos.EXACTLY_ONCE)
                    .build();

            // Publish message
            client.toBlocking().publish(publish);
            client.toBlocking().disconnect();
            System.out.println("Published message!");
        }
        catch (Exception e){
            System.out.println("MQTT server refused connection. Check that the host and port is correct and that the server is running.");
        }
    }

    // Create a new dweet
    public void createDweet(Poll poll) {
        String url = "https://dweet.io/dweet/for/";
        String dweetThingName = "feed-app-polls";

        String pollInfo = getPollInfo(poll);

        String response = restTemplate.getForObject(url + dweetThingName + pollInfo, String.class);
        System.out.println(response);
    }

    // Convert poll to get request path params for dweet.io
    private String getPollInfo(Poll poll) {
        return "?topicName=" + poll.getTopic().getName() +
                "&roomCode=" + poll.getRoomCode() +
                "&startDate=" + poll.getStartDate() +
                "&endDate=" + poll.getEndDate() +
                "&isPrivate=" + poll.isPrivate() +
                "&totalVotes=" + feedAppService.getVoteRepository().countByPoll(poll);
    }

    // Convert poll to
    private String getPollInfoJson(Poll poll) {
        try {
            return objectMapper.writeValueAsString(poll);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
