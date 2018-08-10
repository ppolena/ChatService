package chat.onair.validator;


import chat.onair.entity.Channel;
import chat.onair.repository.ChannelRepository;
import chat.onair.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Component("beforeSaveChannelValidator")
public class PutPatchChannelValidator implements Validator {

    private final ChannelRepository channelRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean supports(Class<?> clazz) {
        return Channel.class.equals(clazz);
    }


    @Override
    public void validate(Object target, Errors errors) {

        entityManager.detach(target);

        Channel newChannel = (Channel) target;
        Channel oldChannel = channelRepository.findByChannelName(newChannel.getChannelName());

        ValidationUtils.rejectIfEmpty(  errors,
                                        "channelName",
                                        "channelName.empty",
                                        Response.EmptyChannelName);

        ValidationUtils.rejectIfEmpty(  errors,
                                        "status",
                                        "status.empty",
                                        Response.EmptyStatus);

        if(!oldChannel.getChannelName().equals(newChannel.getChannelName())){

            errors.rejectValue( "channelName",
                                "channelName.editNotAllowed",
                                Response.ChannelNameEditNotAllowed);
        }

        if(!oldChannel.getDateOfCreation().equals(newChannel.getDateOfCreation())){

            errors.rejectValue( "dateOfCreation",
                                "dateOfCreation.editNotAllowed",
                                Response.DateOfCreationEditNotAllowed);
        }

        if(oldChannel.getDateOfClosing() == null && newChannel.getDateOfClosing() != null){

            errors.rejectValue( "dateOfClosing",
                                "dateOfClosing.editNotAllowed",
                                Response.DateOfClosingEditNotAllowed);
        }

        if(oldChannel.getStatus().equals(Channel.Status.CLOSED)){

            errors.rejectValue( "status",
                                "status.closed",
                                Response.ChannelStatus + Channel.Status.CLOSED);
        }
        else if(newChannel.getStatus().equals(Channel.Status.CLOSED)){

            newChannel.setDateOfClosing(DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
        }
    }
}