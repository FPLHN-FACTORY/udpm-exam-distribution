package fplhn.udpm.examdistribution.infrastructure.conflig.staffexcelconflig.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;

import java.io.ByteArrayInputStream;

public interface ExcelFileStaffService {

    ResponseObject<ByteArrayInputStream> downloadTemplate();


}
