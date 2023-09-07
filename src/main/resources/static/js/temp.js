document.addEventListener("DOMContentLoaded", function () {
    localStorage.setItem('token', "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNCIsImlhdCI6MTY5Mzk5NzE1NSwiZXhwIjoxNjk0MDgzNTU1fQ.FQ7bvFRh_PIHhQHn4ve4usRDlA0vb429fijgwTnueN1dULPCd0B0P5hjVSZa3o0_8Q1v4ADBNmbUE02OfKX9Sw");

    //const button1 = document.getElementById("button1");
    const username = document.getElementById("username");
    const gender = document.getElementById("gender");
    const email = document.getElementById("email");

    //new
    const buttonElem1 = document.getElementById("button1");//메뉴버튼1
    const buttonElem2 = document.getElementById("button2");//메뉴버튼2
    const buttonElem3 = document.getElementById("button3");//메뉴버튼3
    const changeContentBox = document.getElementById('changecontentBox');//메뉴버튼누를시 바뀌는 컨테이너


    // 클릭 이벤트 리스너 추가
    // button1.addEventListener("click", function () {
    //     requestProfileData(username, gender, email);
    // });


    const handleButtonClick = (e) => {
        e.preventDefault();
        console.log(e);
        const template = `
    <div>
    <h1>showMyPage</h1>
    <p>content</p>
</div>
    `
        showMyPage(template);
    }
    buttonElem1.addEventListener("click", handleButtonClick);

    const handleButtonClick2 = (e) => {
        e.preventDefault();
        console.log(e);
        const template = `
    <div>
        <h1>Post</h1>
        <p>content</p>
    </div>
    `
        showPost(template);
    }
    buttonElem2.addEventListener("click", handleButtonClick2);


    const handleButtonClick3 = (e) => {
        e.preventDefault();
        console.log(e);
        const template = `
    <div>
        <h1>LikedPost</h1>
        <p>content</p>
    </div>
    `
        showLikedPost(template);
    }
    buttonElem3.addEventListener("click", handleButtonClick3);

    //new
    const showPost = (template) => {
        changeContentBox.innerHTML = ``;
        changeContentBox.insertAdjacentHTML('afterbegin', template);
    };
    const showMyPage = (template) => {
        changeContentBox.innerHTML = ``;
        changeContentBox.insertAdjacentHTML('afterbegin', template);
    };
    const showLikedPost = (template) => {
        changeContentBox.innerHTML = ``;
        changeContentBox.insertAdjacentHTML('afterbegin', template);
    };



    // 페이지 로드 시 토큰 확인 및 프로필 정보 요청
    const token = localStorage.getItem('token');
    if (token) {
        requestProfileData(username, gender, email, token);
    } else {
        console.error('토큰이 로컬 스토리지에 없습니다.');
    }
});

async function requestProfileData(usernameElement, genderElement, emailElement, token) {
    try {
        const headers = {
            'Content-Type': 'application/json'
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch('/profile', {
            method: 'GET',
            headers: headers,
        });

        if (response.status === 200) {
            const data = await response.json();
            console.log(data);
            usernameElement.innerHTML = "사용자명 : " + data.username;
            genderElement.innerHTML = "성별 : " + data.gender;
            emailElement.innerHTML = "이메일 : " + data.email;
        } else {
            console.error("프로필 정보를 가져오는데 실패했습니다.");
        }
    } catch (error) {
        console.error("오류 발생:", error);
    }
}