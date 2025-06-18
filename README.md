# CookEasy Backend

CookEasy is a Java Spring Boot application designed to support a cooking assistant platform. It provides API endpoints and static HTML pages for recipe management, user interaction, chatbot assistance, and cooking guidance.

## Features

- User authentication and profile management
- Recipe browsing, saving, and detailed views
- AI-powered ChefBot integration (OpenAI)
- Static HTML UI for interacting with recipes
- RESTful API endpoints for frontend consumption

## Project Structure

```

ALP4-Backend/
└── prod/
├── src/
│   └── main/
│       ├── java/com/cookeasy/prod/
│       │   ├── controller/             # REST controllers
│       │   ├── service/                # Business logic
│       │   ├── model/                  # Domain models
│       │   ├── repository/             # Spring Data JPA repositories
│       │   ├── config/                 # Configuration files (OpenAI, etc.)
│       │   └── ProdApplication.java    # Main entry point
│       └── resources/
│           ├── static/                 # HTML pages and images
│           └── application.properties
└── OpenAI\_Integration\_README.md      # AI integration details

```

## Prerequisites

- Java 17+
- Gradle (or use the wrapper `./gradlew`)
- Internet connection (for OpenAI API)

## Getting Started

1. **Clone the repository:**
    ```bash
    git clone https://github.com/VivianWijaya06/ALP4-Backend.git
    cd cookeasy-backend/ALP4-Backend/prod
    ```

2. **Configure application properties:**
    ```bash
    Update `src/main/resources/application.properties` with your database and OpenAI credentials.
    ```

3. **Run the application:**
    ```bash
    ./gradlew bootRun
    ```

4. **Access the UI:**
    ```bash
    Open `http://localhost:8080/` in a browser.
    ```

## API Overview

* `/recipes` – Get all recipes
* `/chatbot` – Interact with the ChefBot
* `/users` – Register or authenticate users
* `/files` – Upload recipe files

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/YourFeature`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature/YourFeature`)
5. Open a pull request

## License

This project is licensed under the [MIT License](/LICENSE.txt).
