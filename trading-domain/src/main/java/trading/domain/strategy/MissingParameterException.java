package trading.domain.strategy;

public class MissingParameterException extends RuntimeException {
    public MissingParameterException(String parameterName) {
        super(String.format("The parameter '%s' has not been specified.", parameterName));
    }
}
