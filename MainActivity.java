package com.example.firesafetymonitoringsystem;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvSystemStatus;
    private TextView tvKitchen;
    private TextView tvStoreRoom;
    private TextView tvOffice;
    private TextView tvAlert;

    private TextView tvTotalRoomsCount;
    private TextView tvSafeRoomsCount;
    private TextView tvDangerRoomsCount;

    private Button btnCheckSafety;

    private Room kitchen;
    private Room storeRoom;
    private Room office;

    private FireSafetySystem safetySystem;

    private final int safeBackground = Color.rgb(21, 55, 47);
    private final int dangerBackground = Color.rgb(67, 28, 42);
    private final int neutralBackground = Color.rgb(23, 29, 48);

    private final int safeText = Color.rgb(101, 230, 165);
    private final int dangerText = Color.rgb(255, 146, 154);
    private final int neutralText = Color.rgb(220, 226, 240);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSystemStatus = findViewById(R.id.tvSystemStatus);
        tvKitchen = findViewById(R.id.tvKitchen);
        tvStoreRoom = findViewById(R.id.tvStoreRoom);
        tvOffice = findViewById(R.id.tvOffice);
        tvAlert = findViewById(R.id.tvAlert);

        tvTotalRoomsCount = findViewById(R.id.tvTotalRoomsCount);
        tvSafeRoomsCount = findViewById(R.id.tvSafeRoomsCount);
        tvDangerRoomsCount = findViewById(R.id.tvDangerRoomsCount);

        btnCheckSafety = findViewById(R.id.btnCheckSafety);

        // Simulated room readings
        kitchen = new Room("Kitchen", 32.5, false);
        storeRoom = new Room("Store Room", 68.0, false);
        office = new Room("Office", 28.0, true);

        safetySystem = new FireSafetySystem();

        showInitialState();

        btnCheckSafety.setOnClickListener(view -> checkAllRooms());
    }

    private void showInitialState() {

        tvSystemStatus.setText("●  READY TO CHECK");
        tvSystemStatus.setBackgroundColor(neutralBackground);
        tvSystemStatus.setTextColor(neutralText);

        tvTotalRoomsCount.setText("3");
        tvSafeRoomsCount.setText("—");
        tvDangerRoomsCount.setText("—");

        tvKitchen.setText(
                "🍳  KITCHEN\n\n" +
                        "Temperature: —\n" +
                        "Smoke status: —\n\n" +
                        "Press Run Safety Check to inspect"
        );

        tvStoreRoom.setText(
                "📦  STORE ROOM\n\n" +
                        "Temperature: —\n" +
                        "Smoke status: —\n\n" +
                        "Press Run Safety Check to inspect"
        );

        tvOffice.setText(
                "💻  OFFICE\n\n" +
                        "Temperature: —\n" +
                        "Smoke status: —\n\n" +
                        "Press Run Safety Check to inspect"
        );

        setRoomNeutral(tvKitchen);
        setRoomNeutral(tvStoreRoom);
        setRoomNeutral(tvOffice);

        tvAlert.setText(
                "SAFETY CHECK NOT RUN\n\n" +
                        "Press the button below to check the " +
                        "simulated room readings and view the results."
        );

        tvAlert.setBackgroundColor(neutralBackground);
        tvAlert.setTextColor(neutralText);

        btnCheckSafety.setText("▶  Run Safety Check");
    }

    private void checkAllRooms() {

        updateRoomStatus(tvKitchen, kitchen, "🍳");
        updateRoomStatus(tvStoreRoom, storeRoom, "📦");
        updateRoomStatus(tvOffice, office, "💻");

        int totalRooms = 3;
        int safeRooms = 0;
        int dangerRooms = 0;

        if (kitchen.isFireRisk()) {
            dangerRooms++;
        } else {
            safeRooms++;
        }

        if (storeRoom.isFireRisk()) {
            dangerRooms++;
        } else {
            safeRooms++;
        }

        if (office.isFireRisk()) {
            dangerRooms++;
        } else {
            safeRooms++;
        }

        tvTotalRoomsCount.setText(String.valueOf(totalRooms));
        tvSafeRoomsCount.setText(String.valueOf(safeRooms));
        tvDangerRoomsCount.setText(String.valueOf(dangerRooms));

        // Save completed safety check and update dashboard session status.
        saveHistoryRecord(totalRooms, safeRooms, dangerRooms);

        if (dangerRooms > 0) {

            tvSystemStatus.setText("⚠  WARNING: RISK DETECTED");
            tvSystemStatus.setBackgroundColor(Color.rgb(113, 31, 43));
            tvSystemStatus.setTextColor(dangerText);

            tvAlert.setText(
                    "⚠  SAFETY ALERT\n\n" +
                            dangerRooms + " room(s) require attention.\n" +
                            "Review the room details below."
            );

            tvAlert.setBackgroundColor(dangerBackground);
            tvAlert.setTextColor(dangerText);

        } else {

            tvSystemStatus.setText("✓  ALL ROOMS SAFE");
            tvSystemStatus.setBackgroundColor(safeBackground);
            tvSystemStatus.setTextColor(safeText);

            tvAlert.setText(
                    "✓  ALL CLEAR\n\n" +
                            "No risks were identified in the checked " +
                            "simulated readings."
            );

            tvAlert.setBackgroundColor(safeBackground);
            tvAlert.setTextColor(safeText);
        }

        btnCheckSafety.setText("↻  Run Safety Check Again");
    }

    private void saveHistoryRecord(
            int totalRooms,
            int safeRooms,
            int dangerRooms
    ) {

        SharedPreferences preferences = getSharedPreferences(
                "fireguard_history",
                MODE_PRIVATE
        );

        JSONArray history;

        try {
            history = new JSONArray(
                    preferences.getString("records", "[]")
            );
        } catch (JSONException e) {
            history = new JSONArray();
        }

        try {
            JSONObject record = new JSONObject();

            String dateTime = new SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a",
                    Locale.getDefault()
            ).format(new Date());

            record.put("dateTime", dateTime);
            record.put("timestamp", System.currentTimeMillis());
            record.put("totalRooms", totalRooms);
            record.put("safeRooms", safeRooms);
            record.put("dangerRooms", dangerRooms);

            record.put(
                    "summary",
                    dangerRooms > 0
                            ? "RISK DETECTED"
                            : "ALL ROOMS SAFE"
            );

            JSONArray rooms = new JSONArray();
            rooms.put(createRoomRecord(kitchen));
            rooms.put(createRoomRecord(storeRoom));
            rooms.put(createRoomRecord(office));

            record.put("rooms", rooms);

            // Keep the newest record first and preserve older history.
            JSONArray updatedHistory = new JSONArray();
            updatedHistory.put(record);

            for (int i = 0; i < history.length(); i++) {
                updatedHistory.put(history.getJSONObject(i));
            }

            // Save history and mark that a check was performed this session.
            preferences.edit()
                    .putString("records", updatedHistory.toString())
                    .putBoolean("check_performed_this_session", true)
                    .apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject createRoomRecord(Room room)
            throws JSONException {

        JSONObject roomRecord = new JSONObject();

        roomRecord.put("name", room.getRoomName());
        roomRecord.put("temperature", room.getTemperature());
        roomRecord.put("smokeDetected", room.isSmokeDetected());
        roomRecord.put("fireRisk", room.isFireRisk());

        return roomRecord;
    }

    private void updateRoomStatus(
            TextView roomView,
            Room room,
            String icon
    ) {

        boolean danger = room.isFireRisk();

        String status = safetySystem.checkRoom(room);

        String smokeStatus = room.isSmokeDetected()
                ? "DETECTED"
                : "NOT DETECTED";

        String safetyLabel = danger
                ? "⚠  DANGER"
                : "✓  SAFE";

        String roomText =
                icon + "  " + room.getRoomName().toUpperCase(Locale.US) +
                        "\n━━━━━━━━━━━━━━━━━━\n\n" +
                        "🌡  TEMPERATURE\n" +
                        String.format(
                                Locale.US,
                                "%.1f °C",
                                room.getTemperature()
                        ) +
                        "\n\n" +
                        "💨  SMOKE DETECTION\n" +
                        smokeStatus +
                        "\n\n" +
                        "🛡  SAFETY STATUS\n" +
                        safetyLabel +
                        "\n\n" +
                        status;

        roomView.setText(roomText);
        roomView.setTextSize(14);
        roomView.setTypeface(
                Typeface.create("sans-serif", Typeface.NORMAL)
        );
        roomView.setLineSpacing(3, 1.0f);

        if (danger) {
            roomView.setBackgroundColor(dangerBackground);
            roomView.setTextColor(dangerText);
        } else {
            roomView.setBackgroundColor(safeBackground);
            roomView.setTextColor(safeText);
        }
    }

    private void setRoomNeutral(TextView roomView) {

        roomView.setBackgroundColor(neutralBackground);
        roomView.setTextColor(neutralText);
        roomView.setTextSize(14);
        roomView.setTypeface(
                Typeface.create("sans-serif", Typeface.NORMAL)
        );
        roomView.setLineSpacing(3, 1.0f);
    }
}

