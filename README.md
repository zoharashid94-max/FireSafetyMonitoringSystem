# FIREGUARD: Fire Safety Monitoring System

FIREGUARD is a fire safety monitoring project I developed to make it easier to view room safety conditions and identify potential fire risks through a simple, easy-to-understand interface.

The system displays room temperature and smoke status, highlights potentially dangerous conditions, and provides separate sections for monitoring, alerts, and safety history. I designed it to present important safety information clearly rather than overwhelming users with technical details.

## Features

- **Dashboard:** Displays the total number of monitored rooms and their current safety status.
- **Room Monitoring:** Shows temperature readings and smoke status for individual rooms.
- **Fire Risk Detection:** Identifies potentially dangerous conditions based on the displayed temperature and smoke readings.
- **Safety Alerts:** Provides a dedicated section for viewing safety-related alerts.
- **Monitoring History:** Stores completed safety checks so previous results can be reviewed.
- **System Information:** Provides information about the monitoring system.

## Technologies Used

- **Java** for application logic
- **XML** for designing the user interface
- **Android Studio** for development
- **SharedPreferences** for storing monitoring history locally
- **JSON** for organizing saved monitoring records

## Example Monitoring Scenario

FIREGUARD demonstrates its monitoring functionality using three simulated rooms:

| Room | Temperature | Smoke Status | Result |
|---|---:|---|---|
| Kitchen | 32.5°C | Not detected | Safe |
| Store Room | 68.0°C | Not detected | Danger |
| Office | 28.0°C | Detected | Danger |

These readings are predefined examples used to demonstrate how the system displays different safety conditions.

## How It Works

When the application starts, the dashboard displays the default safety counters. After a safety check is performed, the system evaluates the simulated room conditions and displays the results.

The application identifies the Store Room as a potential risk because of its high temperature and the Office because smoke is detected. The completed check is saved locally, allowing users to review previous monitoring results in the History section.

## Project Structure

The project includes separate screens for:

- Home Dashboard
- Room Monitoring
- Safety Alerts
- Monitoring History
- System Information

Each screen serves a specific purpose and helps keep the application's functionality organized.

## Getting Started

To explore the project locally:

1. Clone or download this repository.
2. Open the project in Android Studio.
3. Allow Gradle to sync and download the required dependencies.
4. Run the application on an Android emulator or compatible device.

**Requirements:** Android Studio, a compatible Java development environment, and the Android SDK required by the project.

## Current Limitations

FIREGUARD currently uses simulated room readings. It is not connected to physical temperature sensors, smoke detectors, or fire alarm hardware. Its purpose is to demonstrate the interface, monitoring workflow, risk identification logic, and local history management.

## Future Improvements

Possible future enhancements include:

- Integration with physical temperature and smoke sensors
- Real-time monitoring and notifications
- More detailed safety reports
- Improved monitoring and risk analysis

## About the Project

I built FIREGUARD as a practical software development project to explore how a digital system can present safety information in a clear and organized way. Working on it helped me practise Java programming, XML-based interface design, activity navigation, and local data storage.

This project is a starting point for exploring how software can support fire safety monitoring in the future.
