package fplhn.udpm.examdistribution.utils;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.infrastructure.constant.PaginationConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Helper {

    public static String appendWildcard(String url) {
        return url + "/**";
    }

    public static Pageable createPageable(PageableRequest request, String defaultSortBy) {
        return PageRequest.of(
                request.getPage() - 1,
                request.getSize() == 0 ? PaginationConstant.DEFAULT_SIZE : request.getSize(),
                Sort.by(
                        (Sort.Direction.fromString(
                                request.getOrderBy()) == Sort.Direction.DESC ||
                         request.getOrderBy() == null
                        ) ? Sort.Direction.DESC : Sort.Direction.ASC,
                        (request.getSortBy() == null
                         || request.getSortBy().isEmpty()
                        ) ? defaultSortBy : request.getSortBy()
                ));
    }

    public static ResponseEntity<?> createResponseEntity(ResponseObject<?> responseObject) {
        return new ResponseEntity<>(responseObject, responseObject.getStatus());
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^0[0-9]{9,10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return !matcher.matches();
    }

    public static String generateCodeFromName(String name) {
        // Chuyển role name chuỗi thành chữ hoa
        String upperCaseString = name.toUpperCase();

        // Loại bỏ dấu
        String normalizedString = Normalizer.normalize(upperCaseString, Normalizer.Form.NFD);
        String withoutAccentString = normalizedString.replaceAll("\\p{M}", "");

        // Thay thế tất cả khoảng trắng liên tiếp bằng dấu gạch dưới
        return withoutAccentString.replaceAll("\\s+", "_");
    }

    public static String replaceManySpaceToOneSpace(String name) {
        // Thay thế tất cả khoảng trắng liên tiếp bằng dấu gạch dưới
        return name.replaceAll("\\s+", " ");
    }

    public static String replaceSpaceToEmpty(String name) {
        // Thay thế tất cả khoảng trắng liên tiếp bằng dấu gạch dưới
        return name.replaceAll("\\s+", "");
    }

    public static String extractPrefix(String input) {
        Pattern pattern = Pattern.compile("^[A-Za-z]+\\d+");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

}
