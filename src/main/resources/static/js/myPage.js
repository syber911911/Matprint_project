document.addEventListener("DOMContentLoaded", function () {
    localStorage.setItem('token', "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzNCIsImlhdCI6MTY5Mzk5NzE1NSwiZXhwIjoxNjk0MDgzNTU1fQ.FQ7bvFRh_PIHhQHn4ve4usRDlA0vb429fijgwTnueN1dULPCd0B0P5hjVSZa3o0_8Q1v4ADBNmbUE02OfKX9Sw");

    //new
    const buttonElem1 = document.getElementById("button1");//메뉴버튼1
    const buttonElem2 = document.getElementById("button2");//메뉴버튼2
    const buttonElem3 = document.getElementById("button3");//메뉴버튼3
    const changeContentBox = document.getElementById('changecontentBox');//메뉴버튼누를시 바뀌는 컨테이너
    const token = localStorage.getItem('token');

    const clearContentBox = () => changeContentBox.innerHTML = ``;

    const handleButtonClick = (e) => {
        e.preventDefault();
        showProfile(token)
    }
    buttonElem1.addEventListener("click", handleButtonClick);
    const handleButtonClick2 = async (e) => {
        e.preventDefault();
        const likedPosts = await requestLikedPosts(token);
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
            showLikedPost(template);
        })

    }
    buttonElem2.addEventListener("click", handleButtonClick2);


    const handleButtonClick3 = async (e) => {
        e.preventDefault();
        const posts = await requestPosts(token);
        console.log(posts)
        let template;
        clearContentBox();
        posts.map(post => {
            const { title, status, visitDate, id } = post;
            template = `
            <ul>
                    <li key=${id} class="flex">
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
            showPost(template);
        })

    }
    buttonElem3.addEventListener("click", handleButtonClick3);

    //new
    const showPost = (template) => {
        changeContentBox.insertAdjacentHTML('afterbegin', template);
    };
    const showMyPage = (template) => {
        changeContentBox.insertAdjacentHTML('afterbegin', template);
    };
    const showLikedPost = (template) => {
        changeContentBox.insertAdjacentHTML('afterbegin', template);
    };


    // 페이지 로드 시 토큰 확인 및 프로필 정보 요청
    const showProfile = async (token) => {
        const user = await requestProfile(token);
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
        showMyPage(template);
    }
    if (token) {
        showProfile(token)
    } else {
        console.error('토큰이 로컬 스토리지에 없습니다.');
    }
});

async function requestProfile(token) {
    try {
        const headers = {
            'Content-Type': 'application/json'
        };
        if(token){
            headers['Authorization'] = `Bearer ${token}`;
        }
        const response = await fetch('/profile', {
            method: 'GET',
            headers: headers,
        });

        if (response.status === 200) {
            const data = await response.json();
            console.log(data);
            return data;
        } else {
            console.error("프로필 정보를 가져오는데 실패했습니다.");
        }
    } catch (error) {
        console.error("오류 발생:", error);
    }
}
async function requestPosts(token) {
    try {
        const headers = {
            'Content-Type': 'application/json'
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch('/profile/post', {
            method: 'GET',
            headers: headers,
        });

        if (response.status === 200) {
            const data = await response.json();
            console.log(data);
            return data;
        } else {
            console.error("프로필 정보를 가져오는데 실패했습니다2.");
        }
    } catch (error) {
        console.error("오류 발생:", error);
    }
}
async function requestLikedPosts(token) {
    try {
        const headers = {
            'Content-Type': 'application/json'
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch('/profile/wishlist', {
            method: 'GET',
            headers: headers,
        });

        if (response.status === 200) {
            const data = await response.json();
            console.log(data);
            return data;
        } else {
            console.error("프로필 정보를 가져오는데 실패했습니다3.");
        }
    } catch (error) {
        console.error("오류 발생:", error);
    }
}