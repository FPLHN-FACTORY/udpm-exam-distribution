const showToastError = (message) => {
    Toastify({
        text: message,
        style: {
            background: "white",
            color: "black",
            borderLeft: "4px solid red",
        },
        close: true,
        duration: 2000
    }).showToast();
}

const showToastSuccess = (message) => {
    Toastify({
        text: message,
        style: {
            background: "white",
            color: "black",
            borderLeft: "4px solid green",
        },
        close: true,
        duration: 2000
    }).showToast();
}