function isAuthor(postUsername) {
    const currentToken = localStorage.getItem('token') || sessionStorage.getItem('token');
    const payload = decodeJwtToken(currentToken); // JWT 토큰 디코딩하여 payload 추출
    const currentUsername = payload.sub; // 토큰에서 username 추출

    return postUsername === currentUsername; // 글쓴이와 현재 로그인된 사용자가 일치하면 true 반환
}

const token = localStorage.getItem('token');

// 수정 버튼 클릭 시 동작
const editButton = document.getElementById('edit-button');
const postEditForm = document.getElementById('post-edit-form');
const editForm = document.getElementById('edit-form');

const editTitleInput = document.getElementById('edit-title');
const editContentInput = document.getElementById('edit-content');
const editStatusSelect = document.getElementById('edit-status');
const editVisitDateInput = document.getElementById('edit-visit-date');

editButton.addEventListener('click', () => {
    fetchPostDetail()
        .then(() => {
            // 현재 화면에 표시된 게시글 정보를 입력 필드에 채우기
            const postTitleElement = document.getElementById('post-title');
            const postContentElement = document.getElementById('post-content');
            const postStatusElement = document.getElementById('post-status');
            const postVisitDateElement = document.getElementById('post-visit-date');

            editTitleInput.value = postTitleElement.textContent;
            editContentInput.value = postContentElement.textContent;
            editStatusSelect.value = postStatusElement.textContent;

            // editVisitDateInput.value = postVisitDateElement.textContent;

            // 수정 폼 표시
            postEditForm.style.display = 'block';

            // 뒤로 가기 버튼 표시
            const backButton = document.getElementById('back-button');
            backButton.style.display = 'block';
        })
        .catch(error => console.error('Error:', error));
});


// '수정하기' 버튼 클릭 시 동작 설정
editForm.addEventListener('submit', (event) => {
    event.preventDefault(); // 폼 제출 이벤트의 기본 동작(페이지 새로고침) 방지

    const updatedTitle = editTitleInput.value;
    const updatedContent = editContentInput.value;
    const updatedStatus = editStatusSelect.value;
    const updatedVisitDate = editVisitDateInput.value;

    // 서버로 수정된 데이터 전송
    fetch(`/mate/${postId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
            title: updatedTitle,
            content: updatedContent,
            status: updatedStatus,
            visitDate: updatedVisitDate
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log(data);
            location.reload();
        })
        .catch((error) => {
            console.error('Error:', error);
        });
});

// 삭제 버튼 클릭 시 동작
const deleteButton = document.getElementById('delete-button');
deleteButton.addEventListener('click', () => {
    const confirmation = confirm("게시글을 삭제하시겠습니까?");
    if (confirmation) {
        fetch(`/mate/${postId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                console.log(data.message);
                window.location.href = "/matprint/mate";
            })
            .catch((error) => {
                console.error('Error:', error);
            });
    }
});

// 뒤로 가기 버튼 클릭 시 이벤트 처리
const backButton = document.getElementById('back-button');
backButton.addEventListener('click', function() {
    // window.location.href = `/matprint/mate/${postId}`
    postEditForm.style.display = 'none';  // 수정 폼 숨기기
    backButton.style.display = 'none';  // 뒤로 가기 버튼 숨기기
});

window.onload = function() {
    checkLogin();
    fetchComments();
    fetchPostDetail()
        .then(postUsername => {
            console.log("Post author: ", postUsername); // 게시글 작성자 출력
            const currentToken = localStorage.getItem('token'); // 로컬 스토리지에서 토큰을 가져옴
            const payload = decodeJwtToken(currentToken); // JWT 토큰 디코딩하여 payload 추출
            const currentUsername = payload.sub; // 토큰에서 username 추출
            console.log("Logged in user: ", currentUsername); // 현재 로그인된 사용자 출력

            if (isAuthor(postUsername)) {
                const postButtons = document.getElementById('post-buttons');
                postButtons.style.display = 'block';
            }
        })
        .catch(error => console.log(error));
};


