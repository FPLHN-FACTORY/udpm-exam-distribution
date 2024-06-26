package fplhn.udpm.examdistribution.core.headoffice.block.controller;

import fplhn.udpm.examdistribution.core.headoffice.block.model.request.CreateUpdateBlockRequest;
import fplhn.udpm.examdistribution.core.headoffice.block.service.BlockService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_HEAD_OFFICE_BLOCK)
public class BlockRestController {

    private final BlockService blockService;

    @GetMapping("/{semesterId}")
    public ResponseEntity<?> getAllBlockBySemesterId(@PathVariable String semesterId) {
        return Helper.createResponseEntity(blockService.getAllBlockBySemesterId(semesterId));
    }

    @PostMapping
    public ResponseEntity<?> createBlock(@RequestBody CreateUpdateBlockRequest createUpdateBlockRequest) {
        return Helper.createResponseEntity(blockService.createBlock(createUpdateBlockRequest));
    }

    @GetMapping("/get/{blockId}")
    public ResponseEntity<?> getDetailBlock(@PathVariable String blockId) {
        return Helper.createResponseEntity(blockService.getDetailBlock(blockId));
    }

    @PutMapping("/{blockId}")
    public ResponseEntity<?> updateBlock(@PathVariable String blockId, @RequestBody CreateUpdateBlockRequest createUpdateBlockRequest) {
        return Helper.createResponseEntity(blockService.updateBlock(blockId, createUpdateBlockRequest));
    }

    @PutMapping("/status/{blockId}")
    public ResponseEntity<?> changeStatusBlock(@PathVariable String blockId) {
        return Helper.createResponseEntity(blockService.changeStatusBlock(blockId));
    }

}
