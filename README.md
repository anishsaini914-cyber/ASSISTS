# JARVIS - Android AI Assistant

![Build Status](https://github.com/anishsaini/jarvis/workflows/JARVIS%20CI%20Build/badge.svg)
![Android](https://img.shields.io/badge/Android-14-green)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-purple)
![API](https://img.shields.io/badge/API-26%2B-blue)

> **J**ust **A** **R**ather **V**ery **I**ntelligent **S**ystem

JARVIS is a production-ready Android AI voice assistant that combines cloud AI providers (OpenAI, Gemini, AgentRouter) with local LLM inference support. It features voice commands, call handling, notification management, and an intuitive floating overlay interface.

## Features

### 🤖 Multi-Provider AI
- **OpenAI** - GPT-4o, GPT-4-turbo, GPT-3.5-turbo
- **Gemini** - Gemini 1.5 Pro, 1.5 Flash
- **AgentRouter** - Custom endpoint configuration
- **Local LLM** - GGUF model support with JNI bridge

### 🎤 Voice Control
- Wake word detection ("Hey JARVIS")
- Speech-to-text and text-to-speech
- Voice command parsing (Hindi + English)
- Continuous listening in foreground service

### 📞 Call Management
- Answer/reject calls via voice
- Speakerphone and mute control
- Caller announcement via TTS
- Hindi voice commands for calls

### 💬 Chat Interface
- Real-time streaming responses
- Markdown rendering (Markwon)
- Conversation history with Room DB
- Model selector and provider switching

### 🔧 System Control
- Flashlight toggle
- App launcher
- Web search
- Alarm creation
- Battery info
- Notification reading

### 🖥️ Floating Overlay
- Draggable bubble
- Expandable chat card
- Quick voice input
- Edge snapping

### 🌦️ Weather
- Open-Meteo integration (free, no API key)
- Location-based forecasts
- Voice query support

### 🔒 Security
- EncryptedSharedPreferences for API keys
- Biometric authentication ready
- Secure credential storage

## Architecture

```
MVVM + Clean Architecture + Repository Pattern
DI: Hilt (Dagger 2)
Async: Coroutines + Flow
Database: Room (SQLite)
Networking: Retrofit + OkHttp
```

## Tech Stack

| Component | Version |
|-----------|---------|
| Min SDK | 26 (Android 8.0) |
| Target/Compile SDK | 34 (Android 14) |
| Kotlin | 1.9.24 |
| AGP | 8.5.2 |
| Gradle | 8.7 |
| Hilt | 2.51.1 |
| Room | 2.6.1 |
| Retrofit | 2.11.0 |

## Setup

### Prerequisites
- Android Studio Hedgehog (2023.1.1+) or IntelliJ IDEA
- JDK 17 (Temurin recommended)
- Android SDK 34

### Quick Start

1. **Clone the repository**
   ```bash
   git clone https://github.com/anishsaini/jarvis.git
   cd jarvis
   ```

2. **Set up local properties**
   ```bash
   cp local.properties.example local.properties
   ```
   Edit `local.properties` with your SDK path:
   ```
   sdk.dir=/path/to/Android/Sdk
   ```

3. **Build the project**
   ```bash
   chmod +x gradlew
   ./gradlew assembleDebug
   ```

4. **Install on device**
   ```bash
   ./gradlew installDebug
   ```

### DevContainer (VS Code / Codespaces)
This project includes a `.devcontainer` configuration for a consistent development environment with Android SDK, NDK, and CMake pre-installed.

## Project Structure

```
app/src/main/java/com/jarvis/assistant/
├── ai/                    # AI provider implementations
│   ├── AIProvider.kt      # Provider interface
│   ├── OpenAIProvider.kt  # OpenAI integration
│   ├── GeminiProvider.kt  # Google Gemini integration
│   ├── AgentRouterProvider.kt
│   └── LocalLLMProvider.kt
├── data/
│   ├── local/
│   │   ├── db/            # Room database, entities, DAOs
│   │   └── prefs/         # Secure preferences wrapper
│   ├── remote/
│   │   ├── api/           # Retrofit API services
│   │   └── dto/           # Data transfer objects
│   └── repository/        # Repository implementations
├── di/                    # Hilt dependency injection modules
├── domain/
│   ├── model/             # Domain models
│   ├── repository/        # Repository interfaces
│   └── usecase/           # Use cases
├── llm/                   # Local LLM system
│   ├── GGUFLoader.kt      # GGUF file parser
│   ├── LocalLLMManager.kt # LLM lifecycle management
│   └── ...
├── presentation/
│   ├── adapter/           # RecyclerView adapters
│   └── ui/
│       ├── chat/          # Chat interface
│       ├── dashboard/     # Home screen
│       ├── settings/      # Settings screens
│       └── ...            # 13 screens total
├── receiver/
├── service/
│   ├── WakeWordService.kt # Wake word detection
│   ├── OverlayService.kt  # Floating bubble
│   └── ...
└── util/
```

## Permissions

JARVIS requires the following permissions:
- **Microphone** - Voice commands and wake word
- **Camera** - Flashlight control
- **Phone** - Call management
- **Notifications** - Reading and posting notifications
- **Overlay** - Floating bubble interface
- **Location** - Weather forecasts
- **Storage** - Local model import

## API Keys

To use cloud AI providers, add your API keys in Settings → AI Provider:
- **OpenAI**: https://platform.openai.com/api-keys
- **Gemini**: https://ai.google.dev/gemini-api/docs/api-keys
- **AgentRouter**: Your custom endpoint configuration

All keys are stored securely using EncryptedSharedPreferences.

## Local Models

JARVIS supports GGUF format models for local inference:
1. Go to Settings → Local Models
2. Tap "Import Model" and select a GGUF file
3. Wait for the import to complete
4. Set the model as active

Note: Local inference requires a device with the llama.cpp native library. A placeholder JNI implementation is included for devices without native support.

## License

```
MIT License

Copyright (c) 2024 ANISH SAINI

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files...
```

## Contact

**Developer:** ANISH SAINI  
**Email:** anishsaini939@gmail.com  
**GitHub:** [@anishsaini](https://github.com/anishsaini)

---

<div align="center">
  <sub>Built with ❤️ by ANISH SAINI</sub>
</div>
