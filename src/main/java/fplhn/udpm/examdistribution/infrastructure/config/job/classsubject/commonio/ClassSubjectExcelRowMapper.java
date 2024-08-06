package fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.commonio;

import fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.model.ClassSubjectExcelRequest;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;

public class ClassSubjectExcelRowMapper implements RowMapper<ClassSubjectExcelRequest> {

    @Override
    public ClassSubjectExcelRequest mapRow(RowSet rowSet) throws Exception {
        try {
            ClassSubjectExcelRequest lopMonExcelRequest = new ClassSubjectExcelRequest();
            lopMonExcelRequest.setOrderNumber(rowSet.getCurrentRowIndex());
            lopMonExcelRequest.setBlockName(rowSet.getColumnValue(1));
            lopMonExcelRequest.setClassCode(rowSet.getColumnValue(2));
            lopMonExcelRequest.setSubjectCode(rowSet.getColumnValue(3));
            lopMonExcelRequest.setDate(rowSet.getColumnValue(4));
            lopMonExcelRequest.setShift(String.valueOf(rowSet.getColumnValue(5)));
            lopMonExcelRequest.setFacilityChildName(rowSet.getColumnValue(6));
            lopMonExcelRequest.setStaffCode(rowSet.getColumnValue(7));
            return lopMonExcelRequest;
        } catch (Exception e) {
            return null;
        }
    }

}
