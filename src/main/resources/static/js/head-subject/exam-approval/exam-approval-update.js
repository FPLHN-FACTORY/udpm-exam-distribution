function handleApproval(idExam) {
    swal({
        title: "Xác nhận phê duyệt?",
        text: "Bạn chắc chắn muốn phê duyệt đề này không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Phê duyệt",
                className: "btn btn-secondary",
            },
        },
    }).then((ok) => {
        if (ok) {
            let url = ApiConstant.API_HEAD_SUBJECT_MANAGE_EXAM_APPROVAL + '?examPaperId=' + idExam;
            $.ajax({
                type: "PUT",
                url: url,
                success: function (responseBody) {
                    console.log(responseBody)
                    if (responseBody?.status === "OK") {
                        showToastSuccess(responseBody?.message)
                    }
                    clearFormSearch();
                    getExamPapers();
                },
                error: function (error) {
                    showToastError('Có lỗi xảy ra khi phê duyệt đề!');
                }
            });
        }
    });
}

function handleDelete(idExam) {
    swal({
        title: "Xác nhận xóa?",
        text: "Bạn chắc chắn muốn xóa đề này không?",
        type: "warning",
        buttons: {
            cancel: {
                visible: true,
                text: "Hủy",
                className: "btn btn-black",
            },
            confirm: {
                text: "Xóa",
                className: "btn btn-secondary",
            },
        },
    }).then((ok) => {
        if (ok) {
            let url = ApiConstant.API_HEAD_SUBJECT_MANAGE_EXAM_APPROVAL + '?examPaperId=' + idExam;
            $.ajax({
                type: "DELETE",
                url: url,
                success: function (responseBody) {
                    console.log(responseBody)
                    if (responseBody?.status === "OK") {
                        showToastSuccess(responseBody?.message)
                    }
                    clearFormSearch();
                    getExamPapers();
                },
                error: function (error) {
                    showToastError('Có lỗi xảy ra khi xóa đề!');
                }
            });
        }
    });
}