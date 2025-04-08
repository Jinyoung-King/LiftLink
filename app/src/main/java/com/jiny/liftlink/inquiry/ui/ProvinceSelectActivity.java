package com.jiny.liftlink.inquiry.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jiny.liftlink.R;
import com.jiny.liftlink.common.utils.RegionAPIClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProvinceSelectActivity extends AppCompatActivity {
    private static final int REQUEST_DISTRICT_SELECT = 1002;
    private ListView provinceListView;

    private JSONArray provinceArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province_select);

        provinceListView = findViewById(R.id.provinceListView);
        new FetchProvincesTask().execute(); // API 호출

        // 리스트 아이템 클릭 시 해당 시/도 선택
        provinceListView.setOnItemClickListener((parent, view, position, id) -> {
            try {
                JSONObject jsonObject = provinceArray.getJSONObject(position);
                String provinceCode = jsonObject.getString("code");
                String provinceName = jsonObject.getString("name");
                Intent intent = new Intent(ProvinceSelectActivity.this, DistrictSelectActivity.class);
                intent.putExtra("provinceCode", provinceCode);
                intent.putExtra("provinceName", provinceName);
                Toast.makeText(ProvinceSelectActivity.this, "선택한 지역: " + provinceName, Toast.LENGTH_SHORT).show();
                Log.d("Province Select", "선택된 시/도 값: " + provinceName);
                startActivityForResult(intent, REQUEST_DISTRICT_SELECT);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        });
    }

    // ✅ API 호출하여 시/도 목록 가져오는 비동기 작업
    private class FetchProvincesTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> resultList = new ArrayList<>();
            try {
                provinceArray = RegionAPIClient.getProvincesArray();
                for (int i = 0; i < provinceArray.length(); i++) {
                    resultList.add(provinceArray.getString(i));
                }
            } catch (Exception e) {
                Log.e("Province Select", "모든 특별/광역시, 도 반환 작업 중 오류 발생", e);
            }
            return resultList;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            List<String> provinceNames = new ArrayList<>(result.size());
            for (int i = 0; i < provinceArray.length(); i++) {
                JSONObject jsonObject;
                try {
                    jsonObject = provinceArray.getJSONObject(i);
                    provinceNames.add(jsonObject.getString("name"));
                } catch (JSONException e) {
                    Log.e("Province Select", i + "번째 JSONArray 인덱스에서 문제 발생", e);
                    throw new RuntimeException(e);
                }

            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(ProvinceSelectActivity.this, android.R.layout.simple_list_item_1, provinceNames);
            provinceListView.setAdapter(adapter);
        }
    }

    // ✅ 2번 액티비티에서 선택한 결과 받기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("Province Select", "Request Code: " + requestCode + " | Result Code: " + resultCode + " | Data: " + data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DISTRICT_SELECT && resultCode == RESULT_OK && data != null) {
            String provinceName = data.getStringExtra("provinceName");
            String districtName = data.getStringExtra("districtName");

            // ✅ 메인 액티비티로 선택 결과 전달
            Intent resultIntent = new Intent();
            resultIntent.putExtra("provinceName", provinceName);
            resultIntent.putExtra("districtName", districtName);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}
