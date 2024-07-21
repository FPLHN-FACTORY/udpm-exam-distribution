package fplhn.udpm.examdistribution.infrastructure.constant;

public class MailConstant {

    public static final String LOGO_PATH = "/static/assets/image/logo-udpm.png";

    public static final String SUBJECT = "[Exam Distribution][PTPM-XLDL-UDPM] ";

    public static final String BODY = "";

    public static final String BODY_START_EXAM_SHIFT = """
            <html>
                <body>
                    <p>Phòng thi: ${examShiftCode} đã bắt đầu.</p>
                    <p>Môn thi: ${subjectName}</p>
                    <p>Link đề thi: <a href="${pathExamPaper}">${pathExamPaper}</a></p>
                </body>
            </html>
            """;

}
