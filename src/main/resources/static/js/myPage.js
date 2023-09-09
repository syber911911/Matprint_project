document.addEventListener("DOMContentLoaded", async () => {
    localStorage.setItem('token', "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNCIsImlhdCI6MTY5NDIxOTQ1MCwiZXhwIjoxNjk0MzA1ODUwfQ.PxPe5AAKex8w7DD1e3LRlhAk4KxLP2T_kL4mZzzmYutubJykyfNf7J9qsc1b1qidl8WAkECt8bej7gedmWEbNA");
    const httpModule = await import("./HttpHandler.js");
    const userModule = await import("./User.js");

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

    const clearContentBox = () => changeContentBox.innerHTML = ``;
    const handleLeave = (e)=>{
        e.preventDefault();
        User.deleteUser('/profile');
    }
    leaveBtn.addEventListener('click',handleLeave);
    const handleGoback = (e) => {
        e.preventDefault();
        window.location.replace('/main');
    }
    goBackBtn.addEventListener('click', handleGoback);
    const showProfile = async () => {
        const user = await HttpHandler.request('/profile');
        let template;
        clearContentBox();
        const { username, gender, age, email, phone } = user;
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
                    <h3>나이:</h3>
                    <p>${age}</p>
                </div>
                <div>
                    <h3>이메일:</h3>
                    <p>${email}</p>
                </div>
                <div>
                    <h3>휴대폰 번호:</h3>
                    <p>${phone}</p>
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
        const { phone, age, gender, email } = profile;
        template = `
                 <form id="submitForm" action="/profile">
                        <div class ="form-group">
                            <label for = "email">email:</label>
                            <input value=${email} type = "email" id ="email" name = "email" required />
                        </div>
                        <div class ="form-group">
                            <label for = "phone">phone:</label>
                            <input value=${phone} type = "tel" id ="phone" name = "phone" pattern="[0-9]{3}-[0-9]{4}-[0-9]{4}" required />
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
        `
        chageContent(template);
        let checked = document.querySelector('input[name="gender"]:checked').value;
        const genderList = document.querySelectorAll('input[name="gender"]');
        console.log(genderList);
        genderList.forEach((node) => {
            node.addEventListener('change', (e) => {
                console.dir(e.target.checked);
                console.dir(e.target.value);
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
            console.log(formData);
            User.updateUser('/profile', formData);
        }
        const submitForm = document.getElementById("submitForm");
        submitForm.addEventListener('submit', handleSubmit);
    }

    updateBtn.addEventListener("click", showUpdateForm);
    const chageContent = (template) => {
        changeContentBox.insertAdjacentHTML('afterbegin', template);
    };

});