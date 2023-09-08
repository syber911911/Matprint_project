document.addEventListener("DOMContentLoaded", async () => {
    localStorage.setItem('token', "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNCIsImlhdCI6MTY5NDE0MjY4OCwiZXhwIjoxNjk0MjI5MDg4fQ.4z4XahbVrpcMUe9CPakQxmslkylnSbPMZ9IeRD1S3h8Aj1jAPgiNcxK3Y-axIkl3_7TlEVxBvefrUaA0PYN-BA");
    const httpModule = await import("./HttpHandler.js");
    const userModule = await import("./User.js");

    const myInfoBtn = document.getElementById("myInfo");
    const likePostBtn = document.getElementById("likePost");
    const myPostBtn = document.getElementById("myPost");
    const updateBtn = document.getElementById("update");
    const goBackBtn = document.getElementById("goback");
    const logoutBtn = document.getElementById("logout");
    const changeContentBox = document.getElementById('changecontentBox');
    const token = localStorage.getItem('token');
    const HttpHandler = new httpModule.HttpHandler(token);
    const User = new userModule.User(token);

    const clearContentBox = () => changeContentBox.innerHTML = ``;
    const handleGoback = (e) => {
        e.preventDefault();
        window.history.go(-1);
    }
    goBackBtn.addEventListener('click', handleGoback);
    const showProfile = async () => {
        const user = await HttpHandler.request('/profile');
        let template;
        clearContentBox();
        const { username, gender, email } = user;
        template = `
            <ul>
            <li class="flex">
                <div>
                    <h3>사용자명:</h3>
                    <p>${username}</p>
                </div>
                <div>
                    <h3>성별:</h3>
                    <p>${gender}</p>
                </div>
                <div>
                    <h3>이메일:</h3>
                    <p>${email}</p>
                </div>
            </li>
        </ul>
            `
        chageContent(template);
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

    const handleLogout = (e) => {
        e.preventDefault();
        User.logout('/logout');
    }
    logoutBtn.addEventListener('click', handleLogout);

    myInfoBtn.addEventListener("click", showInfo);
    const showLikedPosts = async (e) => {
        e.preventDefault();
        const likedPosts = await HttpHandler.request('/profile/wishlist');
        let template;
        clearContentBox();
        likedPosts.map(post => {
            const { restaurantName, address, roadAddress } = post;
            template = `
            <ul>
            <li class="flex">
                <div>
                    <h3>가게명:</h3>
                    <p>${restaurantName}</p>
                </div>
                <div>
                    <h3>주소:</h3>
                    <p>${address}</p>
                </div>
                <div>
                    <h3>도로주소:</h3>
                    <p>${roadAddress}</p>
                </div>
            </li>
        </ul>
        `
            chageContent(template);
        })

    }
    likePostBtn.addEventListener("click", showLikedPosts);


    const showMyPosts = async (e) => {
        e.preventDefault();
        const posts = await HttpHandler.request('/profile/post');
        console.log(posts)//!
        let template;
        clearContentBox();
        posts.map(post => {
            const { title, status, visitDate } = post;
            template = `
            <ul>
                    <li class="flex">
                        <div>
                            <h3>제목:</h3>
                            <p>${title}</p>
                        </div>
                        <div>
                            <h3>모집상태:</h3>
                            <p>${status}</p>
                        </div>
                        <div>
                            <h3>방문날짜:</h3>
                            <p>${visitDate}</p>
                        </div>
                    </li>
                </ul>
        `
            chageContent(template);
        })

    }
    myPostBtn.addEventListener("click", showMyPosts);

    const showUpdateForm = async (e) => {
        e.preventDefault();
        const profile = await HttpHandler.request('/profile');
        console.log(profile)//!
        let template;
        clearContentBox();
        const { phone,age, gender, email } = profile;
        template = `
                 <form action="/profile">
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
                            <input value="${age}" type = "text" id ="age" name = "age" required />
                        </div>
                        <div class ="form-group">
                            <label for = "male">gender:</label>
                            <input type = "radio" id = "male" name = "gender" required checked=${gender =="남성" ? "checked" : ""}>남성
                            <input type = "radio" id = "female" name = "gender" checked=${gender =="여성" ? "checked" : ""}>여성
                        </div>
                        <button id="submitBtn" type = "submit">회원가입</button>
                    </form>
        `
        chageContent(template);
        console.log(document.getElementById("submitBtn"));
    }

    updateBtn.addEventListener("click",showUpdateForm);
    const chageContent = (template) => {
        changeContentBox.insertAdjacentHTML('afterbegin', template);
    };

});