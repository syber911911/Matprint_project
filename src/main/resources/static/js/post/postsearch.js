// let searchTypeSelect = document.getElementById('search-type-select');
// let keywordInput = document.getElementById('keyword-input');
// let genderSelect = document.getElementById('gender-select');
// let ageRangeSelect = document.getElementById('age-range-select');
// let statusSelect = document.getElementById('status-select');
// let searchButton = document.getElementById('search-button');
//
// let currentSearchConditions = {
//     searchType: "",
//     keyword: "",
//     gender: "",
//     ageRange: "",
//     status: ""
// };
//
// searchButton.addEventListener('click', function(event) {
//     event.preventDefault();
//
//     let selectedSearchType = searchTypeSelect.value;
//     let enteredKeyword = keywordInput.value;
//     let selectedGender = genderSelect.value;
//     let selectedAgeRange = ageRangeSelect.value;
//     let selectedStatus = statusSelect.value;
//
//     console.log(selectedSearchType, enteredKeyword, selectedGender,
//         selectedAgeRange, selectedStatus);
//
//     fetchPostsForSearch(selectedSearchType, enteredKeyword,
//         0, 10, selectedGender,
//         selectedAgeRange, selectedStatus);
// });
//
// function fetchPostsForSearch(searchType="", keyword="", pageNumber=0 , pageSize=10 , gender="", age=0, status="" ) {
//     fetch(`/mate/search?type=${searchType}&target=${keyword}&gender=${gender}&age=${age}&status=${status}&page=${pageNumber}&limit=${pageSize}`)
//         .then(response => response.json())
//         .then(data => {
//             console.log(data);
//             if (!data || !Array.isArray(data.content) || data.content.length === 0) {
//                 clearPosts();
//                 alert('검색 결과가 없습니다.');
//             } else {
//                 displayPosts(data);
//                 updatePageNumbers(data.totalPages);
//             }
//         })
//         .catch(error => {
//             console.error('게시물을 불러오는 중 오류 발생:', error);
//         });
// }
//
// function clearPosts() {
//     const postTableBody = document.getElementById('post-table-body');
//     while (postTableBody.firstChild) {
//         postTableBody.removeChild(postTableBody.firstChild);
//     }
// }
//
// fetchPostsForSearch();