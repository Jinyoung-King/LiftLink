package com.jiny.liftlink.inquiry.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jiny.liftlink.R;
import com.jiny.liftlink.common.utils.RegionAPIClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DistrictSelectActivity extends AppCompatActivity {
    private static final String API_KEY = "YOUR_API_KEY"; // 여기에 API 키 입력
    private ListView districtListView;

    private JSONArray districtArray;
    private String selectedProvinceCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_district_select);

        districtListView = findViewById(R.id.districtListView);
        selectedProvinceCode = getIntent().getStringExtra("provinceCode");

        new FetchDistrictsTask().execute(); // API 호출

        // 리스트 아이템 클릭 시 선택된 지역 반환
        districtListView.setOnItemClickListener((parent, view, position, id) -> {

            try {
                JSONObject  jsonObject = districtArray.getJSONObject(position);
                String districtCode = jsonObject.getString("code");
                String districtName = jsonObject.getString("name");
                /// 이 객체에 설정한 값만 NewInquiryActivity onActivityResult() 메소드 호출로 전달
                Intent resultIntent = new Intent();
                resultIntent.putExtra("districtCode", districtCode);
                resultIntent.putExtra("districtName", districtName);
                Toast.makeText(DistrictSelectActivity.this, "선택한 구역: " + districtName, Toast.LENGTH_SHORT).show();
                Log.d("District Select", "선택된 시/군/구 값: " + districtName);
                setResult(RESULT_OK, resultIntent);
                finish(); // 현재 액티비티 종료
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        });
    }

    // ✅ API 호출하여 시/군/구 목록 가져오는 비동기 작업
    private class FetchDistrictsTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> resultList = new ArrayList<>();
            try {
                String apiUrl = "https://grpc-proxy-server-mkvo6j4wsq-du.a.run.app/v1/regcodes?regcode_pattern=" + selectedProvinceCode.substring(0, 2) + "*00000";
                districtArray = RegionAPIClient.fetchJSONArray(apiUrl, "regcodes");
                for (int i = 0; i < districtArray.length(); i++) {
                    resultList.add(districtArray.getString(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultList;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            List<String> districtNames = new ArrayList<>(result.size());
            for (int i = 0; i < districtArray.length(); i++) {
                JSONObject jsonObject ;
                try {
                    jsonObject = districtArray.getJSONObject(i);
                    if (jsonObject.getString("code").equals(selectedProvinceCode)) {
                        continue;
                    }
                    districtNames.add(jsonObject.getString("name"));
                } catch (JSONException e) {
                    Log.e("District Select", i + "번째 JSONArray 인덱스에서 문제 발생", e);
                    throw new RuntimeException(e);
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(DistrictSelectActivity.this, android.R.layout.simple_list_item_1, districtNames);
            districtListView.setAdapter(adapter);
        }
    }
}
