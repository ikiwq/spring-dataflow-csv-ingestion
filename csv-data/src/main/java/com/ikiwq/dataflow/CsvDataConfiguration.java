package com.ikiwq.dataflow;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan
@EnableJpaRepositories(basePackages = {"com.ikiwq.dataflow.repository"})
@EntityScan(basePackages = "com.ikiwq.dataflow.model.entity")
public class CsvDataConfiguration {
}
