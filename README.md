# ARTINUS Backend Engineer 과제

## 1. 프로젝트 소개

구독 서비스 백엔드 API를 설계하고 구현한 과제입니다.

본 프로젝트는 다음 요구사항을 중심으로 구성했습니다.

- 회원의 현재 구독 상태 관리
- 구독 / 해지 이력 관리
- 채널별 구독 가능 / 해지 가능 정책 반영
- 외부 API(csrng) 호출 결과에 따른 트랜잭션 처리
- 구독 이력 조회 API 제공
- 외부 API 장애 대응 전략 적용
- AWS 환경 배포를 가정한 아키텍처 설계

---

## 2. 기술 스택

### Backend
- Java 21
- Spring Boot 3.4.4
- Spring Web
- Spring Data JPA
- Querydsl
- H2 Database
- Lombok

### API / Infra
- RestClient
- P6Spy
- Swagger(OpenAPI)

### Build
- Gradle Multi Module

---

## 3. 프로젝트 구조

```text
api
 ┣ controller
 ┣ dto
 ┣ service
 ┣ global
 ┗ ApiApplication

core
 ┣ domain
 ┃ ┣ member
 ┃ ┣ subscription
 ┃ ┣ channel
 ┃ ┗ history
 ┣ repository
 ┣ service
 ┣ config
 ┗ exception
```

### 모듈 구성
- `api` 모듈: 컨트롤러, 요청/응답 DTO, API 레이어
- `core` 모듈: 도메인, 서비스, 리포지토리, 외부 API 연동, 예외 처리

---

## 4. 도메인 설계

### 4-1. 핵심 해석
이 과제에서 `채널(Channel)`은 구독 대상이 아니라 **구독/해지 요청이 들어오는 창구(접점)** 입니다.

예를 들어:
- 홈페이지
- 모바일앱
- 네이버
- SKT
- 콜센터
- 이메일

즉, 회원이 여러 채널을 동시에 구독하는 구조가 아니라,
**회원은 하나의 현재 구독 상태를 가지며, 그 상태 변경이 여러 채널을 통해 여러 번 발생할 수 있는 구조**로 설계했습니다.

### 4-2. 엔티티 관계

- `Member` : 회원
- `Subscription` : 회원의 현재 구독 상태
- `Channel` : 구독/해지 요청 채널
- `SubscriptionHistory` : 구독/해지 이력

관계는 다음과 같습니다.

- `Member 1 : 1 Subscription`
- `Member 1 : N SubscriptionHistory`
- `Channel 1 : N SubscriptionHistory`

### 4-3. 현재 상태와 이력 분리 이유

`Subscription`은 현재 상태만 저장합니다.

예:
- NONE
- BASIC
- PREMIUM

`SubscriptionHistory`는 다음 정보를 이력으로 저장합니다.

- 어떤 채널을 통해
- 어떤 상태에서
- 어떤 상태로 변경되었는지
- 언제 변경되었는지

이렇게 현재 상태와 변경 이력을 분리하면,
도메인 책임이 명확해지고 이력 조회 API 및 LLM 요약 생성에 유리합니다.

---

## 5. 주요 기능

### 5-1. 구독하기 API

요청값
- 휴대폰번호
- 채널 ID
- 변경할 구독 상태

처리 규칙
- 입력받은 채널이 구독 가능한 채널인지 검증
- 회원이 없으면 최초 회원으로 생성 가능
- 현재 상태에서 목표 상태로 변경 가능한지 검증
- 외부 API(csrng) 호출
- 외부 API 결과가 성공이면 현재 상태 변경 및 이력 저장
- 외부 API 결과가 실패이면 예외 발생 및 트랜잭션 롤백

허용된 상태 변경
- `NONE -> BASIC`
- `NONE -> PREMIUM`
- `BASIC -> PREMIUM`

### 5-2. 구독 해지 API

요청값
- 휴대폰번호
- 채널 ID
- 변경할 구독 상태

처리 규칙
- 입력받은 채널이 해지 가능한 채널인지 검증
- 회원이 존재해야 함
- 현재 상태에서 목표 상태로 변경 가능한지 검증
- 외부 API(csrng) 호출
- 외부 API 결과가 성공이면 현재 상태 변경 및 이력 저장
- 외부 API 결과가 실패이면 예외 발생 및 트랜잭션 롤백

