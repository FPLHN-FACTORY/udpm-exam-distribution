const showToastError = (message) => {
    Toastify({
        text: message,
        className: "toastify-custom-error",
        backgroundColor: "#990000",
        style: {
            color: "#ff6666"
        },
        close: true,
        duration: 2000
    }).showToast();
}

const showToastSuccess = (message) => {
    Toastify({
        text: message,
        className: "toastify-custom-error",
        close: true,
        duration: 2000
    }).showToast();
}