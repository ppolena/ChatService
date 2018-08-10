package chat.onair.validator;

import chat.onair.entity.Channel;
import chat.onair.repository.ChannelRepository;
import chat.onair.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component("beforeCreateChannelValidator")
public class PostChannelValidator implements Validator {

    private final ChannelRepository channelRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Channel.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Channel channel = (Channel) target;

        ValidationUtils.rejectIfEmpty(  errors,
                                        "channelName",
                                        "channelName.empty",
                                        Response.EmptyChannelName);

        ValidationUtils.rejectIfEmpty(  errors,
                                        "status",
                                        "status.empty",
                                        Response.EmptyStatus);

        if(channelRepository.findByChannelName(channel.getChannelName()) != null){

            errors.rejectValue( "channelName",
                                "channelName.exists",
                                Response.ChannelAlreadyExists);
        }
    }
}