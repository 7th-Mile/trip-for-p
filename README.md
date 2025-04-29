# Trip For P
<div align="center">
  
![Logo](https://github.com/user-attachments/assets/f88a4395-ee1a-4f87-8b8c-3c2af26d249f)

**MBTI P**들을 위한 여행 계획 서비스

</div>

## 목차
1. [프로젝트 개요](#-프로젝트-개요)
1. [팀원 및 역할](#-팀원-및-역할)
3. [주요 기능](#-주요-기능)
4. [기술 스택](#-기술-스택)
5. [시스템 아키텍처](#-시스템-아키텍처)
6. [ERD](#-시스템-아키텍처)
7. [구현 화면](#-구현-화면)

<br>

## 📝 프로젝트 개요
- **개요**: MBTI가 P인 사람들을 위한 여행 계획 서비스
- **기간**: 
  - v1.0: 기본 여행 계획 서비스 (2024.08 ~ 2024.09)
  - v2.0: 기능 고도화 및 성능 최적화, UI 개선 (2024.12 ~)
- **배포 링크**: [Trip For P](https://tripforp.co.kr)
- **테스트 계정**: ID: testuser / PW: qweqwe123

<br>

## 👥 팀원 및 역할

<div align="center">

|<img src="https://github.com/user-attachments/assets/3a7c5441-20a9-42ac-9e19-e527485c5cb3" width="150" height="200"/>|<img src="https://github.com/user-attachments/assets/22896833-6e3a-4006-9e2c-cff8a014fee0" width="150" height="200"/>|<img src="https://github.com/user-attachments/assets/40bbf19c-7051-4e76-9ffb-748622523159" width="150" height="200"/>|
|:-:|:-:|:-:|
|박정균(팀장)<br/>[@junggyun](https://github.com/junggyun)|문석준<br/>[@SaMiGong](https://github.com/SaMiGong)|박준호<br/>[@junhobark](https://github.com/junhobark)|
|**#Frontend**<br>- 개발 총괄<br><br>**#Backend**<br>- 여행코스 API 개발<br>- AI 관련 서비스 개발<br><br>**#Test**<br>- 성능 부하테스트|<br>**#설계**<br>- 화면 설계<br><br>**#Frontend**<br>- 프론트엔드 개발<br><br>**#Backend**<br>- 여행코스 API 개발<br><br> |<br>**#설계**<br>- 화면 설계<br><br>**#Frontend**<br>- 프론트엔드 개발<br><br>**#Backend**<br>- 여행코스 API 개발<br>- AI 개인 맞춤형 여행 코스 추천<br>서비스 개발<br><br>|
|<img src="https://github.com/user-attachments/assets/08475fea-a846-46ef-976c-ea6a29f4c29b" width="150" height="200"/>|<img src="https://github.com/user-attachments/assets/dec42cf9-74d3-4159-a830-42f03d740ff4" width="150" height="200"/>|<img src="https://github.com/user-attachments/assets/c7b2533b-56d5-4e93-ac0a-87a722623ece" width="150" height="200"/>|
|이남경<br/>[@NamK666](https://github.com/NamK666)|조성윤<br/>[@syeej](https://github.com/syeej)|허영윤<br/>[@cloudisme99](https://github.com/cloudisme99)|
|<br>**#설계**<br>- 화면 설계<br><br>**#Backend**<br>- 리뷰 게시글 + 댓글 API 개발<br>- 자유 게시글 + 댓글 API 개발<br>- 매거진 게시글(첨부파일) API 개발<br><br>|<br>**#설계**<br>- ERD 및 배포 구조 설계<br><br>**#Frontend**<br>- 마이페이지 프론트엔드 개발<br><br>**#Backend**<br>- 회원가입 및 이메일 인증 API 개발<br>- 마이페이지 API 개발<br><br>**#기타**<br>- GitHub Actions 활용 CI/CD 구축<br><br>|<br>**#설계**<br>- ERD 설계<br><br>**#Backend**<br>- 로그인, 로그아웃<br>(Spring Security & JWT) 개발<br>- 비밀번호 재설정 API 개발<br>- AI 장소 기반 추천 서비스 API 개발<br><br>|
</div>

<br>

## 💡 주요 기능
### 여행 코스 생성 및 공유
- 구글맵 API를 활용한 장소 검색 및 선택
- 장소의 주소, 평점, 리뷰 수, 구글맵 링크 제공
- 일별로 방문지 순서 선택 가능
- 여행 동선을 지도에서 한눈에 확인
- 지역별 여행 코스 필터링 조회
### 알고리즘 기반 여행 동선 최적화
- DFS 기반 백트래킹 알고리즘으로 구현
- 위도, 경도 좌표 기반으로 haversine 거리 계산 적용
### 데이터 기반 인기 지역/방문지 추천
- 사용자들의 여행 코스 데이터 분석을 통한 인기 방문지 추천
- 지역별 인기 방문지 순위 제공
- 지역별 여행 코스 등록 수를 반영한 인기 지역 제공
### 여행 리뷰 및 커뮤니티
- 여행 코스 리뷰 작성 및 공유
- 사진 첨부 기능으로 생생한 여행 리뷰 전달
- 자유게시판을 통한 여행 정보 공유 및 소통
### 여행 매거진
- 운영자가 주기적으로 작성하는 여행지 소개
- 사진과 텍스트를 통한 상세한 여행 코스 공유

<br>

## 🛠 기술 스택
### Backend
![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=flat-square&logo=SpringBoot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)

### Frontend
![Vue.js](https://img.shields.io/badge/Vue.js-4FC08D?style=flat-square&logo=vue.js&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat-square&logo=javascript&logoColor=white)

### Infrastructure
![AWS EC2](https://img.shields.io/badge/Amazon_EC2-FF9900?style=flat-square&logo=amazonec2&logoColor=white)
![AWS RDS](https://img.shields.io/badge/Amazon_RDS-527FFF?style=flat-square&logo=amazon-rds&logoColor=white)
![AWS S3](https://img.shields.io/badge/Amazon_S3-569A31?style=flat-square&logo=amazon-s3&logoColor=white)

### DevOps & Testing
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat-square&logo=github-actions&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=flat-square&logo=nginx&logoColor=white)
![k6](https://img.shields.io/badge/k6-7D64FF?style=flat-square&logo=k6&logoColor=white)

<br>

## 📐 시스템 아키텍처
![Architecture](https://github.com/user-attachments/assets/97321cc0-43d4-4f64-a360-87755eb2457a)

<br>

<!--
## 🗓️ 개발 일정
<div align="center">

  ![image](https://github.com/user-attachments/assets/9f7a657c-a278-4630-b78f-f9af799f8cfd)
  
</div>

<br>
-->

<!--
## 🖥 요구사항 및 기능 명세
### 회원, 여행 코스
![image](https://github.com/user-attachments/assets/1ad0dc4b-f575-493b-86c8-9d53851afa80)

### 리뷰 게시글, 자유 게시글
![image](https://github.com/user-attachments/assets/01626440-baa2-4c73-8012-e5f241be7023)

### 매거진, AI 활용 추천 서비스
![image](https://github.com/user-attachments/assets/7c3146f8-916b-4041-9cd3-ac7a26733225)


<br>
-->


## 🛢️ ERD
![ERD](https://github.com/user-attachments/assets/c9a26b5a-815a-4a77-95e5-20080bf9d049)

<br>

<!--

## 플로우차트 & 화면 설계
### 🔀 플로우차트
![image](https://github.com/user-attachments/assets/f41ecbba-a59f-42aa-80be-290ca5a5a4c0)

### 🖼️ 화면 설계
![image](https://github.com/user-attachments/assets/e8bbb6bd-42e0-4b62-bf04-ec3ac37eba5a)

<br>
-->

<!--

## 📦 프로젝트 구조
FE 프론트엔드와 BE 백엔드가 합쳐진 프로젝트 디렉토리 구조입니다.
파일이 많은 관계로 프론트엔드는 파일을 말줄임표로 줄였으며, 백엔드는 폴더까지만 보여집니다.

```
├── frontend
│   ├── Dockerfile
│   ├── README.md
│   ├── babel.config.js
│   ├── jsconfig.json
│   ├── package-lock.json
│   ├── package.json
│   ├── public
│   │   ├── favicon.ico
│   │   └── index.html
│   ├── src
│   │   ├── App.vue
│   │   ├── api
│   │   │   ├── index.js
│   │   │   └── interceptor.js
│   │   ├── assets
│   │   │   ├── backbutton.png
│   │   │   ├── biglogo.png
│   │   │   ├── dot.png
│   │   │   ├── email.png
│   │   │   ├── fonts
│   │   │   │   ├── pretendardvariable.css
│   │   │   │   └── woff2
│   │   │   │       └── PretendardVariable.woff2
│   │   │   ├── likes.png
│   │   │   ├── location.png
│   │   │   ├── logo.png
│   │   │   ├── nickname.png
│   │   │   ├── password.png
│   │   │   ├── signup.png
│   │   │   └── viewers.png
│   │   ├── components
│   │   │   ├── DeleteAccount.vue
│   │   │   ├── FooterComponent.vue
│   │   │   ├── HeaderComponent.vue
│   │   │   ├── ItineraryComponent.vue
│   │   │   ├── LikedPlans.vue
│   │   │   ├── MyCourses.vue
│   │   │   └── ...
│   │   ├── main.js
│   │   ├── router
│   │   │   └── index.js
│   │   ├── store
│   │   │   └── index.js
│   │   └── views
│   │       ├── AdminView.vue
│   │       ├── ChatView.vue
│   │       ├── EditFreePostView.vue
│   │       ├── EditMagazineView.vue
│   │       ├── EditPlanView.vue
│   │       ├── EditReviewPostView.vue
│   │       ├── FreePostDetailView.vue
│   │       ├── FreePostListView.vue
│   │       ├── HomeView.vue
│   │       └── ...
│   └── vue.config.js
└── src
    ├── main
    │   ├── java
    │   │   └── team
    │   │       └── seventhmile
    │   │           └── tripforp
    │   │               ├── domain
    │   │               │   ├── file
    │   │               │   │   ├── entity
    │   │               │   │   ├── repository
    │   │               │   │   └── service
    │   │               │   ├── free_comment
    │   │               │   │   ├── controller
    │   │               │   │   ├── dto
    │   │               │   │   ├── entity
    │   │               │   │   ├── repository
    │   │               │   │   └── service
    │   │               │   ├── free_post
    │   │               │   │   ├── controller
    │   │               │   │   ├── dto
    │   │               │   │   ├── entity
    │   │               │   │   ├── repository
    │   │               │   │   └── service
    │   │               │   ├── magazine
    │   │               │   │   ├── controller
    │   │               │   │   ├── dto
    │   │               │   │   ├── entity
    │   │               │   │   ├── repository
    │   │               │   │   └── service
    │   │               │   ├── plan
    │   │               │   │   ├── controller
    │   │               │   │   ├── dto
    │   │               │   │   ├── entity
    │   │               │   │   ├── repository
    │   │               │   │   └── service
    │   │               │   ├── review_comment
    │   │               │   │   ├── controller
    │   │               │   │   ├── dto
    │   │               │   │   ├── entity
    │   │               │   │   ├── repository
    │   │               │   │   └── service
    │   │               │   ├── review_post
    │   │               │   │   ├── controller
    │   │               │   │   ├── dto
    │   │               │   │   ├── entity
    │   │               │   │   ├── repository
    │   │               │   │   └── service
    │   │               │   └── user
    │   │               │       ├── controller
    │   │               │       ├── dto
    │   │               │       ├── entity
    │   │               │       ├── repository
    │   │               │       └── service
    │   │               ├── external
    │   │               │   └── alan
    │   │               │       ├── controller
    │   │               │       ├── dto
    │   │               │       └── service
    │   │               └── global
    │   │                   ├── common
    │   │                   ├── config
    │   │                   ├── exception
    │   │                   │   └── user
    │   │                   └── jwt
    │   └── resources
    └── test
        └── java
            └── team
                └── seventhmile
                    └── tripforp
                        ├── TripForPApplicationTests.java
                        └── domain
                            ├── free_post
                            │   ├── controller
                            │   │   └── FreePostControllerTest.java
                            │   ├── repository
                            │   │   └── FreePostRepositoryTest.java
                            │   └── service
                            │       └── FreePostServiceTest.java
                            ├── plan
                            │   ├── controller
                            │   │   ├── PlanControllerTest.java
                            │   │   └── PlanLikeControllerTest.java
                            │   ├── repository
                            │   │   ├── PlanLikeRepositoryTest.java
                            │   │   └── PlanRepositoryTest.java
                            │   └── service
                            │       ├── PlanLikeServiceTest.java
                            │       └── PlanServiceTest.java
                            ├── review_post
                            │   ├── controller
                            │   │   └── ReviewPostControllerTest.java
                            │   ├── repository
                            │   │   └── ReviewPostRepositoryTest.java
                            │   └── service
                            │       └── ReviewPostServiceTest.java
                            └── user
                                ├── controller
                                │   └── UserControllerTest.java
                                ├── repository
                                │   └── UserRepositoryTest.java
                                └── service
                                    └── UserServiceTest.java

```

<br>
-->

<!--

## API 명세서
### User

| 메서드명 | HTTP 메서드 | 엔드포인트 | 역할 |
|----------|-------------|------------|------|
| createUser | POST | /api/users/registration | 회원 가입 |
| verifyNickname | GET | /api/users/nickname-verification | 닉네임 중복 확인 |
| signIn | POST | /api/users/signin | 로그인(사용자 인증 및 토큰 발급) |
| createRefreshToken | POST | /api/users/reissue| Refresh Token 요청 |
| signOut | POST | /api/users/signout | 로그아웃 |
| findPassword | PATCH | /api/users/password/renewal | 비밀번호 찾기 |
| modifyPassword | PATCH | /api/users/me/password | 비밀번호 변경 |
| deleteUser | PATCH | /api/users/deletion | 회원 탈퇴(마이페이지) |
| getUserInfo | GET | /api/users/me | 회원 정보 조회(마이페이지) |
| updateUser | PATCH | /api/users/me | 회원 정보 수정(마이페이지) |
| resetPassword | POST | /api/users/password/renewal | 비밀번호 재설정 |
| sendPasswordResetEmail | POST | /api/mails/password-reset-verification | 비밀번호 재설정 시 인증 코드 발송 |
| sendVerificationEmail | POST | /api/mails/send-verification | 이메일 인증 코드 발송 |
| verifyEmail | POST | /api/mails/verification | 이메일 인증 코드 검증 |

### Plan

| 메서드명 | HTTP 메서드 | 엔드포인트 | 역할 |
|----------|-------------|------------|------|
| createPlan | POST | /api/plans | 여행 코스 등록 |
| updatePlan | PUT | /api/plans/{id} | 여행 코스 수정 |
| deletePlan | DELETE | /api/plans/{id} | 여행 코스 삭제 |
| getPlanDetail | GET | /api/plans/{id} | 여행 코스 단일 조회 |
| getPlanList | GET | /api/plans?area={area} | 여행 코스 목록 조회 |
| getMyPlanList | GET | /api/plans/me | 사용자가 작성한 여행 코스 목록 조회 |
| getPopularPlaces | GET | /api/plan/popular-places | 인기있는 장소 목록 조회 |
| getPopularPlans | GET | /api/plan/popular-plans | 인기있는 코스 목록 조회 |
| toggleLikePlan | POST | /api/plan-likes | 여행코스 좋아요 또는 좋아요 취소 |
| getMyFavPlanList | GET | /api/plan-likes/me | 사용자가 좋아요한 코스 목록 조회 |

### ReviewPost

| 메서드명 | HTTP 메서드 | 엔드포인트 | 역할 |
|----------|-------------|------------|------|
| createReviewPost | POST | /api/review-posts | 리뷰 게시글 작성 |
| updateReviewPost | PUT | /api/review-posts/{id} | 리뷰 게시글 수정 |
| deleteReviewPost | DELETE | /api/review-posts/{id} | 리뷰 게시글 삭제 |
| getReviewPostDetail | GET | /api/review-posts/{id} | 리뷰 게시글 상세 조회 |
| getReviewPosts | GET | /api/review-posts?size={size}&page={page}&keyword={keyword} | 리뷰 게시글 목록 조회 |
| getMyReviewPostList | GET | /api/review-posts/me | 사용자가 작성한 리뷰 게시글 목록 조회 |


### ReviewComment

| 메서드명 | HTTP 메서드 | 엔드포인트 | 역할 |
|----------|-------------|------------|------|
| getReviewComments	| GET	| /api/review-posts/{postId}/comments	| 리뷰 게시글 댓글 조회 |
| createReviewComment	| POST | /api/review-posts/{postId}/comments	| 리뷰 게시글 댓글 작성 |
| updateReviewComment	| PUT | /api/review-posts/{postId}/comments/{commentId}	| 리뷰 게시글 댓글 수정 |
| deleteReviewComment	| DELETE | /api/review-posts/{postId}/comments/{commentId}	| 리뷰 게시글 댓글 삭제 |
| getMyReviewCommnet	| GET	| /api/review-posts/{postId}/comments/me	| 사용자가 작성한 리뷰 댓글 조회 |

### FreePost

| 메서드명 | HTTP 메서드 | 엔드포인트 | 역할 |
|----------|-------------|------------|------|
| createFreePost | POST	| /api/free-posts/create | 자유 게시글 작성 |
| updateFreePost | PUT | /api/free-posts/{id} | 자유 게시글 수정 |
| deleteFreePost | DELETE	| /api/free-posts/{id} | 자유 게시글 삭제 |
| getFreePostDetail | GET	| /api/free-posts/{id} | 자유 게시글 상세 조회 |
| getFreePosts | GET	| /api/free-posts?size={size}&page={page}&search={keyword} | 자유 게시글 목록 조회 |
| getMyFreePostList | GET	| /api/free-posts/me | 사용자가 작성한 자유 게시글 목록 조회 |

### FreeComment

| 메서드명 | HTTP 메서드 | 엔드포인트 | 역할 |
|----------|-------------|------------|------|
| getFreeComments	| GET	| /api/free-posts/{postId}/comments | 자유 게시판 댓글 조회 |
| createFreeComment	| POST | /api/free-posts/{postId}/comments | 자유 게시판 댓글 작성 |
| updateFreeComment	| PUT	| /api/free-posts/{postId}/comments/{commentId} | 자유 게시판 댓글 수정 |
| deleteFreeComment	| DELETE | /api/free-posts/{postId}/comments/{commentId} | 자유 게시판 댓글 삭제 |
| getMyFreeCommnet	| GET | /api/free-posts/{postId}/comments/me | 사용자가 작성한 자유 게시글 댓글 조회 |

### Magazine

| 메서드명 | HTTP 메서드 | 엔드포인트 | 역할 |
|----------|-------------|------------|------|
| createMagazinePost | POST	| /api/magazines | 매거진 등록 |
| updateMagazinePost | PUT	| /api/magazines/{id} | 매거진 게시글 수정 |
| deleteMagazinePost | DELETE	| /api/magazines/{id}	| 매거진 게시글 삭제 |
| getMagazineDetail | GET	| /api/magazines/{id}	| 매거진 게시글 상세 조회 |
| getAllMagazineList | GET | /api/magazines?size={size}&page={page}&search={keyword}| 매거진 게시글 목록 조회 |

### Alan

| 메서드명 | HTTP 메서드 | 엔드포인트 | 역할 |
|----------|-------------|------------|------|
| processArea | GET | /api/alan/area | 여행 지역 기반 여행지 추천 |
| userprocess | GET | /api/plan/user | 개인맞춤형 여행코스 추천 |

<br>
-->


## 📺 구현 화면
| ![코스 등록](https://github.com/user-attachments/assets/8d018bfe-1c1c-494a-9dc6-33be1c5ad312) | ![코스 조회](https://github.com/user-attachments/assets/72a3ee8f-1d58-43aa-9623-c7a2a0b138a3) |
| :---: | :---: |
| **코스 등록** | **코스 조회** |

| ![핫플 추천](https://github.com/user-attachments/assets/777332a3-2b23-4cde-8629-5dd4b5acabd9) | ![동선 추천](https://github.com/user-attachments/assets/007d224f-ac4c-4083-9f0d-338647e4dfcf) |
| :---: |:-----------------------------------------------------------------------------------------:|
| **데이터 기반 핫플 추천** |                                       **동선 최적화 기능**                                       |








<br>

<!--

## 🔴 시연 영상
<div align="center">

  [![Video Label](http://img.youtube.com/vi/5ZE6PsUTqrM/0.jpg)](https://youtu.be/5ZE6PsUTqrM) 

  [YouTube에서 보기](https://www.youtube.com/watch?v=5ZE6PsUTqrM)
</div>

-->
