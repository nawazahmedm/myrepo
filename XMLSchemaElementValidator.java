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


<?xml version="1.0" encoding="UTF-8"?>
<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"
            targetNamespace="http://www.freddiemac.com/enterpriseeventdatamodel"
            xmlns="http://www.freddiemac.com/enterpriseeventdatamodel"
            elementFormDefault="qualified">

    <xsd:element name="Events">
        <xsd:complexType>
            <xsd:sequence>
                <xsd:element name="EventMetaData">
                    <xsd:complexType>
                        <xsd:sequence>
                            <xsd:element name="MessageIdentifier" type="xsd:string"/>
                            <xsd:element name="EventType" type="xsd:string"/>
                            <xsd:element name="EventName" type="xsd:string"/>
                            <xsd:element name="EventClassification" type="xsd:string"/>
                            <xsd:element name="ProducerName" type="xsd:string"/>
                            <xsd:element name="MessageTimestamp" type="xsd:dateTime"/>
                        </xsd:sequence>
                    </xsd:complexType>
                </xsd:element>
            </xsd:sequence>
        </xsd:complexType>
    </xsd:element>
</xsd:schema>
