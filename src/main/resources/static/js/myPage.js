document.addEventListener("DOMContentLoaded", async () => {
    //TODO localStorage.setItem : 로그인 구현시 삭제
    localStorage.setItem('token', "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0ODg4OCIsImlhdCI6MTY5NDI0MDg4NSwiZXhwIjoxNjk0MzI3Mjg1fQ.0OfhcONPssd_NoYtaND3NHYEgLSuK8qUcO1EDvCA20PLGlqVqsKFsYkKA1QJ9S98pZhpYS7bPDmMpiWo1DzTpg");
    const httpModule = await import("./service/HttpHandler.js");
    const userModule = await import("./service/User.js");
    const templateModule = await import("./service/Template.js");

    const myInfoBtn = document.getElementById("myInfo");
    const likePostBtn = document.getElementById("likePost");
    const myPostBtn = document.getElementById("myPost");
    const updateBtn = document.getElementById("update");
    const goBackBtn = document.getElementById("goback");
    const logoutBtn = document.getElementById("logout");
    const leaveBtn = document.getElementById("leaveBtn");
    const changeContentBox = document.getElementById('changecontentBox');
    const token = localStorage.getItem('token');
    const HttpHandler = new httpModule.HttpHandler(token);
    const User = new userModule.User(token);
    const { likePostTemplate, postTemplate, profileTemplate } = templateModule;

    const clear = () => changeContentBox.innerHTML = ``;
    const paint = (template) => changeContentBox.insertAdjacentHTML('afterbegin', template);

    const handleLeave = (e) => {
        e.preventDefault();
        if (confirm("정말 회원 탈퇴하시겠습니까??") == true){
            User.deleteUser('/profile');
        }else{   //취소
            return false;
        }
    }
    leaveBtn.addEventListener('click', handleLeave);
    const handleGoback = (e) => {
        e.preventDefault();
        window.location.replace('/main');
    }
    goBackBtn.addEventListener('click', handleGoback);
    const showProfile = async () => {
        clear();
        profileTemplate(HttpHandler, paint);
    }
    //start
    if (token) {
        showProfile(token)
    } else {
        console.error('토큰이 로컬 스토리지에 없습니다.');
    }
    const showInfo = (e) => {
        e.preventDefault();
        showProfile()
    }
    myInfoBtn.addEventListener("click", showInfo);

    const handleLogout = (e) => {
        e.preventDefault();
        User.logout('/logout');
    }
    logoutBtn.addEventListener('click', handleLogout);

    const showLikedPosts = async (e) => {
        e.preventDefault();
        clear();
        likePostTemplate(HttpHandler, paint);
    }
    likePostBtn.addEventListener("click", showLikedPosts);


    const showMyPosts = async (e) => {
        e.preventDefault();
        clear();
        postTemplate(HttpHandler, paint);
    }
    myPostBtn.addEventListener("click", showMyPosts);

    const showUpdateForm = async (e) => {
        e.preventDefault();
        clear();
        const profile = await HttpHandler.request('/profile');
        const { phone, age, gender, email } = profile;
        paint(`
    <form id="submitForm" action="/profile">
    <div class ="form-group">
        <label for = "email">email:</label>
        <input value=${email} type = "email" id ="email" name = "email" required />
    </div>
    <div class ="form-group">
        <label for = "phone">phone:</label>
        <input value=${phone} type = "text" id ="phone" name = "phone" required />
    </div>
    <div class ="form-group">
        <label for = "age">age:</label>
        <input id="age" value="${age}" type="number" min="1" max="100" required>
    </div>
    <div class ="form-group">
        <label for = "male">gender:</label>
        <input value="남성" type ="radio" id ="male" name ="gender" required ${gender == "남성" ? "checked" : ""}>남성
        <input value="여성" type ="radio" id ="female" name ="gender" required ${gender == "여성" ? "checked" : ""}>여성
    </div>
    <button id="submitBtn" type = "submit">수정</button>
</form>
    `)
        let checked = document.querySelector('input[name="gender"]:checked').value;
        const genderList = document.querySelectorAll('input[name="gender"]');
        genderList.forEach((node) => {
            node.addEventListener('change', (e) => {
                e.preventDefault();
                if (e.target.checked) {
                    e.target.checked = true;
                    checked = e.target.value;
                } else {
                    e.target.checked = false;
                    checked = "";
                }
                if (node.checked) checked = node.value;
            })
        })

        const handleSubmit = (e) => {
            e.preventDefault();
            const email = document.getElementById("email").value;
            const phone = document.getElementById("phone").value;
            const age = document.getElementById("age").value;
            const formData = {
                email,
                phone,
                age,
                gender: checked
            };
            User.updateUser('/profile', formData);
        }
        const submitForm = document.getElementById("submitForm");
        submitForm.addEventListener('submit', handleSubmit);
    }

    updateBtn.addEventListener("click", showUpdateForm);


});