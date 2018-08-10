package chat.onair.repository;

import chat.onair.entity.Channel;
import chat.onair.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "messages", path = "messages")
public interface MessageRepository extends JpaRepository<Message, String>{

    @RestResource(path = "find-by-message-id")
    Message findByMessageId(@Param("id") String id);

    @RestResource(path = "find-by-account-id")
    List<Message> findByAccountId(@Param("id") String id);

    @RestResource(path = "list-messages")
    List<Message> findByParentAndDateOfCreationGreaterThan( @Param("parent")Channel parent,
                                                            @Param("history") String history);
}
