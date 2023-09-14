# Final_Project_17team
# 멋쟁이 사자처럼 백엔트 스쿨 5기 - JDK17 팀
# 👣 맛자국
Spring Boot Team Project - 맛집 추천 및 동행 찾기 사이트

## 📒프로젝트 개요
인플루언서 맛집, 사용자 검색 맛집 등의 정보를 제공하며, 동행 찾기 글을 통해 맛집 메이트를 구할 수 있는 기능을 구현

## 🗓️ 프로젝트 기간
2023-08-09 ~ 2023-09-15

## 🧑🏻‍💻 개발환경 및 사용기술
- `java version 17`
- `Spring Boot 3.1.1`
- `MySQL`
- `Redis`
- `IntelliJ IDEA Ultimate`
- `Spring Security`
- `JWT token`
- `Spring Data JPA`
- `HTML`
- `Java Script`
- `CSS`
- `Docker`

## ⚙️ 구현 기능

### DB ERD
![erd](https://github.com/likelion-backend-5th/Final_Project_17team/assets/86220874/1686e5bb-de1c-4c53-9682-d10892d7be72)

### 회원
 기능 | HTTP METHOD | URL
 --- | ----------- | ---
 회원가입 | POST | /users/join
 로그인 | POST | /user/login
 회원 정보 조회 | GET | /user/profile
 회원 정보 수정 | PUT | /user/profile
 회원 탈퇴 | DELETE | /user/profile
 사용자 리뷰 전체 조회 | GET | /user/reviews
 즐겨찾기 조회 | GET | /wishlist
 즐겨찾기 삭제 | DELETE | /wishlist
 토큰 재발급 | POST | /users/reissue
 로그아웃 | POST | /users/logout
 

#### //TODO 시큐리티 기능 설명


### 검색으로 맛집 찾기
 기능 | HTTP METHOD | URL
 --- | ----------- | ---
 검색 맛집 페이지 이동 | GET | /search
 맛집 조회 | GET | /search?target={target}

#### RestaurantDto
프론트에서 간단히 보여줄 정보가 담긴 DTO    

#### RestaurantDetailDto
음식점 상세 페이지에서 보여줄 정보가 담긴 DTO

#### 맛집 검색, 상세 페이지 
- 검색
  - 사용자가 searchBox에 target을 입력하면 "target + 음식점" 형태로 질의문을 만듦
  - 네이버 지역 검색 페이지에 질의어(ex. 강남역 음식점)로 검색된 결과(식당 이름, 메뉴, 영업시간, 좌표 등)를 크롤링하여 데이터를 받아옴

![맛집 검색 페이지 ](https://github.com/likelion-backend-5th/Final_Project_17team/assets/86220874/96a83b85-da67-42ed-a327-a94f5df61ecd)

- 상세 페이지
  - 음식점 이름을 클릭하면 상세 페이지로 넘어감
  - 식당 사진, 주소, 영업 시간, 전화번호, 메뉴 등의 정보가 있고, 리뷰 조회, 위시리스트 등록 기능, 로그인 한 사용자에 한해 리뷰 작성 기능 제공
 ![image](https://github.com/likelion-backend-5th/Final_Project_17team/assets/86220874/850031a3-fd07-4cc7-a696-30c242a7a69d)


### 리뷰

### 인플루언서 맛집 찾기

### 맛집 상세 정보



### 동행찾기
   

