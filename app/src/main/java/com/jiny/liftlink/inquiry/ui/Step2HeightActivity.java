package com.jiny.liftlink.inquiry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jiny.liftlink.R;

public class Step2HeightActivity extends AppCompatActivity {
    private String selectedVehicle, selectedHeight;
    private Button btnNext, btnPrevious;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2_height);

        // 이전 액티비티에서 전달된 데이터 받기
        selectedVehicle = getIntent().getStringExtra("vehicle");

        /// 값 가져오기
        // ListView 가져오기
        ListView listView = findViewById(R.id.listView_heights);

        // 배열 불러오기
        String[] workHeights = getResources().getStringArray(R.array.work_heights);

        // 어댑터 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                workHeights
        );

        listView.setAdapter(adapter);

        // 아이템 클릭 시 선택한 높이 반환
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedHeight = workHeights[position];
            Toast.makeText(Step2HeightActivity.this, "선택한 높이: " + selectedHeight, Toast.LENGTH_SHORT).show();

            // 선택된 높이를 이전 액티비티로 전달
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_height", selectedHeight);
            setResult(RESULT_OK, resultIntent);
            finish();
        });


        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(Step2HeightActivity.this, SelectWorkActivity.class);
            intent.putExtra("vehicle", selectedVehicle);
            intent.putExtra("height", selectedHeight);
            startActivityForResult(intent, 2);
//            Toast.makeText(Step2HeightActivity.this, "Test Completed", LENGTH_SHORT).show();
//            // 테스트 성공 시 홈으로 이동
//            Intent intent = new Intent(Step2HeightActivity.this, HomeActivity.class);
//            startActivity(intent);
//            finish(); // 단계2 화면 종료
        });

        btnPrevious.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("vehicle", selectedVehicle);
            setResult(RESULT_CANCELED, returnIntent);
            finish(); // 이전 단계로 돌아감
        });
    }
}
