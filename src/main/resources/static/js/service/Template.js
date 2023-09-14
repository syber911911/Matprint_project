export const profileTemplate = async (HttpHandler, paint) => {
    const user = await HttpHandler.request('/api/profile');
    const { username, gender, email, age, phone, imgUrl } = user;
    paint(`
    <div class="flex changecontentBox__user">
                    <div class="flex changecontentBox__user__image items-center justify-center">
                        <img src=${imgUrl ? imgUrl : "https://picsum.photos/300/300"} class="changecontentBox__user__image-profile"></img>
                    </div>
                    <div class="flex-col changecontentBox__user-info">
                        <ul class="" style="list-style: none;" >
                            <li style="margin-bottom: 0.5rem;"><h3>사용자명:</h3><span>${username}</span></li>
                            <li style="margin-bottom: 0.5rem;"><h3>성별:</h3><span>${gender}</span></li>
                            <li style="margin-bottom: 0.5rem;"><h3>나이:</h3><span>${age}</span></li>
                            <li style="margin-bottom: 0.5rem;"><h3>이메일:</h3><span>${email}</span></li>
                            <li style="margin-bottom: 0.5rem;"><h3>휴대폰번호:</h3><span>${phone}</span></li>
                        </ul>
                    </div>
                </div>
    `)
}
export const likePostTemplate = async (HttpHandler, paint) => {
    const posts = await HttpHandler.request('/api/profile/wishlist');
    posts.map(post => {
        const { restaurantName, address, roadAddress } = post;
        paint(`

        <ul class="" style="list-style: none;">
            <li><h3>가게명:</h3><span>${restaurantName}</span></li>
            <li><h3>주소:</h3><span>${address}</span></li>
            <li><h3>도로주소:</h3><span>${roadAddress}</span></li>
            <li> </li>
            <li> </li>
            <li> </li>
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
    const posts = await HttpHandler.request('/api/profile/post');
    posts.map(post => {
        const { title, status, visitDate } = post;
        paint(`
                <table>
                    <tr>
                        <th>제목:</th>
                        <th>모집상태:</th>
                        <th>방문날짜:</th>
                    </tr>
                    <tr>
                        <td>${title}</td>
                        <td>${status}</td>
                        <td>${visitDate}</td>
                    </tr>
                </table>
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
        User.uploadProfile('/api/profile/image', formData);
    }

    uploadForm.addEventListener('submit', handleSubmit);
}

export const updateFormTemplate = async (HttpHandler, User, paint) => {
    const profile = await HttpHandler.request('/api/profile');
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
        User.updateUser('/api/profile', formData);
    }
    submitForm.addEventListener('submit', handleSubmit);
}