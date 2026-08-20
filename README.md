# AnDoGam (안도감) - Server

휠체어 이용자를 위한 지하철 경로 안내 서비스의 백엔드입니다. 최단 경로가 아닌, 교통약자 편의시설을 실제로 이용할 수 있는 경로를 우선 추천합니다.

## 기능

- 역 검색
- 역 상세정보 조회 (주소, 전화번호, 편의시설 설치여부)
- 경로 탐색 (일반 최단경로 + 접근성 우선경로)
- 역별 리뷰 작성/조회
- 역별 불편사항 신고 등록/조회
- 즐겨찾기 등록/조회/삭제

## 기술 스택

- Java 11, Spring Boot 2.7.3, Spring Data JPA
- MySQL
- ODsay 대중교통 API
- Gradle
- Docker, Docker Compose
- GitHub Actions (CI/CD)
- AWS EC2, Docker Hub

## 실행 방법

```bash
# 1. 데이터베이스 생성
mysql -u root -p -e "CREATE DATABASE AndogamDB;"

# 2. API 키 설정 파일 생성
echo "api-odsay-key=발급받은_ODsay_API_키" > src/main/resources/application-API-KEY.properties

# 3. 편의시설 데이터 적재
mysql -u root -p AndogamDB < accessibility_data.sql

# 4. 실행
./gradlew bootRun
```

## API

| Method | Endpoint | 설명 |
|---|---|---|
| POST | `/searchStation` | 역 검색 |
| GET | `/stationInfo/{stationCode}` | 역 상세정보 |
| POST | `/route` | 경로 탐색 |
| POST | `/stationInfo/{stationCode}/reviewSave` | 리뷰 작성 |
| GET | `/stationInfo/{stationCode}/reviews` | 리뷰 조회 |
| POST | `/stationInfo/{stationCode}/reportSave` | 신고 등록 |
| GET | `/stationInfo/{stationCode}/reports` | 신고 조회 |
| POST | `/bookmark` | 즐겨찾기 등록 |
| DELETE | `/bookmark/{bid}` | 즐겨찾기 삭제 |
| GET | `/bookmarks` | 즐겨찾기 조회 |

### `/route` 요청 예시

```json
{
  "startStationCode": "222",
  "endStationCode": "925",
  "avoidStationIds": ["1910"]
}
```

## 프로젝트 구조

```
src/main/java/org/smwu/andogam/
├── Route/      # 경로 탐색
├── Station/    # 역 상세정보
├── Search/     # 역 검색
├── Review/     # 리뷰
├── Report/     # 불편 신고
├── Bookmark/   # 즐겨찾기
└── Common/     # 전역 예외 처리
```

## 데이터 출처

- [서울교통공사_엘리베이터 설치 정보](https://data.seoul.go.kr) (data.seoul.go.kr)
- [서울교통공사_휠체어리프트 설치현황](https://data.seoul.go.kr) (data.seoul.go.kr)
- [서울교통공사_휠체어경사로 설치 현황](https://data.seoul.go.kr) (data.seoul.go.kr)
- 서울교통공사_9호선 2·3단계 편의시설 현황 (서울교통공사 홈페이지)
- [한국철도공사_역별 승강설비 현황](https://www.data.go.kr) (data.go.kr)
- [ODsay 대중교통 API](https://lab.odsay.com)

## 배포

GitHub Actions로 `main` 브랜치에 push하면 자동으로 Docker 이미지를 빌드해 Docker Hub에 올리고, EC2에서 새 이미지를 pull 받아 재시작합니다.

```
GitHub push → GitHub Actions (빌드) → Docker Hub (이미지 저장) → EC2 (pull & 재시작)
```

### EC2에서 최초 1회 설정

```bash
# Docker, Docker Compose 설치되어 있다고 가정
git clone https://github.com/{계정}/AnDoGam-Server-main.git
cd AnDoGam-Server-main
cp .env.example .env
vi .env   # DB_PASSWORD, ODSAY_API_KEY 등 실제 값 입력
docker compose up -d
```

### GitHub Secrets (Settings → Secrets and variables → Actions)

| Key | 설명 |
|---|---|
| `DOCKERHUB_USERNAME` | Docker Hub 계정명 |
| `DOCKERHUB_TOKEN` | Docker Hub Access Token |
| `EC2_HOST` | EC2 퍼블릭 IP 또는 도메인 |
| `EC2_USERNAME` | EC2 접속 계정 (보통 `ubuntu`) |
| `EC2_SSH_KEY` | EC2 접속용 PEM 키 전체 내용 |

### 보안그룹

EC2 보안그룹에서 인바운드 규칙에 `8080` 포트(앱)를 열어야 외부에서 접속 가능합니다.

### 참고: ODsay API 키 IP 등록

ODsay는 호출하는 서버의 공인 IP를 사전 등록해야 호출이 허용됩니다. EC2 인스턴스를 재시작하면 퍼블릭 IP가 바뀔 수 있으니, **탄력적 IP(Elastic IP)**를 할당해서 고정해두면 매번 재등록할 필요가 없습니다.
