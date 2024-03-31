package gdsc.binaryho.imhere;

import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FailTest {

    @Test
    void Test_Fail_Report_Upload_확인을_위한항상_실패하는_테스트() {
        fail();
    }
}
