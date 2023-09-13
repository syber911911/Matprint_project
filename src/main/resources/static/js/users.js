document.getElementById('logout-button').addEventListener('click', function() {
    logout();
});

function logout() {
    const token = localStorage.getItem('token') || sessionStorage.getItem('token');

    return fetch(`/logout`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`
        },
    })
        .then(response => {
            if (response.ok) {
                localStorage.removeItem('token');
                localStorage.removeItem('autoLogin');
                sessionStorage.removeItem('token');
                sessionStorage.removeItem('autoLogin');
                // 로그아웃 성공 시 메인 페이지로 이동하고 뒤로가기 막기
                window.location.replace("/matprint/main");
                // 브라우저의 페이지 이동 기록을 제거
                window.history.pushState({}, '', '/matprint/main');
            } else {
                console.error('로그아웃 실패:', response.statusText);
                // 오류 발생 시에도 토큰 삭제 후 리다이렉트
                localStorage.removeItem('token');
                localStorage.removeItem('autoLogin');
                sessionStorage.removeItem('token');
                sessionStorage.removeItem('autoLogin');
                window.location.href = '/matprint/main';
            }
        })
        .catch(error => console.error('로그아웃 중 오류 발생:', error));
}

function checkLogin() {
    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    const loginContainer = document.querySelector('.--login-container');
    const notLoginContainer = document.querySelector('.--not-login-container');

    // checkAndRefreshToken();

    if (token) {
        // 로그인 상태
        loginContainer.style.display = 'block';
        notLoginContainer.style.display = 'none';
    } else {
        // 로그아웃 상태
        loginContainer.style.display = 'none';
        notLoginContainer.style.display = 'block';
    }
}

window.onload = function () {
    checkLogin();
    console.log("토큰 확인",localStorage.getItem('token'));
    console.log("토큰 확인",sessionStorage.getItem('token'));
}

// window.onbeforeunload = function() {
//     const autoLogin = localStorage.getItem('autoLogin');
//
//     if (autoLogin === 'F') {
//         localStorage.removeItem('token');
//         localStorage.removeItem('autoLogin');
//     }
// };

// // 서버에 토큰 재발급 요청
// function fetchTokenRefresh() {
//     const token = localStorage.getItem('token');
//
//     return fetch('/reissue', {
//         method: 'POST',
//         headers: {
//             'Authorization': `Bearer ${token}`
//         }
//     })
//         .then(response => {
//             if (response.ok) {
//                 return response.json();
//             } else {
//                 throw new Error('토큰 재발급 실패');
//             }
//         })
//         .then(data => data.newToken)
//         .catch(error => {
//             console.error('토큰 재발급 요청 실패:', error);
//             return null;
//         });
// }
//
// // 토큰 만료 여부 확인 및 재발급 요청
// function checkAndRefreshToken() {
//     const token = localStorage.getItem('token');
//
//     if (!token) {
//         console.error('토큰이 없습니다.');
//         return;
//     }
//
//     // 토큰의 만료 여부 확인
//     const decodedToken = decodeJwtToken(token);
//     const currentTimestamp = Math.floor(Date.now() / 1000);
//
//     if (decodedToken.exp < currentTimestamp) {
//         // 토큰이 만료되었을 경우, 서버에 재발급 요청을 보냄
//         fetchTokenRefresh()
//             .then(newToken => {
//                 // 새로운 토큰을 받았을 경우, 로컬 스토리지에 저장
//                 if (newToken) {
//                     localStorage.setItem('token', newToken);
//                 }
//             })
//             .catch(error => {
//                 console.error('토큰 재발급 실패:', error);
//                 // 실패할 경우 로그아웃 또는 다른 조치 수행
//             });
//     }
// }

// jwt 토큰 디코딩
function decodeJwtToken(token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    console.log(jsonPayload);
    return JSON.parse(jsonPayload);
}