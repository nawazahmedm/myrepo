import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XMLSchemaElementValidatorTest {

    private static File xmlSchemaFile;
    private static XMLSchemaElementValidator xmlSchemaElementValidator;

    @BeforeAll
    static void setup() throws IOException {
        xmlSchemaFile = new ClassPathResource("example.xsd").getFile();
        xmlSchemaElementValidator = new XMLSchemaElementValidator();
    }

    @Test
    void testValidateXML_Success() {
        String validXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Events xmlns="http://www.freddiemac.com/enterpriseeventdatamodel">
                    <EventMetaData>
                        <MessageIdentifier>95ad6cff-2204-4721-9d35-0a120ec626f4</MessageIdentifier>
                        <EventType>BUSINESS</EventType>
                        <EventName>CSPGateway PublicationfromCSS</EventName>
                        <EventClassification>NOTIFICATION</EventClassification>
                        <ProducerName>CSPGateway</ProducerName>
                        <MessageTimestamp>2024-04-01T06:29:53.462-04:00</MessageTimestamp>
                    </EventMetaData>
                </Events>
                """;

        List<String> errors = xmlSchemaElementValidator.validateXMLWithSchema(validXml, xmlSchemaFile);
        assertTrue(errors.isEmpty(), "XML should pass validation with no errors");
    }

    @Test
    void testValidateXML_Failure() {
        String invalidXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <Events xmlns="http://www.freddiemac.com/enterpriseeventdatamodel">
                    <EventMetaData>
                        <EventName>CSPGateway PublicationfromCSS</EventName>
                        <EventClassification>NOTIFICATION</EventClassification>
                        <ProducerName>CSPGateway</ProducerName>
                        <MessageTimestamp>InvalidTimestamp</MessageTimestamp>
                    </EventMetaData>
                </Events>
                """;

        List<String> errors = xmlSchemaElementValidator.validateXMLWithSchema(invalidXml, xmlSchemaFile);
        assertFalse(errors.isEmpty(), "XML should fail validation due to missing fields and incorrect timestamp");
        errors.forEach(System.out::println);
    }
}
