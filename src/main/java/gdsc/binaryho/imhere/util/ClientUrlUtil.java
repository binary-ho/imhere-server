package gdsc.binaryho.imhere.util;

import static gdsc.binaryho.imhere.constant.UrlConstant.LOCAL_CLIENT_URL;
import static gdsc.binaryho.imhere.constant.UrlConstant.PROD_CLIENT_URL;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientUrlUtil {

    private final static String PROD = "prod";
    private final Environment environment;

    public String getClientUrl() {
        if (isProd()) {
            return PROD_CLIENT_URL;
        }
        return LOCAL_CLIENT_URL;
    }

    private boolean isProd() {
        return List.of(environment.getActiveProfiles())
            .contains(PROD);
    }
}
