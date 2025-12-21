# DTO Pattern and MapStruct Guide

This document outlines the standards for using the Data Transfer Object (DTO) pattern and the MapStruct library in this project. The goal is to ensure a clean separation between the persistence layer (JPA Entities) and the presentation layer (API endpoints).

---

### 1. What is a Data Transfer Object (DTO)?

A DTO is a simple object used to transfer data between different layers or processes. In the context of our REST API, its primary roles are:

-   **Data Encapsulation**: To bundle data for network transfer.
-   **API Contract**: To define the precise structure of JSON payloads for requests and responses.
-   **Decoupling**: To separate the API's public contract from the internal database schema.

### 2. Why We Use DTOs

1.  **Security**: We avoid exposing sensitive or internal entity fields (e.g., `passwordHash`, `createdAt`, internal flags) through the API. DTOs allow us to select which data is safe to send to the client.
2.  **API Stability**: Our internal `Entity` models can be refactored (e.g., renaming a field, changing a relationship) without breaking the public API contract, as long as the DTO remains the same.
3.  **Flexibility**: We can create different DTOs for different use cases from the same entity. For example:
    -   `ClientResponseDto`: For detailed client information.
    -   `ClientCreationDto`: For data required to create a new client.
    -   `ClientSummaryDto`: For a list view, containing only essential fields like `id` and `name`.
4.  **Validation**: We can apply specific validation rules (`@NotNull`, `@Size`, etc.) to input DTOs that are different from the constraints on the JPA entities.

---

### 3. What is MapStruct?

MapStruct is a compile-time code generator that automates the process of mapping between Java bean types. Instead of writing boilerplate mapping code manually, we define a mapper interface, and MapStruct generates the implementation during compilation.

### 4. Why We Use MapStruct

1.  **Performance**: Because the mapping code is generated at compile-time, it is extremely fast. There is no runtime overhead from reflection or dynamic proxies.
2.  **Type-Safety**: All mappings are checked at compile-time, so you get immediate feedback if a mapping is incorrect or incomplete, preventing runtime errors.
3.  **Simplicity**: It reduces boilerplate code, making our services and controllers cleaner and more focused on business logic.

---

### 5. How to Implement

#### Step 1: Define the DTO

Create a DTO class that represents the data you want to expose or receive. It should be a simple POJO (Plain Old Java Object).

**Example**: `ClientResponseDto.java`

```java
public class ClientResponseDto {
    private UUID id;
    private String name;
    private String email;
    private String city;
    private String state;
    private UserStatus status;
    private LocalDate registrationDate;

    // Getters and Setters
}
```

#### Step 2: Create the Mapper Interface

Create an interface and annotate it with `@Mapper`. MapStruct will find this interface and generate an implementation class.

**Example**: `ClientMapper.java`

```java
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // 'componentModel = "spring"' allows the mapper to be injected as a Spring bean
public interface ClientMapper {

    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    /**
     * Maps a Client entity to a ClientResponseDto.
     */
    ClientResponseDto toDto(Client client);

    /**
     * Maps a ClientCreationDto to a Client entity.
     */
    Client toEntity(ClientCreationDto clientCreationDto);
}
```

#### Step 3: Use the Mapper in a Service

Inject the mapper into your service or controller and use it to perform the conversion.

**Example**: `ClientService.java`

```java
@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Autowired
    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    public ClientResponseDto findClientById(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        
        // Use the mapper to convert the entity to a DTO
        return clientMapper.toDto(client);
    }
}
```

