package com.jiny.liftlink.common.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RegionAPIClient {

    // ✅ 시/도 목록 조회 함수
    /**
     * 대한민국의 모든 특별/광역시, 도 반환
     * @return
     */
    public static List<String> getProvincesList() throws JSONException {
        JSONArray jsonArray = fetchJSONArray("https://grpc-proxy-server-mkvo6j4wsq-du.a.run.app/v1/regcodes?regcode_pattern=*00000000", "regcodes");
        return fetchData(jsonArray, "name");
    }

    public static JSONArray getProvincesArray() throws JSONException {
        return fetchJSONArray("https://grpc-proxy-server-mkvo6j4wsq-du.a.run.app/v1/regcodes?regcode_pattern=*00000000", "regcodes");
    }

    // ✅ 특정 시/도의 시/군/구 목록 조회 함수
    private static List<String> fetchData(JSONArray jsonArray, String key) throws JSONException {
        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            dataList.add(jsonObject.getString(key));
        }
        return dataList;
    }

    // ✅ API 요청 및 JSON 파싱 함수
    public static JSONArray fetchJSONArray(String apiUrl, String key) {
        try {
            URL url = URI.create(apiUrl).toURL();
            JSONObject jsonResponse = getJsonObject(url);
            return jsonResponse.getJSONArray(key);
        } catch (Exception e) {
            System.out.printf("API URL: %s | Key: %s", apiUrl, key);
            e.printStackTrace();
        }
        return new JSONArray();
    }

    private static JSONObject getJsonObject(URL url) throws IOException, JSONException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();

        // JSON 파싱
        return new JSONObject(response.toString());
    }
}
