$(document).ready(() => {
    $('#btnImportExamShift').click(() => {
        $('#fileImportExamShift').click();

        $('#fileImportExamShift').change(() => {
            handleUploadExamShiftExcel();
        });
    });
});

const handleUploadExamShiftExcel = () => {
    const formData = new FormData();
    const file = $('#fileImportExamShift')[0].files[0];
    formData.append('file', file);
    $.ajax({
        url: `${ApiConstant.API_HEAD_DEPARTMENT_FILE}/upload`,
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: (res) => {
            console.log(res);
            showToastSuccess(res);
        },
        error: (error) => {
            console.log(error);
            showToastError('Tải lên thất bại');
        }
    });
}