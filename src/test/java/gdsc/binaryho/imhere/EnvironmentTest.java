package gdsc.binaryho.imhere;

import java.util.Objects;
import org.junit.jupiter.api.Test;

//@SpringBootTest
public class EnvironmentTest {

    @Test
    void gmailRegexTest() {
        final String GMAIL_REGEX = "^[a-zA-Z0-9]+@gmail\\.com$";
        System.out.println("abc@adfda.com".matches(GMAIL_REGEX));
        System.out.println("abc1@gmail.com".matches(GMAIL_REGEX));
        System.out.println("gdscimhere@gmail.com".matches(GMAIL_REGEX));
        System.out.println("gdscimhere@gmail.com.c".matches(GMAIL_REGEX));
        System.out.println("abcdfghbsxc42432@fgmil.com".matches(GMAIL_REGEX));
    }


    @Test
    void regexTest() {
//        final String PASSWORD_REGEX = "^[a-zA-Z0-9]+@{1}gmail\.com$";
//        System.out.println("abc".matches(PASSWORD_REGEX));
//        System.out.println("abc1".matches(PASSWORD_REGEX));
//        System.out.println("abcdfghbsxc42432adgadgadgdgadagdagdagdgadag".matches(PASSWORD_REGEX));
//        System.out.println("1435134134".matches(PASSWORD_REGEX));
//
//        System.out.println("abcdfghbsxc42432".matches(PASSWORD_REGEX));
    }

    @Test
    void nullObjectEqualsTest() {
        String verificationCode = "1";
        int test = 1;
        System.out.println(Objects.equals(null, null));
        System.out.println(Objects.equals(test, verificationCode));
    }

//    @Test
//    void 테스트() {
//        String str = "1234";
//        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//        List<String> passwords = new ArrayList<>();
//        for (int i = 0; i < 6; i++) {
//            passwords.add(bCryptPasswordEncoder.encode(str));
//        }
//
//        int cnt = 1;
//        for (String password1 : passwords) {
//            for (String password2 : passwords) {
//                if (password1.equals(password2)) {
//                    continue;
//                }
//                System.out.println((cnt++) + "회차 " + password1 + ", " + password2 + " : " + bCryptPasswordEncoder.matches(str, password2));
//            }
//        }
//    }
}
