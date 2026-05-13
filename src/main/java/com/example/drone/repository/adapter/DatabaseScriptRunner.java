package com.example.drone.repository.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

final class DatabaseScriptRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseScriptRunner.class);

    private DatabaseScriptRunner() {
    }

    static void runScript(DataSource dataSource, String classpathLocation) {
        String script = loadScript(classpathLocation);
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            for (String sql : splitStatements(script)) {
                statement.execute(sql);
            }
            logger.info("Executed database script: {}", classpathLocation);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to execute database script: " + classpathLocation, e);
        }
    }

    private static String loadScript(String classpathLocation) {
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("--")) {
                    continue;
                }
                builder.append(line).append('\n');
            }
            return builder.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load database script: " + classpathLocation, e);
        }
    }

    private static String[] splitStatements(String script) {
        return Arrays.stream(script.split(";"))
                .map(String::trim)
                .filter(sql -> !sql.isEmpty())
                .toArray(String[]::new);
    }
}
