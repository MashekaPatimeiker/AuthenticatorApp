# 🔐 Authenticator App

Android-приложение для двухфакторной аутентификации (2FA) с генерацией TOTP-кодов.

---

## 🛠️ Tech Stack

- **Kotlin** — язык разработки  
- **Jetpack Compose** — UI  
- **Material 3** — дизайн-система  
- **MVVM + Clean Architecture** — архитектура  
- **DataStore** — локальное хранение аккаунтов  
- **ML Kit + CameraX** — QR-сканер  
- **Coroutines** — асинхронность  
- **Ktor Client** — HTTP-запросы  

---

## ✨ Features

- Генерация TOTP-кодов (обновление каждые 30 секунд)  
- Добавление аккаунтов через QR-сканер  
- Ручное добавление аккаунтов  
- Сохранение аккаунтов в DataStore  
- Удаление аккаунтов свайпом с подтверждением  
- Просмотр информации об аккаунте (свайп вправо)  
- Генерация случайных секретов  
- Валидация вводимых данных  
- Splash Screen  
- Фиолетовая тема Material 3  
- Плавные анимации переходов  

---

## 📱 Requirements

- Android 7.0 (API 24) или выше  
- Камера (для QR-сканера)  

---

## 🚀 Quick Start

```bash
git clone https://github.com/yourusername/AuthenticatorApp.git
cd AuthenticatorApp
# Открыть в Android Studio → Sync → Run
