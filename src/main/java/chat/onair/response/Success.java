package chat.onair.response;

import lombok.Data;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
public class Success implements Response {
    private String successId;
    private Type type;
    private String dateOfCreation;
    private String data;

    public Success(String data){
        this.successId = UUID.randomUUID().toString();
        this.type = Response.Type.SUCCESS;
        this.dateOfCreation =  DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        this.data = data;
    }
}
