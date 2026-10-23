public class User {
    private final String username;
    private final String password;

    public User(String username, String password) throws Exception {
        // Check the maximum username length
        if (username.length() > 50) {
            throw new Exception(
                    "Username is too long, try something shorter"
            );
        }

        // Define the allowed email format
        String emailPattern =
                "[a-zA-Z0-9._%+\\-]+@" +
                        "[a-zA-Z0-9][a-zA-Z0-9.\\-]*" +
                        "\\.[a-zA-Z]{2,}";

        // Reject usernames that do not match the email format
        if (!username.matches(emailPattern)) {
            throw new Exception(
                    "Please enter a valid Email as username"
            );
        }

        // Check the minimum password length
        if (password.length() < 8) {
            throw new Exception(
                    "Your password is too short, add more characters"
            );
        }

        // Check the maximum password length
        if (password.length() > 12) {
            throw new Exception(
                    "Your password is too long, try a shorter one"
            );
        }

        // Define the allowed special characters
        String specialCharacters = "!@#$%^&*()-_=+";

        // Track the required character types
        boolean hasChar = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        // Check each password character
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') {
                hasChar = true;
            } else if (c >= '0' && c <= '9') {
                hasDigit = true;
            } else if (specialCharacters.indexOf(c) != -1) {
                hasSpecial = true;
            } else {
                // Reject characters outside the allowed set
                throw new Exception("Please enter a valid password");
            }
        }

        // Require at least one letter, digit, and special character
        if (!hasChar  || !hasDigit || !hasSpecial) {
            throw new Exception("Please enter a valid password");
        }

        // Store the values only after all checks pass
        this.username = username;
        this.password = password;
    }

    // Return the username for sorting
    public String getUsername() {
        return username;
    }

    // Return the username and password separated by a space
    @Override
    public String toString() {
        return username + " " + password;
    }
}