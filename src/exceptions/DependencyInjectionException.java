package exceptions;

public class DependencyInjectionException extends Exception {
    public DependencyInjectionException(String reason) {
        super("Dependency injection exception: " + reason);
    }
}
