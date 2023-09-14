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

// async function reissueToken() {
//     const token = localStorage.getItem('token')||sessionStorage.getItem('token');
//     const autoLogin = localStorage.getItem('autoLogin');
//     if (!localStorage.getItem('token') && !sessionStorage.getItem('token')) {
//         document.getElementById('wish').checked = false;
//         console.log("user: anonymous");
//     } else {
//         let response = await fetch(`/api/reissue?autoLogin=${autoLogin}`, {
//             method: 'POST', headers: {'Content-Type': 'application/json'}
//         });
//         let data = await response.json();
//
//         if (localStorage.getItem('autoLogin') === "T") {
//             localStorage.removeItem('token');
//             localStorage.setItem('token', data.accessToken);
//         } else if (localStorage.getItem('autoLogin') === "F") {
//             sessionStorage.removeItem('token');
//             sessionStorage.setItem('token', data.accessToken);
//         }
//         getUsername(data.accessToken);
//     }
// }

async function reissueToken() {
    const autoLogin = localStorage.getItem('autoLogin');
    if (!localStorage.getItem('token') && !sessionStorage.getItem('token')) {
        // document.getElementById('wish').checked = false;
        console.log("user: anonymous");
        return null;  // No token to reissue
    } else {
        let response = await fetch(`/api/reissue?autoLogin=${autoLogin}`, {
            method: 'POST', headers: {'Content-Type': 'application/json',
                credentials: 'include'}
        });
        let data = await response.json();
        console.log(data);

        if (localStorage.getItem('autoLogin') === "T") {
            localStorage.removeItem('token');
            localStorage.setItem('token', data.accessToken);
        } else if (localStorage.getItem('autoLogin') === "F") {
            sessionStorage.removeItem('token');
            sessionStorage.setItem('token', data.accessToken);
        }

        return data.accessToken;  // Return the new token
    }
}

// function fetchPostDetail() {
//     return new Promise((resolve, reject) => {
//         if (!postId) {
//             reject('게시글 ID가 유효하지 않습니다.');
//             return;
//         }
//         fetch(`/api/mate/${postId}`, {
//             headers: headers
//         })
//             .then(response => {
//                 if (!response.ok) {
//                     throw new Error('서버에서 게시글을 가져오는 중 오류 발생');
//                 }
//                 return response.json();
//             })
//             .then(data => {
//                 if (data.error) {
//                     throw new Error(data.error);
//                 }
//                 // 게시글 정보를 동적으로 표시
//                 displayPostDetail(data.postDto);
//                 const { accessUser } = data;
//                 console.log("accessUser1: ",accessUser);
//                 // 게시글 정보가 화면에 표시된 후에 글쓴이 확인
//                 const postUsernameElement = document.getElementById('post-username');
//                 const postUsername = postUsernameElement.textContent.trim();
//                 resolve({accessUser, postUsername});
//             })
//             .catch(error => {
//                 console.error('게시글을 불러오는 중 오류 발생:', error.message);
//                 reject(error.message);
//             });
//     });
// }

function fetchPostDetail() {
    return new Promise(async (resolve, reject) => {
        if (!postId) {
            reject('게시글 ID가 유효하지 않습니다.');
            return;
        }
        try {
            const newToken = await reissueToken();
            console.log("New Token:", newToken);
            let response = await fetch(`/api/mate/${postId}`, { headers: headers });
            // 401 Unauthorized 오류가 발생했을 경우 토큰 재발급
            // if (response.status === 401) {
            //     await reissueToken();
            //     // 토큰 재발급 후 다시 요청
            //     response = await fetch(`/api/mate/${postId}`, { headers: headers });
            // }
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
        } catch(error) {
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
            <img src="${post.imgUrl}" style="border-radius: 50%; box-shadow: 0px 0px 10px rgba(0, 0, 0, .5); width:40px;">
            <b style="margin-left:10px;">${post.username}</b>
         </div>`;
    } else {
        usernameElement.textContent = '';
    }
}