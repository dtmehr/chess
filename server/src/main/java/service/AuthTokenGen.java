package service;
import java.util.UUID;

public class AuthTokenGen {
    public static String genAuthToken() {
        return UUID.randomUUID().toString();
    }
}
