package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    private Map<String, Object> parameters;

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build());
    }

    public ResponseEntity<Object> getAllItemRequest(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItemRequestById(
            long userId,
            long requestId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getItemRequestOtherUsers(
            long userId,
            Integer from,
            Integer size) {
        parameters = Map.of(
                "from", from,
                "size", size);
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> createItemRequest(
            Long userId,
            ItemRequestDto itemRequestDto) {
        return post("", userId, itemRequestDto);
    }
}
