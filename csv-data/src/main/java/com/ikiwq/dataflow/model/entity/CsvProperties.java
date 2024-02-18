package com.ikiwq.dataflow.model.entity;

import jakarta.persistence.Column;
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
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "table_name")
    private String tableName;
    @Column(name = "separator")
    private char separator;
}
