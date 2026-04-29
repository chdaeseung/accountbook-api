# 가계부 REST API + Docker 배포 프로젝트

기존 SSR 기반 [개인 자산 관리를 위한 가계부 서비스](https://github.com/chdaeseung/accountbook)를  
**RESTful API 구조 + JWT 인증 + Docker 기반 실행 환경**으로 구현한 프로젝트입니다.

기존 SSR(Thymeleaf) 구조를 제거하고  
API 서버 중심 아키텍처로 리팩토링한 뒤  
Docker를 통해 **어디서든 동일하게 실행 가능한 환경**을 구성했습니다.

---

## 프로젝트 개요

- 개발 기간: 2026.04.20 ~ 2026.04.27 (SSR → REST API 전환)
- 실행 방식:
  - 로컬 실행
  - Docker Compose 실행 (권장)

---

## 기술 스택

### Backend
- Java 17
- Spring Boot 4.0.4
- Spring Data JPA
- Spring Security
- JWT 인증
- Querydsl

### Database
- MySQL

### Infra / DevOps
- Docker
- Docker Compose
- Docker Hub

---

## 아키텍처
Controller → Service → Repository

- REST API 기반 설계
- Stateless 구조 (JWT 인증)
- 계층 분리 및 책임 분리

---

## 인증 방식

- JWT 기반 인증
- Stateless 구조
- Authorization Header 사용
Authorization: Bearer {JWT_TOKEN}


---

## Docker 기반 실행 (핵심)

이 프로젝트는 Docker를 통해 **한 번의 명령어로 실행**할 수 있습니다.

### 1. docker-compose.yml 생성
프로젝트 실행에 필요한 환경을 구성하기 위해 임의의 폴더에 docker-compose.yml 파일을 생성합니다.
```yaml
services:
  mysql:
    image: mysql:8.4
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: accountbook
    ports:
      - "3307:3306"
    volumes:
      - mysql-data:/var/lib/mysql

  api:
    image: daeseung/accountbook-api:latest
    depends_on:
      - mysql
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/accountbook?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: password
      JWT_SECRET: YOUR_SECRET
      JWT_EXPIRATION: 3600000

volumes:
  mysql-data:
```

### 2. 실행(Window 기준)
docker-compose.yml 파일이 있는 폴더에서 CMD 또는 PowerShell을 실행한 뒤 아래 명령어를 입력합니다.
```
docker compose up -d
```
### 입력 시
- MYSQL 이미지 다운로드
- API 이미지 다운로드
- 컨테이너 실행
- 네트워크 연결

### 3. Swagger 접속
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 데이터 저장 방식
- MySQL은 Docker Volume 사용
```yaml
volumes:
 - mysql-data:/var/lib/mysql
```
### 특징
- 컨테이너 종료 후에도 데이터 유지
- DB 데이터 삭제
```
docker compose down -v
```

--- 

## 실행 / 종료 명령어
```bash
# 실행
docker compose up -d

# 종료
docker compose down

# 로그 확인
docker compose logs api
```

---

## 설계 포인트

### 1. 동시성 제어

- 계좌 잔액은 공유 자원이므로 동시 요청 시 데이터 정합성 문제가 발생할 수 있음
- 이를 방지하기 위해 계좌 단위로 Lock 적용
- 동시에 여러 요청이 들어와도 순차적으로 거래를 처리하도록 설계

### 2. API 응답 및 요청 구조 통일

- REST API 구조에 맞게 요청/응답을 DTO로 분리
- 일관된 API 설계를 통해 프론트 혹은 협업 과정 간의 요청/응답 및 커뮤니케이션 능력 향상
- Controller는 요청/응답만 관리하고 비즈니스 로직은 Service에서 처리

- HTTP Method 별로 역할 분리
  - GET : 조회
  - POST : 생성
  - PUT : 전체 수정
  - PATCH : 부분 수정
  - DELETE : 삭제
 
- 상태 기반 응답 처리
  - 200 OK : 조회/수정 성공
  - 201 Created : 생성 성공
  - 204 No Content : 삭제 성공
  - 401 Unauthorized : 인증 실패
  - 403 Forbidden : 권한 없음
  
---

## 트러블슈팅
### JWT 인증 오류(401 / 403)

#### 문제 상황
Swagger에서 잘못된 JWT 토큰으로 API 요청을 하다가<br />
기대했던 401이 아닌 403으로 응답을 반환하는 문제가 발생

#### 원인 분석
Spring Security에서 인증과 인가가 구분되어 처리되는데<br />
잘못된 토큰이 인증 단계가 아닌 인가 단계에서 처리가 되어 403을 반환함

#### 해결 방법
JWT 필터에서 토큰 검증 실패 시<br />
Spring Security를 거치지않고 직접 401 응답을 반환하도록 수정함

#### 결과
잘못된 토큰 입력 -> 401 Unauthorized
권한 부족 -> 403 Forbidden

---

## 프로젝트 변화 포인트
- SSR -> REST API 구조 전환
- JWT 기반 인증 구조
- Docker 기반 배포
- DB API 컨테이너 분리 설계

---

## 개선 및 확장 예정
- AWS 배포
- CI/CD 구축
- redis
- API Rate Limit

---

## 프로젝트 회고
기존 서버 사이드 렌더링(SSR) 구조의 가계부 프로젝트를 REST API 구조로 전환하면서<br />
클라이언트와 서버의 역할을 명확하게 분리하는 경험을 할 수 있었습니다.<br />
전환 작업 중 PUT과 PATCH를 업데이트 로직에 반영하면서 전체 수정과 부분 수정의 개념을 이해할 수 있었고<br />
DELETE 요청 시 204(No Content)응답을 사용하는 RESTful 설계 방식도 적용해보기도 했습니다.<br />
기존의 세션 기반 인증 방식에서 JWT 인증 방식으로 구현해보면서 Stateless 구조에서 인증 정보를 어떻게 유지하고 검증하는지 이해할 수 있었고<br />
잘못된 토큰의 처리 및 인증/인가의 흐름을 직접 구현해보는 경험을 해봤습니다.
Docker를 도입하면서 개발 환경과 실행 환경을 분리하고 MySQL과 API를 각 컨테이너로 구성하여 서비스 구조를 나눠보기도 했습니다.<br />
docker-compose 명령어를 통해 한번의 입력으로 전체 서비스를 실행할 수 있도록 구성하며 편의성을 크게 향상시킬 수 있었습니다.<br />
이번 REST API 전환 과정을 진행하며 REST API 설계와 JWT 인증 구조 Docker 배포 환경 구성까지 경험할 수 있었고<br />
다음 단계로 AWS를 기반으로 운영환경 구축과 CI/CD 자동화를 진행하도록 하겠습니다.

---
## 개발자

* 채대승
* GitHub: https://github.com/chdaeseung
