package fplhn.udpm.examdistribution.infrastructure.config.job.examshift.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@Slf4j
public class DownloadExamShiftTemplate {

    public ResponseObject<ByteArrayInputStream> downloadTemplate() {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Template Ca thi");

            Row row = sheet.createRow(0);

            Font font = workbook.createFont();
            font.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setLocked(true);
            headerCellStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setFont(font);
            headerCellStyle.setWrapText(true);

            String[] headers = {
                    "#", "Tòa nhà", "Ngày tháng", "Ca", "Phòng", "Khóa học", "Mã môn",
                    "Ngày học cuối cùng", "Mã lớp môn", "Giáo viên dạy môn", "Giám thị 1",
                    "Giám thị 2", "Mã ngành", "Trùng lịch", "Số dự thi", "Block", "Ghi chú", "Mã cơ sở"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            setDataValidation(sheet, Shift.getValidShift(), 3);

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ResponseObject<>(
                    new ByteArrayInputStream(outputStream.toByteArray()),
                    HttpStatus.OK,
                    "Tải template thông tin ca thi thành công"
            );
        } catch (IOException ex) {
            log.error("Đã có lỗi xảy ra khi tạo file template", ex);
            return ResponseObject.errorForward(
                    "Đã có lỗi xảy ra khi tạo file template",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private void setDataValidation(Sheet sheet, String[] data, int col) {
        data = java.util.Arrays.stream(data)
                .filter(s -> (s != null && !s.isEmpty()))
                .toArray(String[]::new);
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(data);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, col, col);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);
        validation.setShowErrorBox(true);
        validation.setSuppressDropDownArrow(true);
        validation.createErrorBox("Sai Dữ Liệu", "Hãy Chọn Dữ Liệu Cho Sẵn");
        validation.createPromptBox("Chọn Dữ Liệu", "Chọn Dữ Liệu Cho Sẵn");
        sheet.addValidationData(validation);
    }

}
