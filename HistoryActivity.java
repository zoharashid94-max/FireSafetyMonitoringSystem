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

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout historyRecordsContainer;
    private TextView tvHistoryEmpty;
    private TextView tvHistoryDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyRecordsContainer = findViewById(R.id.historyRecordsContainer);
        tvHistoryEmpty = findViewById(R.id.tvHistoryEmpty);
        tvHistoryDescription = findViewById(R.id.tvHistoryDescription);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistoryRecords();
    }

    private void loadHistoryRecords() {
        historyRecordsContainer.removeAllViews();

        android.content.SharedPreferences preferences =
                getSharedPreferences("fireguard_history", MODE_PRIVATE);

        String savedRecords = preferences.getString("records", "[]");

        try {
            JSONArray records = new JSONArray(savedRecords);

            if (records.length() == 0) {
                tvHistoryEmpty.setVisibility(TextView.VISIBLE);
                tvHistoryDescription.setVisibility(TextView.VISIBLE);
                tvHistoryEmpty.setText("No saved safety checks yet.");
                tvHistoryDescription.setText(
                        "Open Monitoring and press Check Safety to save your first record."
                );
                return;
            }

            tvHistoryEmpty.setVisibility(TextView.GONE);
            tvHistoryDescription.setVisibility(TextView.GONE);

            for (int i = 0; i < records.length(); i++) {
                JSONObject record = records.getJSONObject(i);
                addHistoryCard(record);
            }

        } catch (JSONException e) {
            tvHistoryEmpty.setVisibility(TextView.VISIBLE);
            tvHistoryDescription.setVisibility(TextView.VISIBLE);
            tvHistoryEmpty.setText("Unable to load history.");
            tvHistoryDescription.setText(
                    "Please run a new safety check and try again."
            );
        }
    }

    private void addHistoryCard(JSONObject record) {
        String dateTime = record.optString("dateTime", "Date unavailable");
        String summary = record.optString("summary", "UNKNOWN");
        int totalRooms = record.optInt("totalRooms", 0);
        int safeRooms = record.optInt("safeRooms", 0);
        int dangerRooms = record.optInt("dangerRooms", 0);

        boolean hasRisk = dangerRooms > 0;

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.rgb(8, 11, 24));
        background.setCornerRadius(dp(14));
        background.setStroke(
                dp(1),
                hasRisk ? Color.rgb(255, 95, 95) : Color.rgb(48, 190, 140)
        );
        card.setBackground(background);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        cardParams.bottomMargin = dp(14);
        card.setLayoutParams(cardParams);

        TextView dateText = createText(dateTime, 13, "#AAB4CC", false);
        card.addView(dateText);

        TextView statusText = createText(
                hasRisk ? "⚠ " + summary : "✓ " + summary,
                17,
                hasRisk ? "#FF6B6B" : "#42D6A4",
                true
        );
        setTopMargin(statusText, 10);
        card.addView(statusText);

        TextView countsText = createText(
                "Total rooms: " + totalRooms
                        + "\nSafe rooms: " + safeRooms
                        + "\nRooms with risk: " + dangerRooms,
                14,
                "#FFFFFF",
                false
        );
        setTopMargin(countsText, 10);
        card.addView(countsText);

        JSONArray rooms = record.optJSONArray("rooms");

        if (rooms != null && rooms.length() > 0) {
            TextView roomHeading = createText("ROOM DETAILS", 12, "#AAB4CC", true);
            setTopMargin(roomHeading, 16);
            card.addView(roomHeading);

            for (int i = 0; i < rooms.length(); i++) {
                JSONObject room = rooms.optJSONObject(i);

                if (room == null) {
                    continue;
                }

                String roomName = room.optString("name", "Room");
                double temperature = room.optDouble("temperature", 0);
                boolean smoke = room.optBoolean("smokeDetected", false);
                boolean fireRisk = room.optBoolean("fireRisk", false);

                String roomStatus = fireRisk ? "RISK DETECTED" : "SAFE";

                String details = roomName
                        + "\nTemperature: "
                        + String.format(java.util.Locale.getDefault(), "%.1f°C", temperature)
                        + "\nSmoke detected: " + (smoke ? "Yes" : "No")
                        + "\nStatus: " + roomStatus;

                TextView roomText = createText(
                        details,
                        13,
                        fireRisk ? "#FF8B8B" : "#C9D3E8",
                        false
                );

                setTopMargin(roomText, 12);
                card.addView(roomText);
            }
        }

        historyRecordsContainer.addView(card);
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

        view.setLineSpacing(dp(4), 1.0f);
        return view;
    }

    private void setTopMargin(TextView view, int marginDp) {
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.topMargin = dp(marginDp);
        view.setLayoutParams(params);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
