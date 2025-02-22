package com.javalearnings.securitydemo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class CustomJsonValidator {

    public static void main(String[] args) throws IOException {
        String jsonMessage = "{\n" +
                "  \"name\": \"John Doe\",\n" +
                "  \"age\": 30,\n" +
                "  \"email\": \"john.doe@example.com\",\n" +
                "  \"address\": {\n" +
                "    \"street\": \"123 Main St\",\n" +
                "    \"city\": \"Somewhere\",\n" +
                "    \"zipcode\": \"12345\"\n" +
                "  },\n" +
                "  \"isActive\": true,\n" +
                "  \"createdDate\": \"2023-04-01T10:00:00Z\"\n" +
                "}";

        File jsonSchemaFile = new ClassPathResource("example-schema.json").getFile();
        List<String> errors = validateJsonMessageAgainstSchema(jsonMessage, jsonSchemaFile);
        errors.forEach(System.out::println);
    }

    public static Schema loadSchema(File schemaFile) throws IOException {
        try (FileInputStream schemaStream = new FileInputStream(schemaFile)) {
            JSONObject jsonSchema = new JSONObject(new JSONTokener(schemaStream));
            return SchemaLoader.load(jsonSchema);
        }
    }

    public static List<String> validateJsonMessageAgainstSchema(String jsonMessage, File jsonSchemaFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Schema schema = loadSchema(jsonSchemaFile);

        JsonNode jsonNode = objectMapper.readTree(jsonMessage);
        return validateJson(schema, jsonNode);
    }

    public static List<String> validateJson(Schema schema, JsonNode jsonNode) {
        try {
            schema.validate(new JSONObject(jsonNode.toString()));
            return List.of();
        } catch (org.everit.json.schema.ValidationException e) {
            return e.getAllMessages().stream()
                    .map(message -> "Validation error: " + message)
                    .collect(Collectors.toList());
        }
    }
}
