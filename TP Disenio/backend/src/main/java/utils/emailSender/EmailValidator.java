package utils.emailSender;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class EmailValidator {
    private String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

    public boolean checkEmail(String email) {
        try {
            Pattern pattern = Pattern.compile(ePattern);
            Matcher matcher = pattern.matcher(email);
            // Using regex
            return matcher.matches();
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
}
