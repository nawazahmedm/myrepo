import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class JSONSchemaValidatorTest {

    // Helper method to create a temporary schema file
    private File createTempSchemaFile(String schemaContent) throws IOException {
        File tempFile = File.createTempFile("schema", ".json");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(schemaContent);
        }
        return tempFile;
    }

    @Test
    public void testValidJson() throws IOException {
        // Sample valid JSON message
        String validJson = "{ \"name\": \"John Doe\", \"age\": 30, \"email\": \"john.doe@example.com\" }";

        // Sample JSON schema
        String schemaContent = "{\n" +
                "  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"name\": { \"type\": \"string\" },\n" +
                "    \"age\": { \"type\": \"integer\" },\n" +
                "    \"email\": { \"type\": \"string\" }\n" +
                "  },\n" +
                "  \"required\": [\"name\", \"age\", \"email\"]\n" +
                "}";

        // Create temporary schema file
        File schemaFile = createTempSchemaFile(schemaContent);

        // Create an instance of JSONSchemaValidator and validate the JSON message
        JSONSchemaValidator validator = new JSONSchemaValidator();
        Set<String> errors = validator.validateJSONMessageAgainstSchema(validJson, schemaFile);

        // Assert that there are no validation errors
        assertTrue(errors.isEmpty(), "Expected no validation errors for valid JSON.");

        // Delete the temporary schema file
        assertTrue(schemaFile.delete(), "Failed to delete temporary schema file.");
    }

    @Test
    public void testInvalidJsonMissingField() throws IOException {
        // Sample JSON message missing the 'email' field
        String invalidJson = "{ \"name\": \"John Doe\", \"age\": 30 }";

        // Sample JSON schema
        String schemaContent = "{\n" +
                "  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"name\": { \"type\": \"string\" },\n" +
                "    \"age\": { \"type\": \"integer\" },\n" +
                "    \"email\": { \"type\": \"string\" }\n" +
                "  },\n" +
                "  \"required\": [\"name\", \"age\", \"email\"]\n" +
                "}";

        // Create temporary schema file
        File schemaFile = createTempSchemaFile(schemaContent);

        // Create an instance of JSONSchemaValidator and validate the JSON message
        JSONSchemaValidator validator = new JSONSchemaValidator();
        Set<String> errors = validator.validateJSONMessageAgainstSchema(invalidJson, schemaFile);

        // Assert that there are validation errors
        assertFalse(errors.isEmpty(), "Expected validation errors for JSON missing required 'email' field.");
        // Assert that the error message indicates the missing 'email' field
        assertTrue(errors.stream().anyMatch(msg -> msg.contains("email")), "Expected error message to mention missing 'email' field.");

        // Delete the temporary schema file
        assertTrue(schemaFile.delete(), "Failed to delete temporary schema file.");
    }

    @Test
    public void testInvalidJsonIncorrectType() throws IOException {
        // Sample JSON message with 'age' as a string instead of an integer
        String invalidJson = "{ \"name\": \"John Doe\", \"age\": \"thirty\", \"email\": \"john.doe@example.com\" }";

        // Sample JSON schema
        String schemaContent = "{\n" +
                "  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"name\": { \"type\": \"string\" },\n" +
                "    \"age\": { \"type\": \"integer\" },\n" +
                "    \"email\": { \"type\": \"string\" }\n" +
                "  },\n" +
                "  \"required\": [\"name\", \"age\", \"email\"]\n" +
                "}";

        // Create temporary schema file
        File schemaFile = createTempSchemaFile(schemaContent);

        // Create an instance of JSONSchemaValidator and validate the JSON message
        JSONSchemaValidator validator = new JSONSchemaValidator();
        Set<String> errors = validator.validateJSONMessageAgainstSchema(invalidJson, schemaFile);

        // Assert that there are validation errors
        assertFalse(errors.isEmpty(), "Expected validation errors for JSON with incorrect 'age' type.");
        // Assert that the error message indicates the incorrect type for 'age'
        assertTrue(errors.stream().anyMatch(msg -> msg.contains("age") && msg.contains("integer")), "Expected error message to mention 'age' field and expected 'integer' type.");

        // Delete the temporary schema file
        assertTrue(schemaFile.delete(), "Failed to delete temporary schema file.");
    }

    @Test
    public void testInvalidJsonAdditionalProperties() throws IOException {
        // Sample JSON message with an additional 'address' field not defined in the schema
        String invalidJson = "{ \"name\": \"John Doe\", \"age\": 30, \"email\": \"john.doe@example.com\", \"address\": \"123 Main St\" }";

        // Sample JSON schema with 'additionalProperties' set to false
        String schemaContent = "{\n" +
                "  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"name\": { \"type\": \"string\" },\n" +
                "    \"age\": { \"type\": \"integer\" },\n" +
                "    \"email\": { \"type\": \"string\" }\n" +
                "  },\n" +
                "  \"required\": [\"name\", \"age\", \"email\"],\n" +
                "  \"additionalProperties\": false\n" +
                "}";

        // Create temporary schema file
        File schemaFile = createTempSchemaFile(schemaContent);

        // Create an instance of JSONSchemaValidator and validate the JSON message
        JSONSchemaValidator validator = new JSONSchemaValidator();
        Set<String> errors = validator.validateJSONMessageAgainstSchema(invalidJson, schema
::contentReference[oaicite:0]{index=0}
 
