package com.ofss.mock.rest.xml;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ofss.mock.rest.xml.dto.ApiEndpoints;

public class XmlService {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String MOCK_FILE_VAR = "OBPMockRestXmlFile";
    private static final String DEFAULT_FILE_NAME = "xmlResponses.json";
    private static File file;
    private Map<String, String> hashMapForXML = new HashMap<>();

    static {
        initializeFile();
    }

    private static void initializeFile() {
        try {
            String path = System.getenv(MOCK_FILE_VAR);
            if (path != null && !path.trim().isEmpty()) {
                file = new File(path);
                // Ensure parent directory exists if a path was provided
                if (file.getParentFile() != null) {
                    Files.createDirectories(file.getParentFile().toPath());
                }
            } else {
                // Default to a writable location in user's home directory
                file = new File(System.getProperty("user.home"), DEFAULT_FILE_NAME);
                if (file.getParentFile() != null) {
                    Files.createDirectories(file.getParentFile().toPath());
                }
            }

            // If file doesn't exist, seed from classpath resource (if available), else create empty JSON object
            if (!file.exists()) {
                try (InputStream in = XmlService.class.getClassLoader().getResourceAsStream(DEFAULT_FILE_NAME)) {
                    if (in != null) {
                        Files.copy(in, file.toPath());
                    } else {
                        Files.write(file.toPath(), "{}".getBytes(StandardCharsets.UTF_8));
                    }
                }
            }

            System.out.println("XML responses file initialized at: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to initialize data file at configured/home path. Falling back to temp. Reason: " + e.getMessage());
            try {
                file = new File(System.getProperty("java.io.tmpdir"), DEFAULT_FILE_NAME);
                if (!file.exists()) {
                    Files.write(file.toPath(), "{}".getBytes(StandardCharsets.UTF_8));
                }
                System.out.println("XML responses file initialized at temp: " + file.getAbsolutePath());
            } catch (Exception ignored) {
                System.err.println("Unable to initialize any data file for XML responses.");
            }
        }
    }

    private HashMap<String, String> readFromFile() {
        try {
            if (file == null) {
                initializeFile();
            }
            if (file == null || !file.exists()) {
                return new HashMap<>();
            }
            return mapper.readValue(file, new TypeReference<HashMap<String, String>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private synchronized boolean writeToFile(Map<String, String> xmlInput) {
        try {
            if (file == null) {
                initializeFile();
            }
            if (file == null) {
                return false;
            }
            ObjectNode objectNode = mapper.createObjectNode();
            for (Map.Entry<String, String> entry : xmlInput.entrySet()) {
                objectNode.put(entry.getKey(), entry.getValue());
            }
            mapper.writeValue(file, objectNode);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void reloadHashmap() {
        hashMapForXML = readFromFile();
    }

    public String getXmlByKey(String key) {
        try {
            if (hashMapForXML.isEmpty()) {
                hashMapForXML = readFromFile();
            }
            return hashMapForXML.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public String modifyXml(String key, String xmlInput, String operation) {
        try {
            hashMapForXML = readFromFile();
            hashMapForXML.put(key, xmlInput);
            boolean persisted = writeToFile(hashMapForXML);
            return persisted
                    ? "Response " + operation + " for the SPI: " + key
                    : "Failed to persist response for the SPI: " + key;
        } catch (Exception e) {
            return "Response for the SPI: " + key + " wasn't " + operation;
        }
    }

    public String deleteXml(String key) {
        try {
            reloadHashmap();
            hashMapForXML.remove(key);
            boolean persisted = writeToFile(hashMapForXML);
            return persisted
                    ? "Response removed for the SPI: " + key
                    : "Failed to remove response for the SPI: " + key;
        } catch (Exception e) {
            return "Failed to remove response for the SPI: " + key;
        }
    }

    public Set<String> getAllSpi() {
        try {
            hashMapForXML = readFromFile();
            return new HashSet<>(hashMapForXML.keySet());
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

    public List<ApiEndpoints> getApiHelp() {

        List<ApiEndpoints> apiList = new ArrayList<ApiEndpoints>();
        apiList.add(new ApiEndpoints("GET", "/fetch/{spiName}", "Returns the response associated with the SPI name given in the url. [GET]"));
        apiList.add(new ApiEndpoints("POST", "/fetch/{spiName}", "Returns the response associated with the SPI name given in the url. [POST]"));
        apiList.add(new ApiEndpoints("POST", "/add/{spiName}", "Adds the Xml in the request with the SPI given in the url as its response."));
        apiList.add(new ApiEndpoints("POST", "/update/{spiName}", "Updates the existing response for the the SPI with the new one given in the request-body."));
        apiList.add(new ApiEndpoints("DELETE", "/delete/{spiName}", "Deletes the given SPI response associated with the SPI name given in the url."));
        apiList.add(new ApiEndpoints("GET", "/fetchAll", "Returns a list of all SPI names and their total count."));
        apiList.add(new ApiEndpoints("GET", "/default", "Too lazy to add response , just the url for you."));
        apiList.add(new ApiEndpoints("GET", "/help", "List of all available endpoints"));

        return apiList;
    }

}
