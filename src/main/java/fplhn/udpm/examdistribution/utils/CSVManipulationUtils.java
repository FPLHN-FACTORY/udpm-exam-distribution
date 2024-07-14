package fplhn.udpm.examdistribution.utils;

import fplhn.udpm.examdistribution.infrastructure.constant.ConfigurationsConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.infrastructure.exception.RestApiException;
import fplhn.udpm.examdistribution.infrastructure.log.LoggerObject;
import fplhn.udpm.examdistribution.infrastructure.projection.SimpleEntityProjection;
import fplhn.udpm.examdistribution.repository.FacilityRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Setter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CSVManipulationUtils {

    @Setter(onMethod_ = {@Autowired})
    private HttpSession session;

    @Setter(onMethod_ = {@Autowired})
    private HttpServletRequest httpServletRequest;

    @Setter(onMethod_ = {@Autowired})
    private HttpServletResponse httpServletResponse;

    @Setter(onMethod_ = {@Autowired})
    private FacilityRepository facilityRepository;

    private List<LoggerObject> parseCSV(String pathFile, boolean reverseOrder) {
        List<LoggerObject> list = new ArrayList<>();
        try (Reader reader = new FileReader(pathFile);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

            List<CSVRecord> csvRecords = csvParser.getRecords();
            if (reverseOrder) {
                Collections.reverse(csvRecords);
            }
            int count = reverseOrder ? 1 : csvRecords.size();
            for (CSVRecord record : csvRecords) {
                LoggerObject loggerObject = new LoggerObject();
                loggerObject.setMail(record.get(0));
                loggerObject.setIP(record.get(1));
                loggerObject.setCreateDate(record.get(2));
                loggerObject.setMethod(record.get(3));
                loggerObject.setContent(record.get(4));
                loggerObject.setAuthor(record.get(5));
                loggerObject.setStatus(record.get(6));
                loggerObject.setStt(reverseOrder ? count++ : count--);
                list.add(loggerObject);
            }
        } catch (IOException e) {
            throw new RestApiException("File log không tồn tại");
        }
        return list;
    }

    public List<LoggerObject> readFileCSV(LoggerObject object) {
        return parseCSV(object.getPathFile(), true);
    }

    public List<LoggerObject> readFileCSVLuongChuNhiem(LoggerObject object, List<String> listSubject) {
        return parseCSV(object.getPathFile(), true).stream()
                .filter(loggerObject -> listSubject == null || listSubject.contains(loggerObject.getStatus()))
                .collect(Collectors.toList());
    }

    private void writeCSV(String pathFile, List<List<String>> records) {
        createFile(pathFile);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathFile, true));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            for (List<String> record : records) {
                csvPrinter.printRecord(record);
            }
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    public void writerFileCSV(LoggerObject loggerObject) {
        List<String> record = List.of(
                loggerObject.getMail(),
                loggerObject.getIP(),
                loggerObject.getCreateDate(),
                loggerObject.getMethod(),
                loggerObject.getContent(),
                loggerObject.getAuthor(),
                loggerObject.getStatus()
        );
        writeCSV(loggerObject.getPathFile(), List.of(record));
    }

    public void writerAllObjectFileCSV(List<LoggerObject> loggerObjects, String pathFile) {
        List<List<String>> records = loggerObjects.stream().map(loggerObject -> List.of(
                session.getAttribute(SessionConstant.CURRENT_USER_EMAIL) == null ? "" : session.getAttribute(SessionConstant.CURRENT_USER_EMAIL).toString(),
                httpServletRequest.getRemoteAddr(),
                new Date().toString(),
                httpServletRequest.getMethod(),
                loggerObject.getContent(),
                switchAuthor(httpServletRequest.getRequestURI()),
                loggerObject.getStatus()
        )).collect(Collectors.toList());
        writeCSV(pathFile, records);
    }

    public LoggerObject writerLoggerObjectIsNotData(LoggerObject loggerObject) {
        if (loggerObject.getMail() == null) {
            loggerObject.setMail(ConfigurationsConstant.KHONG_CO_DU_LIEU);
        }
        if (loggerObject.getIP() == null) {
            loggerObject.setIP(ConfigurationsConstant.KHONG_CO_DU_LIEU);
        }
        if (loggerObject.getCreateDate() == null) {
            loggerObject.setCreateDate(ConfigurationsConstant.KHONG_CO_DU_LIEU);
        }
        if (loggerObject.getMethod() == null) {
            loggerObject.setMethod(ConfigurationsConstant.KHONG_CO_DU_LIEU);
        }
        if (loggerObject.getContent() == null) {
            loggerObject.setContent(ConfigurationsConstant.KHONG_CO_DU_LIEU);
        }
        if (loggerObject.getAuthor() == null) {
            loggerObject.setAuthor(ConfigurationsConstant.KHONG_CO_DU_LIEU);
        }
        return loggerObject;
    }

    public void createFileCSV(String pathFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathFile, true));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    public String getPathFile500() {
        return getPropertiesRead(ConfigurationsConstant.PATH_FILE_TEMPLATE) + "file_bug.csv";
    }

    public String getPathFileBadRequest() {
        return getPropertiesRead(ConfigurationsConstant.PATH_FILE_TEMPLATE) + "log_bug_bad_request.csv";
    }

    public LoggerObject createLoggerObject(String content, String pathFile, String status) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDate = simpleDateFormat.format(new Date());

        LoggerObject loggerObject = new LoggerObject();
        loggerObject.setPathFile(pathFile);
        loggerObject.setContent(content);
        loggerObject.setStatus(status);
        loggerObject.setIP(httpServletRequest.getRemoteAddr());
        loggerObject.setMail(session.getAttribute(SessionConstant.CURRENT_USER_EMAIL) == null ? "" : session.getAttribute(SessionConstant.CURRENT_USER_EMAIL).toString());
        loggerObject.setCreateDate(formattedDate);
        loggerObject.setMethod(httpServletRequest.getMethod());
        loggerObject.setAuthor(switchAuthor(httpServletRequest.getRequestURI()));
        return loggerObject;
    }

    public String switchAuthor(String api) {
        if (api.startsWith(MappingConstants.API_TEACHER_PREFIX)) return "Giảng viên";
        if (api.startsWith(MappingConstants.API_HEAD_DEPARTMENT_PREFIX)) return "Chủ nhiệm bộ môn";
        if (api.startsWith(MappingConstants.API_HEAD_OFFICE_PREFIX)) return "Ban đào tạo";
        if (api.startsWith(MappingConstants.API_HEAD_SUBJECT_PREFIX)) return "Trưởng môn";
        return "";
    }

    public String convertFileName(String fileOld) {
        String withoutDiacritics = removeDiacritics(fileOld).toLowerCase();
        return withoutDiacritics.replaceAll("[^a-zA-Z0-9]", "_").replace(" ", "_");
    }

    public static String removeDiacritics(String text) {
        return text.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]", "A")
                .replaceAll("[ÈÉẸẺẼÊỀẾỆỂỄ]", "E")
                .replaceAll("[ÌÍỊỈĨ]", "I")
                .replaceAll("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]", "O")
                .replaceAll("[ÙÚỤỦŨƯỪỨỰỬỮ]", "U")
                .replaceAll("[ỲÝỴỶỸ]", "Y")
                .replaceAll("[Đ]", "D");
    }

    public String getSwitchFacility() {
        List<SimpleEntityProjection> facilities = (List<SimpleEntityProjection>) session.getAttribute("listCoSo");
        if (facilities == null) {
            facilities = facilityRepository.findAllSimpleEntity();
        }

        String facilityId = (String) session.getAttribute(SessionConstant.CURRENT_USER_FACILITY_ID);
        if (facilityId == null) {
            throw new IllegalStateException("Facility ID không tồn tại trong session");
        }

        String facility = facilities.stream()
                .filter(el -> el.getId().equals(facilityId))
                .map(SimpleEntityProjection::getName)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy cơ sở với ID: " + facilityId));

        return getPropertiesRead(ConfigurationsConstant.PATH_FILE_TEMPLATE) + convertFileName(facility) + "/";
    }

    public String getPropertiesRead(String constant) {
        PropertiesReader po = new PropertiesReader();
        String property = po.getPropertyConfig(constant);
        if (property == null) {
            throw new IllegalStateException("Property không tồn tại cho constant: " + constant);
        }
        return property;
    }

    public boolean createFolder(String folderPath) {
        Path path = Paths.get(folderPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("Folder created at path: " + path);
                return true;
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }
        return false;
    }

    public void createFile(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent()); // Create parent directories if not exists
                Files.createFile(path); // Create the file
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }
    }

    public boolean updateFolder(String oldFolderPath, String newFolderPath) {
        File oldFolder = new File(oldFolderPath);
        File newFolder = new File(newFolderPath);
        if (oldFolder.exists() && !newFolder.exists()) {
            return oldFolder.renameTo(newFolder);
        }
        return false;
    }

    public void genFolderLogger() {
        String pathFile = getPropertiesRead(ConfigurationsConstant.PATH_FILE_TEMPLATE);
        if (createFolder(pathFile)) {
            createFolder(pathFile + getPropertiesRead(ConfigurationsConstant.FOLDER_ACTOR_HEAD_DEPARTMENT));
            createFolder(pathFile + getPropertiesRead(ConfigurationsConstant.FOLDER_ACTOR_HEAD_SUBJECT));
            createFolder(pathFile + getPropertiesRead(ConfigurationsConstant.FOLDER_ACTOR_HEAD_OFFICE));
            createFolder(pathFile + getPropertiesRead(ConfigurationsConstant.FOLDER_ACTOR_HEAD_TEACHER));
        }
    }

}
