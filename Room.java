package com.example.firesafetymonitoringsystem;
public class Room {

    private String roomName;
    private double temperature;
    private boolean smokeDetected;

    public Room(String roomName, double temperature,
                boolean smokeDetected) {
        this.roomName = roomName;
        this.temperature = temperature;
        this.smokeDetected = smokeDetected;
    }

    public String getRoomName() {
        return roomName;
    }

    public double getTemperature() {
        return temperature;
    }

    public boolean isSmokeDetected() {
        return smokeDetected;
    }

    public boolean isFireRisk() {
        return temperature >= 60 || smokeDetected;
    }

    public String getStatus() {
        if (isFireRisk()) {
            return "WARNING: Possible Fire Risk!";
        }

        return "Normal: No Fire Risk Detected";
    }
}

