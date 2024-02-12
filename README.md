# Spring CSV ingestion example
## Overview
This is a Spring Dataflow project made to provide an example on how to handle CSV ingestion to the database. 
The project is divided between the processor and the sink.

### Overall structure
<img src="https://github.com/ikiwq/spring-dataflow-csv-ingestion/assets/110495658/50c80bb7-1f0f-4648-81f7-2e20079f811b" width="900"/>

### The processor
<img src="https://github.com/ikiwq/spring-dataflow-csv-ingestion/assets/110495658/31975dc2-fd4c-4cc8-a1dd-9fb0d6be1a06" width="700"/>

#### Fetching the properties
The processor takes as input the file reference from the source application. Before being elaborated further, 
the specific file properties (such as the separator and the table where the data will be inserted into) are fetched from the database. 
The properties will be contained inside a table where the primary key will be the file regex.

```plaintext
+-----------------+------------+-----------+
|    filename     | tablename  | separator |
+-----------------+------------+-----------+
| regex_file_1    | table1     |    |      |
| regex_file_2    | table2     |    ,      |
| regex_file_3    | table3     |   \s+     |
+-----------------+------------------------+
```
#### Elaborating headers
Once the processor finds a matching regex, the properties are then taken and used to build a CSV parser to analyze the first set of headers. The headers are "sanitized". Every douplicate header is labeled in a different way.

#### Fragmenting the file
The file is then fragmented based on the batch size present in the application.yaml file. A DTO containing the file reference, separator, headers and desired dump path are then published into the messaging queue, ready to be read from the sink.

### The sink
<img src="https://github.com/ikiwq/spring-dataflow-csv-ingestion/assets/110495658/bb135b1b-8a89-4be7-b5a8-46d2002e8e18" width="700"/>

The sink takes the information from the messaging queue. To ensure that data is not lost, if the chosen table is not present, the sink creates a temporary table filling the fields with VARCHAR fields and only then the data loap scripts are written and executed.

### Requisites
- Java SDK 17 or higher. Download Java from the official [oracle website](https://www.oracle.com/java/technologies/downloads/).
- Spring Dataflow. Download Spring Dataflow following the [official guide](https://dataflow.spring.io/docs/installation/).

### License
This project is licensed under the MIT license.

