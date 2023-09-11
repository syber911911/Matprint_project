// 현재 페이지의 URL 가져오기
const currentUrl = window.location.href;
// URL에서 컨텍스트 경로를 제외한 나머지 경로 가져오기
const pathWithoutContext = currentUrl.replace("http://localhost:8080/matprint", "");
// 경로에서 게시글 ID를 추출
const postId = pathWithoutContext.split('/')[2];
console.log('게시글 ID:', postId);
function fetchPostDetail() {
    if (!postId) {
        console.error('게시글 ID가 유효하지 않습니다.');
        return;
    }
    // 서버로 GET 요청을 보내서 게시글 상세 정보를 가져옴
    fetch(`/mate/${postId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('서버에서 게시글을 가져오는 중 오류 발생');
            }
            return response.json();
        })
        .then(data => {
            if (data.error) {
                throw new Error(data.error); // 서버에서 반환한 오류 메시지를 사용
            }
            // 게시글 정보를 동적으로 표시
            displayPostDetail(data.postDto);
        })
        .catch(error => {
            console.error('게시글을 불러오는 중 오류 발생:', error.message);
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
    visitDateElement.textContent = post ? post.visitDate || '' : '';
    statusElement.textContent = post ? post.status || '' : '';
}

// 페이지 로드 시 게시글 상세 정보 가져오기
window.onload = function() {
    fetchPostDetail();
    fetchComments();
};

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
            displayComments(data.comments);
        })
        .catch(error => {
            console.error('댓글을 불러오는 중 오류 발생:', error.message);
        });
}

// 댓글을 화면에 표시하는 함수
function displayComments(comments) {
    const commentsList = document.getElementById('comments-list');
    commentsList.innerHTML = ''; // 기존 댓글 내용 초기화

    if (comments && comments.content.length > 0) {
        comments.content.forEach(comment => {
            const commentElement = document.createElement('div');
            commentElement.classList.add('comment');
            commentElement.innerHTML = `
                <p>${comment.username}:</p>
                <p>${comment.text}</p>
            `;
            commentsList.appendChild(commentElement);
        });
    } else {
        commentsList.innerHTML = '<p>댓글이 없습니다.</p>';
    }
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
        body: JSON.stringify({ text: commentText }),
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