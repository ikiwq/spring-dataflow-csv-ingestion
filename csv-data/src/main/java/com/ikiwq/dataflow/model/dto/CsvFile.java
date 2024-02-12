package com.ikiwq.dataflow.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CsvFile {
    private File file;
    // The relative path refers to the MySQL folder.
    // When loading a file in MySQL, by default it only accepts file inside its server directory.
    // So, for example an absolute filepath like C:/User/MySQL/Server/Dumps/Dump.csv would be equal to Dumps/Dump.csv
    private String relativePath;

    private List<String> headers;
    private String table;
    private Character separator;
}
