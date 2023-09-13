window.onload = function() {
    checkLogin();
};

// 현재 페이지의 URL 가져오기
const currentUrl = window.location.href;
// URL에서 컨텍스트 경로를 제외한 나머지 경로 가져오기
// const pathWithoutContext = currentUrl.replace("http://localhost:8080/matprint", "");
const pathWithoutContext = currentUrl.replace("https://matprint.site/matprint", "");
// 경로에서 게시글 ID를 추출
const postId = pathWithoutContext.split('/')[2];
console.log('게시글 ID:', postId);

function fetchPostDetail() {
    return new Promise((resolve, reject) => {
        if (!postId) {
            reject('게시글 ID가 유효하지 않습니다.');
            return;
        }
        fetch(`/mate/${postId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('서버에서 게시글을 가져오는 중 오류 발생');
                }
                return response.json();
            })
            .then(data => {
                if (data.error) {
                    throw new Error(data.error);
                }
                // 게시글 정보를 동적으로 표시
                displayPostDetail(data.postDto);
                // 게시글 정보가 화면에 표시된 후에 글쓴이 확인
                const postUsernameElement = document.getElementById('post-username');
                const postUsername = postUsernameElement.textContent.trim();
                resolve(postUsername);
            })
            .catch(error => {
                console.error('게시글을 불러오는 중 오류 발생:', error.message);
                reject(error.message);
            });
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

    if (post && post.username) {
        usernameElement.innerHTML =
            `<div style="display: flex; align-items: center;">
            <img src="${post.imgUrl}" style="border-radius: 50%; box-shadow: 0px 0px 10px rgba(0, 0, 0, .5); width:40px;">
            <b style="margin-left:10px;">${post.username}</b>
         </div>`;
    } else {
        usernameElement.textContent = '';
    }
}