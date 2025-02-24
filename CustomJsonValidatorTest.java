import com.javalearnings.securitydemo.utils.CustomJsonValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.util.List;

class CustomJsonValidatorTest {

    private File jsonSchemaFile;

    @BeforeEach
    void setup() throws Exception {
        jsonSchemaFile = new ClassPathResource("example-schema.json").getFile();
        jsonSchemaFile2 = new ClassPathResource("example-schema2.json").getFile();
    }

    @Test
    void testValidJsonMessage() throws Exception {
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

        List<String> errors = CustomJsonValidator.validateJsonMessageAgainstSchema(jsonMessage, jsonSchemaFile);
        Assertions.assertTrue(errors.isEmpty(), "Expected no validation errors");
    }

    @Test
    void testInvalidJsonMessageCombinationOfErrors() throws Exception {
        String jsonMessage = "{\n" +
                "  \"age\": -5,\n" +
                "  \"email\": \"john.doeexample.com\",\n" +
                "  \"address\": {\n" +
                "    \"street\": \"123 Main St\",\n" +
                "    \"city\": \"Somewhere\",\n" +
                "    \"zipcode\": \"12\"\n" +
                "  },\n" +
                "  \"isActive\": true,\n" +
                "  \"createdDate\": \"2023/04/01\"\n" +
                "}";

        List<String> errors = CustomJsonValidator.validateJsonMessageAgainstSchema(jsonMessage, jsonSchemaFile);
        Assertions.assertFalse(errors.isEmpty(), "Expected validation errors");

        Assertions.assertTrue(errors.contains("Validation error: #: required key [name] not found"));
        Assertions.assertTrue(errors.contains("Validation error: #/age: -5.0 is not higher or equal to 0"));
        Assertions.assertTrue(errors.contains("Validation error: #/email: [john.doeexample.com] is not a valid email address"));
        Assertions.assertTrue(errors.contains("Validation error: #/address/zipcode: string [12] does not match pattern ^[0-9]{5}$"));
        Assertions.assertTrue(errors.contains("Validation error: #/createdDate: [2023/04/01] is not a valid date-time. Expected [yyyy-MM-dd'T'HH:mm:ssZ, yyyy-MM-dd'T'HH:mm:ss.[0-9]{1,9}Z]"));
    }

    @Test
    public void testInvalidEnumValue() throws Exception {
        // JSON message with incorrect enum value
        String jsonMessage = "{\n" +
                "  \"documentDelivery\": {\n" +
                "    \"associatedDealType\": \"InvalidType\",\n" +
                "    \"associationNameValuePairs\": {},\n" +
                "    \"documentBinaryData\": \"data\",\n" +
                "    \"documentMetadata\": {}\n" +
                "  }\n" +
                "}";

        // Load JSON schema
        List<String> errors = CustomJsonValidator.validateJsonMessageAgainstSchema(jsonMessage, jsonSchemaFile2);

        // Assert that there are validation errors
        Assertions.assertFalse(errors.isEmpty());

        Assertions.assertTrue(errors.contains("Validation error: #/documentDelivery/associatedDealType: InvalidType is not a valid enum value"));
        Assertions.assertTrue(errors.contains("Validation error: #/documentDelivery: extraneous key [documentMetadata] is not permitted"));
        Assertions.assertTrue(errors.contains("Validation error: #/documentDelivery: extraneous key [associationNameValuePairs] is not permitted"));
        Assertions.assertTrue(errors.contains("Validation error: #/documentDelivery: extraneous key [documentBinaryData] is not permitted"));
    }
}
