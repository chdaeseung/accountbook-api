# 가계부 웹 어플리케이션

개인 자산을 효율적으로 관리하기 위한 **가계부 웹 애플리케이션**입니다.<br />
단순 CRUD를 넘어 **정기 지출 자동화, 계좌 기반 잔액 관리, 대시보드 시각화**까지 구현하여
실제 서비스 수준의 구조를 목표로 개발했습니다.

---

## 프로젝트 개요

* **프로젝트 목적**

  * 개인이 스스로 자산 관리를 할 수 있는 서비스 구현
  * Spring Boot 기반 웹 어플리케이션 제작

* **개발 기간**

  * 2026.03.20 ~ 2026.04.15

* **배포 주소**

  * 로컬 환경에서 실행 가능합니다.
  * 추후 AWS 기반 배포 예정

---

## 기술 스택

### Backend

* Java 17
* Spring Boot
* Spring MVC
* Spring Data JPA
* Spring Security
* Querydsl
* Lombok

### Frontend

* Thymeleaf
* HTML / CSS
* JavaScript (Vanilla)
  
프론트엔드 UI 구성 및 디자인/스타일링은 ChatGPT의 도움을 받아 작성했습니다.

### Database

* MySQL

  

---

## 프로젝트 구조

```
src/main/java/chdaeseung/accountbook
├── transaction
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
├── recurring
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   ├── scheduler
│   └── service
├── global
│   ├── config
│   ├── controller
│   ├── exception
│   └── interceptor
├── bankaccount
├── category
├── dashboard
├── transfer
├── user
├── weather
└── ...

src/main/resources
├── templates
│   ├── transaction
│   ├── recurring
│   ├── bankaccount
│   ├── category
│   ├── dashboard
│   ├── transfer
│   ├── user
│   ├── weather
│   └── ...
├── static
│   └── css
└── application.properties
```

---

## 주요 기능

### 1. 대시보드 (Dashboard)

<img width="1277" height="878" alt="image" src="https://github.com/user-attachments/assets/4b0a9f9d-cafa-4886-82c2-9e9d9ceb34c8" />

* 사용자의 자산 현황 요약
* 최근 거래 내역 조회
* 자산 변화 그래프
* 데이터 시각화 중심 UI, 사용자에게 직관적인 정보 제공

---

### 2. 거래 관리 (Transaction)

<img width="1256" height="1007" alt="image" src="https://github.com/user-attachments/assets/64432f91-c379-4b4a-9c9d-93dadf01cda6" />

* 거래 등록 / 조회 / 수정 / 삭제 구현
* 카테고리 및 계좌 기반 관리
* 거래 발생 시 계좌 잔액 자동 반영
* 수정 / 삭제 시에도 이전 데이터 고려하여 재계산

---

### 3. 계좌 관리 (Bank Account)

<img width="1284" height="1141" alt="image" src="https://github.com/user-attachments/assets/63b2b612-379b-4894-9976-746c32e0a7cf" />

* 계좌 생성 / 조회 / 수정 / 삭제 구현
* 계좌별 잔액 관리
* 마이너스 잔액 허용 여부 옵션, 여부에 따라 잔액 음수 허용/차단 로직 분리
* 

---

### 4. 정기 지출 (Recurring Expense)

<img width="1261" height="721" alt="image" src="https://github.com/user-attachments/assets/4f401ac0-3f31-4788-8128-c3cc63ba3e50" />

* 정기 지출 등록
* 특정 주기에 따라 자동 거래 생성
* 스케줄러를 통해 거래일 날짜에 자동으로 거래 생성
* 생성된 거래는 일반 거래와 동일하게 관리되며 '정기'로 구분

---

## 핵심 설계 포인트

### 1. 도메인 중심 설계

* Transaction, BankAccount, Category 중심 구조
* 엔티티 간 관계 명확히 분리

---

### 2. 계층 분리

* Controller → Service → Repository 구조
* 비즈니스 로직은 Service 계층에 집중

---

### 3. 상태 기반 로직 처리

* 거래 생성 / 수정 / 삭제 시
  → 계좌 잔액 변경 로직 일관성 유지

---

### 4. 사용자 경험 (UX)

* 테이블 행 전체 클릭으로 상세 이동
* 카드 기반 대시보드 UI
* 모달을 활용한 카테고리 관리

---

## 주요 비즈니스 로직 예시

### 거래 생성 시 잔액 반영

거래가 등록되면 단순히 내역만 등록되는 것이 아닌 거래에 사용된 계좌의 현재 잔액에도 즉시 반영되도록 구현했습니다.
```java
public void increaseBalance(Long amount) {
    this.balance += amount;
}

public void decreaseBalance(Long amount) {
    long newBalance = this.balance - amount;

    if(!this.negativeBalanceAllowed && newBalance < 0) {
        throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);
    }

    this.balance = newBalance;
}
```

### 거래 수정/삭제 시 이전 값 고려

거래 수정/삭제는 해당 거래의 금액이 기존 계좌에 반영된 상태이므로 먼저 원복한 뒤 금액을 변경하도록 구현했습니다.
```java
// 거래 수정
BankAccount oldBankAccount = transaction.getBankAccount();
if(oldBankAccount != null) {
    rollbackBalance(oldBankAccount, transaction.getType(), transaction.getAmount());
}

BankAccount newBankAccount = getBankAccountOrNull(requestDto.getBankAccountId(), userId);
if(requestDto.getBankAccountId() != null) {
    applyBalance(newBankAccount, requestDto.getType(), requestDto.getAmount());
}

// 거래 삭제
private void rollbackBalance(BankAccount bankAccount, TransactionType type, Long amount) {
    if(type == TransactionType.INCOME) {
        bankAccount.decreaseBalance(amount);
    } else {
        bankAccount.increaseBalance(amount);
    }
}
```

