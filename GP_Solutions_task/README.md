## Property View – Hotel Management API

Java 21, Spring Boot REST API for managing hotels (search, amenities, histograms) on port **8092**.

### Tech stack

- **Java 21**, **Maven**
- **Spring Boot 3** (Web, Data JPA, Validation)
- **H2** (default DB), **Liquibase**
- **springdoc-openapi** (Swagger UI)
- `spring.jpa.open-in-view=false` for cleaner service-bound DB access

### Running locally

```bash
./mvnw spring-boot:run
```

Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

App runs at `http://localhost:8092`.

- Swagger UI: `http://localhost:8092/swagger-ui/index.html`
- H2 console: `http://localhost:8092/h2-console` (JDBC URL `jdbc:h2:mem:propertyview`)

### Main endpoints (all prefixed with `/property-view`)

- `GET /property-view/hotels` – list all hotels (brief)
- `GET /property-view/hotels/{id}` – hotel details
- `GET /property-view/search?name=&brand=&city=&country=&amenities=` – search
- `POST /property-view/hotels` – create hotel
- `POST /property-view/hotels/{id}/amenities` – replace amenities
- `GET /property-view/histogram/{param}` – `param` in `brand|city|country|amenities`

### Design overview (ERD-style)

- `Hotel` is the root entity.
- `Hotel` embeds:
  - `HotelAddress` (`houseNumber`, `street`, `city`, `country`, `postCode`)
  - `HotelContacts` (`phone`, `email`)
  - `HotelArrivalTime` (`checkIn`, `checkOut`)
- `Hotel` has `amenities` as an element collection in `hotel_amenities`.
- API uses DTOs:
  - request DTOs for input validation
  - brief/detail response DTOs for output shape control

### Example create request

```json
{
  "name": "DoubleTree by Hilton Minsk",
  "description": "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms...",
  "brand": "Hilton",
  "address": {
    "houseNumber": "9",
    "street": "Pobediteley Avenue",
    "city": "Minsk",
    "country": "Belarus",
    "postCode": "220004"
  },
  "contacts": {
    "phone": "+375 17 309-80-00",
    "email": "doubletreeminsk.info@hilton.com"
  },
  "arrivalTime": {
    "checkIn": "14:00",
    "checkOut": "12:00"
  }
}
```

### Tests

```bash
./mvnw test
```

Windows (PowerShell):

```powershell
.\mvnw.cmd test
```

Includes service unit tests and integration tests with H2.

Test scope includes:
- success flows for all required endpoints
- validation errors (`POST /hotels` invalid payload)
- not-found handling (`GET /hotels/{id}`)
- invalid histogram param handling
- amenities update behavior

### Docker

Build image:

```bash
docker build -t property-view .
```

Run container:

```bash
docker run --rm -p 8092:8092 property-view
```

### Docker Compose

Start app:

```bash
docker compose up --build
```

Run in background:

```bash
docker compose up --build -d
```

Stop:

```bash
docker compose down
```

### Switching DB (example: PostgreSQL)

Override Spring properties (e.g. `application-postgres.yml` or environment variables):

- `spring.datasource.url=jdbc:postgresql://host:5432/db`
- `spring.datasource.username=...`
- `spring.datasource.password=...`
- `spring.jpa.hibernate.ddl-auto=none` (schema managed by Liquibase)

Profiles are included:
- `application-postgres.yml`
- `application-mysql.yml`

Run with profile (example PostgreSQL):

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
```

