console.log('게시글 ID:', postId);

function isAuthor(commentUsername) {
    const currentToken = localStorage.getItem('token') || sessionStorage.getItem('token');
    const payload = decodeJwtToken(currentToken); // JWT 토큰 디코딩하여 payload 추출
    const currentUsername = payload.sub; // 토큰에서 username 추출

    return commentUsername === currentUsername; // 글쓴이와 현재 로그인된 사용자가 일치하면 true 반환
}

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

            // 각각의 댓글 요소에 고유한 ID 값을 설정:
            commentElement.id = 'comment-' + comment.id;

            // 수정/삭제 버튼 생성
            let buttonsHTML = '';
            if (isAuthor(comment.username)) { // 현재 로그인한 사용자와 댓글 작성자가 동일하다면
                buttonsHTML = `
               <button class="comment-button" onclick="editComment(${comment.id})">  수정</button>
               <button class="comment-button" onclick="deleteComment(${comment.id})">  삭제</button>
           `;
            }
            commentElement.innerHTML = `
                <p style="font-weight: bold;">${comment.username} : </p>
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

    if (!commentText) {
        alert('댓글 내용을 입력해주세요.');
        return;
    }

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

function deleteComment(commentId) {
    console.log('Deleting comment with ID:', commentId);
    const confirmation = confirm("댓글을 삭제하시겠습니까?");
    if (confirmation) {
        fetch(`/mate/${postId}/comment/${commentId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => {
                if(!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                window.location.href = `/matprint/mate/${postId}`;
            })
            .catch((error) => {
                console.error('Error', error);
            });
    }
}

function editComment(commentId) {
    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
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
    editForm.addEventListener("submit", function(event){
        event.preventDefault();

        const editedContent=contentTextarea.value;

        fetch(`/mate/${postId}/comment/${commentId}`,{
            method:"PUT",
            headers:{
                "Content-Type":"application/json",
                "Authorization":`Bearer ${token}`
            },
            body: JSON.stringify({content: editedContent}),
        })
            .then((response)=>{
                if(!response.ok){
                    throw new Error("댓글 수정 중 오류 발생");
                }
                return response.json();
            })
            .then((data)=>{
                if(data.error){
                    throw new Error(data.error);
                }
                // 수정된 내용으로 업데이트 및 폼 숨기고 원래 내용 표시
                contentElement.textContent=editedContent;
                commentElement.replaceChild(contentElement,editForm);
            })
            .catch((error)=>{
                console.error("댓글 수정 중 오류 발생:",error.message);
            });
    });
    // 원래 내용을 폼으로 교체
    commentElement.replaceChild(editForm,contentElement);
}




