import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonToMapParser {

    public static void main(String[] args) {
        String filePath = "data.json"; // Replace with your JSON file path

        try {
            File jsonFile = new File(filePath);
            ObjectMapper objectMapper = new ObjectMapper();

            // Parse the JSON file into a JsonNode tree
            JsonNode rootNode = objectMapper.readTree(jsonFile);

            // Map to store key-value pairs
            Map<String, Object> resultMap = new HashMap<>();

            // Parse JSON and populate the map
            parseJsonNode(rootNode, "", resultMap);

            // Print the map
            resultMap.forEach((key, value) -> System.out.println(key + " : " + value));
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
    }

    /**
     * Recursively parses a JsonNode tree and populates a map with key-value pairs.
     *
     * @param node      Current JsonNode to process
     * @param parentKey Key prefix for nested elements
     * @param resultMap Map to store the parsed key-value pairs
     */
    private static void parseJsonNode(JsonNode node, String parentKey, Map<String, Object> resultMap) {
        if (node.isObject()) {
            // If the node is an object, iterate over its fields
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String key = parentKey.isEmpty() ? field.getKey() : parentKey + "." + field.getKey();
                parseJsonNode(field.getValue(), key, resultMap);
            }
        } else if (node.isArray()) {
            // If the node is an array, iterate over its elements
            int index = 0;
            for (JsonNode arrayElement : node) {
                String key = parentKey + "[" + index + "]";
                parseJsonNode(arrayElement, key, resultMap);
                index++;
            }
        } else {
            // If the node is a value, add it to the map
            resultMap.put(parentKey, node.asText());
        }
    }
}
