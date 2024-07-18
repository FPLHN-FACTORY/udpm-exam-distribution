//start state
let idExam = '';
//end state

//start setter
const setIdExam = (id) => {
    idExam = id;
};
//end setter

//start getter
const getIdExam = () => idExam;

//end getter

function handleApproval() {
    let check = true;
    if ($('#exam-paper-type').val()?.trim().length === 0) {
        check = false;
        $('#exam-paper-type-error').text('Chọn loại đề');
    } else {
        $('#exam-paper-type-error').text('');
    }
    if (check) {
        $('#modal-exam-peper-approval').modal('hide');
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
                let data = {
                    examPaperId: getIdExam(),
                    examPaperType: $('#exam-paper-type').val()
                }
                let url = ApiConstant.API_HEAD_SUBJECT_MANAGE_EXAM_APPROVAL;
                $.ajax({
                    type: "PUT",
                    url: url,
                    data: JSON.stringify(data),
                    contentType: "application/json",
                    success: function (responseBody) {
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
            }else {
                $('#modal-exam-peper-approval').modal('show');
            }
        });
    }
}

function handleOpenModalExamApproval(idExam) {

    setIdExam(idExam);

    $('#exam-paper-type').val('');

    $('#modal-exam-peper-approval').modal('show');

}

$(document).ready(function () {
    $('#btn-exam-approval').on('click', handleApproval)
})

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
            showLoading();
            let url = ApiConstant.API_HEAD_SUBJECT_MANAGE_EXAM_APPROVAL + '?examPaperId=' + idExam;
            $.ajax({
                type: "DELETE",
                url: url,
                success: function (responseBody) {
                    if (responseBody?.status === "OK") {
                        showToastSuccess(responseBody?.message)
                    }
                    clearFormSearch();
                    getExamPapers();
                    hideLoading();
                },
                error: function (error) {
                    showToastError('Có lỗi xảy ra khi xóa đề!');
                    hideLoading();
                }
            });
        }
    });
}