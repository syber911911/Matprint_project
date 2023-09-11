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
                window.location.href = "/matprint/main"; // 메인 페이지로 이동
            } else {
                console.error('로그아웃 실패:', response.statusText);
                // 오류 발생 시에도 토큰 삭제 후 리다이렉트
                localStorage.removeItem('token');
                window.location.href = '/matprint/main';
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

// 토큰 만료 여부 확인 및 재발급 요청
function checkAndRefreshToken() {
    const token = localStorage.getItem('token');

    if (!token) {
        console.error('토큰이 없습니다.');
        return;
    }

    // 토큰의 만료 여부 확인
    const decodedToken = decodeJwtToken(token);
    const currentTimestamp = Math.floor(Date.now() / 1000);

    if (decodedToken.exp < currentTimestamp) {
        // 토큰이 만료되었을 경우, 서버에 재발급 요청을 보냄
        fetchTokenRefresh()
            .then(newToken => {
                // 새로운 토큰을 받았을 경우, 로컬 스토리지에 저장
                if (newToken) {
                    localStorage.setItem('token', newToken);
                }
            })
            .catch(error => {
                console.error('토큰 재발급 실패:', error);
                // 실패할 경우 로그아웃 또는 다른 조치 수행
            });
    }
}

// JWT 토큰 디코딩
function decodeJwtToken(token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64));
    return JSON.parse(jsonPayload);
}

// 서버에 토큰 재발급 요청
function fetchTokenRefresh() {
    const token = localStorage.getItem('token');

    return fetch('/reissue', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('토큰 재발급 실패');
            }
        })
        .then(data => data.newToken)
        .catch(error => {
            console.error('토큰 재발급 요청 실패:', error);
            return null;
        });
}

// 페이지 로드 시 토큰 체크 및 재발급 시도
window.onload = function() {
    checkAndRefreshToken();
    // 이후에 페이지 초기화 및 기타 작업 수행
};