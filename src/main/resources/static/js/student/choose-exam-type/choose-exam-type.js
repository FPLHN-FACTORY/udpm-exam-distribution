$(document).ready(function () {
    clearLocalStorage();
})

function clearLocalStorage() {
    localStorage.removeItem('startTime');
    localStorage.removeItem('mockExamPaper');
    localStorage.removeItem('practiceRoom');
    handleShowSectionContainer();
}