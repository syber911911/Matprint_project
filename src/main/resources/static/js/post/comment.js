console.log('게시글 ID:', postId);

let headers = {
    'Content-Type': 'application/json'
};
if (token) {
    headers['Authorization'] = `Bearer ${token}`;
}

function isAuthor(accessUser, commentUsername) {
    return accessUser == commentUsername;
}

// 댓글 목록을 가져오는 함수
function fetchComments() {
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
            let response = await fetch(`/api/mate/${postId}/comment`, {headers: headers});
            if (!response.ok) {
                throw new Error('서버에서 댓글을 가져오는 중 오류 발생');
            }
            const data = await response.json();
            if (data.error) {
                throw new Error(data.error);
            }
            const { accessUser } = data;
            displayComments(data.comment.content, accessUser);
            const commentUsernameElement = document.getElementById('comment-username');
            const commentUsername = commentUsernameElement.textContent.trim();
            resolve({accessUser, commentUsername});
        } catch(error) {
            reject(error.message);
        }
    });
}



// 댓글을 화면에 표시
function displayComments(comments, accessUser) {
    const commentsList = document.getElementById('comments-list');
    commentsList.innerHTML = ''; // 기존 댓글 내용 초기화

        comments.forEach(comment => {
            const commentUsername = comment.username;
            const commentElement = document.createElement('div');
            commentElement.classList.add('comment');

            commentElement.id = 'comment-' + comment.id;
            console.log("accessUser3: ",accessUser);
            // 수정/삭제 버튼 생성
            let buttonsHTML = '';
            if (isAuthor(accessUser, commentUsername)) {
                buttonsHTML = `
               <button class="comment-button" onclick="editComment(${comment.id})">  수정</button>
               <button class="comment-button" onclick="deleteComment(${comment.id})">  삭제</button>
           `;
            }
            commentElement.innerHTML = `
                <div style="display: flex; align-items: center; overflow: visible;">
                <img src="${comment.imgUrl}" style="border-radius: 50%; object-fit: cover; width:40px; height:40px; box-shadow: 0px 0px 10px rgba(0, 0, 0, .4); position:relative; top:5px; right:-7px;">
                <b style="margin-left:10px width:30px; object-fit: cover; position:relative; top:5px; right:-12px;;">${comment.username}</b></div>
                <p class="content" style="background-color: white; padding: 10px; margin: 10px 0;">${comment.content}</p>
                <p style="text-align: right;">${buttonsHTML}</p>
             `;
            commentsList.appendChild(commentElement);
        });
}

// 댓글 작성 폼 제출 시
const commentForm = document.getElementById('comment-form');
commentForm.addEventListener('submit', function(event) {
    event.preventDefault();

    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    const commentTextarea = document.getElementById('comment');
    const commentText = commentTextarea.value;

    if (!token) {
        alert("로그인을 해주세요.");
        return;
    }

    if (!commentText) {
        alert('댓글 내용을 입력해주세요.');
        return;
    }

    function postComment() {
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
                let response = await fetch(`/api/mate/${postId}/comment`, {
                    method: 'POST',
                    headers: headers,
                    body: JSON.stringify({ content: commentText }),
                });
                if (!response.ok) {
                    throw new Error('댓글을 저장하는 중 오류 발생');
                }
                const data = await response.json();
                if (data.error) {
                    throw new Error(data.error);
                }
                // 댓글을 성공적으로 저장한 후, 다시 댓글 목록을 가져와서 업데이트
                fetchComments();
                // 댓글 입력 창 비우기
                commentTextarea.value = '';
                resolve();
            } catch(error) {
                console.error('댓글을 저장하는 중 오류 발생:', error.message);
                reject(error);  // If an error occurs, reject the promise
            }
        });
    }
    postComment();
});

function deleteComment(commentId) {
    return new Promise(async (resolve, reject) => {
        console.log('Deleting comment with ID:', commentId);
        const confirmation = confirm("댓글을 삭제하시겠습니까?");

        if (confirmation) {
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
                let response = await fetch(`/api/mate/${postId}/comment/${commentId}`, {
                    method: 'DELETE',
                    headers: headers,
                });
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                window.location.href = `/mate/${postId}`;
                resolve();  // If everything is successful, resolve the promise
            } catch(error) {
                console.error('Error', error);
                reject(error);  // If an error occurs, reject the promise
            }
        }
    });
}


function editComment(commentId) {
    return new Promise(async (resolve, reject) => {
        const commentElement = document.querySelector(`#comments-list #comment-${commentId}`);
        const contentElement = commentElement.querySelector('.content');
        const originalContent = contentElement.textContent;

        // 수정 폼 생성
        const editForm=document.createElement("form");
        editForm.id=`edit-form-${postId}`;

        // 수정 텍스트 영역 생성 및 스타일 적용
        const contentTextarea=document.createElement("textarea");
        contentTextarea.name='content';
        contentTextarea.value=originalContent;
        editForm.style.textAlign="right";

        // 텍스트 영역에 패딩 및 마진 추가
        contentTextarea.style.padding = "8px";
        contentTextarea.style.marginTop = "8px";
        contentTextarea.style.width = "100%";
        contentTextarea.style.boxSizing = "border-box";

        // 제출 버튼 추가 및 스타일 적용
        const submitButton=document.createElement("button");
        submitButton.type="submit";

        // 제출 버튼에 배경색, 글자색, 패딩 추가
        submitButton.textContent="제출";
        submitButton.style.backgroundColor= "#4CAF50";
        submitButton.style.color= "white";
        submitButton.style.padding= "12px 15px";
        submitButton.style.borderRadius = "5px";
        submitButton.style.border= "none";
        submitButton.style.cursor= "pointer";

        editForm.appendChild(contentTextarea);
        editForm.appendChild(submitButton);

        // 폼 제출 시 서버로 요청 보내기
        editForm.addEventListener("submit", async function(event){
            event.preventDefault();

            const editedContent=contentTextarea.value;
            try {
                const editedContent=contentTextarea.value;
                const newToken = await reissueToken();
                console.log("New Token:", newToken);
                const token = localStorage.getItem('token') || sessionStorage.getItem('token');
                let headers = {
                    'Content-Type': 'application/json'
                };
                if (token) {
                    headers['Authorization'] = `Bearer ${token}`;
                }
                let response = await fetch(`/api/mate/${postId}/comment/${commentId}`,{
                    method:"PUT",
                    headers:headers,
                    body: JSON.stringify({content: editedContent}),
                });
                if (!response.ok) {
                    throw new Error("댓글 수정 중 오류 발생");
                }
                let data = await response.json();
                if(data.error){
                    throw new Error(data.error);
                }
                // 수정된 내용으로 업데이트 및 폼 숨기고 원래 내용 표시
                contentElement.textContent=editedContent;
                commentElement.replaceChild(contentElement,editForm);
                resolve();
            } catch(error) {
                console.error("댓글 수정 중 오류 발생:",error.message);
                reject(error);  // If an error occurs, reject the promise
            }
        });

        // 원래 내용을 폼으로 교체
        commentElement.replaceChild(editForm,contentElement);
    });
}




