package fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.infrastructure.constant.SemesterName;

import java.io.ByteArrayInputStream;

public interface ExcelFileClassSubjectService {

    ResponseObject<ByteArrayInputStream> downloadTemplate(SemesterName semester, Integer year);

}
