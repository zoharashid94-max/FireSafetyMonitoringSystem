package com.example.firesafetymonitoringsystem;
public class FireSafetySystem {

    public String checkRoom(Room room) {

        if (room.isFireRisk()) {
            return "WARNING: Fire risk detected in "
                    + room.getRoomName() + "!";
        }

        return "SAFE: " + room.getRoomName()
                + " is currently safe.";
    }

    public String getSystemStatus(Room room) {

        if (room.isFireRisk()) {
            return "DANGER";
        }

        return "SYSTEM NORMAL";
    }
}
