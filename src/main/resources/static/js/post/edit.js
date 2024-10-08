function isAuthor(accessUser, postUsername) {
    return accessUser == postUsername;
}

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

    updatePost(postId, updatedTitle, updatedContent, updatedStatus, updatedVisitDate)
        .then(() => {
            console.log("Post has been successfully updated.");
        })
        .catch((error) => {
            console.error('Error:', error);
        });
});

function updatePost(postId, title, content, status, visitDate) {
    return new Promise(async (resolve, reject) => {
        try {
            const newToken = await reissueToken();
            console.log("New Token:", newToken);
            const token = localStorage.getItem('token') || sessionStorage.getItem('token');
            let headers = {
                'Content-Type': 'application/json'
            };
            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
            }
            let response = await fetch(`/api/mate/${postId}`, {
                method: 'PUT',
                headers: headers,
                body: JSON.stringify({
                    title,
                    content,
                    status,
                    visitDate
                })
            });
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            let data = await response.json();
            console.log(data);
            // 페이지 새로 고침
            location.reload();
            resolve();
        } catch(error) {
            reject(error);
        }
    });
}


// 삭제 버튼 클릭 시 동작
const deleteButton = document.getElementById('delete-button');
deleteButton.addEventListener('click', () => {
    const confirmation = confirm("게시글을 삭제하시겠습니까?");
    if (confirmation) {
        deletePost(postId)
            .then(() => {
                console.log("Post has been successfully deleted.");
            })
            .catch((error) => {
                console.error('Error:', error);
            });
    }
});

function deletePost(postId) {
    return new Promise(async (resolve, reject) => {
        try {
            const newToken = await reissueToken();
            console.log("New Token:", newToken);
            const token = localStorage.getItem('token') || sessionStorage.getItem('token');
            let headers = {
                'Content-Type': 'application/json'
            };
            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
            }
            let response = await fetch(`/api/mate/${postId}`, {
                method: 'DELETE',
                headers: headers,
            });
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            let data = await response.json();
            console.log(data.message);
            // 페이지 이동
            window.location.href = "/mate";
            resolve();
        } catch(error) {
            reject(error);
        }
    });
}


// 뒤로 가기 버튼 클릭 시 이벤트 처리
const backButton = document.getElementById('back-button');
backButton.addEventListener('click', function() {
    postEditForm.style.display = 'none';  // 수정 폼 숨기기
    backButton.style.display = 'none';  // 뒤로 가기 버튼 숨기기
});

window.onload = function() {
    checkLogin();
    fetchPostDetail()
        .then(data => {
            const { postUsername, accessUser } = data;
            console.log("Post author!: ", postUsername);
            console.log("accessUser!: ", postUsername);
            if (isAuthor(accessUser, postUsername)) {
                const postButtons = document.getElementById('post-buttons');
                postButtons.style.display = 'block';
            }
        })
        .catch(error => console.log(error));
    fetchComments();
};


