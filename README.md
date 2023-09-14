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
 상세페이지에서 즐겨찾기 | POST | /wishlist

#### RestaurantDto
프론트에서 간단히 보여줄 정보가 담긴 DTO    

#### RestaurantDetailDto
음식점 상세 페이지에서 보여줄 정보가 담긴 DTO

#### 맛집 검색, 상세 페이지 
- 검색
  - 사용자가 searchBox에 target을 입력하면 "target + 음식점" 형태로 질의문을 만듦
  - 네이버 지역 검색 페이지에 질의어(ex. 강남역 음식점)로 검색된 결과(식당 이름, 메뉴, 영업시간, 좌표 등)를 크롤링하여 데이터를 받아옴
  - 수십만 개의 음식점 공공 데이터를 사용하기보다 이러한 방식을 사용하여 자체적인 DB를 구축 하였음
  - 네이버 지도 api를 활용하여 마커를 찍어주고, 검색했을 때 지도의 첫 화면은 결과 리스트들의 평균 좌표를 구해주어 한눈에 보이게 하였음 

![맛집 검색 페이지 ](https://github.com/likelion-backend-5th/Final_Project_17team/assets/86220874/96a83b85-da67-42ed-a327-a94f5df61ecd)

- 상세 페이지
  - 음식점 이름을 클릭하면 상세 페이지로 넘어감
  - 식당 사진, 주소, 영업 시간, 전화번호, 메뉴 등의 정보가 있고, 리뷰 조회, 위시리스트 등록 기능, 로그인 한 사용자에 한해 리뷰 작성 기능 제공
 ![image](https://github.com/likelion-backend-5th/Final_Project_17team/assets/86220874/850031a3-fd07-4cc7-a696-30c242a7a69d)


### 리뷰
 기능 | HTTP METHOD | URL
 --- | ----------- | ---
 리뷰 작성 | POST | /review/post
 리뷰 수정 | PUT | /review/edit
 리뷰 조회 | GET | /review
 리뷰 삭제 | DELETE | /review

#### CreateReviewDto
사용자가 리뷰 작성한 내용을 받아오는 DTO. 리뷰 내용과 평점은 필수로 들어가야 함

#### ReadReviewDto
사용자에 따라 ui 변화를 주기 위해 현재 해당 요청을 보낸 사용자의 정보를 포함해 함께 반환

#### UpdateReviewDto
이전 리뷰와 동일한지를 판단하는 로직 들어있음

#### 리뷰 작성, 수정, 조회, 삭제
- 작성
  - 로그인한 사용자만 작성할 수 있음
  - 리뷰에 이미지를 첨부할 수 있으며, 이미지는 bucket에서 관리 함     

- 수정
  - 리뷰에 수정사항이 있을 때만 수정할 수 있으며, 리뷰를 작성했던 유저만이 수정할 수 있음

- 조회
  - 음식점 상세페이지에서 그 페이지에 해당하는 식당에 대한 리뷰 조회 가능

- 삭제
  - 리뷰를 작성했던 유저만이 수정 가능

### 인플루언서 맛집 찾기
 기능 | HTTP METHOD | URL
 --- | ----------- | ---
 인플루언서 맛집 찾기 페이지 이동 | GET | /named
 카테고리 선택 조회 | GET | api/named
 카테고리 선택 정렬 조회 | GET |  api/named/sort

 #### RestaurantDto
 인플루언서 맛집 찾기 페이지에서 보여줄 간단한 정보들만 있는 DTO

 #### 인플루언서 맛집 데이터 수집, 조회
- 데이터 수집
  - 인플루언서들이 다녀간 맛집으로 유명한 곳은 많지만, 가보고 싶어 찾아보려면 일일이 검색해야하 번거로움이 있음
  - 성시경 맛집 같은 경우 다녀간 맛집들을 식당 정보와 함께 유튜브에 업로드 함
  - 유튜브에서 크롤링한 비정형 데이터를 Tokenization, Normalization를 통해 네이버 API에 검색이 잘 되도록 질의어를 만듦
  - 이 질의어를 통해 네이버 검색에 검색함으로써 비정형 데이터를 정형 데이터로 바꿔줌.

- 데이터 조회
  - 일련의 데이터 수집과정으로 얻어온 인플루언서 맛집 데이터들을 조회하는 함수를 만듦
  - RequestParam으로 카테고리는 필수적으로 받아서, 어떤 인플루언서의 맛집인지 보여줌
  - 선택적인 파라미터로 이름 순, 리뷰 많은 순, 평점 높은 순 등의 정렬 기준을 입력받아 조회할 수 있음
  - 웹 페이지에서는 네이버 지도 api를 활용하여 마커를 인플루언서의 얼굴로 설정하여 차별적인 요소를 넣었고, 마커를 누르면 식당의 간단한 설명이 나옴
  - 맛집 검색과 동일하게 음식점 이름을 누르면 상세 페이지로 이동함
  - 성시경, 또간집 맛집은 전국적으로 분포되어 있어 처음 중심 좌표를 전국이 다 보이게 설정함
  - 이영자 맛집은 서울에 분포되어 있어, 맛집 가장 밀집되어 있는 좌표를 기준으로 한 눈에 보이게 설정함

![image](https://github.com/likelion-backend-5th/Final_Project_17team/assets/86220874/078f349f-f81c-4b86-add4-df0a8430f20b)


### 동행찾기
  기능 | HTTP METHOD | URL
  --- | ----------- | ---
  동행 찾기 페이지 이동 | GET | /mate
  사용자 동행 글 전체조회 | GET | api/user/posts
  동행 글 작성 | POST |  api/mate
  동행 글 수정 | PUT |  api/mate
  동행 글 삭제 | DELETE |  api/mate/post/
  동행 글 단일 조회 | GET | api/mate/{postId}
  동행 글 전체 조회 | GET | api/mate
  동행 글 검색 | GET | api/mate/search?
  댓글 생성 | POST | api/mate/{postId}/comment
  댓글 조회 | GET | api/{postId}/comment
  댓글 수정 | PUT | api/mate/{postId}/comment/{commentId}
  댓글 삭제 | DELETE | api/mate/{postId}/comment/{commentId}


  #### PostDto
  특정 유저가 작성한 동행글 조회를 위한 DTO

  #### CreatePostDto
  동행글 작성을 위한 DTO  
   
  #### ReadPostDto
  검색어를 통해 동행 글을 찾기 위해 작성한 DTO
 
  #### UpdatePostDto
  동행글 수정을 위한 DTO. 이전의 것과 비교해서 수정된 것이 없으면 업데이트 하지 않는다.

  #### CommentDto
  특정 동행글의 댓글 조회를 위한 DTO
 
  #### CreateCommentDto
  댓글 작성을 위한 DTO
 
  #### UpdateCommentDto
  댓글 수정을 위한 DTO

