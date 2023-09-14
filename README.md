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

### 프로젝트 구조
![구조도]("https://github.com/likelion-backend-5th/Final_Project_17team/assets/64578367/6c532fcd-7c1e-405f-b4fe-508930c91b2d")

### DB ERD
![erd](https://github.com/likelion-backend-5th/Final_Project_17team/assets/86220874/1686e5bb-de1c-4c53-9682-d10892d7be72)

### 회원
 기능 | HTTP METHOD | URL
 --- | ----------- | ---
 회원가입 | POST | /api/join
 로그인 | POST | /api/login
 회원 정보 조회 | GET | /api/profile
 회원 정보 수정 | PUT | /api/profile
 프로필 이미지 업로드 | PUT | /api/profile/image
 회원 탈퇴 | DELETE | /api/profile
 사용자 리뷰 전체 조회 | GET | /api/profile/reviews
 사용자 동행 글 전체 조회 | GET | /api/profile/post
 사용자 즐겨찾기 조회 | GET | /api/profile/wishlist
 즐겨찾기 삭제 | DELETE | /wishlist
 토큰 재발급 | POST | /api/reissue
 로그아웃 | POST | /api/logout
 

#### 회원 가입
사용자의 정보를 기반으로 회원가입 진행.
최초 사용자의 프로필 이미지는 S3 에 저장된 기본 이미지로 셋팅

#### 로그인
사용자의 ID, PW 를 검증 후 ACCESS TOKEN 과 재발급용 REFRESH TOKEN 을 발급

TOKEN 발급 후 Redis 에 username, access token, refresh token 을 저장
 - 중복 로그인 처리와 로그아웃 처리를 위함
 - 사용자가 다른 기기 혹은 브라우저, 새로운 탭 등 기존 로그인한 환경이 아닌 다른 환경에서 로그인을 해서 token 을 발급받게 되면 최초 발급 받은 token 은 Redis 에서 삭제되어 사용이 불가능 함
 - 요청에 포함되어서 오는 access token 은 필터에서 redis 존재 여부를 검증 후 로그인 상태가 기록되지 않은 access token 의 경우 요청을 reject
   
사용자가 자동 로그인을 선택한 경우에는 REFRESH TOKEN 을 2주 기간의 Cookie 에 저장, ACCESS TOKEN 은 브라우저 local storage 에 저장

자동 로그인을 선택하지 않은 경우에는 REFRESH TOKEN 은 Session Cookie 에 저장, ACCESS TOKEN 은 브라우저 session storage 에 저장 
 - 브라우저 종료 시 사용자 로그아웃 처리를 위함
 - 최초 아이디어는 브라우저 종료 이벤트 감지 시 서버 측으로 로그아웃 요청을 보내는 것 하지만 브라우저 종료 이벤트 감지의 범위가 페이지 이동이나 새로고침까지 감지가 되기에 매번 로그아웃이 되는 상황이 발생
 - 현재는 페이지 브라우저 종료 시 로그아웃 요청이 가는 것이 아니기 때문에 Redis 에는 로그인 기록이 남아 있어서 access token 과 refresh token 이 유출된다면 문제가 발생할 여지가 있음 (access token 은 시간이 짧기 때문에 비교적 문제가 심각하지 않음)
 - 현재는 적합한 해결방안을 찾지 못함
 - 정상적으로 서비스의 로그아웃 기능을 사용해서 로그아웃 하는 경우에는 문제가 없음

중복 로그인이나 로그아웃 처리를 위해 로그인 시에 Redis 에 로그인 기록을 저장하는 방식은 추후 로그아웃 시 access token, refresh token 을 blacklist 로 지정해 서버 측에서 사용자의 상태 정보를 최대한 저장하지 않는 방식으로 변경할 수 있음

#### 회원 정보 조회 / 수정 / 탈퇴 / 리뷰, 동행 글, 즐겨찾기 전체 조회 / 프로필 이미지 업로드
- 회원 정보 조회
  - access token 의 사용자 정보를 바탕으로 사용자 정보를 조회
- 회원 정보 수정
  - 회원 정보 중 ID, PW 를 제외한 정보 수정
- 프로필 이미지 업로드
  - 사용자 프로필 업로드
  - 사용자가 업로드한 이미지는 S3 에 저장 
- 회원 탈퇴
  - 회원 탈퇴 기능
  - 회원 탈퇴 시 해당 사용자가 작성한 리뷰, 동행 글, 등록한 즐겨 찾기, 업로드 한 이미지 전체를 삭제
- 리뷰 전체 조회
  - 해당 사용자가 작성한 리뷰 전체 조회
- 사용자 동행 글 전체 조회
  - 해당 사용자가 작성한 동행 글 전체 조회 
- 즐겨 찾기 조회
  - 해당 사용자가 등록한 즐겨 찾기 전체 조회

#### 로그아웃
사용자 로그아웃 기능
Redis 에 기록된 사용자 로그인 이력을 삭제

#### 토큰 재발급
access token 만료 시 refresh token 으로 토큰 재발급
재발급 진행 후 기존의 로그인 이력에 저장된 access token 과 refresh token 값 update

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

  #### 동행 게시판
  - 동행글 등록, 조회, 수정, 삭제
     - 동행 글 등록은 title, content, 방문 예정 날짜, 동행 스타일 등의 내용이 들어있음
     - 동행 글 조회는 게시판에서는 모든 동행 글을 보여주며, 이용자는 제목, 검색어, 성별, 나이, 모집 상태 등을 필터로 검색 가능함. 또한 마이페이지에서 내가 쓴 동행글을 조회 가능함
     - 동행 글 수정, 삭제는 해당 글 작성자만 가능하고 수정은 변경사항이 있어야만 가능함
    
  - 댓글 등록, 조회, 수정, 삭제
     - 댓글 등록은 댓글 내용만 적어주면 되고, 로그인 한 사용자만 이용 가능
     - 댓글 조회는 해당 동행글에서 조회 가능
     - 댓글 수정, 삭제는 댓글 작성자만 가능함

### 프로젝트 배포 프로세스
사용 기술 : AWS EC2 (Ubuntu), S3, RDS, Route53, Load Balancing, Https, SSL, Docker, Gradle
- AWS EC2 : Docker container 가 올라갈 EC2 인스턴스
- S3 : 서비스에서 업로드 되는 이미지 저장소
- RDS : Mysql Server
- Route53 : 도메인 적용 및 로드 밸런서로 라우팅
- Https, SSL, Load Balancing : Https 적용을 위한 SSL 인증서 및 Load Balancing 을 활용해 80 포트로 들어오는 요청을 443 포트로 리다이렉트, 443 포트로 들어온 요청을 다시 EC2 80 포트로 전달
- Gradle : 프로젝트 빌드 (make jar file)
- Docker : 프로젝트 빌드 후 jar file 로 docker image 생성, docker hub 에 업로드 후 ec2 에서 다운로드 받아 container 화 시킴

EC2 인스턴스에서 spring image, redis image, redis-insight image 를 동시에 띄우기 위해 docker-compose 활용

spring 의 application.yaml 파일 내부의 민감 정보는 환경 변수로 설정(EC2 인스턴스에서 직업 .env 파일 작성)

#### 작성한 docker-compose.yml (EC2 인스턴스에서 직접 작성)
```
services:
  redis:
    image: redis
    ports:
      - 6379:6379
    container_name: redis
    restart: always

  redis-insight:
    image: redislabs/redisinsight:latest
    ports:
      - 8001:8001
    container_name: redis-insight
    restart: always

  matprint:
    image: syber911911/matprint:0.1
    ports:
      - 80:8080
    container_name: matprint
    restart: always
    environment:
      - DB_URL=${DB_URL}
      - DB_USER=${DB_USER}
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
      - ACCESS_KEY=${ACCESS_KEY}
      - BUCKET=${BUCKET}
      - SECRET_KEY=${SECRET_KEY}
    depends_on:
      - redis
      - redis-insight
```
