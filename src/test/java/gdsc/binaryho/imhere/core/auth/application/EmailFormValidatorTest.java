package gdsc.binaryho.imhere.core.auth.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gdsc.binaryho.imhere.core.auth.exception.EmailFormatMismatchException;
import gdsc.binaryho.imhere.core.auth.util.EmailFormValidator;
import gdsc.binaryho.imhere.mock.TestContainer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class EmailFormValidatorTest {

    EmailFormValidator emailFormValidator = TestContainer
        .builder()
        .build()
        .emailFormValidator;

    @ParameterizedTest
    @ValueSource(strings = {"naver.com", "mail.hongik.ac.kr", "g.hongik.ac.k", "gmail.net"})
    void 홍익대학교_이메일이나_구글_이메일이_아닌_경우_예외가_발생한다(String postfix) {
        // given
        // when
        // then
        assertThatThrownBy(() ->
            emailFormValidator.validateEmailForm("test@" + postfix))
            .isInstanceOf(EmailFormatMismatchException.class);
    }
}
