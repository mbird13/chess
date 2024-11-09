package servicehelpers;

public record LoginResult(String username, String authToken, String exceptionMessage) {
}
