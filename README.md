# LOLPros
Small project for learning purposes. It's a simple web application that allows you to search for a League of Legends player and see his statistics. The application uses the [LOLPros](https://lolpros.gg/) and [Riot](https://developer.riotgames.com/) API to get the data.

# Prerequisites
Before you can run the project, ensure you have the following installed:

`Java JDK 21`
`Maven 3.6 or higher`
# Getting Started
To get the project running on your local machine for development and testing purposes, follow these steps:

# Clone the repository

`git clone https://github.com/Wyderekk/LOLPros.git`
### Navigate to the project directory

`cd LOLPros`
### Compile and run the application

`mvn clean install`
`mvn spring-boot:run`
The application should now be running at `http://localhost:8080`.

# Project Structure
- `src/main/java` - Contains the backend Java code including UI logic, views, and services.
- `src/main/resources` - Contains various resources such as templates and application properties.
- `frontend/` - Holds the frontend files, which can include HTML, CSS, JavaScript, or TypeScript.
# Building for Production
To prepare a production build, use the following Maven command:

`mvn clean package -Pproduction`
#### This will generate an optimized build suitable for a production environment.


# License
This project is open-sourced under the UNLICENSE License. See the LICENSE file for full license text.

# Acknowledgments
Vaadin Framework - [Vaadin Framework](https://vaadin.com/)
LOLPros - [LOLPros](https://lolpros.gg/)
