package com.ikiwq.dataflow.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "csv_properties")
@NoArgsConstructor
@AllArgsConstructor
public class CsvProperties {
    @Id
    private String fileName;
    private String tableName;
    private char separator;
}
