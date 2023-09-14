let headers = {
    'Content-Type': 'application/json'
};
if (token) {
    headers['Authorization'] = `Bearer ${token}`;
}

async function reissueToken() {
    const autoLogin = localStorage.getItem('autoLogin');
    if (!localStorage.getItem('token') && !sessionStorage.getItem('token')) {
        console.log("user: anonymous");
        return null;
    } else {
        let response = await fetch(`/api/reissue?autoLogin=${autoLogin}`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            credentials: 'include'
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        let data = await response.json();
        if (data.error) {
            throw new Error(data.error);
        }
        if (localStorage.getItem('autoLogin') === "T") {
            localStorage.removeItem('token');
            localStorage.setItem('token', data.accessToken);
        } else if (localStorage.getItem('autoLogin') === "F") {
            sessionStorage.removeItem('token');
            sessionStorage.setItem('token', data.accessToken);
        }
        return data.accessToken;
    }
}

function createPost(title, content, visitDate, prefer) {
    return new Promise(async (resolve, reject) => {
        try {
            const data = {
                title,
                content,
                visitDate,
                prefer
            };
            const newToken = await reissueToken();
            console.log("New Token:", newToken);
            const token = localStorage.getItem('token') || sessionStorage.getItem('token');
            let headers = {
                'Content-Type': 'application/json'
            };
            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
            }
            let response = await fetch('/api/mate', {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(data)
            });
            if (!response.ok) {
                throw new Error('게시글 생성에 실패했습니다.');
            }
            // 게시글 생성 성공 여부 확인
            alert('게시글이 생성되었습니다.');
            window.location.href = '/mate';
            resolve();
        } catch(error) {
            alert('게시글 생성에 실패했습니다.');
            console.error('게시글 생성 중 오류 발생:', error);
            reject(error);
        }
    });
};


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