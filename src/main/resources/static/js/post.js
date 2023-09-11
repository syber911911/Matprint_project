function fetchPosts() {
    // 서버로 GET 요청을 보내서 게시물 목록을 가져옴
    fetch('/mate')
        .then(response => response.json())
        .then(data => {
            // 게시물이 없을 경우 알림을 표시
            if (data.length === 0) {
                showNoPostsAlert();
            } else {
                // 게시물이 있을 경우 게시물을 표시
                displayPosts(data);
            }
        })
        .catch(error => {
            console.error('게시물을 불러오는 중 오류 발생:', error);
        });
}

// 게시물이 없을 때 알림 표시
function showNoPostsAlert() {
    const alertDiv = document.getElementById('no-posts-alert');
    alertDiv.style.display = 'block'; // 알림을 보이도록 변경
}

// 게시물 표시
function displayPosts(postsPage) {
    const posts = postsPage.content; // content 속성을 가져옴
    const postTableBody = document.getElementById('post-table-body');

    if (postTableBody) {
        postTableBody.innerHTML = '';
        // 나머지 코드
    } else {
        console.error('post-table-body 요소를 찾을 수 없습니다.');
    }

    // 게시물을 동적으로 추가
    posts.forEach(post => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${post.id}</td>
            <td><a href="/matprint/mate/${post.id}" class="post-link">${post.title}</a></td>
            <td>${post.status}</td>
            <td>${post.visitDate}</td>
        `;
        postTableBody.appendChild(row);
    });
}

// 페이지 로드 시 게시물 가져오기
window.onload = function() {
    fetchPosts();
};

// 페이지 선택 버튼과 페이지 번호 관리
const prevPageButton = document.getElementById('prev-page-button');
const nextPageButton = document.getElementById('next-page-button');
const pageNumberSpan = document.getElementById('page-numbers');
let currentPage = 1;

// 이전 페이지로 이동
prevPageButton.addEventListener('click', () => {
    if (currentPage > 1) {
        currentPage--;
        fetchPosts(currentPage);
    }
});

// 다음 페이지로 이동
nextPageButton.addEventListener('click', () => {
    // 페이지 수는 총 페이지 수에 따라 수정해야 함
    const totalPages = 5; // 예시로 5 페이지까지 있다고 가정
    if (currentPage < totalPages) {
        currentPage++;
        fetchPosts(currentPage);
    }
});

// 페이지 로드 시 초기 페이지의 게시물 불러오기
window.onload = function () {
    fetchPosts(currentPage);
};

// 페이지 번호 업데이트
function updatePageNumbers(totalPages) {
    pageNumberSpan.textContent = `${currentPage} / ${totalPages}`;
}

// 페이지 번호 업데이트 예시
// updatePageNumbers(5); // 총 5페이지가 있을 때 호출

