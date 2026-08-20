package org.smwu.andogam.Route.dto;

import lombok.Builder;
import lombok.Getter;
import org.smwu.andogam.Route.domain.SubwayNode;
import org.smwu.andogam.Route.domain.TransferInfo;

@Getter
@Builder
public class RouteStepDto {
    private String stationId;
    private String stationName;
    private String laneName;
    private boolean transfer;           // 이 역에서 환승이 발생하는지
    private boolean transferAccessible; // 환승이 발생한다면, 그 환승이 접근 가능한지 (transfer=false면 의미 없음)

    // 빠른환승 정보 (transfer=true 이고 데이터가 있을 때만 채워짐)
    private Boolean fastTransferAvailable; // 빠른환승 데이터 존재 여부
    private Integer fastTransferCar;       // 빠른환승 칸 번호 (0이면 전 칸 무관)
    private Integer fastTransferDoor;      // 빠른환승 문 번호
    private String fastTransferDescription; // ODsay가 제공하는 안내 문구

    public static RouteStepDto of(SubwayNode node, boolean transfer, boolean transferAccessible) {
        return RouteStepDto.builder()
                .stationId(node.getStationId())
                .stationName(node.getStationName())
                .laneName(node.getLaneName())
                .transfer(transfer)
                .transferAccessible(transferAccessible)
                .build();
    }

    public static RouteStepDto of(SubwayNode node, boolean transfer, boolean transferAccessible,
                                   TransferInfo transferInfo) {
        RouteStepDtoBuilder builder = RouteStepDto.builder()
                .stationId(node.getStationId())
                .stationName(node.getStationName())
                .laneName(node.getLaneName())
                .transfer(transfer)
                .transferAccessible(transferAccessible);

        if (transferInfo != null && transferInfo.isDataAvailable()) {
            builder.fastTransferAvailable(true)
                    .fastTransferCar(transferInfo.getFastTrain())
                    .fastTransferDoor(transferInfo.getFastDoor())
                    .fastTransferDescription(transferInfo.getFastTrainInfo());
        } else if (transfer) {
            builder.fastTransferAvailable(false);
        }

        return builder.build();
    }
}
