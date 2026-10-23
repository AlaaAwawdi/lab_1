import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class UsersApp {
    public static void main(String[] args) {
        // Define users list and the input file
        ArrayList<User> users = new ArrayList<>();
        File InFile = new File("users.txt");

        try (Scanner reader = new Scanner(InFile)) {

            // Read each line and separate the username and password
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] parts = line.trim().split(" +");

                try {
                    // The constructor checks the username and password
                    User user = new User(parts[0], parts[1]);
                    users.add(user);

                } catch (Exception e) {
                    System.out.println(line + " -> " + e.getMessage());
                }
            }

            // Sort valid users alphabetically by username
            users.sort(Comparator.comparing(User::getUsername));

            // Write valid users to the output file
            File OutFile = new File("out1.txt");
            try (PrintWriter writer = new PrintWriter(OutFile)) {
                for (User user : users) {
                    writer.println(user);
                }
            }

        }
        catch (FileNotFoundException e) {
            System.out.println(
                    "Could not open the input or output file: " + e.getMessage());
        }
    }
}