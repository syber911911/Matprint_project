let token = localStorage.getItem('token') || sessionStorage.getItem('token');
let header = {};
if (token) {
    header['Authorization'] = `Bearer ${token}`;
}

document.getElementById('logout-button').addEventListener('click', function() {
    logout();
});

function logout() {
    const token = localStorage.getItem('token') || sessionStorage.getItem('token');

    return fetch(`/api/logout`, {
        method: 'POST',
        headers: header,
    })
        .then(response => {
            if (response.ok) {
                localStorage.removeItem('token');
                localStorage.removeItem('autoLogin');
                sessionStorage.removeItem('token');
                sessionStorage.removeItem('autoLogin');
                // 로그아웃 성공 시 메인 페이지로 이동하고 뒤로가기 막기
                window.location.replace("/");
                // 브라우저의 페이지 이동 기록을 제거
                window.history.pushState({}, '', '/matprint/main');
            } else {
                console.error('로그아웃 실패:', response.statusText);
                // 오류 발생 시에도 토큰 삭제 후 리다이렉트
                localStorage.removeItem('token');
                localStorage.removeItem('autoLogin');
                sessionStorage.removeItem('token');
                sessionStorage.removeItem('autoLogin');
                window.location.href = "/";
            }
        })
        .catch(error => console.error('로그아웃 중 오류 발생:', error));
}

function checkLogin() {
    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    const loginContainer = document.querySelector('.--login-container');
    const notLoginContainer = document.querySelector('.--not-login-container');

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
}

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