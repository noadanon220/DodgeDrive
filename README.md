<div align="center">
  <img src="screenshots/dodgeDrive_title.png" alt="Game flow"/>
</div>

**'DodgeDrive'** is a simple Android game app developed as part of the **Mobile Application Development** course in a Bachelor's degree in Computer Science.

The game challenges the player to avoid falling rocks by controlling a car across three lanes. The player starts with three lives, which are visually represented as hearts. The game ends after three collisions.

<div align="center">
  <img src="screenshots/dodgeDrive_user_workflow.png" alt="Game flow"/>
</div>

# 🕹️ Dodge Drive – Part 2 Update Overview

This update introduces several key gameplay enhancements and features:

---

## 🎮 Gameplay Modes

### 1. Slow Mode  
Obstacles fall at a relaxed pace – ideal for beginners or a casual experience.

### 2. Fast Mode  
A challenging mode with high-speed obstacles for experienced players seeking intense action.

### 3. Sensor Mode  
Control the car by tilting your device **left** or **right**. Movement is responsive to your device's orientation.

---

## 🛣️ Expanded Game Board

- The game grid now includes **5 lanes** instead of 3.
- This provides more space and complexity for dodging obstacles and collecting coins.

---

## 🪙 Coins, Extra Lives & Background Music

- 🪙 **Coins** collected during the game add **10 points** each to the final score.
- ❤️ **Extra lives** can also appear on the board – collecting them increases the player's remaining lives.
- 💥 Collisions reduce the player's life count.
- Players begin with **3 lives** (represented by hearts) and the game ends when all lives are lost.
- 🎵 A **background soundtrack** was added to enhance the gameplay experience.

---

## 🏆 Scoreboard & Record Location

- After each game, the result is saved with the following details:
  - 👤 **Player name**
  - 🪙 **Final score**
  - 📍 **GPS location** where the score was set
  - 📅 **Date** of the achievement

- The **Top 10 high scores** are saved persistently.

- Players can **View scores on a map** to see where records were made

---

## 🛠️ Technologies Used

- **Kotlin** – Main programming language
- **Android SDK** – Core framework for Android development
- **ViewBinding** – Efficient access to views
- **Fragments & Activities** – Structured navigation and UI flow
- **RecyclerView** – Dynamic display of the scoreboard
- **SharedPreferences** – Persistent local storage for top 10 scores
- **Google Maps SDK** – To show where each high score was achieved
- **SensorManager** – Enables motion-based control in Sensor Mode
- **MediaPlayer** – Background music and sound effects (coin, rock, extra life)
- **Custom Drawables & XML Styling** – Rounded cards, shadows, and game visuals
- **Gradle** – Build and dependency management

---

## 📲 Installation

To run the app on an Android device or emulator:

1. Clone the repository:
   ```bash
   git clone https://github.com/noadanon220/DodgeDrive.git

