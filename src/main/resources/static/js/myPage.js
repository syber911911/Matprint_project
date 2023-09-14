
document.addEventListener("DOMContentLoaded", async () => {

    const httpModule = await import("./service/HttpHandler.js");
    const userModule = await import("./service/User.js");
    const templateModule = await import("./service/Template.js");

    const myInfoBtn = document.getElementById("myInfo");
    const likePostBtn = document.getElementById("likePost");
    const myPostBtn = document.getElementById("myPost");
    const updateBtn = document.getElementById("update");
    const goBackBtn = document.getElementById("goback");
    const logoutBtn = document.getElementById("logout");
    const leaveBtn = document.getElementById("leaveBtn");
    const uploadBtn = document.getElementById("upload");
    const changeContentBox = document.getElementById('changecontentBox');
    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    console.log(token);
    const HttpHandler = new httpModule.HttpHandler(token);
    const User = new userModule.User(token);
    const { likePostTemplate, postTemplate, profileTemplate, uploadTemplate, updateFormTemplate } = templateModule;

    const clear = () => changeContentBox.innerHTML = ``;
    const paint = (template) => changeContentBox.insertAdjacentHTML('afterbegin', template);

    const handleUpload = (e) => {
        e.preventDefault();
        clear();
        uploadTemplate(User, paint);
    }
    uploadBtn.addEventListener('click', handleUpload);

    const handleLeave = (e) => {
        e.preventDefault();
        const confirmed = confirm("정말 회원 탈퇴하시겠습니까??");
        if (confirmed) {
            User.deleteUser('/api/profile');
        } else {
            console.log("회원 탈퇴가 취소되었습니다.");
        }
    }
    leaveBtn.addEventListener('click', handleLeave);

    const handleGoback = (e) => {
        e.preventDefault();
        window.location.replace('/');
    }
    goBackBtn.addEventListener('click', handleGoback);
    const showProfile = async () => {
        clear();
        profileTemplate(HttpHandler, paint);
    }
    //start
    if (token) {
        showProfile(token)
    } else {
        console.error('토큰이 없습니다.');
    }
    const showInfo = (e) => {
        e.preventDefault();
        showProfile()
    }
    myInfoBtn.addEventListener("click", showInfo);

    const handleLogout = (e) => {
        e.preventDefault();
        User.logout('api/logout');
    }
    logoutBtn.addEventListener('click', handleLogout);

    const showLikedPosts = async (e) => {
        e.preventDefault();
        clear();
        likePostTemplate(HttpHandler, paint);
    }
    likePostBtn.addEventListener("click", showLikedPosts);


    const showMyPosts = async (e) => {
        e.preventDefault();
        clear();
        postTemplate(HttpHandler, paint);
    }
    myPostBtn.addEventListener("click", showMyPosts);

    const showUpdateForm = async (e) => {
        e.preventDefault();
        clear();
        updateFormTemplate(HttpHandler, User, paint);
    }

    updateBtn.addEventListener("click", showUpdateForm);


});