허용된 상태 변경
- `PREMIUM -> BASIC`
- `PREMIUM -> NONE`
- `BASIC -> NONE`

### 5-3. 구독 이력 조회 API

요청값
- 휴대폰번호

응답값
- 구독/해지 이력 목록
- 추후 LLM을 통해 생성할 자연어 요약 확장 가능

조회 결과는 DTO로 변환하여 반환하며,
엔티티를 직접 외부로 노출하지 않도록 구성했습니다.

---

## 6. API 설계

### 6-1. 구독하기

```http
POST /artinus/subscription/subscribe
Content-Type: application/json

{
  "phoneNumber": "010-1111-2222",
  "channelId": 1,
  "targetStatus": "BASIC"
}
```

### 6-2. 구독 해지

```http
POST /artinus/subscription/cancel
Content-Type: application/json

{
  "phoneNumber": "010-1111-2222",
  "channelId": 5,
  "targetStatus": "NONE"
}
```

### 6-3. 구독 이력 조회

```http
GET /artinus/subscription/{phoneNumber}
```

예:

```http
GET /artinus/subscription/010-1111-2222
```

---

## 7. 요청값 검증 전략

구독/해지 요청 DTO에 Bean Validation을 적용했습니다.

검증 항목 예시
- 휴대폰번호 필수 여부
- 휴대폰번호 형식
- 채널 ID 필수 여부
- 목표 상태 필수 여부

검증 실패 시 `MethodArgumentNotValidException`, `HttpMessageNotReadableException` 등을 글로벌 예외 처리기에서 받아 일관된 에러 메시지를 반환하도록 구성했습니다.

---

## 8. 외부 API(csrng) 연동 전략

### 8-1. 사용 목적
구독하기 / 해지하기 API는 외부 API 결과에 따라 커밋 또는 롤백되어야 하므로,
csrng API를 호출하여 `random` 값을 받아 처리합니다.

- `random = 1` → 정상 처리, 트랜잭션 커밋
- `random = 0` → 비즈니스 실패, 예외 발생, 트랜잭션 롤백

### 8-2. 처리 방식
- 외부 API 호출 전 도메인 검증 수행
- 외부 API 호출
- 성공 응답일 경우에만 현재 구독 상태 변경
- 이력 저장 후 트랜잭션 커밋
- 실패 응답 또는 장애 시 전체 롤백

---

## 9. 외부 API 장애 대응 전략

과제 요구사항에 맞춰 외부 API 장애 상황을 다음과 같이 처리했습니다.

### 9-1. 타임아웃 설정
- 연결 타임아웃 설정
- 응답 타임아웃 설정

무한 대기 상태를 방지하고, 장애 감지를 빠르게 하도록 구성했습니다.

### 9-2. 재시도
- 일시적인 네트워크 오류를 고려해 제한된 횟수만큼 재시도
- 무한 재시도는 방지

### 9-3. 최종 실패 처리
- 재시도 이후에도 실패하면 예외 발생
- 트랜잭션 전체 롤백
- API 응답은 적절한 상태 코드와 함께 반환

### 9-4. 장애와 비즈니스 실패 분리
다음 두 경우를 구분했습니다.

1. **장애**
    - 타임아웃
    - 네트워크 오류
    - 비정상 응답
    - 빈 응답
    - 파싱 실패

2. **비즈니스 실패**
    - 외부 API 정상 응답이지만 `random = 0`

장애는 재시도 대상이고,
비즈니스 실패는 재시도하지 않고 즉시 롤백 처리합니다.

### 9-5. 로깅
다음 정보를 로그로 남기도록 구성했습니다.
- 시도 횟수
- 성공/실패 여부
- 외부 API 장애 유형
- 최종 실패 여부

---

## 10. 트랜잭션 전략

구독 / 해지 API는 `@Transactional`로 처리했습니다.

트랜잭션 범위 내 작업
- 회원 조회/생성
- 채널 조회
- 현재 구독 상태 조회
- 상태 변경 검증
- 외부 API 호출 결과 확인
- 현재 상태 변경
- 이력 저장

예외 발생 시 런타임 예외를 통해 전체 롤백되도록 구현했습니다.

---

## 11. 예외 처리 전략

