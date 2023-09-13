let searchTypeSelect = document.getElementById('search-type-select');
let keywordInput = document.getElementById('keyword-input');
let genderSelect = document.getElementById('gender-select');
let ageRangeSelect = document.getElementById('age-range-select');
let statusSelect = document.getElementById('status-select');
let searchButton = document.getElementById('search-button');

let currentSearchConditions = {
    searchType: "",
    keyword: "",
    gender: "",
    ageRange: "",
    status: ""
};

searchButton.addEventListener('click', function(event) {
    event.preventDefault();

    currentSearchConditions.searchType = searchTypeSelect.value;
    currentSearchConditions.keyword = keywordInput.value;
    currentSearchConditions.gender = genderSelect.value;
    currentSearchConditions.ageRange = ageRangeSelect.value;
    currentSearchConditions.status = statusSelect.value;

    console.log(currentSearchConditions.searchType, currentSearchConditions.keyword,
        currentSearchConditions.gender,currentSearchConditions.ageRange,
        currentSearchConditions.status);

    currentPage=0; // 새로운 검색이므로 페이지 번호 초기화
    fetchPosts(currentPage);
});

function fetchPosts(pageNumber=0 , pageSize=10 ) {

    let {searchType, keyword, gender, ageRange: age , status}
        =currentSearchConditions;

    fetch(`/mate/search?type=${searchType}&target=${keyword}&gender=${gender}&age=${age}&status=${status}&page=${pageNumber}&limit=${pageSize}`)
        .then(response => response.json())
        .then(data => {
            console.log(data);
            if (!data || !Array.isArray(data.content) || data.content.length === 0) {
                clearPosts();
                alert('검색 결과가 없습니다.');
            } else {
                displayPosts(data);
                updatePageNumbers(data.totalPages);
            }
        })
        .catch(error => {
            console.error('게시물을 불러오는 중 오류 발생:', error);
        });
}

function clearPosts() {
    const postTableBody=document.getElementById("post-table-body");
    while(postTableBody.firstChild){
        postTableBody.removeChild(postTableBody.firstChild)
    }
}

fetchPosts();
let pageNumberSpan;
let prevPageButton;
let nextPageButton;
let currentPage=0;

window.onload=function(){

    pageNumberSpan=document.getElementById("page-numbers");
    prevPageButton=document.getElementById("prev-page-button");
    nextPageButton=document.getElementById("next-page-button");

    checkLogin();
    fetchPosts; // 초기 페이지의 게시물 불러오기

    prevPageButton.addEventListener("click", ()=>{
        if(currentPage>0){
            currentPage--;
            fetchPosts(currentPage);
        }
    });

    nextPageButton.addEventListener("click", ()=>{
        const totalPages=parseInt(pageNumberSpan.textContent.split("/")[1]);
        if(currentPage<totalPages-1){
            currentPage++;
            fetchPosts(currentPage);
        }
    });
};

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

        // visitDate를 Date 객체로 변환하고 각 부분 추출
        const dateObj = new Date(post.visitDate);
        const year = dateObj.getFullYear();
        const month = dateObj.getMonth() + 1; // getMonth()는 0부터 시작하므로 1을 더해줍니다.
        const date = dateObj.getDate();

        // 추출한 부분들을 조합하여 원하는 형식의 문자열 생성
        const visitDate = `${year}.${month}.${date}`;

        row.innerHTML = `
            <td>${post.id}</td>
            <td><a href="/matprint/mate/${post.id}" class="post-link">${post.title}</a></td>
            <td>${post.status}</td>
            <td>${visitDate}</td>
            <td>${post.username}</td>
        `;
        postTableBody.appendChild(row);
    });
}

// 페이지 번호 업데이트 함수 추가
function updatePageNumbers(totalPages) {
    pageNumberSpan.textContent=`${currentPage+1} / ${totalPages}`;   // 사용자에게 보여주는 페이지 번호는 +1 해줘야 함.
}
