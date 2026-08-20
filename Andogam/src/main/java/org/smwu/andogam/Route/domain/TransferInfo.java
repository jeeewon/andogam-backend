package org.smwu.andogam.Route.domain;

import lombok.Getter;

/**
 * ODsay subwayTransitInfo API 응답의 한 항목.
 * 특정 역에서 "takeLaneName 노선을 타고 와서 exLaneName 노선으로 환승"할 때의
 * 빠른환승(몇 번째 칸, 몇 번 문) 정보를 담는다.
 */
@Getter
public class TransferInfo {

    private final String takeLaneName; // 타고 온 노선명
    private final String exLaneName;   // 환승할 노선명
    private final String fastTrainInfo; // 환승 안내 문구
    private final int fastTrain;        // 빠른환승 열차(칸) 번호. -2면 데이터 수집중, -1이면 정산후 승차(공항철도), 0이면 전 칸 무관
    private final int fastDoor;         // 빠른환승 문 번호
    private final int fastTrainNum;     // 전체 칸 수

    public TransferInfo(String takeLaneName, String exLaneName, String fastTrainInfo,
                         int fastTrain, int fastDoor, int fastTrainNum) {
        this.takeLaneName = takeLaneName;
        this.exLaneName = exLaneName;
        this.fastTrainInfo = fastTrainInfo;
        this.fastTrain = fastTrain;
        this.fastDoor = fastDoor;
        this.fastTrainNum = fastTrainNum;
    }

    /** 실제로 활용 가능한 빠른환승 데이터인지 (데이터 수집중이 아닌지) */
    public boolean isDataAvailable() {
        return fastTrain != -2;
    }
}
