package chat.onair.response;

public interface Response{
    String ChannelCreationSuccessful = "CHANNEL_CREATION_SUCCESSFUL";
    String MessageCreationSuccessful = "MESSAGE_CREATION_SUCCESSFUL";
    String ChannelCreationFailed = "CHANNEL_CREATION_FAILED";
    String ChannelAlreadyExists = "CHANNEL_ALREADY_EXISTS";
    String ChannelStatusChanged = "CHANNEL_STATUS_CHANGED";
    String ChannelNotFound = "CHANNEL_NOT_FOUND";
    String SessionNotFound = "SESSION_NOT_FOUND";
    String ChannelStatus = "CHANNEL_";
    String NoSessionChannelStatus = "NO_SESSION_CHANNEL_";
    String AuthorizationSuccessful = "AUTHORIZATION_SUCCESSFUL";
    String AuthorizationFailed = "AUTHORIZATION_FAILED";
    String NotAuthorizedToRead = "NOT_AUTHORIZED_TO_READ";
    String NotAuthorizedToWrite = "NOT_AUTHORIZED_TO_WRITE";
    String EmptyChannelName = "EMPTY_CHANNEL_NAME";
    String EmptyStatus = "EMPTY_STATUS";
    String EmptyAccountId = "EMPTY_ACCOUNT_ID";
    String EmptyParent = "EMPTY_PARENT";
    String EmptyData = "EMPTY_DATA";
    String NoSuchStatus = "NO_SUCH_STATUS";
    String ChannelNameEditNotAllowed = "CHANNEL_NAME_EDIT_NOT_ALLOWED";
    String MessageIdEditNotAllowed = "MESSAGE_ID_EDIT_NOT_ALLOWED";
    String AccountIdEditNotAllowed = "ACCOUNT_ID_EDIT_NOT_ALLOWED";
    String ParentEditNotAllowed = "PARENT_EDIT_NOT_ALLOWED";
    String DateOfCreationEditNotAllowed = "DATE_OF_CREATION_EDIT_NOT_ALLOWED";
    String DateOfClosingEditNotAllowed = "DATE_OF_CLOSING_EDIT_NOT_ALLOWED";
    String ObjectMapperIoException = "OBJECT_MAPPER_IO_EXCEPTION";

    enum Type{
        MESSAGE, ERROR, SUCCESS
    }
}
