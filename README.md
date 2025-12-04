# 💰 Budget Tracker

A modern, feature-rich Android application for managing personal finances, built with **Jetpack Compose** and **Kotlin**.

![Budget Tracker Banner](http://res.cloudinary.com/diee2a2rx/image/upload/v1764841672/budgetTracker/profile/i5ugjmaiuiout1d9enjz.jpg)

## 📱 Overview

**Budget Tracker** helps users track their income and expenses with a beautiful, glassmorphic dark theme. It provides real-time financial insights through interactive charts and dashboards, making personal finance management intuitive and engaging.

## ✨ Features

### 📊 Dashboard
- **Financial Overview**: Real-time tracking of total balance, income, and expenses.
- **Interactive Charts**: Visual breakdown of spending and income sources.
- **Recent Transactions**: Quick view of latest financial activities.
- **Auto-Refresh**: Data updates automatically when navigating between screens.

### 💸 Income & Expense Management
- **Easy Entry**: Add transactions with categories, amounts, and dates.
- **Emoji Support**: Categorize entries with fun emojis.
- **Transaction History**: View detailed lists of all past transactions.
- **Edit & Delete**: Manage your records with ease.
- **Excel Export**: Download reports for offline analysis.

### 👤 User Profile
- **Secure Authentication**: JWT-based login and registration.
- **Profile Customization**: Update personal details and profile picture.
- **Security**: Change password and secure logout functionality.

### 🎨 Modern UI/UX
- **Dark Theme**: Sleek, battery-friendly dark mode.
- **Glassmorphism**: Premium feel with translucent, blurred elements.
- **Neon Accents**: Vibrant colors for a modern aesthetic.
- **Smooth Animations**: Fluid transitions and interactions.

## 🛠️ Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Async Programming**: Coroutines + Flow
- **Image Loading**: Coil
- **Charting**: YCharts
- **Navigation**: Jetpack Navigation Compose

## 🚀 Getting Started

### Prerequisites
- Android Studio (Latest version recommended)
- JDK 11 or higher
- Android SDK 35

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/sachinkumar2222/budget_app.git
   cd budget_app
   ```

2. **Open in Android Studio**
   - Launch Android Studio.
   - Select "Open" and navigate to the cloned directory.

3. **Sync Project**
   - Allow Gradle to sync and download dependencies.

4. **Build and Run**
   - Connect an Android device or start an emulator.
   - Click the **Run** button (▶️) or execute:
     ```bash
     ./gradlew installDebug
     ```

## ⚙️ Configuration

The app connects to a backend API. You can configure the base URL in `app/src/main/java/com/example/budgettracker/util/Constants.kt`:

```kotlin
object Constants {
    // Production API
    const val BASE_URL = "https://budget-tracker-app-pnwq.onrender.com"
    
    // Local Development
    // const val BASE_URL = "http://10.0.2.2:8000"
}
```

## 📱 Screenshots

| Dashboard | Add Expense | Profile |
|:---:|:---:|:---:|
| ![Dashboard](http://res.cloudinary.com/diee2a2rx/image/upload/v1764841491/budgetTracker/profile/cqanvfnxz9ce8rdwlcqz.jpg) | ![Add Expense](http://res.cloudinary.com/diee2a2rx/image/upload/v1764841247/budgetTracker/profile/vwzollr0sbjj1bceimbl.jpg) | ![Profile](http://res.cloudinary.com/diee2a2rx/image/upload/v1764841744/budgetTracker/profile/tcja8a32ctbz675oehtn.jpg) |

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
Built with ❤️ using Jetpack Compose


