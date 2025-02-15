package com.example.jsonvalidation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.ValidationMessage;
import com.networknt.schema.SpecVersion;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class JSONMessageValidationAgainstSchema {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static boolean validateJson(String jsonMessage, String schemaPath) {
        try {
            // Load JSON Schema
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            JsonSchema schema = factory.getSchema(new File(schemaPath));

            // Load JSON Message
            JsonNode jsonNode = objectMapper.readTree(jsonMessage);

            // Validate JSON
            Set<ValidationMessage> errors = schema.validate(jsonNode);

            // Print Errors (if any)
            if (!errors.isEmpty()) {
                System.out.println("JSON Validation Failed:");
                errors.forEach(error -> System.out.println(error.getMessage()));
                return false;
            }

            System.out.println("JSON Validation Passed!");
            return true;

        } catch (IOException e) {
            System.err.println("Error reading JSON or Schema: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        // Example JSON message (replace with actual JSON)
        String jsonMessage = "{ \"securityIssuingAccountIdentifier\": \"ABC123\", \"securityFunderAccount\": 12345 }";
        
        // Path to JSON Schema file
        String schemaPath = "path/to/schema.json";

        // Validate JSON against schema
        validateJson(jsonMessage, schemaPath);
    }
}
