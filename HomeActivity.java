package com.example.firesafetymonitoringsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout cardMonitoring;
    private LinearLayout cardAlerts;
    private LinearLayout cardHistory;
    private LinearLayout cardSystemInfo;

    private TextView tvHomeStatus;
    private TextView tvHomeStatusDescription;
    private TextView tvHomeLastCheck;
    private TextView tvHomeTotalRooms;
    private TextView tvHomeSafeRooms;
    private TextView tvHomeDangerRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Reset dashboard results whenever the app launches.
        // Old history records are preserved.
        SharedPreferences preferences =
                getSharedPreferences("fireguard_history", MODE_PRIVATE);

        preferences.edit()
                .putBoolean("check_performed_this_session", false)
                .apply();

        tvHomeStatus = findViewById(R.id.tvHomeStatus);
        tvHomeStatusDescription =
                findViewById(R.id.tvHomeStatusDescription);
        tvHomeLastCheck = findViewById(R.id.tvHomeLastCheck);

        tvHomeTotalRooms = findViewById(R.id.tvHomeTotalRooms);
        tvHomeSafeRooms = findViewById(R.id.tvHomeSafeRooms);
        tvHomeDangerRooms = findViewById(R.id.tvHomeDangerRooms);

        cardMonitoring = findViewById(R.id.cardMonitoring);
        cardAlerts = findViewById(R.id.cardAlerts);
        cardHistory = findViewById(R.id.cardHistory);
        cardSystemInfo = findViewById(R.id.cardSystemInfo);

        cardMonitoring.setOnClickListener(v ->
                openScreen(MainActivity.class));

        cardAlerts.setOnClickListener(v ->
                openScreen(AlertsActivity.class));

        cardHistory.setOnClickListener(v ->
                openScreen(HistoryActivity.class));

        cardSystemInfo.setOnClickListener(v ->
                openScreen(SystemInfoActivity.class));

        loadDashboardData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh when returning from Monitoring.
        // Do not reset the session flag here.
        loadDashboardData();
    }

    private void loadDashboardData() {

        SharedPreferences preferences =
                getSharedPreferences("fireguard_history", MODE_PRIVATE);

        String savedRecords =
                preferences.getString("records", "[]");

        try {
            JSONArray records = new JSONArray(savedRecords);

            boolean checkPerformedThisSession =
                    preferences.getBoolean(
                            "check_performed_this_session", false);

            // Show default counters until a check is performed
            // after the current app launch.
            if (!checkPerformedThisSession || records.length() == 0) {

                tvHomeStatus.setText("READY TO CHECK");
                tvHomeStatus.setTextColor(
                        Color.rgb(255, 180, 84));

                tvHomeStatusDescription.setText(
                        "Open Monitoring and run a safety check."
                );

                tvHomeLastCheck.setText(
                        "No check recorded this session");

                tvHomeTotalRooms.setText("3");
                tvHomeSafeRooms.setText("0");
                tvHomeDangerRooms.setText("0");

                return;
            }

            // Show the latest check performed in this session.
            JSONObject latestRecord = records.getJSONObject(0);

            int totalRooms = latestRecord.optInt("totalRooms", 3);
            int safeRooms = latestRecord.optInt("safeRooms", 0);
            int dangerRooms = latestRecord.optInt("dangerRooms", 0);

            String dateTime = latestRecord.optString(
                    "dateTime", "Time unavailable");

            tvHomeTotalRooms.setText(String.valueOf(totalRooms));
            tvHomeSafeRooms.setText(String.valueOf(safeRooms));
            tvHomeDangerRooms.setText(String.valueOf(dangerRooms));

            if (dangerRooms > 0) {

                tvHomeStatus.setText("RISK DETECTED");
                tvHomeStatus.setTextColor(
                        Color.rgb(255, 110, 120));

                tvHomeStatusDescription.setText(
                        dangerRooms + " room(s) need attention. " +
                                "Open Alerts to review the reported risks."
                );

            } else {

                tvHomeStatus.setText("ALL ROOMS SAFE");
                tvHomeStatus.setTextColor(
                        Color.rgb(101, 230, 165));

                tvHomeStatusDescription.setText(
                        "The latest safety check reported all rooms safe."
                );
            }

            tvHomeLastCheck.setText(dateTime);

        } catch (JSONException e) {

            tvHomeStatus.setText("DATA UNAVAILABLE");
            tvHomeStatus.setTextColor(
                    Color.rgb(255, 180, 84));

            tvHomeStatusDescription.setText(
                    "Unable to read saved safety records."
            );

            tvHomeLastCheck.setText("Check history data");

            tvHomeTotalRooms.setText("3");
            tvHomeSafeRooms.setText("0");
            tvHomeDangerRooms.setText("0");
        }
    }

    private void openScreen(Class<?> screenClass) {
        Intent intent = new Intent(HomeActivity.this, screenClass);
        startActivity(intent);
    }
}
