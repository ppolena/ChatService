package chat.onair.entity;

import chat.onair.response.Response;
import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "message")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message implements Response {

    @Id
    @NotNull
    @Column(updatable=false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String messageId;

    @Transient
    private Type type;

    @Transient
    private String authorization;

    @NotNull
    @Column(updatable=false)
    private String dateOfCreation = DateTimeFormatter.ISO_INSTANT.format(Instant.now());

    private String data;

    @NotNull
    @Column(updatable=false)
    private String accountId;

    @Transient
    private String channelName;

    @NotNull
    @JsonIgnore
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "channel_name")
    @JsonIdentityReference(alwaysAsId = true)
    @JoinColumn(name = "parent_id", nullable = false)
    @ManyToOne
    private Channel parent;

    public Message(){}

    public Message(String accountId, String data, Channel parent){
        this.type = Response.Type.MESSAGE;
        this.accountId = accountId;
        this.data = data;
        this.parent = parent;
    }
}
