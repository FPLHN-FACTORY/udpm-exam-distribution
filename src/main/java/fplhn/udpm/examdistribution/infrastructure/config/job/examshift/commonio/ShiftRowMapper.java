package fplhn.udpm.examdistribution.infrastructure.config.job.examshift.commonio;

import fplhn.udpm.examdistribution.infrastructure.config.job.examshift.model.ImportShiftRequest;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;

public class ShiftRowMapper implements RowMapper<ImportShiftRequest> {

    @Override
    public ImportShiftRequest mapRow(RowSet rowSet) throws Exception {
        try {
            ImportShiftRequest importShiftRequest = new ImportShiftRequest();
            importShiftRequest.setOrderNumber(rowSet.getCurrentRowIndex());
            importShiftRequest.setBuildingLetter(rowSet.getColumnValue(1));
            importShiftRequest.setStartDate(rowSet.getColumnValue(2));
            importShiftRequest.setShift(rowSet.getColumnValue(3));
            importShiftRequest.setRoomInfo(rowSet.getColumnValue(4));
            importShiftRequest.setSubjectName(rowSet.getColumnValue(5));
            importShiftRequest.setSubjectCode(rowSet.getColumnValue(6));
            importShiftRequest.setLastTimeLearnOfSubject(rowSet.getColumnValue(7));
            importShiftRequest.setClassSubjectCode(rowSet.getColumnValue(8));
            importShiftRequest.setTeacherOfSubject(rowSet.getColumnValue(9));
            importShiftRequest.setFirstSupervisorCode(rowSet.getColumnValue(10));
            importShiftRequest.setSecondSupervisorCode(rowSet.getColumnValue(11));
            importShiftRequest.setMajorCode(rowSet.getColumnValue(12));
            importShiftRequest.setDuplicate(rowSet.getColumnValue(13));
            importShiftRequest.setTotalStudent(rowSet.getColumnValue(14));
            importShiftRequest.setBlock(rowSet.getColumnValue(15));
            importShiftRequest.setNote(rowSet.getColumnValue(16));
            importShiftRequest.setCampusCode(rowSet.getColumnValue(17));
            return importShiftRequest;
        } catch (Exception e) {
            return null;
        }
    }

}
