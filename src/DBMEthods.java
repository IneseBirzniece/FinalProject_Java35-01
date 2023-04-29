package tasks;

import javax.swing.*;
import java.sql.*;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;



public class DBMEthods {

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
    public static void insertCustomer(Connection conn, Scanner scanner) throws SQLException {
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
    public static void insertTool(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter tool category");
        String newToolCategory = scanner.nextLine();

        String newToolID;
        while (true) {
            System.out.println("Enter tool ID, that contains 3-5 letters and 1-3 numbers");
            newToolID = scanner.nextLine().toUpperCase(Locale.ROOT).trim();

            if (!Pattern.matches("[A-Z]{3,5}[0-9]{1,3}", newToolID)) {
                System.out.println("Your inputted tool ID number is not valid, try again");
            } else if (toolIdExists(conn, newToolID)) {
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
    public static void deleteTool(Connection conn, Scanner scanner) throws SQLException{
        String id;
        while (true) {
            System.out.println("Enter tool ID you want to delete");
            id = scanner.nextLine().toUpperCase(Locale.ROOT).trim();
            if (!toolIdExists(conn, id)) {
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
    public static boolean toolIdExists(Connection conn, String id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tools WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        return count > 0;
    }

    // metode, lai izdzēstu Customer
    public static void deleteCustomer(Connection conn, Scanner scanner) throws SQLException{
        String personalIDNo;
        while (true) {
            System.out.println("Enter customer personal ID No. you want to delete");
            personalIDNo = scanner.nextLine().trim();
            if (!customerExistsInDB(conn, personalIDNo)) {
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
    public static void readTools(Connection conn) throws SQLException {

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
    public static void readCustomer(Connection conn) throws SQLException {

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

    public static boolean customerExistsInDB(Connection conn, String personalIDNo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE personalIDNo = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, personalIDNo);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        return count > 0;
    }



    public static void returnTool (Connection conn, int timeOfUse, String toolID ) throws SQLException {
        String sql = "UPDATE main SET untilService = untilService - ?, available = 1 WHERE toolID = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, timeOfUse);
        preparedStatement.setString(2, toolID);
        int resultSet = preparedStatement.executeUpdate();

        if (resultSet > 0) {
            System.out.println("return success");
        } else {
            System.out.println("smth went wrong");
        }

    }

    public static void popServiceWin (Connection conn, String newToolID) throws SQLException{
        String sql = "SELECT untilService FROM main WHERE toolID = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1,newToolID);

        ResultSet resultSet = preparedStatement.executeQuery();
        int hours;
        if (resultSet.next()){
            hours = resultSet.getInt("untilService");
            if (hours < 25) {
                System.out.println("Warning!!! Till service less than 24 hours");

            } else {
                System.out.println( "Till service " + hours + "hours. All ok");
            }
        }else {

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


            } else if (resultSet4.next()) {
                System.out.println("Handed out to:\n"
                        + resultSet4.getString(6) + ", " + resultSet4.getString(7) + ", " + resultSet4.getString(8));
            }
        } else {
            System.out.println("ID does not exist");
        }
    }

}
