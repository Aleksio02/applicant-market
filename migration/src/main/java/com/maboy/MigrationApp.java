package com.maboy;

import org.flywaydb.core.Flyway;
import org.yaml.snakeyaml.Yaml;
import com.maboy.config.ApplicationConfig;
import com.maboy.config.ApplicationConfig.Datasource;

import java.io.InputStream;

public class MigrationApp {

    public static void main(String[] args) {

        Yaml yaml = new Yaml();
        InputStream inputStream = MigrationApp.class
            .getClassLoader()
            .getResourceAsStream("application.yml");

        ApplicationConfig config = yaml.loadAs(inputStream, ApplicationConfig.class);

        Datasource datasource = config.getDatasource();

        Flyway flyway = Flyway.configure()
            .dataSource(datasource.getUrl(), datasource.getUsername(), datasource.getPassword())
            .locations("classpath:migration")
            .placeholderReplacement(false)
            .load();

        flyway.migrate();
    }
}