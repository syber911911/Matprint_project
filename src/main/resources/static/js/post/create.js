let headers = {
    'Content-Type': 'application/json'
};
if (token) {
    headers['Authorization'] = `Bearer ${token}`;
}

function createPost(title, content, visitDate, prefer) {
    const data = {
        title,
        content,
        visitDate,
        prefer
    };

    // return fetch('/api/mate', {
    //     method: 'POST',
    //     headers: headers,
    //     body: JSON.stringify(data)
    // })
    //     .then(response => response.json())
    //     .then(data => {
    //         // 게시글 생성 성공 여부 확인
    //         if (data.success) {
    //             window.location.href = '/mate';
    //         } else {
    //             alert('게시글 생성에 실패했습니다.');
    //             window.location.href = '/mate';
    //         }
    //     })
    //     .catch(error => {
    //         alert('게시글 생성에 실패했습니다.');
    //         console.error('게시글 생성 중 오류 발생:', error);
    //     })
    return fetch('/api/mate', {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            // 게시글 생성 성공 여부 확인
            if (data && data.success) { // 응답 데이터의 구조를 확인
                window.location.href = '/mate';
            } else {
                window.location.href = '/mate';
            }
        })
        .catch(error => {
            alert('게시글 생성에 실패했습니다.');
            console.error('게시글 생성 중 오류 발생:', error);
        });
}

document.querySelector("#create-post-form").addEventListener("submit", function(event) {
    event.preventDefault();

    const title = document.querySelector("#post-title").value;
    const content = document.querySelector("#post-content").value;
    const visitDateInput = document.querySelector("#post-visit-date");
    const prefer = document.querySelector("#post-prefer").value;
    const selectedDate = visitDateInput.value;
    const formattedDate = new Date(selectedDate).toISOString().split('T')[0];

    createPost(title, content, formattedDate, prefer);
});
