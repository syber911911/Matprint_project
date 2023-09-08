document.addEventListener("DOMContentLoaded", async () => {
    localStorage.setItem('token', "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNCIsImlhdCI6MTY5NDA4MTkzNywiZXhwIjoxNjk0MTY4MzM3fQ.9Md4Ba0EBlq7qP4tLfsKEFhkzxMJHQAwkLpdUQDG8GND6uIT7iban14JBgynzo3_iXZZ5l_gicfYgXB5TxVemQ");
    const module = await import("./httpHandler.js");
    const myInfoBtn = document.getElementById("button1");
    const likePostBtn = document.getElementById("button2");
    const myPostBtn = document.getElementById("button3");
    const changeContentBox = document.getElementById('changecontentBox');
    const token = localStorage.getItem('token');
    const httpHandler = new module.HttpHandler(token);

    const clearContentBox = () => changeContentBox.innerHTML = ``;
    // 페이지 로드 시 토큰 확인 및 프로필 정보 요청
    const showProfile = async () => {
        const user = await httpHandler.request('/profile');
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
    myInfoBtn.addEventListener("click", showInfo);
    const showLikedPosts = async (e) => {
        e.preventDefault();
        const likedPosts = await httpHandler.request('/profile/wishlist');
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
        const posts = await httpHandler.request('/profile/post');
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

    const chageContent = (template) => {
        changeContentBox.insertAdjacentHTML('afterbegin', template);
    };

});