글로벌 예외 처리기를 통해 예외 응답을 일관되게 관리했습니다.

예외 예시
- `ExternalApiUnavailableException`
- `ExternalApiRejectedException`
- `ClientException`
- `SubscriptionException`
- `ChannelException`
- `MethodArgumentNotValidException`
- `HttpMessageNotReadableException`

예외별로 다음과 같이 상태 코드를 나눴습니다.

- `400 Bad Request` : 잘못된 요청, 검증 실패
- `409 Conflict` : 상태 전이 불가, 외부 API 비즈니스 실패
- `503 Service Unavailable` : 외부 API 장애
- `500 Internal Server Error` : 처리되지 않은 서버 내부 오류

---

## 12. 데이터 초기화

테스트 및 시연을 위해 `data.sql`을 사용해 초기 데이터를 구성했습니다.

초기 데이터에는 다음이 포함됩니다.
- 채널 목록
- 회원 목록
- 현재 구독 상태
- 구독/해지 이력

이력을 분리해 관리함으로써,
구독 이력 조회 API와 LLM 요약 기능 확장에 유리한 구조를 만들었습니다.

---

## 13. 조회 응답 DTO 설계

조회 API에서는 엔티티를 직접 반환하지 않고 DTO를 사용했습니다.

이유
- 엔티티 직접 노출 방지
- LAZY 로딩 직렬화 문제 방지
- 응답 형식 통제 가능
- 프론트엔드/외부 소비자 입장에서 명확한 응답 제공

---

## 14. AWS 배포/운영 아키텍처 제안

### 14-1. 기본 구성
- API 서버: ECS Fargate 또는 EC2 기반 Spring Boot 애플리케이션
- DB: RDS (MySQL 또는 PostgreSQL)
- 로드밸런서: ALB
- 비밀 관리: AWS Secrets Manager / SSM Parameter Store
- 로그 및 모니터링: CloudWatch

### 14-2. 보안
- 민감한 환경변수는 GitHub에 포함하지 않음
- DB 접속 정보는 Secret Manager에 분리 저장
- ALB + HTTPS 적용
- 보안 그룹을 통한 DB 접근 제한
- 내부망과 외부망 분리 고려

### 14-3. 확장성
- API 서버는 무상태로 설계하여 수평 확장 가능
- DB는 RDS로 관리형 운영
- 외부 API 장애 시 재시도 및 실패 응답 처리
- 향후 캐시(Redis), 비동기 메시징(Kafka/SQS) 등 확장 가능

### 14-4. 운영 관점 고려사항
- 애플리케이션 로그 수집
- 외부 API 실패율 모니터링
- 상태 전이 실패 비율 관찰
- 배포 자동화(CI/CD) 도입 가능

---

## 15. 설계 의도 요약

이 프로젝트는 다음 원칙을 중심으로 설계했습니다.

1. 현재 상태와 이력 분리
2. 채널을 구독 대상이 아닌 요청 경로로 해석
3. 외부 API 결과에 따른 명확한 트랜잭션 처리
4. DTO 기반 응답으로 API 안정성 확보
5. 글로벌 예외 처리로 일관된 오류 응답 제공
6. 실제 운영 환경을 고려한 장애 대응 및 배포 구조 설계

---

## 16. 개선 여지

과제 범위를 넘어서 확장할 수 있는 방향은 다음과 같습니다.

- TDD 케이스 추가

---

## 17. 실행 방법

### 애플리케이션 실행

```bash
./gradlew clean bootRun
```

### API 테스트 예시

구독하기

```http
POST /artinus/subscription/subscribe
Content-Type: application/json

{
  "phoneNumber": "010-1111-2222",
  "channelId": 1,
  "targetStatus": "PREMIUM"
}
```

해지하기

```http
POST /artinus/subscription/cancel
Content-Type: application/json

{
  "phoneNumber": "010-1111-2222",
  "channelId": 5,
  "targetStatus": "NONE"
}
```

이력 조회

```http
GET /artinus/subscription/010-1111-2222
```

---

## 18. 마무리

본 과제에서는 단순 CRUD를 넘어서,
도메인 규칙, 외부 API 연동, 트랜잭션 처리, 장애 대응, 운영 환경을 함께 고려하는 방향으로 설계하고 구현했습니다.

