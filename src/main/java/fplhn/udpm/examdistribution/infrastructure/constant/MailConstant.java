package fplhn.udpm.examdistribution.infrastructure.constant;

public class MailConstant {

    public static final String LOGO_PATH = "/static/assets/image/logo-udpm.png";

    public static final String SUBJECT = "[Exam Distribution][PTPM-XLDL-UDPM] ";

    public static final String HEADER = """
            <!DOCTYPE hmtl>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Document</title>
                <style>
                    .container {
                        max-width: 1140px;
                        margin: 0 auto;    
                        font-size: 16px;               
                    }

                    .bg-secondary {
                        background-color: #6c757d !important;
                        color: #fff !important;
                    }

                    .border {
                        border: 1px solid #dee2e6 !important;
                    }

                    .border-2 {
                        border-width: 2px !important;
                    }

                    .text-center {
                        text-align: center !important;
                    }

                    .small {
                        font-size: 0.875rem !important;
                    }

                    .list-unstyled {
                        padding-left: 0;
                        list-style: none;
                    }

                    .border-bottom {
                        border-bottom: 3px solid #ddd;
                    }
                    .border-top {
                        border-top: 3px solid #ddd;
                    }
                    table {
                        border-collapse: collapse; 
                        width: 100%;
                    }
                    th, td {
                        border: 1px solid #c9cbcd; 
                        padding: 8px;
                    }        
                </style>
            </head>

            <body>
                <div class="container">
                    <div class=" text-center border-bottom" style="background-color: #132A4D;">
                        <img src="cid:logoImage" height="80px">
                    </div>
                    <div class="container" style="margin: 10px 10px;">
                    <div style="text-align: center"><h3>${title}</h3></div>
                        """;

    public static final String BODY = """
                <ul>
                    <li>Mã đề: ${examPaperCode}</li>
                    <li>Thời gian: ${timeSend}</li>
                    <li>Môn: ${subjectName}</li>
                    <li>Chuyên ngành: ${majorName}</li>
                    <li>Bộ môn: ${departmentName}</li>
                    <li>Kỳ học: ${semesterName}</li>
                </ul>
            """;

    public static final String BODY_START_EXAM_SHIFT = """
                <ul>
                    <li>Phòng thi: ${examShiftCode} - ${room}</li>
                    <li>Ngày thi: ${examDate} - Ca: ${shift}</li>
                    <li>Lớp: ${classSubjectCode}</li>
                    <li>Môn thi: ${subjectName}</li>
                    <li>Giám thị 1: ${nameFirstSupervisor} - ${codeFirstSupervisor}</li>
                    <li>Giám thị 2: ${nameSecondSupervisor} - ${codeSecondSupervisor}</li>
                    <li>Link đề thi: <a href="https://drive.google.com/file/d/${pathExamPaper}/view">Tại đây</a></li>
                </ul>
            """;

    public static final String BODY_CREATE_EXAM_SHIFT = """
                <ul>
                    <li>Phòng thi: ${examShiftCode} - ${room}</li>
                    <li>Mật khẩu: ${password}</li>
                    <li>Ngày thi: ${examDate} - Ca: ${shift}</li>
                    <li>Lớp: ${classSubjectCode}</li>
                    <li>Môn thi: ${subjectName}</li>
                    <li>Giám thị 1: ${nameFirstSupervisor} - ${codeFirstSupervisor}</li>
                    <li>Giám thị 2: ${nameSecondSupervisor} - ${codeSecondSupervisor}</li>
                </ul>
            """;

    public static final String BODY_REJECT_EXAM_PAPER = """
                <h4>Thông tin đề thi</h4>
                <ul>
                    <li>Mã đề: ${examPaperCode}</li>
                    <li>Thời gian từ chối: ${timeSend}</li>
                    <li>Môn: ${subjectName}</li>
                    <li>Chuyên ngành: ${majorName}</li>
                    <li>Bộ môn: ${departmentName}</li>
                </ul>
            """;

    public static final String FOOTER = """
                        <div class=" text-center border-top small" style="background-color: #132A4D; padding: 10px 10px;color: #FFF;">
                            <ul class="list-unstyled">
                                <li>Lưu ý : Đây là email tự động vui lòng không phải hồi email này.</li>
                                <li>Mọi thắc mắc xin liên hệ xưởng dự án của Bộ môn Phát Triển Phần Mềm.</li>
                            </ul>
                        </div>
                    </div>
                </body>
            </html>
            """;

}
