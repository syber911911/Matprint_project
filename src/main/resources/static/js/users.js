const getJwt = () => localStorage.getItem('token')

const fetchLoggedIn = function () {
    if (!getJwt())
        return new Promise(resolve => {
            resolve(false)
        })
    else return fetch("/users/is-logged-in", {
        headers: {
            'Authorization': `Bearer ${getJwt()}`
        }
    }).then(response => {
        if (response.ok) return response.json()
        else return false
    })
}

const createUserElem = function () {
    return fetchLoggedIn().then(userInfo => {
        if (!userInfo) return anonymousUserElem()
        else return loggedInUserElem(userInfo.username)
    })
}

const loggedInUserElem = function (username) {
    const container = document.createElement('div')
    const usernameElem = document.createElement('p')
    usernameElem.innerText = `${username}님, 반갑습니다! `

    const logoutElem = document.createElement('form')
    const logoutButton = document.createElement('input')
    logoutButton.value = '로그아웃'
    logoutButton.type = 'submit'
    logoutElem.appendChild(logoutButton)
    logoutElem.addEventListener('submit', e => {
        e.preventDefault()
        localStorage.removeItem('token')
        location.reload()
    })


    container.appendChild(usernameElem)
    container.appendChild(logoutElem)
    return container
}

const anonymousUserElem = function () {
    const container = document.createElement('div')
    const loginLink = document.createElement('a')
    loginLink.href = `/views/login?next=${location.href}`
    loginLink.innerText = '로그인'

    const nbsp = document.createTextNode(' ')
    const registerLink = document.createElement('a')
    registerLink.href = `/views/register?next=${location.href}`
    registerLink.innerText = '회원가입'

    container.appendChild(loginLink)
    container.appendChild(nbsp)
    container.appendChild(registerLink)

    return container
}

const requireLogin = function() {
    alert('로그인이 필요합니다.')
    location.href = `/views/login?next=${location.href}`
}