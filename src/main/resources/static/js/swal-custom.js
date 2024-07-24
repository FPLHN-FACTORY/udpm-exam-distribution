const showAlert = (
    title,
    text,
    type,
    confirmText,
    callbackConfirm,
    callbackCancel
) => {
    swal({
        title: title,
        text: text,
        type: type,
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Xác nhận",
                className: "btn btn-black",
            },
        },
    }).then((confirm) => {
        if (confirm) {
            callbackConfirm();
        } else {
            callbackCancel();
        }
    });
};