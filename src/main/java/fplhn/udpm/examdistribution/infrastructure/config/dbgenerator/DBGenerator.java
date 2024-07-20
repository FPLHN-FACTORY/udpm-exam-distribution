//package fplhn.udpm.examdistribution.infrastructure.config.dbgenerator;
//
//import fplhn.udpm.examdistribution.entity.Block;
//import fplhn.udpm.examdistribution.entity.Facility;
//import fplhn.udpm.examdistribution.entity.Role;
//import fplhn.udpm.examdistribution.entity.Semester;
//import fplhn.udpm.examdistribution.entity.Staff;
//import fplhn.udpm.examdistribution.entity.StaffRole;
//import fplhn.udpm.examdistribution.infrastructure.config.dbgenerator.repository.DBGenBlockRepository;
//import fplhn.udpm.examdistribution.infrastructure.config.dbgenerator.repository.DBGenFacilityRepository;
//import fplhn.udpm.examdistribution.infrastructure.config.dbgenerator.repository.DBGenRoleRepository;
//import fplhn.udpm.examdistribution.infrastructure.config.dbgenerator.repository.DBGenSemesterRepository;
//import fplhn.udpm.examdistribution.infrastructure.config.dbgenerator.repository.DBGenStaffRepository;
//import fplhn.udpm.examdistribution.infrastructure.config.dbgenerator.repository.DBGenStaffRoleRepository;
//import fplhn.udpm.examdistribution.infrastructure.constant.BlockName;
//import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
//import fplhn.udpm.examdistribution.infrastructure.constant.SemesterName;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class DBGenerator {
//
//    private final DBGenRoleRepository roleRepository;
//
//    private final DBGenStaffRepository staffRepository;
//
//    private final DBGenStaffRoleRepository staffRoleRepository;
//
//    private final DBGenFacilityRepository facilityRepository;
//
//    private final DBGenSemesterRepository semesterRepository;
//
//    private final DBGenBlockRepository blockRepository;
//
//    @PostConstruct
//    private void generateData() {
//        Semester semester = new Semester();
//        semester.setSemesterName(SemesterName.SPRING);
//        semester.setYear(2024);
//        semester.setStartTime(1720224000000L);
//        semester.setStatus(EntityStatus.ACTIVE);
//        Semester semesterSaved = semesterRepository.save(semester);
//
//        Block block1 = new Block();
//        block1.setName(BlockName.BLOCK_1);
//        block1.setStartTime(1720224000000L);
//        block1.setEndTime(1725580800000L);
//        block1.setSemester(semesterSaved);
//        block1.setStatus(EntityStatus.ACTIVE);
//        blockRepository.save(block1);
//
//        Block block2 = new Block();
//        block2.setName(BlockName.BLOCK_2);
//        block2.setStartTime(1725580800000L);
//        block2.setEndTime(1730937600000L);
//        block2.setSemester(semesterSaved);
//        block2.setStatus(EntityStatus.ACTIVE);
//        blockRepository.save(block2);
//
//        Facility facility = new Facility();
//        facility.setName("Hà Nội");
//        facility.setStatus(EntityStatus.ACTIVE);
//        Facility facilitySaved = facilityRepository.save(facility);
//
//        Role admin = new Role();
//        admin.setCode(fplhn.udpm.examdistribution.infrastructure.constant.Role.BAN_DAO_TAO.name());
//        admin.setName("Ban Đào Tạo");
//        admin.setFacility(facilitySaved);
//        admin.setStatus(EntityStatus.ACTIVE);
//        Role roleSaved = roleRepository.save(admin);
//
//        Staff staff = new Staff();
//        staff.setName("Nguyễn Vĩ Mạnh");
//        staff.setStaffCode("PH43516");
//        staff.setAccountFe("manhnvph43516@fe.edu.vn");
//        staff.setAccountFpt("manhnvph43516@fpt.edu.vn");
//        staff.setPicture("https://lh3.googleusercontent.com/a/ACg8ocLzcrXLsC4uLq_zOcza1zxE-P2mawteXAPALCZ_4X1IFprBrtw=s96-c");
//        staff.setStatus(EntityStatus.ACTIVE);
//        Staff staffSaved = staffRepository.save(staff);
//
//        StaffRole staffRole = new StaffRole();
//        staffRole.setRole(roleSaved);
//        staffRole.setStaff(staffSaved);
//        staffRole.setStatus(EntityStatus.ACTIVE);
//        staffRoleRepository.save(staffRole);
//    }
//}
