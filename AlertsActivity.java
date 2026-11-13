package com.example.firesafetymonitoringsystem;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class AlertsActivity extends AppCompatActivity {

    private LinearLayout alertRecordsContainer;
    private TextView tvAlertsEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        alertRecordsContainer = findViewById(R.id.alertRecordsContainer);
        tvAlertsEmpty = findViewById(R.id.tvAlertsEmpty);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLatestAlerts();
    }

    private void loadLatestAlerts() {
        alertRecordsContainer.removeAllViews();

        android.content.SharedPreferences preferences =
                getSharedPreferences("fireguard_history", MODE_PRIVATE);

        String savedRecords = preferences.getString("records", "[]");

        try {
            JSONArray records = new JSONArray(savedRecords);

            if (records.length() == 0) {
                tvAlertsEmpty.setVisibility(TextView.VISIBLE);
                return;
            }

            JSONObject latestRecord = records.getJSONObject(0);
            JSONArray rooms = latestRecord.optJSONArray("rooms");

            if (rooms == null || rooms.length() == 0) {
                tvAlertsEmpty.setVisibility(TextView.VISIBLE);
                return;
            }

            tvAlertsEmpty.setVisibility(TextView.GONE);

            TextView checkTime = createText(
                    "Last check: " +
                            latestRecord.optString("dateTime", "Time unavailable"),
                    13,
                    "#AAB4CC",
                    false
            );

            checkTime.setPadding(0, dp(4), 0, dp(12));
            alertRecordsContainer.addView(checkTime);

            int riskCount = 0;

            for (int i = 0; i < rooms.length(); i++) {
                JSONObject room = rooms.optJSONObject(i);

                if (room == null) {
                    continue;
                }

                boolean fireRisk = room.optBoolean("fireRisk", false);

                if (fireRisk) {
                    riskCount++;
                }

                addRoomCard(room);
            }

            TextView summary = createText(
                    riskCount == 0
                            ? "✓  ALL ROOMS SAFE"
                            : "⚠  " + riskCount + " ROOM(S) NEED ATTENTION",
                    15,
                    riskCount == 0 ? "#65E6A5" : "#FF929A",
                    true
            );

            summary.setGravity(Gravity.CENTER);
            summary.setPadding(dp(12), dp(16), dp(12), dp(16));

            GradientDrawable summaryBackground = new GradientDrawable();
            summaryBackground.setColor(
                    riskCount == 0
                            ? Color.rgb(21, 42, 39)
                            : Color.rgb(53, 29, 41)
            );
            summaryBackground.setCornerRadius(dp(12));
            summary.setBackground(summaryBackground);

            alertRecordsContainer.addView(summary, 0);

        } catch (JSONException e) {
            tvAlertsEmpty.setVisibility(TextView.VISIBLE);
        }
    }

    private void addRoomCard(JSONObject room) {
        String roomName = room.optString("name", "Room");
        double temperature = room.optDouble("temperature", 0);
        boolean smoke = room.optBoolean("smokeDetected", false);
        boolean fireRisk = room.optBoolean("fireRisk", false);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));

        GradientDrawable background = new GradientDrawable();
        background.setColor(
                fireRisk
                        ? Color.rgb(53, 29, 41)
                        : Color.rgb(21, 42, 39)
        );
        background.setCornerRadius(dp(12));
        background.setStroke(
                dp(1),
                fireRisk
                        ? Color.rgb(255, 110, 120)
                        : Color.rgb(70, 190, 140)
        );
        card.setBackground(background);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        cardParams.bottomMargin = dp(12);
        card.setLayoutParams(cardParams);

        TextView heading = createText(
                (fireRisk ? "⚠  " : "✓  ") + roomName.toUpperCase(Locale.getDefault()),
                17,
                fireRisk ? "#FF929A" : "#65E6A5",
                true
        );
        card.addView(heading);

        String status;

        if (smoke && fireRisk) {
            status = "SMOKE ALERT";
        } else if (fireRisk) {
            status = "HIGH TEMPERATURE";
        } else {
            status = "SAFE";
        }

        String details =
                "Temperature: " +
                        String.format(Locale.getDefault(), "%.1f°C", temperature) +
                        "\nSmoke: " + (smoke ? "Detected" : "Not detected") +
                        "\nStatus: " + status;

        TextView detailText = createText(
                details,
                15,
                fireRisk ? "#F0DCE0" : "#D7E8E1",
                false
        );

        LinearLayout.LayoutParams detailParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        detailParams.topMargin = dp(8);
        detailText.setLayoutParams(detailParams);
        card.addView(detailText);

        alertRecordsContainer.addView(card);
    }

    private TextView createText(
            String text,
            int size,
            String color,
            boolean bold
    ) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(Color.parseColor(color));
        view.setTextSize(size);

        if (bold) {
            view.setTypeface(null, Typeface.BOLD);
        }

        view.setLineSpacing(dp(5), 1.0f);
        return view;
    }

    private int dp(int value) {
        return (int) (
                value * getResources().getDisplayMetrics().density + 0.5f
        );
    }
}
