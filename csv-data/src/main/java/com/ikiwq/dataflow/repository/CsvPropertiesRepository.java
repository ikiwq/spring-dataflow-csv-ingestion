package com.ikiwq.dataflow.repository;

import com.ikiwq.dataflow.model.entity.CsvProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CsvPropertiesRepository extends JpaRepository<CsvProperties, String> {
}
