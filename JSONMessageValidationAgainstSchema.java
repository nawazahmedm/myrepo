package com.example.jsonvalidation;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class JSONMessageValidationAgainstSchema {

    public static boolean validateJson(String jsonMessage, String schemaPath) {
        try (InputStream inputStream = new FileInputStream(new File(schemaPath))) {
            // Load JSON Schema
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            Schema schema = SchemaLoader.load(rawSchema);

            // Convert JSON message to JSONObject
            JSONObject json = new JSONObject(jsonMessage);

            // Validate JSON
            schema.validate(json);

            System.out.println("✅ JSON Validation Passed!");
            return true;

        } catch (org.everit.json.schema.ValidationException e) {
            System.out.println("❌ JSON Validation Failed!");
            System.out.println(e.getAllMessages()); // Print all validation errors
            return false;
        } catch (Exception e) {
            System.err.println("⚠️ Error: " + e.getMessage());
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
