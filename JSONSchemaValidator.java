import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

public class JSONSchemaValidator {

    /**
     * Validates a JSON message against a JSON schema.
     *
     * @param jsonMessage The JSON message as a String.
     * @param schemaFile  The JSON schema as a File.
     */
    public void validateJSONMessageAgainstSchema(String jsonMessage, File schemaFile) {
        try (FileInputStream schemaStream = new FileInputStream(schemaFile)) {
            // Initialize ObjectMapper for JSON processing
            ObjectMapper objectMapper = new ObjectMapper();

            // Parse the JSON schema
            JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
            JsonSchema schema = schemaFactory.getSchema(schemaStream);

            // Parse the JSON message
            JsonNode jsonNode = objectMapper.readTree(jsonMessage);

            // Validate the JSON message against the schema
            Set<ValidationMessage> validationMessages = schema.validate(jsonNode);

            // Output validation results
            if (validationMessages.isEmpty()) {
                System.out.println("JSON is valid.");
            } else {
                System.out.println("JSON validation failed with the following errors:");
                validationMessages.forEach(vm -> System.out.println(vm.getMessage()));
            }
        } catch (IOException e) {
            System.err.println("Error reading schema file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Sample JSON message
        String jsonMessage = "{ \"name\": \"John Doe\", \"age\": 30, \"email\": \"john.doe@example.com\" }";

        // Specify the path to the JSON schema file
        File schemaFile = new File("path/to/your/schema.json");

        // Create an instance of JSONSchemaValidator and validate the JSON message
        JSONSchemaValidator validator = new JSONSchemaValidator();
        validator.validateJSONMessageAgainstSchema(jsonMessage, schemaFile);
    }
}
