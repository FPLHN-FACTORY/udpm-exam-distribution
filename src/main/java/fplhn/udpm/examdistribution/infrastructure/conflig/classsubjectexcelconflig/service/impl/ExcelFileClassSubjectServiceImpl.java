package fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.entity.Semester;
import fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.repository.BlockExcelCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.repository.FacilityChildExcelCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.repository.SemesterExcelCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.repository.SubjectExcelCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.service.ExcelFileClassSubjectService;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SemesterName;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class ExcelFileClassSubjectServiceImpl implements ExcelFileClassSubjectService {

    private static final Logger log = LoggerFactory.getLogger(ExcelFileClassSubjectServiceImpl.class);

    private final SemesterExcelCustomRepository semesterRepository;

    private final BlockExcelCustomRepository blockCustomRepository;

    private final FacilityChildExcelCustomRepository facilityChildCustomRepository;;
    private final SubjectExcelCustomRepository subjectExcelCustomRepository;

    public ExcelFileClassSubjectServiceImpl(SemesterExcelCustomRepository semesterRepository,
                                            BlockExcelCustomRepository blockCustomRepository,
                                            FacilityChildExcelCustomRepository facilityChildCustomRepository, SubjectExcelCustomRepository subjectExcelCustomRepository) {
        this.semesterRepository = semesterRepository;
        this.blockCustomRepository = blockCustomRepository;
        this.facilityChildCustomRepository = facilityChildCustomRepository;
        this.subjectExcelCustomRepository = subjectExcelCustomRepository;
    }

    @Override
    public ResponseObject<ByteArrayInputStream> downloadTemplate(SemesterName semester, Integer year) {
        try (Workbook workbook = new XSSFWorkbook()) {

            List<Semester> semesters = semesterRepository.findAllBySemesterNameAndYearAndStatus(semester,year,EntityStatus.ACTIVE);

            if (semesters.isEmpty()){
                log.error("Semester not found: {}", semester);
                return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"Học kỳ "+semester+" năm "+year+" không tồn tại!");
            }

            Sheet sheet = workbook.createSheet("Template thông tin lớp môn");

            Row row = sheet.createRow(0);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setLocked(true);
            headerCellStyle.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Cell cell = row.createCell(0);
            cell.setCellValue("STT");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue("Block");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(2);
            cell.setCellValue("Mã lớp");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(3);
            cell.setCellValue("Mã môn");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(4);
            cell.setCellValue("Ngày");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(5);
            cell.setCellValue("Ca");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(6);
            cell.setCellValue("Tên cơ sở con");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(7);
            cell.setCellValue("Mã nhân viên");
            cell.setCellStyle(headerCellStyle);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            List<String> validBlock = blockCustomRepository.findAllBySemester(semesters.get(0).getId());
            if(validBlock.isEmpty()) {
                log.error("Không có block nào trong học kỳ này");
                return null;
            }
            String[] validShift = {Shift.CA1.toString(), Shift.CA2.toString(), Shift.CA3.toString(), Shift.CA4.toString(), Shift.CA5.toString(), Shift.CA6.toString(), Shift.CA7.toString(), Shift.CA8.toString(), Shift.CA9.toString(), Shift.CA10.toString()};
            List<String> validFacilityChild = facilityChildCustomRepository.findAllDistinct();
            List<String> validSubject = subjectExcelCustomRepository.findAllSubject();
            CellRangeAddressList dataDate = new CellRangeAddressList(1, 500, 4, 4);
            CellRangeAddressList dataShift = new CellRangeAddressList(1, 500, 5, 5);
            CellRangeAddressList dataFacilityChild = new CellRangeAddressList(1, 500, 6, 6);
            CellRangeAddressList dataBlock = new CellRangeAddressList(1, 500, 1, 1);
            CellRangeAddressList dataSubject = new CellRangeAddressList(1, 500, 3, 3);

            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            DataValidationConstraint constraintBlock = validationHelper.createExplicitListConstraint(validBlock.toArray(new String[0]));
            DataValidationConstraint constraintShift = validationHelper.createExplicitListConstraint(validShift);
            DataValidationConstraint constraintFacilityChild = validationHelper.createExplicitListConstraint(validFacilityChild.toArray(new String[0]));
            DataValidationConstraint constraintDate = validationHelper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN, ""+ DateUtil.getExcelDate(sdf.parse("01/01/1900")), ""+DateUtil.getExcelDate(sdf.parse("31/12/9999")), "dd/MM/yyyy");
            DataValidationConstraint constraintSubject = validationHelper.createExplicitListConstraint(validSubject.toArray(new String[0]));

            DataValidation validationBlock = validationHelper.createValidation(constraintBlock, dataBlock);
            DataValidation validationShift = validationHelper.createValidation(constraintShift, dataShift);
            DataValidation validationFacilityChild = validationHelper.createValidation(constraintFacilityChild, dataFacilityChild);
            DataValidation validationSubject = validationHelper.createValidation(constraintSubject, dataSubject);
            DataValidation validationDate = validationHelper.createValidation(constraintDate, dataDate);

            validationBlock.setShowErrorBox(true);
            validationBlock.setSuppressDropDownArrow(true);
            validationBlock.createErrorBox("Sai dữ liệu", "Hãy chọn dữ liệu cho sẵn");
            validationBlock.createPromptBox("Chọn dữ liệu", "Hãy chọn dữ liệu cho sẵn");

            validationShift.setShowErrorBox(true);
            validationShift.setSuppressDropDownArrow(true);
            validationShift.createErrorBox("Sai dữ liệu", "Hãy chọn dữ liệu cho sẵn");
            validationShift.createPromptBox("Chọn dữ liệu", "Hãy chọn dữ liệu cho sẵn");

            validationFacilityChild.setShowErrorBox(true);
            validationFacilityChild.setSuppressDropDownArrow(true);
            validationFacilityChild.createErrorBox("Sai dữ liệu", "Hãy chọn dữ liệu cho sẵn");
            validationFacilityChild.createPromptBox("Chọn dữ liệu", "Hãy chọn dữ liệu cho sẵn");

            validationSubject.setShowErrorBox(true);
            validationSubject.setSuppressDropDownArrow(true);
            validationSubject.createErrorBox("Sai dữ liệu", "Hãy chọn dữ liệu cho sẵn");
            validationSubject.createPromptBox("Chọn dữ liệu", "Hãy chọn dữ liệu cho sẵn");

            validationDate.setShowErrorBox(true);
            validationDate.createErrorBox("Sai dữ liệu", "Hãy nhập đúng định dạng ngày (MM/dd/yyyy)");
            validationDate.createPromptBox("Nhập dữ liệu", "Hãy nhập ngày (MM/dd/yyyy)");


            sheet.addValidationData(validationBlock);
            sheet.addValidationData(validationShift);
            sheet.addValidationData(validationFacilityChild);
            sheet.addValidationData(validationSubject);
            sheet.addValidationData(validationDate);

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ResponseObject<>(new ByteArrayInputStream(outputStream.toByteArray()), HttpStatus.OK,"Download template successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
