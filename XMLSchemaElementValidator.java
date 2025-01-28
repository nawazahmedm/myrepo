import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import java.io.File;

public class XMLSchemaElementValidator {

    public static void validateXMLWithSchema(String xmlFilePath, String schemaFilePath) {
        try {
            // Load the schema
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new File(schemaFilePath));

            // Create a Validator
            Validator validator = schema.newValidator();

            // Custom ErrorHandler to capture schema violations
            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) {
                    System.out.println("Warning: " + exception.getMessage());
                }

                @Override
                public void error(SAXParseException exception) {
                    System.out.println("Error: " + exception.getMessage());
                }

                @Override
                public void fatalError(SAXParseException exception) {
                    System.out.println("Fatal Error: " + exception.getMessage());
                }
            });

            // Validate the XML
            validator.validate(new StreamSource(new File(xmlFilePath)));
            System.out.println("XML validation completed successfully!");

            // Parse and log each element
            parseAndLogElements(xmlFilePath);
        } catch (Exception e) {
            System.err.println("Validation failed: " + e.getMessage());
        }
    }

    public static void parseAndLogElements(String xmlFilePath) {
        try {
            // Create a SAX Parser
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();

            // Parse XML and log each element
            saxParser.parse(new File(xmlFilePath), new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    System.out.println("Validating element: <" + qName + ">");
                    for (int i = 0; i < attributes.getLength(); i++) {
                        System.out.println("  Attribute: " + attributes.getQName(i) + " = " + attributes.getValue(i));
                    }
                }

                @Override
                public void error(SAXParseException e) throws SAXException {
                    System.err.println("Error in element: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Element parsing failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String xmlFilePath = "example.xml";
        String schemaFilePath = "example.xsd";

        validateXMLWithSchema(xmlFilePath, schemaFilePath);
    }
}


<note>
    <to>Tove</to>
    <from>Jani</from>
    <heading>Reminder</heading>
    <body>Don't forget me this weekend!</body>
</note>

<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
    <xs:element name="note">
        <xs:complexType>
            <xs:sequence>
                <xs:element name="to" type="xs:string"/>
                <xs:element name="from" type="xs:string"/>
                <xs:element name="heading" type="xs:string"/>
                <xs:element name="body" type="xs:string"/>
            </xs:sequence>
        </xs:complexType>
    </xs:element>
</xs:schema>
