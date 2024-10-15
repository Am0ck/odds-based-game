# Simple Odds-based Game with Kotlin and Spring Boot

## Overview
This project is a simple odds-based game built with Kotlin, Spring Boot, and an in-memory H2 database. It allows players to register, manage wallets, and place bets based on game logic. The game supports features such as:
- Player registration.
- Player wallet management (debit/credit operations).
- Placing bets with a specified bet number and bet amount.
- A leaderboard of top players based on their winnings.

The game exposes several REST APIs to manage the game mechanics, along with unit tests to ensure functionality.

## Technologies Used
- **Kotlin** for the application development.
- **Spring Boot** as the application framework.
- **Reactive Programming** with **Spring WebFlux** for non-blocking and reactive programming.
- **H2 Database** as an in-memory database to manage player and bet data.
- **JUnit 5** and **Mockk** for unit testing.
- **Maven** for dependency management and build automation.

## Features
- Player registration.
- Wallet transactions (credit and debit operations).
- Placing bets with randomized game logic.
- Leaderboard showing the top players by total winnings.
- Reactive and non-blocking REST APIs.
- Exception handling for various scenarios, such as insufficient balance or duplicate usernames.

## Prerequisites
To run this project, ensure you have the following installed:
- **Java 21** or later.
- **Maven 3.6+**.
- **Kotlin** (already included in the Maven configuration).

## Running the Application

### Clone the repository:
```bash
git clone github.com:Am0ck/odds-based-game.git
