package com.jiny.liftlink.inquiry.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jiny.liftlink.R;
import com.jiny.liftlink.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewInquiryActivity extends AppCompatActivity {
    private static final String INQUIRIES_PATH = "inquiries";
    private static final String VEHICLES_PATH = "vehicles";

    private static final int REQUEST_REGION_SELECT = 1001; // 지역 선택 요청 코드
    private static final int REQUEST_DURATION_SELECT = 2001; // 지역 선택 요청 코드
    private TextView txtSelectedRegion;
    private TextView txtSelectedDuration;

    private int currentStep = 1;
    private ViewGroup[] steps;
    private Button btnPrevious, btnNext;
    private TextView tvSelectedDate;

    private String selectedVehicle;
    private String selectedHeight;
    private String selectedWork;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_inquiry);

        // 단계별 레이아웃 연결
        steps = new ViewGroup[]{
                findViewById(R.id.step1_vehicle),
                findViewById(R.id.step2_height),
                findViewById(R.id.step3_task),
                findViewById(R.id.step4_location),
                findViewById(R.id.step5_date),
                findViewById(R.id.step6_time)
        };

        // UI 요소 초기화
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext); btnNext.setVisibility(View.GONE);
        tvSelectedDate = findViewById(R.id.btnSelectDate);

        // 1️⃣ ListView 참조
        ListView listViewVehicle = findViewById(R.id.listViewVehicle);

        // 2️⃣ 차량 목록 데이터 준비
        List<String> vehicleList = new ArrayList<>();

        // 3️⃣ ArrayAdapter 설정 (기본 리스트 아이템 스타일 사용)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, vehicleList);

        // 4️⃣ ListView에 어댑터 연결
        listViewVehicle.setAdapter(adapter);
        listViewVehicle.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // 단일 선택 모드 설정

        // 5️⃣ 차량 데이터 로드
        loadVehicleData(vehicleList, adapter);

        // 6️⃣ 아이템 클릭 이벤트 처리
        listViewVehicle.setOnItemClickListener((parent, view, position, id) -> {
            selectedVehicle = vehicleList.get(position);
//            Toast.makeText(NewInquiryActivity.this, "선택한 차량: " + selectedVehicle, Toast.LENGTH_SHORT).show();
            btnNext.setVisibility(View.VISIBLE);
        });


        // 초기 상태 설정
        updateStepUI();

        // 다음 버튼 클릭 이벤트
        btnNext.setOnClickListener(v -> {
            if (currentStep < steps.length) {
                currentStep++;
                updateStepUI();
            } else if ("완료".contentEquals(btnNext.getText())) {
                String inquiryId = db.collection(INQUIRIES_PATH).document().getId();

                Map<String, Object> reservation = new HashMap<>();
                reservation.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                reservation.put("vehicle", selectedVehicle);
                reservation.put("height", selectedHeight);
                reservation.put("work", selectedWork);
                reservation.put("region", txtSelectedRegion.getText());
                reservation.put("date", tvSelectedDate.getText());
                reservation.put("duration", txtSelectedDuration.getText());
                reservation.put("timestamp", new Timestamp(new Date()));

                db.collection(INQUIRIES_PATH).document(inquiryId)
                        .set(reservation)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(NewInquiryActivity.this, "견적 문의 완료!", Toast.LENGTH_SHORT).show();
                            // 테스트 성공 시 홈으로 이동
                            Intent intent = new Intent(NewInquiryActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // 견적 문의 화면 종료
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(NewInquiryActivity.this, "견적 문의 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        // 이전 버튼 클릭 이벤트
        btnPrevious.setOnClickListener(v -> {
            if (currentStep > 1) {
                currentStep--;
                updateStepUI();
            }
        });

        /// 2. 작업 높이 선택
        // ListView 가져오기
        ListView listView = findViewById(R.id.listView_heights);

        // 배열 불러오기
        String[] workHeights = getResources().getStringArray(R.array.work_heights);

        // 어댑터 설정
        listView.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_single_choice,
                workHeights
        ));
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        // 아이템 클릭 시 선택한 높이 반환
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedHeight = workHeights[position];
                Toast.makeText(NewInquiryActivity.this, "선택한 높이: " + selectedHeight, Toast.LENGTH_SHORT).show();
            }
        });


        /// 3. 작업 내용 선택
        // ListView 가져오기
        ListView listView_work_types = findViewById(R.id.listView_work_types);

        // 작업 내용 배열 불러오기
        String[] workTypes = getResources().getStringArray(R.array.work_types);

        // 어댑터 설정 (단순 텍스트 목록)
        listView_work_types.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_single_choice, // 선택 가능한 리스트
                workTypes
        ));
        listView_work_types.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // 단일 선택 모드

        // 아이템 클릭 시 선택한 값 반환
        listView_work_types.setOnItemClickListener((parent, view, position, id) -> {
            selectedWork = workTypes[position];
            Toast.makeText(NewInquiryActivity.this, "선택한 작업: " + selectedWork, Toast.LENGTH_SHORT).show();
        });

        /// 4. 지역 선택
        Button btnSelectRegion = findViewById(R.id.btnSelectRegion);
        txtSelectedRegion = findViewById(R.id.txtSelectedRegion);

        // ✅ 지역 선택 버튼 클릭 시 `ProvinceSelectActivity` 실행
        btnSelectRegion.setOnClickListener(v -> {
            Intent intent = new Intent(NewInquiryActivity.this, ProvinceSelectActivity.class);
            startActivityForResult(intent, REQUEST_REGION_SELECT);
        });


        // 날짜 선택 버튼
        tvSelectedDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                tvSelectedDate.setText(String.format("%04d-%02d-%02d", year, month + 1, day));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        txtSelectedDuration = findViewById(R.id.txtSelectedDuration);
        Button btnGoToEstimatedTime = findViewById(R.id.btn_go_to_estimated_time);
        btnGoToEstimatedTime.setOnClickListener(v -> {
            Intent intent = new Intent(NewInquiryActivity.this, EstimatedTimeActivity.class);
            startActivityForResult(intent, REQUEST_DURATION_SELECT);
        });
    }

    private void loadVehicleData(List<String> vehicleList, ArrayAdapter<String> adapter) {

        db.collection(VEHICLES_PATH)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "실시간 데이터 로드 실패", error);
                        return;
                    }

                    vehicleList.clear(); // 기존 데이터 삭제

                    if (value != null) {
                        for (DocumentSnapshot document : value.getDocuments()) {
                            String vehicleName = document.getString("desc");
                            if (vehicleName != null) {
                                vehicleList.add(vehicleName);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged(); // UI 업데이트
                });
    }

    private void updateStepUI() {
        for (int i = 0; i < steps.length; i++) {
            steps[i].setVisibility(i + 1 == currentStep ? View.VISIBLE : View.GONE);
        }

        // 버튼 가시성 변경
        btnPrevious.setVisibility(currentStep == 1 ? View.GONE : View.VISIBLE);
        btnNext.setText(currentStep == steps.length ? "완료" : "다음");
    }

    // ✅ 지역 선택 후 결과 받기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("New Inquiry", "Request Code: " + requestCode + " | Result Code: " + resultCode + " | Data: " + data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REGION_SELECT && resultCode == RESULT_OK && data != null) {
            String provinceName = data.getStringExtra("provinceName");
            String districtName = data.getStringExtra("districtName");
            Log.d("New Inquiry", "provinceName: " + provinceName + " | districtName: " + districtName);

            if (districtName != null) {
                txtSelectedRegion.setText(districtName);
            }
        } else if (requestCode == REQUEST_DURATION_SELECT && resultCode == RESULT_OK && data != null) {
            String duration = data.getStringExtra("duration");
            Log.d("New Inquiry", "소요 시간: " + duration);
            if (duration != null) {
                txtSelectedDuration.setText(duration);
            }
        }
    }
}
