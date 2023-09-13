let pageNumberSpan;
let prevPageButton;
let nextPageButton;
let currentPage = 0;   // 페이지 번호는 0부터 시작

function fetchPosts(pageNumber = 0, pageSize = 10) {
    // 서버로 GET 요청을 보내서 게시물 목록을 가져옴
    fetch(`/mate?page=${pageNumber}&limit=${pageSize}`)
        .then(response => response.json())
        .then(data => {
            // 게시물이 없을 경우 알림을 표시
            if (data.length === 0) {
                // showNoPostsAlert();
            } else {
                // 게시물이 있을 경우 게시물을 표시
                displayPosts(data);
                updatePageNumbers(data.totalPages);
            }
        })
        .catch(error => {
            console.error('게시물을 불러오는 중 오류 발생:', error);
        });
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

window.onload = function() {
    pageNumberSpan = document.getElementById('page-numbers');
    prevPageButton = document.getElementById('prev-page-button');
    nextPageButton = document.getElementById('next-page-button');

    checkLogin();
    fetchPosts();   // 초기 페이지의 게시물 불러오기

    prevPageButton.addEventListener('click', () => {
        if (currentPage > 0) {
            currentPage--;
            fetchPosts(currentPage);
        }
    });

    nextPageButton.addEventListener('click', () => {
        const totalPages = parseInt(pageNumberSpan.textContent.split(" / ")[1]);
        if (currentPage < totalPages - 1) {
            currentPage++;
            fetchPosts(currentPage);
        }
    });
};

// 페이지 번호 업데이트 함수 추가
function updatePageNumbers(totalPages) {
    pageNumberSpan.textContent = `${currentPage + 1} / ${totalPages}`;   // 사용자에게 보여주는 페이지 번호는 +1 해줘야 함.
}


