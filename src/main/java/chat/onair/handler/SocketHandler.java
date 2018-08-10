package chat.onair.handler;

import chat.onair.response.Error;
import chat.onair.response.Response;
import chat.onair.repository.ChannelRepository;
import chat.onair.service.AuthorizationService;
import chat.onair.service.ChannelService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;

@Component
@Configurable
@RequiredArgsConstructor
public class SocketHandler extends TextWebSocketHandler {

    private final AuthorizationService authorizationService;
    private final ChannelRepository channelRepository;
    private final ChannelService channelService;

    @Override
    public void handleTextMessage(  WebSocketSession session,
                                    TextMessage message)
                                    throws IOException{

        Map<String, String> messageJson = new Gson().fromJson(message.getPayload(), Map.class);

        String channelName = (String) session.getAttributes().get("channelName");

        if(messageJson.get("type").equals("authorization") &&
                !(boolean)session.getAttributes().get("authenticated")){

            authorizationService.handleAuthorizationMessage(session,
                                                            messageJson,
                                                            channelName);
        }
        else {
            if((boolean)session.getAttributes().get("authenticated") &&
                    (boolean)session.getAttributes().get("canWrite")) {

                channelService.saveAndSendMessage(  session,
                                                    channelName,
                                                    (String) session.getAttributes().get("accountId"),
                                                    messageJson.get("data"));
            }
            else{
                session.sendMessage(
                        new TextMessage(
                                new Gson().toJson(
                                        new Error(Response.NotAuthorizedToWrite))));
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException{

        String channelName = (String) session.getAttributes().get("channelName");

        if(channelRepository.findByChannelName(channelName) != null) {
            channelService.addSession(session);
        }
        else{
            session.sendMessage(
                    new TextMessage(
                            new Gson().toJson(
                                    new Error(Response.ChannelNotFound))));
        }
    }
}