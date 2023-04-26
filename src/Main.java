import java.sql.*;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) {
        String dbURL = "jdbc:mysql://localhost:3306/java35";
        String username = "root";
        String password = "";
        Scanner scanner = new Scanner(System.in);
        char again = 'y';

        try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
            // Test: to be commented out later
            System.out.println("Connected to database!");

            while (again == 'y') {

                // šis ir pagaidām, lai varētu pārbaudīt vai lietas strādā
                System.out.println("Choose one option (a,b,c,d,e,f)");
                System.out.println("a - reading tools");
                System.out.println("b - reading available tools and tools with hours till service <=24");
                System.out.println("c - inserting tools");
                System.out.println("d - inserting customers");
                System.out.println("e - deleting tools");
                System.out.println("f - deleting customer");
                System.out.println("g - reading customer");
                System.out.println("h - checking if Customer exists in DB"); // būs jādzēša ārā, jo veidoku, lai tikai pārbaudotu vai darbojas

                char action = scanner.nextLine().charAt(0);

                if (action == 'd') {

                    insertCustomer(conn, scanner);

                } else if (action == 'c') {

                    insertTool(conn, scanner);

                } else if (action == 'e') {
                    while (true) {
                        System.out.println("Enter tool ID you want to delete");
                        String id = scanner.nextLine().toUpperCase(Locale.ROOT).trim();
                        if (!toolIdExists(conn, id)) {
                            System.out.println("Tool with ID " + id + " does not exist, try again");
                        } else {
                            deleteTool(conn, id);
                            break;
                        }
                    }

                } else if (action == 'f') {
                    while (true) {
                        System.out.println("Enter customer personal ID No. you want to delete");
                        String personalIDNo = scanner.nextLine().trim();
                        if (!personalIdExists(conn, personalIDNo)) {
                            System.out.println("Customer with personal ID No. " + personalIDNo + " does not exist, try again");
                        } else {
                            deleteUser(conn, personalIDNo);
                            break;
                        }
                    }

                } else if (action == 'a') {
                    readTools(conn);

                } else if (action == 'b') {
                    readAvailableTools(conn);

                } else if (action == 'g') {
                    readCustomer(conn);

                    // šis būs jāliek citā vietā, if not present in SQL Insert customer
                }else if (action == 'h') {

                    System.out.println("Enter customers personal ID No.:");
                    String customerIDNo = scanner.nextLine();
                    if (!customerExistsInDB(conn, customerIDNo)) {
                        System.out.println("Customer with personal ID No." + customerIDNo + " don't exists, please input it in DB");
                        insertCustomer(conn, scanner);
                    } else {
                        // dati par Customer ielasās DB main
                    }
                }

                System.out.println("Do you want to do something more? y/n");
                again = scanner.nextLine().charAt(0);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

        /* To be inserted in methods:
        <<accessmodifier methodname>> (Connection conn) throws SQLException{
        String sql = "SELECT * FROM <<table customers, tools or main>>";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            }
         */

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
        String newToolPrice = scanner.nextLine();

        String sql = "INSERT INTO tools (category, ID, name, specifications, serviceHours, priceDay) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, newToolCategory);
        preparedStatement.setString(2, newToolID);
        preparedStatement.setString(3, newNToolName);
        preparedStatement.setString(4, newToolParameters);
        preparedStatement.setInt(5, newUsageHours);
        preparedStatement.setString(6, newToolPrice);

        int rowInserted = preparedStatement.executeUpdate();

        if (rowInserted > 0){
            System.out.println("A new tool was inserted successfully");
        }else {
            System.out.println("Something went wrong");
        }
    }
    // metode, lai izdzēstu instrumentu
    public static void deleteTool(Connection conn, String id) throws SQLException{

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
    private static boolean toolIdExists(Connection conn, String id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tools WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        return count > 0;
    }

    // metode, lai izdzēstu Customer
    public static void deleteUser(Connection conn, String personalIDNo) throws SQLException{

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
    private static boolean personalIdExists(Connection conn, String personalIDNo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE personalIDNo = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, personalIDNo);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        return count > 0;
    }

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


    private static boolean customerExistsInDB(Connection conn, String personalIDNo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE personalIDNo = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, personalIDNo);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        return count > 0;
    }


}
