function isAuthor(postUsername) {
    const currentToken = localStorage.getItem('token');
    const payload = decodeJwtToken(currentToken); // JWT 토큰 디코딩하여 payload 추출
    const currentUsername = payload.sub; // 토큰에서 username 추출

    return postUsername === currentUsername; // 글쓴이와 현재 로그인된 사용자가 일치하면 true 반환
}

// 수정 버튼 클릭 시 동작
const editButton = document.getElementById('edit-button');
editButton.addEventListener('click', () => {
    // TODO: 게시글 수정 로직 작성
    console.log("Edit button clicked");
});

// 삭제 버튼 클릭 시 동작
const deleteButton = document.getElementById('delete-button');
deleteButton.addEventListener('click', () => {
    // TODO: 게시글 삭제 로직 작성
    console.log("Delete button clicked");
});

window.onload = function() {
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
