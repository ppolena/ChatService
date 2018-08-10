package chat.onair.validator;

import chat.onair.entity.Message;
import chat.onair.repository.MessageRepository;
import chat.onair.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RequiredArgsConstructor
@Component("beforeSaveMessageValidator")
public class PutPatchMessageValidator implements Validator {

    private final MessageRepository messageRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean supports(Class<?> clazz) {
        return Message.class.equals(clazz);
    }


    @Override
    public void validate(Object target, Errors errors) {

        entityManager.detach(target);

        Message newMessage = (Message) target;
        Message oldMessage = messageRepository.findByMessageId(newMessage.getMessageId());

        ValidationUtils.rejectIfEmpty(  errors,
                                        "data",
                                        "data.empty",
                                        Response.EmptyData);

        if(!oldMessage.getMessageId().equals(newMessage.getMessageId())){

            errors.rejectValue( "messageId",
                                "messageId.editNotAllowed",
                                Response.MessageIdEditNotAllowed);
        }

        if(!oldMessage.getDateOfCreation().equals(newMessage.getDateOfCreation())){

            errors.rejectValue( "dateOfCreation",
                                "dateOfCreation.editNotAllowed",
                                Response.DateOfCreationEditNotAllowed);
        }

        if(!oldMessage.getAccountId().equals(newMessage.getAccountId())){

            errors.rejectValue( "accountId",
                                "accountId.editNotAllowed",
                                Response.AccountIdEditNotAllowed);
        }
    }
}