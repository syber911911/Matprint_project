// 게시글 생성 버튼 클릭 이벤트 리스너 추가
const createPostButton = document.getElementById('create-post-button');
createPostButton.addEventListener('click', function() {
    createPost();
});

// 게시글 생성 함수
function createPost() {
    const token = localStorage.getItem('token');
    fetch('/mate', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
            title: document.getElementById('post-title').value,
            content: document.getElementById('post-content').value,
            visitDate: document.getElementById('post-visit-date').value,
            prefer: document.getElementById('post-prefer').value
        })
    })
        .then(response => response.json())
        .then(data => {
            // 게시글 생성 성공 여부 확인
            if (data.success) {
                window.location.href = '/matprint/mate';
            } else {
                alert('게시글 생성에 실패했습니다.'); // 생성 실패 시 알림 표시
            }
        })
        .catch(error => {
            console.error('게시글 생성 중 오류 발생:', error);
        });
}