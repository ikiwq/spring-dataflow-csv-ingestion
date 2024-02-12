package com.ikiwq.dataflow.streams.sink;

import com.ikiwq.dataflow.model.dto.CsvFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CsvSink {
    private final JdbcTemplate jdbcTemplate;

    @Bean
    public Consumer<CsvFile> saveFile(){
        return csvFile -> {
            log.info("Received csvFile {}", csvFile);

            String createStatement = generateCreateStatement(csvFile.getHeaders(), csvFile.getTable());
            log.debug("Executing statement {}", createStatement);
            jdbcTemplate.execute(createStatement);

            String loadStatement = generateLoadDataStatement(csvFile.getFile(), csvFile.getRelativePath(), csvFile.getTable(), csvFile.getSeparator());
            log.debug("Executing statement {}", loadStatement);
            jdbcTemplate.execute(loadStatement);
        };
    }

    private String generateCreateStatement(List<String> headers, String tableName){
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        for(String header: headers){
            sql.append("`").append(header).append("`").append(" VARCHAR(100),");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(");");

        return sql.toString();
    }

    private String generateLoadDataStatement(File file, String relativeDumpDirectory, String tableName, Character separator){
        StringBuilder sql = new StringBuilder("LOAD DATA INFILE '").append(relativeDumpDirectory).append(file.getName()).append("' ");
        sql.append("IGNORE ");
        sql.append("INTO TABLE ").append(tableName).append(" ");
        sql.append("FIELDS TERMINATED BY '").append(separator).append("' ");
        sql.append("OPTIONALLY ENCLOSED BY '\"' ");
        sql.append("LINES TERMINATED BY '\\n';");

        return sql.toString();
    }
}
