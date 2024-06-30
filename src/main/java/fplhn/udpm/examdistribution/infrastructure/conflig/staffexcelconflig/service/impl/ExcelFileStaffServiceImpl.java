package fplhn.udpm.examdistribution.infrastructure.conflig.staffexcelconflig.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.role.repository.HORoleFacilityRepository;
import fplhn.udpm.examdistribution.entity.Facility;
import fplhn.udpm.examdistribution.infrastructure.conflig.staffexcelconflig.repository.DepartmentFacilityCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.conflig.staffexcelconflig.repository.RoleCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.conflig.staffexcelconflig.service.ExcelFileStaffService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class ExcelFileStaffServiceImpl implements ExcelFileStaffService {

    private final HORoleFacilityRepository facilityRepo;

    private final DepartmentFacilityCustomRepository departmentFacilityRepository;

    private final RoleCustomRepository roleRepository;

    public ExcelFileStaffServiceImpl(HORoleFacilityRepository facilityRepo,
                                     DepartmentFacilityCustomRepository departmentFacilityRepository,
                                     RoleCustomRepository roleRepository) {
        this.facilityRepo = facilityRepo;
        this.departmentFacilityRepository = departmentFacilityRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public ResponseObject<ByteArrayInputStream> downloadTemplate() {
        //fix cứng id cơ sở , sau khi có đăng nhập sẽ sửa lại
        List<Facility> facilities = facilityRepo.findAll();
        String idFacility = "";
        if (!facilities.isEmpty()) {
            idFacility=facilities.get(0).getId();
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Template Thông Tin Nhân Viên");

            Row row = sheet.createRow(0);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setLocked(true);
            headerCellStyle.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setWrapText(true);

            Cell cell = row.createCell(0);
            cell.setCellValue("STT");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue("Mã Nhân Viên");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(2);
            cell.setCellValue("Họ và Tên");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(3);
            cell.setCellValue("Email FPT");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(4);
            cell.setCellValue("Email FE");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(5);
            cell.setCellValue("Bộ Môn");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(6);
            cell.setCellValue("Chức vụ");
            cell.setCellStyle(headerCellStyle);

            List<String> validDepartmentFacility = departmentFacilityRepository.findAllByIdFacility(idFacility);
            List<String> validRole = roleRepository.findAllByFacilityId(idFacility);

            CellRangeAddressList dataDepartmentFacility = new CellRangeAddressList(1, 1000, 5, 5);
            CellRangeAddressList dataRole = new CellRangeAddressList(1, 1000, 6, 6);

            DataValidationHelper validationHelper = sheet.getDataValidationHelper();

            DataValidationConstraint constraintDepartmentFacility = validationHelper.createExplicitListConstraint(validDepartmentFacility.toArray(new String[0]));
            DataValidationConstraint constraintRole = validationHelper.createExplicitListConstraint(validRole.toArray(new String[0]));

            DataValidation dataValidationDepartmentFacility = validationHelper.createValidation(constraintDepartmentFacility, dataDepartmentFacility);
            DataValidation dataValidationRole = validationHelper.createValidation(constraintRole, dataRole);

            dataValidationDepartmentFacility.setShowErrorBox(true);
            dataValidationDepartmentFacility.setSuppressDropDownArrow(true);
            dataValidationDepartmentFacility.createErrorBox("Sai Dữ Liệu", "Hãy Chọn Dữ Liệu Cho Sẵn");
            dataValidationDepartmentFacility.createPromptBox("Chọn Dữ Liệu", "Chọn Dữ Liệu Cho Sẵn");

            dataValidationRole.setShowErrorBox(true);
            dataValidationRole.setSuppressDropDownArrow(true);
            dataValidationRole.createErrorBox("Sai Dữ Liệu", "Hãy Chọn Dữ Liệu Cho Sẵn");
            dataValidationRole.createPromptBox("Chọn Dữ Liệu", "Chọn Dữ Liệu Cho Sẵn");

            sheet.addValidationData(dataValidationDepartmentFacility);
            sheet.addValidationData(dataValidationRole);

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ResponseObject<>(new ByteArrayInputStream(outputStream.toByteArray()), HttpStatus.OK, "download template successfully");
        } catch (IOException ex) {
            log.error("Error during export Excel file", ex);
            return null;
        }
    }

}
