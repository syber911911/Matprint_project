document.getElementById('logout-button').addEventListener('click', function() {
    logout();
});

function logout() {
    const token = localStorage.getItem('token');

    return fetch(`/logout`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`
        },
    })
        .then(response => {
            if (response.ok) {
                localStorage.removeItem('token');
                window.location.href = "/main"; // 메인 페이지로 이동
            } else {
                console.error('로그아웃 실패:', response.statusText);
                // 오류 발생 시에도 토큰 삭제 후 리다이렉트
                localStorage.removeItem('token');
                window.location.href = '/main';
            }
        })
        .catch(error => console.error('로그아웃 중 오류 발생:', error));
}

window.onload = function() {
    const token = localStorage.getItem('token');
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
