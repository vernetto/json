package org.pierre.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * https://www.baeldung.com/jackson-object-mapper-tutorial
 */
public class TestSerialization {
    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        list(objectMapper);

        String jsonCarArray  = "[{ \"color\" : \"Black\", \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"FIAT\" }]";
        List<Car> carsRead = objectMapper.readValue(jsonCarArray , new TypeReference<List<Car>>(){});
        System.out.println(carsRead);

        String json = "{ \"color\" : \"Black\", \"type\" : \"FIAT\" }";
        JsonNode jsonNode = objectMapper.readTree(json);
        String color = jsonNode.get("color").asText();
        System.out.println(color);

        textMap(objectMapper);

        unknownParameters(objectMapper);
        customSerializer();
        customDeserializer();
    }

    private static void list(ObjectMapper objectMapper) throws IOException {
        Car car1 = new Car("yellow", "renault");
        Car car2 = new Car("red", "ferrari");
        List<Car> cars = Arrays.asList(car1, car2);
        objectMapper.writeValue(new File("target/car.json"), cars);
        String dtoAsString = objectMapper.writeValueAsString(cars);
        System.out.println(dtoAsString);
    }

    private static void textMap(ObjectMapper objectMapper) throws com.fasterxml.jackson.core.JsonProcessingException {
        String json3 = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";
        Map<String, Object> map = objectMapper.readValue(json3, new TypeReference<Map<String,Object>>(){});
        System.out.println(map);
    }

    private static void unknownParameters(ObjectMapper objectMapper) throws com.fasterxml.jackson.core.JsonProcessingException {
        String jsonStringExtra = "{ \"color\" : \"Black\", \"type\" : \"Fiat\", \"year\" : \"1970\" }";
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Car car3 = objectMapper.readValue(jsonStringExtra, Car.class);
        System.out.println(car3);
        JsonNode jsonNodeRoot = objectMapper.readTree(jsonStringExtra);
        JsonNode jsonNodeYear = jsonNodeRoot.get("year");
        String year = jsonNodeYear.asText();
        System.out.println(year);
    }

    private static void customSerializer() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module =
                new SimpleModule("CustomCarSerializer", new Version(1, 0, 0, null, null, null));
        module.addSerializer(Car.class, new CustomCarSerializer());
        mapper.registerModule(module);
        Car car = new Car("yellow", "renault");
        String carJson = mapper.writeValueAsString(car);
        System.out.println(carJson);
    }

    private static void customDeserializer() throws JsonProcessingException {
        String json = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module =
                new SimpleModule("CustomCarDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Car.class, new CustomCarDeserializer());
        mapper.registerModule(module);
        Car car = mapper.readValue(json, Car.class);
        System.out.println(car);

    }
}
