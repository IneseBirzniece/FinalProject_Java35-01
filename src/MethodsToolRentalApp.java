package tasks;

import java.sql.*;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;


public class MethodsToolRentalApp {
    private String toolIDSc;

    public static void logIn() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your email address: ");
        String email = scanner.nextLine();
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        String domain2 = "group2.com";
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();
        int maxLength = 8;

        while ((domain != domain2) && (password.length() != maxLength)) {
            System.out.print("Enter your email address: ");
            email = scanner.nextLine();
            System.out.println("Enter your password: ");
            password = scanner.nextLine();
        }

        System.out.println("You are logged in");

    }

    // metode, ar kuru ievada jaunu klientu
    public static void insertCustomerInDb(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter customers name and surname");
        String newName = scanner.nextLine();

        String newPersonalIDNo;
        while (true) {
            System.out.println("Enter customers personal ID number");
            newPersonalIDNo = scanner.nextLine().trim();
            if (Pattern.matches("[0-9]{6}-[0-9]{5}", newPersonalIDNo)) {
                break; // Exit the loop if the input is valid
            } else {
                System.out.println("Your inputted personal ID number is not valid, try again");
            }
        }

        System.out.println("Enter customers phone number");
        String newPhoneNumber = scanner.nextLine();


        String sql = "INSERT INTO customers (name, personalIDNo, phoneNumber) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, newName);
        preparedStatement.setString(2, newPersonalIDNo);
        preparedStatement.setString(3, newPhoneNumber);

        int rowInserted = preparedStatement.executeUpdate();

        if (rowInserted > 0){
            System.out.println("A new customer was inserted successfully");
        }else {
            System.out.println("Something went wrong");
        }
    }

    // metode, ar kuru tiek ievadīts jauns instruments
    // !!! nestrādā pēdējais apgalvojums par priceDay
    public static void insertToolInDb(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter tool category");
        String newToolCategory = scanner.nextLine();

        String newToolID;
        while (true) {
            System.out.println("Enter tool ID, that contains 3-5 letters and 1-3 numbers");
            newToolID = scanner.nextLine().toUpperCase(Locale.ROOT).trim();

            if (!Pattern.matches("[A-Z]{2,5}[0-9]{1,3}", newToolID)) {
                System.out.println("Your inputted tool ID number is not valid, try again");
            } else if (checkIfToolIdExists(conn, newToolID)) {
                System.out.println("The inputted tool ID already exists, try again");
            } else {
                break;// Exit the loop if the input is valid
            }
        }

        System.out.println("Enter tool name");
        String newNToolName = scanner.nextLine();

        System.out.println("Enter tool main parameters");
        String newToolParameters = scanner.nextLine();

        System.out.println("Enter maximum usage hours");
        int newUsageHours = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter tool rental price per day");
        double newToolPrice = scanner.nextDouble();
        scanner.nextLine();

        String sql = "INSERT INTO tools (category, ID, name, specifications, serviceHours, priceDay) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, newToolCategory);
        preparedStatement.setString(2, newToolID);
        preparedStatement.setString(3, newNToolName);
        preparedStatement.setString(4, newToolParameters);
        preparedStatement.setInt(5, newUsageHours);
        preparedStatement.setDouble(6, newToolPrice);

        int rowInserted = preparedStatement.executeUpdate();

        if (rowInserted > 0){
            System.out.println("A new tool was inserted successfully");
        }else {
            System.out.println("Something went wrong");
        }
    }
    // metode, lai izdzēstu instrumentu
    public static void deleteToolFromDb(Connection conn, Scanner scanner) throws SQLException{
        String id;
        while (true) {
            System.out.println("Enter tool ID you want to delete");
            id = scanner.nextLine().toUpperCase(Locale.ROOT).trim();
            if (!checkIfToolIdExists(conn, id)) {
                System.out.println("Tool with ID " + id + " does not exist, try again");
            } else {
                break;
            }
        }

        String sql = "DELETE FROM tools WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, id);

        if (preparedStatement.executeUpdate() > 0){
            System.out.println("Tool was deleted successfully");
        }else{
            System.out.println("Something went wrong");
        }
    }

    // metode ar kuru pārbauda, vai ievadot jaunu instrumenta ID jau tāds neeksistē
    // kā arī ar šo pašu pārbauda, vai dzēšot instrumentu, vispār tāds eksistē
    public static boolean checkIfToolIdExists(Connection conn, String id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tools WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        return count > 0;
    }

    // metode, lai izdzēstu Customer
    public static void deleteCustomerFromDb(Connection conn, Scanner scanner) throws SQLException{
        String personalIDNo;
        while (true) {
            System.out.println("Enter customer personal ID No. you want to delete");
            personalIDNo = scanner.nextLine().trim();
            if (!checkIfCustomerIdExistsInDB(conn, personalIDNo)) {
                System.out.println("Customer with personal ID No. " + personalIDNo + " does not exist, try again");
            } else {
                break;
            }
        }

        String sql = "DELETE FROM customers WHERE personalIDNo = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, personalIDNo);

        if (preparedStatement.executeUpdate() > 0){
            System.out.println("Customer was deleted successfully");
        }else{
            System.out.println("Something went wrong");
        }
    }

    // metode ar kuru pārbauda vai Customer tabulā vispar eksistē personal ID No. kuru grib izdzēst

    // metode, lai parādītu instrumentu tabulu
    public static void readToolsFromDb(Connection conn) throws SQLException {

        String sql = "SELECT * FROM tools";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()){
            String category = resultSet.getString(1);
            String id = resultSet.getString(2);
            String name = resultSet.getString(3);
            String specifications = resultSet.getString(4);
            int serviceHours = resultSet.getInt(5);
            String priceDay = resultSet.getString(6);

            String output = "Tool : %s - %s - %s - %s - %s - %s";
            System.out.println(String.format(output, category, id, name, specifications, serviceHours, priceDay));
        }

    }
    public static void readCustomerFromDb(Connection conn) throws SQLException {

        String sql = "SELECT * FROM customers";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()){
            String name = resultSet.getString(1);
            String personalIDNo = resultSet.getString(2);
            String phoneNumber = resultSet.getString(3);

            String output = "Customer : %s - %s - %s";
            System.out.println(String.format(output, name, personalIDNo, phoneNumber));
        }

    }

    //metode, lai parādītu Main tabulu
    public static void readAvailableTools(Connection conn) throws SQLException {

        String sql = "SELECT * FROM main WHERE available = 1 || untilService >= 24";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            int nPk = resultSet.getInt(1);
            String toolID = resultSet.getString(2);
            String toolName = resultSet.getString(3);
            int available = resultSet.getInt(4);
            int untilService = resultSet.getInt(5);
            String rented = resultSet.getString(6);
            String rentedPIN= resultSet.getString(7);
            String contacts = resultSet.getString(8);


            String output = ": %s - %s - %s - %s - %s - %s - %s - %s";
            System.out.println(String.format(output, nPk, toolID, toolName, available, untilService, rented, rentedPIN, contacts));
        }
    }

    public static boolean checkIfCustomerIdExistsInDB(Connection conn, String personalIDNo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE personalIDNo = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, personalIDNo);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        return count > 0;
    }

    public static void returnTool(Connection conn, Scanner scanner ) throws SQLException {
        String newToolID;
        while (true) {
            System.out.println("Enter tool ID number");
            newToolID = scanner.nextLine().toUpperCase(Locale.ROOT).trim();

            if (!Pattern.matches("[A-Z]{2,5}[0-9]{1,3}", newToolID)) {
                System.out.println("Your inputted tool ID number is not valid, try again");
            } else if (!tasks.MethodsToolRentalApp.checkIfToolIdExists(conn, newToolID)) {
                System.out.println("The inputted tool ID don't exists, try again");
            } else {
                break;// Exit the loop if the input is valid
            }
        }

        // Pārbauda, vai instruments vispār ir ticis ievadīts main tabulā
        PreparedStatement everTaken = conn.prepareStatement("SELECT available FROM main WHERE toolID = ?");
        everTaken.setString(1, newToolID);
        ResultSet resultSet2 = everTaken.executeQuery();

        if (resultSet2.next()) {
            int available = resultSet2.getInt("available");
            if (available == 1) {
                System.out.println("Error! Tool has already been returned");
                return;
            }
        }
        System.out.println("Enter worked hours");
        int timeOfUse = scanner.nextInt();
        scanner.nextLine();

        String sql = "UPDATE main SET untilService = untilService - ?, available = 1 WHERE toolID = ? AND available = 0";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, timeOfUse);
        preparedStatement.setString(2, newToolID);
        int resultSet = preparedStatement.executeUpdate();

        if (resultSet > 0) {
            System.out.println("Return successful");
            hoursTillService(conn, newToolID, scanner);
        } else {
            System.out.println("Something went wrong");
        }

    }

    public static void hoursTillService(Connection conn, String newToolID, Scanner scanner) throws SQLException {
        String sql = "SELECT untilService FROM main WHERE toolID = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, newToolID);

        ResultSet resultSet = preparedStatement.executeQuery();
        int hours;
        if (resultSet.next()) {
            hours = resultSet.getInt("untilService");
            if (hours < 24) {
                System.out.println("Warning!!! Till service less than 24 hours.");
                System.out.println("Send to service now? y/n");
                char confirm = scanner.nextLine().charAt(0);

                while (confirm == 'y') {

                    PreparedStatement toolReset = conn.prepareStatement("UPDATE main SET untilService = 500 WHERE toolID = ?");
                    toolReset.setString(1, newToolID);
                    if (toolReset.executeUpdate() > 0) {
                        System.out.println("Service hours for the tool have been reset to 500");

                    } else {
                        System.out.println("Resetting service hours failed");
                    }break;
                }
            } else {
                System.out.println("Till service " + hours + " hours. All ok");
            }
        } else {

        }
    }

    public static double calculateRentPrice(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String sql = "SELECT priceDay FROM tools WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        System.out.println("Enter tool ID: ");
        String toolID = scanner.nextLine();

        preparedStatement.setString(1, toolID);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            double pricePerDay = resultSet.getDouble("priceDay");

            System.out.println("Enter days for rent: ");
            int numOfDays = scanner.nextInt();

// Pievienoju validāciju dienu skaitam, citādi, ja ievada ko nepareizu, cenu neaprēķina, parāda Tool not found, bet main tabulu updeito
            while (numOfDays < 1) {
                System.out.println("The inputted number of days is not valid, try again");
                System.out.println("Enter days for rent: ");
                numOfDays = scanner.nextInt();
            }
            double rentPrice = pricePerDay * numOfDays;
            System.out.println("Rent price is: " + rentPrice);
            return rentPrice;
        } else {
            System.out.println("Tool not found");
            return 0.0;
        }
    }

    // Agneses metode
    public static void toolIDSearch(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter tool ID:");
        String toolIDSc = scanner.nextLine().toUpperCase().trim();
        while ((!Pattern.matches("[A-Z]{2,5}[0-9]{1,3}", toolIDSc))) {
            System.out.println("Your inputted tool ID number is not valid, try again");
            System.out.println("Enter tool ID");
            toolIDSc = scanner.nextLine().toUpperCase().trim();
        }

        // Salīdzina SQL ar tool ID scanner ievadi. Ja ID atrasts, izdrukā id un name
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM tools WHERE id = ?");
        statement.setString(1, toolIDSc);
        ResultSet resultSet = statement.executeQuery();

        // Pārbauda, vai instruments vispār ir ticis ievadīts main tabulā
        PreparedStatement everTaken = conn.prepareStatement("SELECT available FROM main WHERE toolID = ?");
        everTaken.setString(1, toolIDSc);
        ResultSet resultSet2 = everTaken.executeQuery();

        // Ja instruments ir main tabulā, pārbauda, vai available nav 0
        PreparedStatement isNowAvailable = conn.prepareStatement("SELECT * FROM main WHERE available <> 0 and toolID = ?");
        isNowAvailable.setString(1, toolIDSc);
        ResultSet resultSet3 = isNowAvailable.executeQuery();

        // Pārbauda, vai un kas ir paņēmis instrumentu. Ja ir paņemts, norāda klienta datus
        PreparedStatement takenBy = conn.prepareStatement("SELECT * FROM main WHERE available = 0 AND toolID = ?");
        takenBy.setString(1, toolIDSc);
        ResultSet resultSet4 = takenBy.executeQuery();


        if (resultSet.next()) {
            System.out.println("ID: " + resultSet.getString(2) + "\t Name: " + resultSet.getString(3));
            if (!resultSet2.next() || resultSet3.next()) {
                System.out.println("Tool is available");
                updateMainDb(conn);
                // Pārbauda, vai instruments vispār ir ticis ievadīts main tabulā

            } else if (resultSet4.next()) {
                System.out.println("Handed out to:\n"
                        + resultSet4.getString(6) + ", " + resultSet4.getString(7) + ", " + resultSet4.getString(8));
            }
        } else {
            System.out.println("ID does not exist");
            toolIDSearch(conn);
        }
    }
    public static void updateMainDb(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Confirm tool ID");
        String toolIDSc = scanner.nextLine().toUpperCase().trim();
        while ((!Pattern.matches("[A-Z]{2,5}[0-9]{1,3}", toolIDSc))) {
            System.out.println("Your inputted tool ID number is not valid, try again");
            System.out.println("Confirm tool ID");
            toolIDSc = scanner.nextLine().toUpperCase().trim();
        }

        PreparedStatement statement = conn.prepareStatement("SELECT * FROM tools WHERE id = ?");
        statement.setString(1, toolIDSc);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            PreparedStatement updateTools = conn.prepareStatement("INSERT INTO main (toolID, toolName) SELECT id, name FROM tools WHERE id = ?");
            updateTools.setString(1, toolIDSc);
            updateTools.executeUpdate();
        }

