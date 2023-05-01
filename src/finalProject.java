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
            System.out.println("Connected to database!");

            while (again == 'y') {
                System.out.println("Choose one option (a,b,c,d,e,q)");

                System.out.println("a - hand out tool");
                System.out.println("b - return tool");
                System.out.println("c - reading available tools and tools with hours till service >=24h ");
                System.out.println("d - actions with tools DB");
                System.out.println("e - actions with customers DB");
                System.out.println("q - quit");
                char action = scanner.nextLine().toLowerCase().trim().charAt(0);
                switch(action){
                    case 'a':
                        DBMEthods.toolIDSearch(conn);
                        break;
                    case 'b':
                        DBMEthods.returnTool(conn, scanner);
                        break;
                    case 'c':
                        DBMEthods.readAvailableTools(conn);
                        break;
                    case 'd':
                        System.out.println("Choose one option (f,g,h)");
                        System.out.println("f - reading tools");
                        System.out.println("g - inserting tools");
                        System.out.println("h - deleting tool");
                        char action2 = scanner.nextLine().toLowerCase().charAt(0);
                         switch(action2){
                             case 'f':
                                 DBMEthods.readTools(conn);
                                 break;
                             case 'g':
                                 DBMEthods.insertTool(conn, scanner);
                                 break;
                             case 'h':
                                 DBMEthods.deleteTool(conn, scanner);
                                 break;
                             default:
                                 System.out.println("Invalid option. Please try again.");
                                 break;
                         }
                         System.out.println("Do you want to do something more? y/n");
                         again = scanner.nextLine().toLowerCase().charAt(0);
                        if (again == 'n'){
                            System.out.println("Goodbye!");
                        }
                        break;
                    case 'e':
                        System.out.println("Choose one option (i,j,k)");
                        System.out.println("i - reading customers");
                        System.out.println("j - inserting customers");
                        System.out.println("k - deleting customer");
                        char action3 = scanner.nextLine().toLowerCase().charAt(0);
                        switch(action3){
                            case 'i':
                                DBMEthods.readCustomer(conn);
                                break;
                            case 'j':
                                DBMEthods.insertCustomer(conn, scanner);
                                break;
                            case 'k':
                                DBMEthods.deleteCustomer(conn, scanner);
                                break;
                            default:
                                System.out.println("Invalid option. Please try again.");
                                break;
                        }
                        System.out.println("Do you want to do something more? y/n");
                        again = scanner.nextLine().toLowerCase().charAt(0);
                        if (again == 'n'){
                            System.out.println("Goodbye!");
                        }
                        break;
                    case 'q':
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }

            }
            conn.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
