package fplhn.udpm.examdistribution.core.teacher.trackhistory.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.trackhistory.repository.TTrackHistoryRepository;
import fplhn.udpm.examdistribution.core.teacher.trackhistory.service.TTrackHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TTrackHistoryServiceImpl implements TTrackHistoryService {

    private final TTrackHistoryRepository trackHistoryRepository;

    @Override
    public ResponseObject<?> getListStudentTrack() {
        return null;
    }
}
