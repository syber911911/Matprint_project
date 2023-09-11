console.log('게시글 ID:', postId);
// 댓글 목록을 가져오는 함수
function fetchComments() {
    fetch(`/mate/${postId}/comment`)
        .then(response => {
            if (!response.ok) {
                throw new Error('서버에서 댓글을 가져오는 중 오류 발생');
            }
            return response.json();
        })
        .then(data => {
            if (data.error) {
                throw new Error(data.error);
            }
            // 댓글 목록을 화면에 표시
            console.log('댓글 데이터:', data);
            displayComments(data.comment.content);
        })
        .catch(error => {
            console.error('댓글을 불러오는 중 오류 발생:', error.message);
        });
}

// 댓글을 화면에 표시
function displayComments(comments) {
    const commentsList = document.getElementById('comments-list');
    commentsList.innerHTML = ''; // 기존 댓글 내용 초기화

        comments.forEach(comment => {
            const commentElement = document.createElement('div');
            commentElement.classList.add('comment');
            commentElement.innerHTML = `
                <p>${comment.username}:</p>
                <p>${comment.content}</p>
            `;
            commentsList.appendChild(commentElement);
        });
}


// 댓글 작성 폼 제출 시
const commentForm = document.getElementById('comment-form');
commentForm.addEventListener('submit', function(event) {
    event.preventDefault();

    const token = localStorage.getItem('token');
    const commentTextarea = document.getElementById('comment');
    const commentText = commentTextarea.value;

    // 서버로 댓글을 저장하는 요청 보내기
    fetch(`/mate/${postId}/comment`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ content: commentText }),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('댓글을 저장하는 중 오류 발생');
            }
            return response.json();
        })
        .then(data => {
            if (data.error) {
                throw new Error(data.error);
            }
            // 댓글을 성공적으로 저장한 후, 다시 댓글 목록을 가져와서 업데이트
            fetchComments();
            // 댓글 입력 창 비우기
            commentTextarea.value = '';
        })
        .catch(error => {
            console.error('댓글을 저장하는 중 오류 발생:', error.message);
        });
});

// 페이지 로드 시 게시글 상세 정보 가져오기
window.onload = function() {
    fetchPostDetail();
    fetchComments();
};