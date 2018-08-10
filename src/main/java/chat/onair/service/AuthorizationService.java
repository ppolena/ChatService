package chat.onair.service;

import chat.onair.response.Error;
import chat.onair.entity.Message;
import chat.onair.repository.ChannelRepository;
import chat.onair.repository.MessageRepository;
import chat.onair.response.Response;
import chat.onair.response.Success;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service("AuthorizationService")
public class AuthorizationService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ChannelService channelService;

    public ResponseEntity authenticate(String accountId,
                                       String channelName,
                                       String token){

        String url = getApiUrl(accountId, channelName);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        if(!token.isEmpty()) {
            headers.add("authorization", token);
        }

        HttpEntity entity = new HttpEntity(headers);

        try{
            HttpEntity<String> response = restTemplate.exchange(url,
                                                                HttpMethod.GET,
                                                                entity,
                                                                String.class);
            return new ResponseEntity<>(response,
                                        new HttpHeaders(),
                                        HttpStatus.OK);
        }
        catch(HttpServerErrorException e){
            return new ResponseEntity<>(new Error(Response.AuthorizationFailed),
                                        new HttpHeaders(),
                                        HttpStatus.UNAUTHORIZED);
        }
    }

    public void handleAuthorizationMessage( WebSocketSession session,
                                            Map<String, String> messageJson,
                                            String channelName) throws IOException {

        String token = messageJson.get("authorization") == null ? "" : messageJson.get("authorization");

        String accountId = messageJson.get("accountId") == null ? "guest" : messageJson.get("accountId");

        ResponseEntity response = authenticate( accountId,
                                                channelName,
                                                token);

        if(response.getStatusCode() == HttpStatus.OK){

            String body = ((HttpEntity<String>)(response.getBody())).getBody();

            Map<String, Object> responseJson = new Gson().fromJson(body, Map.class);

            if(!(boolean)responseJson.get("canRead")){

                channelService.getSessions().get(channelName).remove(session);

                session.sendMessage(
                        new TextMessage(
                                new Gson().toJson(
                                        new Error(Response.NotAuthorizedToRead))));
            }
            else {
                ObjectMapper objectMapper = new ObjectMapper();

                List<Message> validMessages =
                        messageRepository.findByParentAndDateOfCreationGreaterThan(
                                channelRepository.findByChannelName(channelName),
                                (Instant.now().minusSeconds(30*60)).toString());

                for (Message m : validMessages) {
                    session.sendMessage(
                            new TextMessage(
                                    objectMapper.writeValueAsString(m)));
                }

                session.getAttributes().put("authenticated", true);
                session.getAttributes().put("canRead", (boolean)responseJson.get("canRead"));
                session.getAttributes().put("canWrite", (boolean)responseJson.get("canWrite"));

                if(responseJson.get("profilePictureId") != null) {
                    session.getAttributes().put("profilePictureId", responseJson.get("profilePictureId"));
                }

                session.getAttributes().put("accountId", accountId);

                session.sendMessage(
                        new TextMessage(
                                new Gson().toJson(
                                        new Success(Response.AuthorizationSuccessful))));
            }
        }
        else{
            session.sendMessage(
                    new TextMessage(
                            new Gson().toJson(response.getBody())));
        }
    }

    private String getApiUrl(String accountId, String channelName){

        String apiUrl = System.getenv("userAuthenticationApi");
        apiUrl = apiUrl.replace("accountId", accountId);
        apiUrl = apiUrl.replace("channelName", channelName);

        return apiUrl;
    }
}
