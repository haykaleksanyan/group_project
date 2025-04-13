# 🚢 Battle of Ships

This is the version-controlled repository for the **Battle of Ships** group project by students from **AUA OOP 2025, Section D**.

## 👥 Team Members

- Karine Papoyan – <karine_papoyan@edu.aua.am>
- Emilya Arustamyan – <emilya_arustamyan@edu.aua.am>
- Hayk Aleksanyan – <hayk_aleksanyan@edu.aua.am>

---

## 📝 Project Description

**Battle of Ships** is a terminal-based strategy game. The goal is to locate and destroy the opponent's ships before they destroy yours.

- Each time the ships are placed randomly (or placed by the player) on the board.
- Players take turns firing at coordinates on the opponent's grid.
- A hit marks part of a ship, a miss leaves it empty.
- The game continues until one player looses all his ships.
- The first player to sink all enemy ships wins.

This project demonstrates object-oriented programming (OOP) principles such as:
- **Encapsulation**
- **Abstraction**
- **Inheritance**
- **Polymorphism**

---

## 📁 Project Structure
```
src/ 
│ 
├── Main.java 
├── Board.java 
├── Player.java
├── Coordinate.java 
├── Ship.java 
├── Rocket.java 
├── Weapon.java 
├── Bomb.java 
```
---

## 🧱 Class Overview

### `Ship`
Represents a ship object on the board.

### `Board`
Represents the playing grid for each player.

### `Player`
Represents the player logic and state.

### `Coordinate`
Defines grid cell with row and column values.

### `Weapon` *(abstract)*
An abstract class for all weapons used in the game.

### `Bomb`
A type of weapon, used to attack 5x5 matrix.

### `Rocket`
Another type of weapon, used to attack a row or a column.

### `Main`
Sets up the game, players.


