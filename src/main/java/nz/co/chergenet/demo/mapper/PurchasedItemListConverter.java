package nz.co.chergenet.demo.mapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import nz.co.chergenet.demo.model.PurchasedItemDto;

import java.io.IOException;
import java.util.List;

@Converter
public class PurchasedItemListConverter implements AttributeConverter<List<PurchasedItemDto>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<PurchasedItemDto> purchasedItemList) {
        try {
            return objectMapper.writeValueAsString(purchasedItemList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert purchased item list to JSON", e);
        }
    }

    @Override
    public List<PurchasedItemDto> convertToEntityAttribute(String purchasedItemListJson) {
        try {
            return objectMapper.readValue(purchasedItemListJson, new TypeReference<List<PurchasedItemDto>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON to purchased item list", e);
        }
    }
}
