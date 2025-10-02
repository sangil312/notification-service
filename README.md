# 알림 발송 서비스
### 💡알림 발송 예약 접수

`POST http://localhost:8080/api/notifications`
- Headers <br>

|       헤더        |   설명    |
|:---------------:|:-------:|
| Idempotency-Key | 멱등성 Key |

- Request Body

```json
{
    "title": "test",
    "contents": "contents",
    "reserveTime": "2025-09-30T18:00:00"
}
```

|     필드      |   타입   | 필수 |  설명   |
|:-----------:|:------:|:--:|:-----:|
|    title    | String | O  | 알림 제목 |
|  contents   | String | O  | 알림 내용 |
| reserveTime | String | O  | 예약 시간 |

- Response `200 OK`

| 필드             |   타입    |          설명 |
|:---------------|:-------:|------------:|
| notificationId |  Long   |       알림 ID |
| status         | String  |       알림 상태 |
| success        | Boolean | 알림 접수 성공 여부 |
| reservedTime   | String  |      예약된 시간 |

---

### 💡알림 발송 에약 내역 조회
`GET http://localhost:8080/api/notifications`

- Request Parameter
`http://localhost:8080/api/notifications?page=0&size=10&status=pending`

|  파라미터  | 필수 |    설명    |
|:------:|:---|:--------:|
|  page  | X  |  페이지 번호  |
|  size  | X  |  페이지 크기  |
| status | X  | 알림 접수 상태 |

- Response `200 OK` <br>
  (Pageable 관련 응답은 생략)

```json
{
    "content": [
        {
            "notificationId": 10,
            "status": "PENDING",
            "retryCount": 1,
            "title": "알림 제목10",
            "reservedTime": "2025-10-04T15:15:15",
            "retryTime": "2025-10-04T15:15:16",
            "acceptTime": "2025-10-05T14:15:15"
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 50,
        "sort": {
            "sorted": false,
            "empty": true,
            "unsorted": true
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 10,
    "first": true,
    "size": 50,
    "number": 0,
    "sort": {
        "sorted": false,
        "empty": true,
        "unsorted": true
    },
    "numberOfElements": 10,
    "empty": false
}
```

| 필드             |   타입    |        설명 |
|:---------------|:-------:|----------:|
| notificationId |  Long   |     알림 ID |
| status         | String  |     알림 상태 |
| retryCount        | Int | 발송 재시도 횟수 |
| title   | String  |     알림 제목 |
| reservedTime   | String  |    예약된 시간 |
| retryTime   | String  |    재시도 시간 |
| acceptTime   | String  |     접수 시간 |

---

### 💡알림 발송 예약 취소
`PATCH http://localhost:8080/api/notifications/{id}/status`

- Request

|  Path Variable  | 필수 |    설명    |
|:------:|:---|:--------:|
|  id  | O  |  알림 ID  |

- Response `200 OK`

---

### 📌4xx, 5xx 공통 Response
- Response

```json
{
    "error": "Bad Request",
    "statusCode": 400,
    "timestamp": "2025-10-03T12:00:00.798983400",
    "message": "에러 메세지"
}
```

| 필드             |   타입    |     설명 |
|:---------------|:-------:|-------:|
| error |  String   |  예외 타입 |
| statusCode         | Int  |  상태 코드 |
| timestamp        | String |  응답 시간 |
| message   | String  | 에러 메세지 |