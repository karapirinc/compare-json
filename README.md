**RESTful Base64 Binary Data Comparator**

This project exposes 2 API for comparing Base64 data.

* /v1/diff/`<id>`/`<comparison-side>` (Persist Base64 binary data)
* /v1/diff/`<id>` (Compares previously stored binary data)

### Requirements
  * `Java 8` 

### Usage 
    
    https://github.com/karapirinc/compare-json.git
    cd compare-json
    
### API Documentation

    mvnw package
  * `/target/generated-docs/index.html`
  * `/docs/javadocs/index.html`

### Run

    mvnw spring-boot:run
    
Services will be available at localhost:8080