// Jauna koda daļa: pārbauda, cik reižu instruments ierakstīts main tabulā kā pieejams
        PreparedStatement serviceCheck = conn.prepareStatement("SELECT * FROM main WHERE available = 1 AND toolID = ?");
        serviceCheck.setString(1, toolIDSc);
        ResultSet resultSet5 = serviceCheck.executeQuery();

        int count = 0;
        while (resultSet5.next()){
            count++;
        }

        // Ja tikai vienu reizi, Java paņem sākuma vērtību 500 un izmanto to turpmākiem aprēķiniem
        if(count == 1){
            PreparedStatement checkService = conn.prepareStatement("UPDATE main SET untilService = 500 WHERE toolID = ?");
            checkService.setString(1, toolIDSc);
            checkService.executeUpdate();
            // Ja vairākas reizes, Java paņem vērtību no iepriekšējās rindas, kas attiecas uz konkrēto toolID
            // Nav ideāli, bet strādā - katru reizi atjaunojas visa kolonna, nevar apskatīties, cik h katrs klients lietojis
            // Bet visu rēķina pareizi, arī pēc service reset atjaunojas uz 500 h

        } else if(count > 1){
            PreparedStatement isCheckService = conn.prepareStatement("UPDATE main\n" +
                    "SET untilService = (\n" +
                    "    SELECT untilService\n" +
                    "    FROM (\n" +
                    "        SELECT untilService\n" +
                    "        FROM main\n" +
                    "        WHERE nPk = (SELECT MAX(nPk) FROM main WHERE untilService IS NOT NULL AND toolID = ?)\n" +
                    "       ) AS subquery\n" +
                    ")\n" +
                    "WHERE toolID = ?");

            isCheckService.setString(1, toolIDSc);
            isCheckService.setString(2, toolIDSc);
            isCheckService.executeUpdate();
        }

        System.out.println("Enter customer ID:");
        String personalIDNo = scanner.nextLine().trim();

        PreparedStatement checkCustomer = conn.prepareStatement("SELECT * FROM customers WHERE personalIDNo = ?");
        checkCustomer.setString(1, personalIDNo);
        ResultSet customerResultSet = checkCustomer.executeQuery();

        if (customerResultSet.next()) {
            PreparedStatement updateMain = conn.prepareStatement("UPDATE main SET available = 0, rented = ?, rentedPIN = ?,  contacts = ? WHERE toolID = ?");
            updateMain.setString(1, customerResultSet.getString("name"));
            updateMain.setString(2, personalIDNo);
            updateMain.setString(3, customerResultSet.getString("phoneNumber"));
            updateMain.setString(4, toolIDSc);
            updateMain.executeUpdate();

            System.out.println("Main table updated successfully!");
            System.out.println("Lets calculate the price");
            calculateRentPrice(conn);
        } else {
            System.out.println("Customer not found!");
            System.out.println("Enter 1 to insert new customer, or 2 to try again to gain out tool:");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                insertCustomerInDb(conn, scanner);
            } else if (choice.equals("2")) {
                toolIDSearch(conn);
            } else {
                System.out.println("Invalid input. Returning to main menu...");

                // Šo tekstu parāda, bet neparāda pašu main menu
                //Arta - man parāda gan šo tekstu, gan main menu
                return;
            }


        }
    }


}
