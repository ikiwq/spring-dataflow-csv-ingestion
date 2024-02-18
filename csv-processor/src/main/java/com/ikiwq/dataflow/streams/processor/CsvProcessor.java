package com.ikiwq.dataflow.streams.processor;

import com.ikiwq.dataflow.model.dto.CsvFile;
import com.ikiwq.dataflow.model.entity.CsvProperties;
import com.ikiwq.dataflow.repository.CsvPropertiesRepository;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CsvProcessor {

    @Value("${processor.batch-size}")
    private Integer batchSize;

    @Value("${processor.absolute-dump-directory}")
    private String absoluteDumpDirectory;

    private final CsvPropertiesRepository csvPropertiesRepository;

    @Bean
    public Function<File, List<Message<CsvFile>>> elaborateCsv() {
        return file -> {
            BufferedReader reader = allocateBufferedReader(file);

            CsvProperties properties = findPropertyByFilename(file.getName());
            CSVParser csvParser = csvParser(properties.getSeparator());

            // Assuming the first csv line are the headers
            String[] headers = readAndParseLine(reader, csvParser);
            List<String> newHeaders = generateUniqueHeaders(headers);

            try {
                return organizeFileIntoBatches(reader, file.getName()).parallelStream().map(batch -> {
                    CsvFile csvFile = new CsvFile(batch, newHeaders, properties.getTableName(), properties.getSeparator());
                    return MessageBuilder.withPayload(csvFile).build();
                }).toList();
            } catch (IOException e) {
                log.error("Could not organize file batches: ", e);
                throw new RuntimeException(e);
            }
        };
    }

    private CSVParser csvParser(Character separator) {
        return new CSVParserBuilder().withSeparator(separator).build();
    }

    private BufferedReader allocateBufferedReader(File file) {
        try {
            return new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            String error = String.format("File not found: %s", file.getAbsoluteFile());
            log.error(error, e);
            throw new RuntimeException(e);
        }
    }

    private BufferedWriter allocateBufferedWriter(File file) {
        try {
            return new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            String error = String.format("File not found %s", file.getAbsoluteFile());
            log.error(error, e);
            throw new RuntimeException(e);
        }
    }

    private String[] readAndParseLine(BufferedReader reader, CSVParser parser) {
        String line;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            log.error("Could not read line: ", e);
            throw new RuntimeException(e);
        }

        try {
            return parser.parseLine(line);
        } catch (IOException e) {
            String error = String.format("Could not parse line %s :", line);
            log.error(error);
            throw new RuntimeException(e);
        }
    }


    private Set<File> organizeFileIntoBatches(BufferedReader reader, String filename) throws IOException {
        Set<File> files = new HashSet<>();

        int lineCount = 0;
        String line;
        File currentFile = new File(absoluteDumpDirectory + generateDumpFileName(filename));
        BufferedWriter writer = allocateBufferedWriter(currentFile);

        while ((line = reader.readLine()) != null) {
            writer.write(line);
            writer.newLine();
            lineCount++;

            if (lineCount % batchSize == 0) {
                writer.close();
                files.add(currentFile);

                currentFile = new File(absoluteDumpDirectory + generateDumpFileName(filename));
                writer = allocateBufferedWriter(currentFile);
            }
        }
        // Prevent file not being handled if the lineCount is not multiple of batchSize
        if(lineCount % batchSize != 0){
            writer.close();
            files.add(currentFile);
        }

        return files;
    }

    // Checks for duplicate properties and labels them with a count
    private List<String> generateUniqueHeaders(String[] headers) {
        HashMap<String, Integer> countMap = new HashMap<>();
        List<String> newHeaders = new ArrayList<>();

        for (String header : headers) {
            if (!countMap.containsKey(header)) {
                newHeaders.add(header);
                countMap.put(header, 1);
            } else {
                int count = countMap.get(header);
                newHeaders.add(header + "_" + count);
                countMap.put(header, count + 1);
            }
        }
        return newHeaders;
    }

    // Generates name for dump file with the current time
    private String generateDumpFileName(String fileName) {
        String pattern = "yyyy-dd-mm-ss-SSS";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String formattedDate = sdf.format(new Date());

        return String.format("DUMP-%s-%s-%s", UUID.randomUUID(), formattedDate, fileName);
    }

    private CsvProperties findPropertyByFilename(String filename) {
        List<CsvProperties> properties = csvPropertiesRepository.findAll();
        // Searches for a matching regex inside the record
        for (CsvProperties property : properties) {
            if (filename.matches(property.getFileName())) {
                return property;
            }
        }
        throw new RuntimeException(String.format("CsvProperties for file %s not found.", filename));
    }
}
