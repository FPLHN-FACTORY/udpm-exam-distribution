package fplhn.udpm.examdistribution.core.headoffice.block.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.block.model.request.CreateUpdateBlockRequest;
import jakarta.validation.Valid;

public interface BlockService {

    ResponseObject<?> getAllBlockBySemesterId(String semesterId);

    ResponseObject<?> createBlock(@Valid CreateUpdateBlockRequest createUpdateBlockRequest);

    ResponseObject<?> getDetailBlock(String blockId);

    ResponseObject<?> updateBlock(String blockId, @Valid CreateUpdateBlockRequest createUpdateBlockRequest);

    ResponseObject<?> changeStatusBlock(String blockId);

}
