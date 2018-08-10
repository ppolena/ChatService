package chat.onair.service;

import chat.onair.entity.Channel;
import chat.onair.response.Error;
import chat.onair.entity.Message;
import chat.onair.response.Success;
import chat.onair.response.Response;
import chat.onair.repository.ChannelRepository;
import chat.onair.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service("ChannelService")
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    private Map<String, List<WebSocketSession>> sessions = new HashMap<>();

    public Map<String, List<WebSocketSession>> getSessions() {

        return sessions;
    }

    public void saveAndSendMessage( WebSocketSession session,
                                    String channelName,
                                    String accountId,
                                    String data)
                                    throws IOException{

        if (channelRepository.findByChannelName(channelName).getStatus().equals(Channel.Status.ACTIVE)) {

            ObjectMapper objectMapper = new ObjectMapper();

            Message message = new Message(  accountId,
                                            data,
                                            channelRepository.findByChannelName(channelName));

            messageRepository.save(message);

            for (WebSocketSession webSocketSession : sessions.get(channelName)) {

                webSocketSession.sendMessage(
                        new TextMessage(
                                objectMapper.writeValueAsString(message)));
            }
        }
        else {
            session.sendMessage(
                    new TextMessage(
                            new Gson().toJson(
                                    new Error(  Response.ChannelStatus
                                                + channelRepository.findByChannelName(channelName).getStatus()))));
        }
    }

    public ResponseEntity saveAndSendMessage(
            String channelName,
            String accountId,
            String data){

        if(sessions.get(channelName) != null &&
                channelRepository.findByChannelName(channelName).getStatus().equals(Channel.Status.ACTIVE)){

            Message message = new Message(  accountId,
                                            data,
                                            channelRepository.findByChannelName(channelName));

            try {
                ObjectMapper objectMapper = new ObjectMapper();

                for (WebSocketSession webSocketSession : sessions.get(channelName)) {
                    webSocketSession.sendMessage(
                            new TextMessage(objectMapper.writeValueAsString(message)));
                }

                return new ResponseEntity<>(new Success(Response.MessageCreationSuccessful),
                                            new HttpHeaders(),
                                            HttpStatus.CREATED);
            }
            catch(IOException e){
                return new ResponseEntity<>(new Error(Response.ObjectMapperIoException),
                                            new HttpHeaders(),
                                            HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else if(sessions.get(channelName) == null &&
                channelRepository.findByChannelName(channelName).getStatus().equals(Channel.Status.ACTIVE)){

            return new ResponseEntity<>(new Error(Response.SessionNotFound),
                                        new HttpHeaders(),
                                        HttpStatus.NOT_FOUND);
        }
        else if(sessions.get(channelName) == null &&
                !(channelRepository.findByChannelName(channelName).getStatus().equals(Channel.Status.ACTIVE))){

            return new ResponseEntity<>(new Error(  Response.NoSessionChannelStatus
                                                    + channelRepository.findByChannelName(channelName).getStatus()),
                                        new HttpHeaders(),
                                        HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(new Error(  Response.ChannelStatus
                                                    + channelRepository.findByChannelName(channelName).getStatus()),
                                        new HttpHeaders(),
                                        HttpStatus.FORBIDDEN);
        }
    }

    public void addSession(WebSocketSession session){

        if(sessions.containsKey(session.getAttributes().get("channelName"))){
            sessions.get(session.getAttributes().get("channelName")).add(session);
        }
        else{
            List<WebSocketSession> sessionList = new ArrayList<>();
            sessionList.add(session);
            sessions.put((String)session.getAttributes().get("channelName"), sessionList);
        }
    }
}
