package com.jiny.liftlink.inquiry.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.jiny.liftlink.R;

public class SelectWorkActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.jiny.liftlink.R.layout.activity_select_work);

        // ListView 가져오기
        ListView listView = findViewById(R.id.listView_work_types);

        // 작업 내용 배열 불러오기
        String[] workTypes = getResources().getStringArray(R.array.work_types);

        // 어댑터 설정 (단순 텍스트 목록)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_single_choice, // 선택 가능한 리스트
                workTypes
        );

        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // 단일 선택 모드

        // 아이템 클릭 시 선택한 값 반환
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedWork = workTypes[position];
            Toast.makeText(SelectWorkActivity.this, "선택한 작업: " + selectedWork, Toast.LENGTH_SHORT).show();

            // 선택한 작업 내용을 이전 액티비티로 반환
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_work", selectedWork);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
