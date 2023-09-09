export const profileTemplate = async (HttpHandler, paint) => {
    const user = await HttpHandler.request('/profile');
    const { username, gender, email, age, phone } = user;
    paint(`
    <ul>
            <li class="flex">
                <div>
                    <h3>사용자명:</h3>
                    <p>${username}</p>
                </div>
                <div>
                    <h3>성별:</h3>
                    <p>${gender}</p>
                </div>
                <div>
                    <h3>나이:</h3>
                    <p>${age}</p>
                </div>
                <div>
                    <h3>이메일:</h3>
                    <p>${email}</p>
                </div>
                <div>
                    <h3>휴대폰 번호:</h3>
                    <p>${phone}</p>
                </div>
            </li>
        </ul>
    `)
}
export const likePostTemplate = async (HttpHandler, paint) => {
    const posts = await HttpHandler.request('/profile/wishlist');
    const { restaurantName, address, roadAddress } = posts;
    posts.map(post => {
        const { restaurantName, address, roadAddress } = post;
        paint(`
        <ul>
        <li class="flex">
            <div>
                <h3>가게명:</h3>
                <p>${restaurantName}</p>
            </div>
            <div>
                <h3>주소:</h3>
                <p>${address}</p>
            </div>
            <div>
                <h3>도로주소:</h3>
                <p>${roadAddress}</p>
            </div>
        </li>
    </ul>
    `)
    })
}
export const postTemplate = async (HttpHandler, paint) => {
    const posts = await HttpHandler.request('/profile/post');
    const { title, status, visitDate } = posts;
    posts.map(post => {
        const { title, status, visitDate } = post;
        paint(`
        <ul>
                <li class="flex">
                    <div>
                        <h3>제목:</h3>
                        <p>${title}</p>
                    </div>
                    <div>
                        <h3>모집상태:</h3>
                        <p>${status}</p>
                    </div>
                    <div>
                        <h3>방문날짜:</h3>
                        <p>${visitDate}</p>
                    </div>
                </li>
            </ul>
    `)
    })
}