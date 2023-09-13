export const profileTemplate = async (HttpHandler, paint) => {
    const user = await HttpHandler.request('/profile');
    const { username, gender, email, age, phone, imgUrl } = user;
    paint(`
    <ul class="flex changecontentBox__user">
                    <li class="flex changecontentBox__user__image items-center justify-center">
                        <img src=${imgUrl ? imgUrl : "https://picsum.photos/300/300"} class="changecontentBox__user__image-profile"></img>
                    </li>
                    <li class="flex-col changecontentBox__user-info">
                        <div class="field">
                            <h3>사용자명:</h3>
                            <p>${username}</p>
                        </div>
                        <div class="field">
                            <h3>성별:</h3>
                            <p>${gender}</p>
                        </div>
                        <div class="field">
                            <h3>나이:</h3>
                            <p>${age}</p>
                        </div>
                        <div class="field">
                            <h3>이메일:</h3>
                            <p>${email}</p>
                        </div>
                        <div class="field">
                            <h3>휴대폰 번호:</h3>
                            <p>${phone}</p>
                        </div>
                    </li>
                </ul>
    `)
}
export const likePostTemplate = async (HttpHandler, paint) => {
    const posts = await HttpHandler.request('/profile/wishlist');
    posts.map(post => {
        const { restaurantName, address, roadAddress } = post;
        paint(`
        <ul>
        <li class="flex">
            <div class="field">
                <h3>가게명:</h3>
                <p>${restaurantName}</p>
            </div>
            <div class="field">
                <h3>주소:</h3>
                <p>${address}</p>
            </div>
            <div class="field">
                <h3>도로주소:</h3>
                <p>${roadAddress}</p>
            </div>
        </li>
    </ul>
    `)
    })
}


const formatTime = (utc) => {
    const date = new Date().toLocaleDateString('kr-KR', { weekday: "long", year: "numeric", month: "short", day: "numeric" }).split(' ');
    const year = date[0]; //2023년
    const month = date[1]; //9월
    const day = date[2]; //12일
    return year + ' ' + month + ' ' + day;
}

export const postTemplate = async (HttpHandler, paint) => {
    const posts = await HttpHandler.request('/profile/post');
    posts.map(post => {
        const { title, status, visitDate } = post;
        paint(`
        <ul>
                <li class="flex">
                    <div class="field">
                        <h3>제목:</h3>
                        <p>${title}</p>
                    </div>
                    <div class="field">
                        <h3>모집상태:</h3>
                        <p>${status}</p>
                    </div>
                    <div class="field">
                        <h3>방문날짜:</h3>
                        <p>${formatTime(visitDate)}</p>
                    </div>
                </li>
            </ul>
    `)
    })
}

export const uploadTemplate = async (User, paint) => {
    paint(`
    <form id="uploadForm" method="post" enctype="multipart/form-data">
                    <div>
                        <label for="file">Choose file to upload</label>
                        <input type="file" id="fileInput" name="file" accept="image/*" />
                    </div>
                    <div>
                        <button>Submit</button>
                    </div>
                </form>
    `);
    const uploadForm = document.getElementById("uploadForm");
    const fileInput = document.getElementById('fileInput');
    const handleSubmit = (e) => {
        e.preventDefault();
        var formData = new FormData();
        formData.append('multipartFile', fileInput.files[0]);
        User.uploadProfile('/profile/image', formData);
    }

    uploadForm.addEventListener('submit', handleSubmit);
}

export const updateFormTemplate = async (HttpHandler, User, paint) => {
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
    const submitForm = document.getElementById("submitForm");
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
    submitForm.addEventListener('submit', handleSubmit);
}