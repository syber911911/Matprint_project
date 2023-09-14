window.onload = function() {
    checkLogin();
};

// 현재 페이지의 URL 가져오기
const currentUrl = window.location.href;
// URL에서 컨텍스트 경로를 제외한 나머지 경로 가져오기
const pathWithoutContext = currentUrl.replace("http://localhost:8080", "");
// const pathWithoutContext = currentUrl.replace("https://matprint.site", "");
// 경로에서 게시글 ID를 추출
const postId = currentUrl.split('/').pop();
console.log('게시글 ID:', postId);

async function reissueToken() {
    const autoLogin = localStorage.getItem('autoLogin');
    if (!localStorage.getItem('token') && !sessionStorage.getItem('token')) {
        console.log("user: anonymous");
        return null;
    } else {
        let response = await fetch(`/api/reissue?autoLogin=${autoLogin}`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            credentials: 'include'
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        let data = await response.json();
        if (data.error) {
            throw new Error(data.error);
        }
        if (localStorage.getItem('autoLogin') === "T") {
            localStorage.removeItem('token');
            localStorage.setItem('token', data.accessToken);
        } else if (localStorage.getItem('autoLogin') === "F") {
            sessionStorage.removeItem('token');
            sessionStorage.setItem('token', data.accessToken);
        }
        return data.accessToken;
    }
}

function fetchPostDetail() {
    return new Promise(async (resolve, reject) => {
        if (!postId) {
            reject('게시글 ID가 유효하지 않습니다.');
            return;
        }
        try {
            const newToken = await reissueToken();
            console.log("New Token:", newToken);
            const token = localStorage.getItem('token') || sessionStorage.getItem('token');
            let headers = {
                'Content-Type': 'application/json'
            };
            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
            }
            let response = await fetch(`/api/mate/${postId}`, { headers: headers });
            if (!response.ok) {
                throw new Error('서버에서 게시글을 가져오는 중 오류 발생');
            }
            const data = await response.json();
            if (data.error) {
                throw new Error(data.error);
            }
            displayPostDetail(data.postDto);
            const { accessUser } = data;
            console.log("accessUser1: ",accessUser);
            const postUsernameElement = document.getElementById('post-username');
            const postUsername = postUsernameElement.textContent.trim();
            resolve({accessUser, postUsername});
        }catch(error) {
            console.error('게시글을 불러오는 중 오류 발생:', error.message);
            reject(error.message);
        }
    });
}


// 게시글 상세 정보를 화면에 표시
function displayPostDetail(post) {
    const titleElement = document.querySelector('#post-title');
    const contentElement = document.querySelector('#post-content');
    const usernameElement = document.querySelector('#post-username');
    const ageElement = document.querySelector('#post-age');
    const genderElement = document.querySelector('#post-gender');
    const visitDateElement = document.querySelector('#post-visitDate');
    const statusElement = document.querySelector('#post-status');

    // 정보를 화면에 표시
    titleElement.textContent = post ? post.title || '' : '';
    contentElement.textContent = post ? post.content || '' : '';
    usernameElement.textContent = post ? post.username || '' : '';
    ageElement.textContent = post ? post.age || '' : '';
    genderElement.textContent = post ? post.gender || '' : '';
    // visitDateElement.textContent = post ? post.visitDate || '' : '';

    // 방문 날짜 포맷 변경
    if (post && post.visitDate) {
        const dateParts = new Date(post.visitDate).toLocaleDateString('ko-KR').split('.');
        visitDateElement.textContent =
            `${dateParts[0]}.${dateParts[1]}.${dateParts[2]}`;
    } else {
        visitDateElement.textContent = '';
    }
    statusElement.textContent = post ? post.status || '' : '';

    if (post && post.username) {
        usernameElement.innerHTML =
            `<div style="display: flex; align-items: center;">
            <img src="${post.imgUrl}" style="border-radius: 50%; object-fit: cover; width:40px; height:40px; box-shadow: 0px 0px 10px rgba(0, 0, 0, .5); width:40px;">
            <b style="margin-left:10px;">${post.username}</b>
         </div>`;
    } else {
        usernameElement.textContent = '';
    }
}