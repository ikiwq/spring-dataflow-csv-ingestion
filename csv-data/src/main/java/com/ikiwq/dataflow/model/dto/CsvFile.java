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
    private List<String> headers;
    private String table;
    private Character separator;
}
