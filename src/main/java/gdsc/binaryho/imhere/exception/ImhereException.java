package gdsc.binaryho.imhere.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImhereException extends RuntimeException {

    private ErrorInfo errorInfo;
}
