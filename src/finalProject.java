import tasks.DBMEthods;
import tasks.izmeginajums;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

public class finalProject {

    public static void main(String[] args) {

        //DBMEthods.logIn();

        String dbURL = "jdbc:mysql://localhost:3306/java35";
        String username = "root";
        String password = "Arta";
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
                System.out.println("i - hand out");
                System.out.println("r - return");

                char action = scanner.nextLine().toLowerCase().charAt(0);

                if (action == 'd') {
                    DBMEthods.insertCustomer(conn, scanner);

                } else if (action == 'c') {
                    DBMEthods.insertTool(conn, scanner);

                } else if (action == 'e') {
                    DBMEthods.deleteTool(conn, scanner);

                } else if (action == 'f') {
                    DBMEthods.deleteCustomer(conn, scanner);

                } else if (action == 'a') {
                    DBMEthods.readTools(conn);

                } else if (action == 'b') {
                    DBMEthods.readAvailableTools(conn);

                } else if (action == 'g') {
                    DBMEthods.readCustomer(conn);

                    // šis būs jāliek citā vietā, if not present in SQL Insert customer
                }else if (action == 'h') {

                    System.out.println("Enter customers personal ID No.:");
                    String customerIDNo = scanner.nextLine();
                    if (!DBMEthods.customerExistsInDB(conn, customerIDNo)) {
                        System.out.println("Customer with personal ID No." + customerIDNo + " don't exists, please input it in DB");
                        DBMEthods.insertCustomer(conn, scanner);
                    } else {
                        // dati par Customer ielasās DB main
                    }
                } else if (action == 'i') {

                    DBMEthods.toolIDSearch(conn);
                    System.out.println("Lets calculate the price");
                    DBMEthods.calculateRentPrice(conn);

                } else if (action == 'r') {
                    String newToolID;
                    while (true) {
                        System.out.println("Enter tool ID number");
                        newToolID = scanner.nextLine().toUpperCase(Locale.ROOT).trim();

                        if (!Pattern.matches("[A-Z]{3,5}[0-9]{1,3}", newToolID)) {
                            System.out.println("Your inputted tool ID number is not valid, try again");
                        } else if (!DBMEthods.toolIdExists(conn, newToolID)) {
                            System.out.println("The inputted tool ID don't exists, try again");
                        } else {
                            break;// Exit the loop if the input is valid
                        }
                    }


                    System.out.println("Enter worked hours");
                    int toolUsed = scanner.nextInt();
                    scanner.nextLine();

                    DBMEthods.returnTool(conn, toolUsed, newToolID);

                    DBMEthods.hoursTillService(conn,newToolID, scanner);

                    //izmeginajums.resetHours(conn);

                }


                System.out.println("Do you want to do something more? y/n");
                again = scanner.nextLine().toLowerCase().charAt(0);


            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

        /*To be inserted in methods:
        <<accessmodifier methodname>> (Connection conn) throws SQLException{
        String sql = "SELECT * FROM <<table customers, tools or main>>";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            }
         */

}
