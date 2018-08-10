package chat.onair.entity;

import lombok.Data;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@Entity
@Table(name = "channel")
public class Channel {

    @Id
    @NotNull
    @Column(updatable=false)
    private String channelName;

    @NotNull
    private Status status;

    @NotNull
    @Column(updatable=false)
    private String dateOfCreation = DateTimeFormatter.ISO_INSTANT.format(Instant.now());

    private String dateOfClosing;

    @RestResource(path = "list-of-messages")
    @OneToMany(fetch = FetchType.EAGER, mappedBy="parent", cascade = CascadeType.MERGE)
    private List<Message> listOfMessages;

    public enum Status{
        DRAFT, ACTIVE, CLOSED
    }
}
