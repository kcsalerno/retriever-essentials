package re.api.domain;

import java.util.regex.Pattern;

public class Validations {
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[\\w-\\.]+@[\\w-]+\\.[a-z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );

    public static boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    public static boolean isValidUrl(String value) {
        return URL_PATTERN.matcher(value).matches();
    }

    public static boolean isValidEmail(String value) {
        return EMAIL_PATTERN.matcher(value).matches();
    }
}