package fplhn.udpm.examdistribution.infrastructure.config.job.staff.commonio;

import fplhn.udpm.examdistribution.infrastructure.config.job.staff.model.request.StaffExcelRequest;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;

public class StaffRowMapper implements RowMapper<StaffExcelRequest> {

    @Override
    public StaffExcelRequest mapRow(RowSet rowSet) {
        try {
            StaffExcelRequest importExcelRequest = new StaffExcelRequest();
            importExcelRequest.setOrderNumber(rowSet.getCurrentRowIndex());
            importExcelRequest.setStaffCode(rowSet.getColumnValue(1));
            importExcelRequest.setName(rowSet.getColumnValue(2));
            importExcelRequest.setAccountFpt(rowSet.getColumnValue(3));
            importExcelRequest.setAccountFe(rowSet.getColumnValue(4));
            importExcelRequest.setDepartmentFacilityName(rowSet.getColumnValue(5));
            importExcelRequest.setRoleFacilityName(rowSet.getColumnValue(6));
            return importExcelRequest;
        } catch (Exception e) {
            return null;
        }
    }
}
