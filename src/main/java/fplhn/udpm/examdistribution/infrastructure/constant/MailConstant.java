package fplhn.udpm.examdistribution.infrastructure.constant;

public class MailConstant {

    public static final String LOGO_PATH = "/static/assets/image/logo-udpm.png";

    public static final String SUBJECT = "[Exam Distribution][PTPM-XLDL-UDPM] ";

    public static final String BODY = "";

    public static final String BODY_START_EXAM_SHIFT = """
            <html>
                <body>
                    <p>Phòng thi: ${examShiftCode} - ${room}</p>
                    <p>Ngày thi: ${examDate} - Ca: ${shift}</p>
                    <p>Lớp: ${classSubjectCode}</p>
                    <p>Môn thi: ${subjectName}</p>
                    <p>Giám thị 1: ${nameFirstSupervisor} - ${codeFirstSupervisor}</p>
                    <p>Giám thị 2: ${nameSecondSupervisor} - ${codeSecondSupervisor}</p>
                    <p>Link đề thi: <a href="https://drive.google.com/file/d/${pathExamPaper}/view">Tại đây</a></p>
                </body>
            </html>
            """;

}
