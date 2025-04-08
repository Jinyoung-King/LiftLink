package com.jiny.liftlink.inquiry.ui;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.jiny.liftlink.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EstimatedTimeActivity extends AppCompatActivity {

    private EditText etStartTime, etEndTime;
    private TextView tvDuration;
    private Calendar startTime, endTime;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimated_time);

        etStartTime = findViewById(R.id.et_start_time);
        etEndTime = findViewById(R.id.et_end_time);
        tvDuration = findViewById(R.id.tv_duration);
        Button btnNext = findViewById(R.id.btn_next);

        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();

        etStartTime.setOnClickListener(v -> showTimePicker(true));
        etEndTime.setOnClickListener(v -> showTimePicker(false));

        btnNext.setOnClickListener(v -> {
            // 다음 단계로 이동하는 코드 추가 (예: Intent)
            Intent resultIntent = new Intent();
            resultIntent.putExtra("duration", tvDuration.getText());
            setResult(RESULT_OK, resultIntent);
            finish(); // 현재 액티비티 종료
        });
    }

    private void showTimePicker(boolean isStartTime) {
        Calendar calendar = isStartTime ? startTime : endTime;
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            updateDisplayedTime(isStartTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void updateDisplayedTime(boolean isStartTime) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        if (isStartTime) {
            etStartTime.setText(timeFormat.format(startTime.getTime()));
        } else {
            etEndTime.setText(timeFormat.format(endTime.getTime()));
            calculateDuration();
        }
    }

    private void calculateDuration() {
        long diff = endTime.getTimeInMillis() - startTime.getTimeInMillis();
        if (diff < 0) {
            tvDuration.setText("올바른 시간을 입력하세요");
            return;
        }
        long minutes = diff / (1000 * 60);
        long hours = minutes / 60;
        minutes %= 60;
        tvDuration.setText(String.format(Locale.getDefault(), "예상 소요 시간: %d시간 %d분", hours, minutes));
    }
}
