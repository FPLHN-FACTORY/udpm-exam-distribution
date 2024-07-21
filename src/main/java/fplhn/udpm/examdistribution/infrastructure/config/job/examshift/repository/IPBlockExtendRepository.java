package fplhn.udpm.examdistribution.infrastructure.config.job.examshift.repository;

import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.infrastructure.constant.BlockName;
import fplhn.udpm.examdistribution.repository.BlockRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPBlockExtendRepository extends BlockRepository {

    List<Block> findByName(BlockName name);

}
