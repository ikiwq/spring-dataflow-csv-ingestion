package com.ikiwq.dataflow.streams;

import com.ikiwq.dataflow.CsvDataConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({CsvDataConfiguration.class})
public class CsvProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CsvProcessorApplication.class, args);
    }

}