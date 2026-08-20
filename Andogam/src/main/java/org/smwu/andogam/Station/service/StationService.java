package org.smwu.andogam.Station.service;

import lombok.RequiredArgsConstructor;
import org.smwu.andogam.Station.domain.Repository.ElevatorRepository;
import org.smwu.andogam.Station.domain.Repository.LiftRepository;
import org.smwu.andogam.Station.domain.Entity.Station;
import org.smwu.andogam.Station.domain.Repository.RampRepository;
import org.smwu.andogam.Station.domain.Repository.StationRepository;
import org.smwu.andogam.Station.dto.StationInfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.simple.parser.JSONParser;

@RequiredArgsConstructor
@Service
public class StationService {

    private final ElevatorRepository elevatorRepository;
    private final StationRepository stationRepository;
    private final RampRepository rampRepository;
    private final LiftRepository liftRepository;

    @Value("${api-odsay-key}")
    private String odsay_key;

    public Boolean elevatorInstalled(String stationName, String laneName) {
        return elevatorRepository.existsByStationNameAndLaneName(stationName, laneName);
    }

    public Boolean liftInstalled(String stationName, String laneName) {
        return liftRepository.existsByStationNameAndLaneName(stationName, laneName);
    }

    public Boolean rampInstalled(String stationName, String laneName) {
        return rampRepository.existsByStationNameAndLaneName(stationName, laneName);
    }

    public StationInfoDto getStationInfo(String stationCode) throws IOException {
        // Odsay로 넘어감
        String urlInfo = "https://api.odsay.com/v1/api/subwayStationInfo?lang=0&stationID=" + stationCode + "&apiKey=" + odsay_key;

        // http 연결
        URL url = new URL(urlInfo);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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

        try {
            String jsonStr = sb.toString();
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(jsonStr);

            // ODsay가 잘못된 stationCode 등으로 요청 실패 시 "result" 대신 "error"를 준다.
            Object errorNode = jsonObj.get("error");
            if (errorNode != null) {
                JSONObject error = (JSONObject) errorNode;
                throw new org.smwu.andogam.Common.exception.InvalidStationException(
                        "존재하지 않거나 유효하지 않은 역 코드입니다 (stationCode=" + stationCode
                                + ", ODsay 오류코드=" + error.get("code") + ", 메시지=" + error.get("msg") + ")");
            }

            JSONObject jsonResult = (JSONObject) jsonObj.get("result");
            if (jsonResult == null) {
                throw new org.smwu.andogam.Common.exception.InvalidStationException(
                        "역 정보를 찾을 수 없습니다 (stationCode=" + stationCode + ")");
            }

            Gson gson = new Gson();
            String jsonArray = jsonResult.get("defaultInfo").toString();
            Map<String, Object> infoList = gson.fromJson(jsonArray, new TypeToken<Map<String, Object>>() {
            }.getType());

            String tel = String.valueOf(infoList.get("tel"));
            String address = String.valueOf(infoList.get("new_address"));

            // ODsay 응답에서 바로 역명/노선명을 받아온다 (로컬 Station 테이블은 채워져 있지 않음)
            String stationName = String.valueOf(jsonResult.get("stationName")).trim();
            String laneName = String.valueOf(jsonResult.get("laneName")).trim();

            //장애인 편의시설 설치유무 확인
            boolean elevator = elevatorInstalled(stationName, laneName);
            boolean lift = liftInstalled(stationName, laneName);
            boolean ramp = rampInstalled(stationName, laneName);

            return new StationInfoDto(stationCode, stationName, laneName, address, tel, elevator, lift, ramp);

        } catch (ParseException e) {
            throw new org.smwu.andogam.Common.exception.ExternalApiException(
                    "ODsay 응답 파싱 실패 (stationCode=" + stationCode + ")", e);
        }
    }
}