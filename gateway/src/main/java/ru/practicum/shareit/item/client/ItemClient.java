package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    private Map<String, Object> parameters;

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build());
    }

    public ResponseEntity<Object> getAllItems(
            long userId,
            Integer from,
            Integer size) {
        parameters = Map.of(
                "from", from,
                "size", size);
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemById(
            long userId,
            Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemSearch(
            String text,
            Integer from,
            Integer size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Переданы некорректные значения from и/или size");
        }
        return get("/search?text=" + text + "&from=" + from);
    }

    public ResponseEntity<Object> createItem(
            ItemDto itemDto,
            long userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> patchItem(
            ItemDto itemDto,
            Long userId,
            Long itemId) {
        return patch("/" + itemId, userId, itemDto);
    }

    public void removeItemById(Long itemId) {
        delete("/" + itemId);
    }
}