### 정기 지출 자동 생성

정기적으로 발생하는 정기 지출은 Cron 기반 스케줄러를 통해 거래가 자동 생성되도록 구현했습니다.
```java
@Scheduled(cron = "0 0 0 * * *")
public void daily() {
    recurringSchedulerService.generateTodayRecurringTransactions();
}
```

### 마이너스 잔액 허용 여부 검증

계좌별로 negativeBalanceAllowed 값을 두어 지출 거래 발생 시 음수 잔액 허용 여부를 검증하도록 구현했습니다.
```java
long newBalance = this.balance - amount;

if(!this.negativeBalanceAllowed && newBalance < 0) {
    throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);
}
this.balance = newBalance;
```

---

## 트러블 슈팅

### 거래 수정, 삭제 시 계좌 잔액 정합성 문제

#### 문제 상황
거래 수정, 삭제 후 계좌 잔액에 제대로 반영되지 않는 문제가 생겼습니다.

#### 원인 분석
거래 수정, 삭제 시 기존에 등록된 거래 내용에서 계좌에 반영된 금액을 제거하지 않고 새로운 값을 적용하여 잔액이 누적되는 문제가 있었습니다.

#### 해결 방법
거래 수정, 삭제 시 기존 거래 금액을 돌려놓은 뒤 변경된 금액을 다시 반영하는 방식으로 고쳤습니다.

#### 결과
가계부에서 계좌 같은 데이터는 단순 덮어쓰기 방식이 아닌 이전 상태를 고려한 방식이 필요함을 알게 되었습니다.<br />
계좌의 수정, 삭제 관련 모든 과정에 제대로 잔액이 반영되게끔 설계할 수 있게됐습니다.

---

### 마이너스 잔액 허용 기능 도입 시

#### 문제 상황
계좌에 *마이너스 잔액 허용 여부* 기능을 추가하는 과정에서<br />
기존에 사용하던 잔액 감소 기능과 충돌하여 제대로 동작하지 않던 문제가 생겼습니다.

#### 원인 분석
초기엔 모든 계좌의 잔액 감소 기능을 동일한 방식으로 사용하고 있었기 때문에<br />
추가된 마이너스 계좌를 고려하지 못한 상태였습니다.<br />
이로 인해 마이너스 계좌에서 마이너스 잔액이 되지 않고 예외가 발생하는 문제가 있었습니다.

#### 해결 방법
계좌 엔티티에 negativeBalanceAllowed 필드를 추가하고<br />지출 거래 등록 처리 중 마이너스 잔액 허용 여부를 미리 검증하도록 방식을 수정했습니다.

#### 결과
단순한 기능 추가를 하더라도 기존 비즈니스 방식에 미치는 영향을 고려해야 된다는 것을 알게 되었습니다.

---

## ERD

<img width="1516" height="902" alt="image" src="https://github.com/user-attachments/assets/6897383e-3180-4d13-a493-7d913ef7ad7f" />

가계부 프로젝트는 사용자(User)를 중심으로 계좌와 거래를 관리하는 구조로 설계했습니다.
특히 거래(Transaction)를 중심으로 자산 흐름이 관리되도록 설계했습니다.
* 사용자는 여러 개의 계좌(BankAccount)를 가질 수 있으며, 계좌별로 잔액을 관리합니다.
* 거래는 계좌, 카테고리와 연결되어 사용자의 거래를 기록합니다.
* 정기 지출(Recurring Transaction)은 별도의 테이블로 관리되며, 스케줄러를 통해 자동으로 결제일에 맞춰 거래를 생성합니다.
* 계좌 간 이체는 Transfer 테이블을 통해 관리되며, 출금 <> 입금 거래를 별도의 그룹 키를 생성하여 계좌 잔액의 일관성을 유지합니다.

## 개선 및 확장 예정

* 이메일 기반 아이디/비밀번호 찾기 및 재설정 기능
* AWS 기반 배포 및 운영 환경 구축
* 현재 Thymeleaf 기반 서버 렌더링 구조에서 REST API기반 구조로 확장
* 월별 리포트(카테고리별 소비 분석, 지난달 대비 소비량 등) 추가
* AI를 활용한 기능, 서비스 도입

---

## 프로젝트 회고

단순한 CRUD 기능 구현을 넘어 거래 생성, 수정, 삭제 과정에서 계좌 잔액이 어떻게 변하는지에 대해 고민하여 데이터 정합성을 유지하는 방식으로 설계했습니다.<br />
특히 수정, 삭제시 기존 값을 원복한 뒤 다시 반영하는 방식으로 문제를 해결하면서 현재 계좌 잔액같은 상태 기반 데이터 처리의 중요성을 체감할 수 있었습니다.<br />
또한 계좌 금액의 마이너스 허용 여부와 같은 도메인 정책을 적용하기 위해 기존 엔티티에 새로운 필드를 추가하고 관련 로직을 확장하면서<br /> 단순 기능 구현이 아닌 도메인 요구사항을 구조적으로 반영하는 경험을 할 수 있었습니다.
정기 지출 자동 생성 기능을 스케줄러로 구현하는 과정을 통해 단순한 요청 <> 응답 기반이 아닌 백그라운드 처리 구조를 경험해봤습니다.<br />
이번 가계부 프로젝트를 통해 단순한 기능을 구현하는 것을 넘어 데이터 흐름과 상태를 고려한 도메인 중심 설계의 중요성을 배우는 시간이 되었으며<br /> 나중에는 REST API 기반 구조로 확장하고 AWS를 활용한 운영 환경까지 구축하여 실제 서비스 수준으로 발전시키고자 합니다.




---

## 개발자

* 채대승
* GitHub: https://github.com/chdaeseung

---
