package re.api.domain;

import java.util.ArrayList;
import java.util.List;

public class Result<T> {
    private final ArrayList<String> messages = new ArrayList<>();
    private ResultType type = ResultType.SUCCESS;
    private T payload;

    public ResultType getType() {
        return type;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public void addMessage(ResultType type, String message) {
        this.type = type;
        messages.add(message);
    }

    public boolean isSuccess() {
        return (messages.size() == 0) || (type == ResultType.SUCCESS);
    }
}