$(document).ready(() => {
    const loginInfo = getExamDistributionInfo();
    if (loginInfo) {
        $('#user-login-name').text(loginInfo.userFullName);
        $('#user-login-avatar').attr('src', loginInfo.userPicture);
        $('#user-info-avatar').attr('src', loginInfo.userPicture);
        $('#user-info-name').text(loginInfo.userFullName);
        $('#user-info-email').text(loginInfo.userEmailFPT);
    }
});