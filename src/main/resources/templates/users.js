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

    document.getElementById('logout-button').addEventListener('click', function(event) {
        event.preventDefault(); // 기본 클릭 동작 막기
        logout(); // logout 함수 호출
    });
}
function logout() {
    // 로컬 스토리지에서 토큰을 가져옴
    const token = localStorage.getItem('token');

    // 토큰이 존재하는 경우
    if (token) {
        // HTTP 요청 헤더에 토큰을 포함하여 로그아웃 요청을 서버로 보냄
        fetch('/users/logout', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}` // Authorization 헤더에 토큰을 포함
            }
        })
            .then(response => {
                if (response.status === 200) {
                    // 로그아웃이 성공한 경우
                    // 로컬 스토리지에서 토큰을 삭제
                    localStorage.removeItem('token');
                    // 페이지 리다이렉트
                    window.location.href = '/main'; // 메인 페이지로 이동
                } else {
                    // 로그아웃이 실패한 경우
                }
            })
            .catch(error => {
                console.error('로그아웃 중 오류 발생:', error);
            });
    } else {
        // 토큰이 로컬 스토리지에 없는 경우
        console.error('토큰이 로컬 스토리지에 없습니다.');
        // 추가적인 오류 처리 또는 로그인 상태 확인 로직 수행
    }
}