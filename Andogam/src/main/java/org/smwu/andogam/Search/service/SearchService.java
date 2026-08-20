package org.smwu.andogam.Search.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.smwu.andogam.Search.dto.SearchDetailDto;
import org.smwu.andogam.Search.dto.SearchDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.simple.parser.JSONParser;

@Service
public class SearchService {

    @Value("${api-odsay-key}")
    private String odsay_key;

    public List<SearchDetailDto> searchStation(SearchDto searchDto) throws IOException {
        List<SearchDetailDto> searchDetailDtos = new ArrayList<>();
        // Odsay로 넘어감
        String urlInfo = "https://api.odsay.com/v1/api/searchStation?lang=0&stationName="+URLEncoder.encode(searchDto.getStationName(), "UTF-8")+"&apiKey="+ odsay_key;

        // http 연결
        URL url = new URL(urlInfo);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        bufferedReader.close();
        conn.disconnect();

        // 결과 출력
        System.out.println(sb.toString());

        try {

            String jsonStr = sb.toString();

            JSONParser parser = new JSONParser();


            JSONObject jsonObj= (JSONObject)parser.parse(jsonStr);
            JSONObject jsonResult = (JSONObject)jsonObj.get("result");

            Gson gson = new Gson();
            List<Map<String, Object>> stationList = null;
            String jsonArray = jsonResult.get("station").toString();
            stationList = gson.fromJson(jsonArray, new TypeToken<List<Map<String, Object>>>() {}.getType());


            for (int i = 0; i < stationList.size(); i++){
                if (stationList.get(i).get("stationClass").equals(2.0)){
                    //지하철역
                    System.out.println(stationList.get(i));
                    StringBuilder addsb = new StringBuilder();
                    addsb.append(String.valueOf(stationList.get(i).get("do"))+' ');
                    addsb.append(String.valueOf(stationList.get(i).get("gu"))+' ');
                    addsb.append(String.valueOf(stationList.get(i).get("dong")));

                    Object stationIdRaw = stationList.get(i).get("stationID");
                    String stationId = stationIdRaw == null ? null
                            : String.valueOf(((Double) stationIdRaw).longValue());

                    SearchDetailDto searchDetailDto = SearchDetailDto.builder()
                            .type(String.valueOf(stationList.get(i).get("type")))
                            .stationId(stationId)
                            .stationName(String.valueOf(stationList.get(i).get("stationName")).trim())
                            .ebid(String.valueOf(stationList.get(i).get("ebid")))
                            .address(addsb.toString())
                            .build();

                    searchDetailDtos.add(searchDetailDto);
                }
                else{
                    continue;
                }
            }
        }
        catch (ParseException e){
            e.printStackTrace();
        }

        return searchDetailDtos;
    }
}