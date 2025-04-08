package com.jiny.liftlink.inquiry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.jiny.liftlink.R;

public class Step1VehicleActivity extends AppCompatActivity {
    private Spinner spinnerVehicle;
    private Button btnNext;
    private String selectedVehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step1_vehicle);

        spinnerVehicle = findViewById(R.id.spinnerVehicle);
        btnNext = findViewById(R.id.btnNext);

        // Spinner 설정
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vehicle_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicle.setAdapter(adapter);

        spinnerVehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVehicle = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(Step1VehicleActivity.this, Step2HeightActivity.class);
            intent.putExtra("vehicle", selectedVehicle);
            startActivityForResult(intent, 1);
        });
    }
}
