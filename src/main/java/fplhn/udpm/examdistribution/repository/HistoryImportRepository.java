package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.HistoryImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryImportRepository extends JpaRepository<HistoryImport, String> {

    List<HistoryImport> findAllByFacility_IdAndStaff_Id(String facilityId, String staffId);

}
