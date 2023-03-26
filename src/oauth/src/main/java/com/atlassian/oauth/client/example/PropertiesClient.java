package com.atlassian.oauth.client.example;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class PropertiesClient {
    public static final String CONSUMER_KEY = "consumer_key";
    public static final String PRIVATE_KEY = "private_key";
    public static final String REQUEST_TOKEN = "request_token";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String SECRET = "secret";
    public static final String JIRA_HOME = "jira_home";


    private final static Map<String, String> DEFAULT_PROPERTY_VALUES = ImmutableMap.<String, String>builder()
            .put(JIRA_HOME, "http://localhost:8080")
            .put(CONSUMER_KEY, "OauthKey")
            .put(PRIVATE_KEY, "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMCnci4qqqH9uyteU55drws4ZjEbWY4j/YMEZIq8gvx1htdnRIA/26dPXU61l7FvwhNtZSgEjXMsK4ZoaNjJKTmg/UEEI6pO203KL/I3S7SQZISb/tU1140F/ih/IC/wUUxw+t19kWpprryFkqGRtGTci+26S7Oj6PWZOvkt8X95AgMBAAECgYBoWEkXiDRzGKJPkv9nPwaX5Sw0XMPcoNGmLLLoEcJASseF04Delbe6ntnCz8ghao6LRy0Kx6x1PO82FUb0Y7/XbUFYub3bj0N6OO04gET6scnazNuWmu6jrxJzb/Kv94//+7p7Tb/Fs6C+Va/L9ddc0jtomOaJKoJ/DHBFu6MoIQJBAPKQua1vatuEy/6votd5MAEUjIoeeAMaU/w6hwJ913ZKzvj4DsahhejRxFAzH4KnAuKv5gORGPuLpDpkO34K6McCQQDLUwqThs1nsK0X5UTVe+TOg9znbF5rpd2bAAVmbzocrCP0VFb+DgsFLDsIU1J54er+eTuq2YO+m+hQBARlHpW/AkEAmR5Wt26vy6bGhx0j9FThwqzQEpgtNfg2r+/aLe52RovunycnXKe1ukRAYkgDShxXK/XRgsrjNFbv8pQ3IdeNHQJBAJJ/PruVASKY1d7FvpbjMbTqpZvJJS8Cz20C0uE/eut1zGIa8qMSkzYi7FXPUzmmYZ8A0tEC8D3CL9yXSGNjahkCQHxZBf6f2d5GR6ICK7oMdpo6x4QFeLjwcm2b15krtkZerLOUnDZVBBWO4Zc+PiahoowvxCfgbCby2+JCxxYu5CU=")
            .build();

    private final String fileUrl;
    private final String propFileName = "config.properties";

    public PropertiesClient() throws Exception {
        fileUrl = "./" + propFileName;
    }

    public Map<String, String> getPropertiesOrDefaults() {
        try {
            Map<String, String> map = toMap(tryGetProperties());
            map.putAll(Maps.difference(map, DEFAULT_PROPERTY_VALUES).entriesOnlyOnRight());
            return map;
        } catch (FileNotFoundException e) {
            tryCreateDefaultFile();
            return new HashMap<>(DEFAULT_PROPERTY_VALUES);
        } catch (IOException e) {
            return new HashMap<>(DEFAULT_PROPERTY_VALUES);
        }
    }

    private Map<String, String> toMap(Properties properties) {
        return properties.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(o -> o.getKey().toString(), t -> t.getValue().toString()));
    }

    private Properties toProperties(Map<String, String> propertiesMap) {
        Properties properties = new Properties();
        propertiesMap.entrySet()
                .stream()
                .forEach(entry -> properties.put(entry.getKey(), entry.getValue()));
        return properties;
    }

    private Properties tryGetProperties() throws IOException {
        InputStream inputStream = new FileInputStream(new File(fileUrl));
        Properties prop = new Properties();
        prop.load(inputStream);
        return prop;
    }

    public void savePropertiesToFile(Map<String, String> properties) {
        OutputStream outputStream = null;
        File file = new File(fileUrl);

        try {
            outputStream = new FileOutputStream(file);
            Properties p = toProperties(properties);
            p.store(outputStream, null);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            closeQuietly(outputStream);
        }
    }

    public void tryCreateDefaultFile() {
        System.out.println("Creating default properties file: " + propFileName);
        tryCreateFile().ifPresent(file -> savePropertiesToFile(DEFAULT_PROPERTY_VALUES));
    }

    private Optional<File> tryCreateFile() {
        try {
            File file = new File(fileUrl);
            file.createNewFile();
            return Optional.of(file);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            // ignored
        }
    }
}
