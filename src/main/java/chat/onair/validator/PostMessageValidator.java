package chat.onair.validator;

import chat.onair.response.Error;
import chat.onair.entity.Message;
import chat.onair.repository.ChannelRepository;
import chat.onair.response.Response;
import chat.onair.service.AuthorizationService;
import chat.onair.service.ChannelService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Map;

@RequiredArgsConstructor
@Component("beforeCreateMessageValidator")
public class PostMessageValidator implements Validator{

    private final ChannelRepository channelRepository;
    private final ChannelService channelService;
    private final AuthorizationService authorizationService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Message.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors){

        Message message = (Message) target;

        ValidationUtils.rejectIfEmpty(  errors,
                                        "accountId",
                                        "accountId.empty",
                                        Response.EmptyAccountId);

        ValidationUtils.rejectIfEmpty(  errors,
                                        "data",
                                        "data.empty",
                                        Response.EmptyData);

        if(message.getChannelName() == null ||
                channelRepository.findByChannelName(message.getChannelName()) == null){

            errors.rejectValue( "channelName",
                                "channelName.notFound",
                                Response.ChannelNotFound);
        }
        else {
            message.setParent(channelRepository.findByChannelName(message.getChannelName()));

            ResponseEntity response = authorizationService.authenticate(message.getAccountId(),
                                                                        message.getParent().getChannelName(),
                                                                        message.getAuthorization());

            if (response.getStatusCode() == HttpStatus.OK) {

                String body = ((HttpEntity<String>)(response.getBody())).getBody();

                Map<String, Object> responseJson = new Gson().fromJson(body, Map.class);

                if (!(boolean) responseJson.get("canWrite")) {

                    errors.rejectValue( "accountId",
                                        "accountId.cannotWrite",
                                        Response.NotAuthorizedToWrite);
                }
                else{

                    response = channelService.saveAndSendMessage(   message.getParent().getChannelName(),
                                                                    message.getAccountId(),
                                                                    message.getData());

                    if(response.getStatusCode() != HttpStatus.CREATED){

                        errors.rejectValue( "accountId",
                                            "accountId.authorizationFailed",
                                            ((Error)response.getBody()).getData());
                    }
                }
            }
        }
    }
}
