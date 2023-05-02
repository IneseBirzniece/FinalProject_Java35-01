import java.sql.*;
import java.util.Scanner;
import java.util.regex.Pattern;


// Izmaiņas veicu tikai šajā atsevišķajā failā

/*public class Untilservice_improved {


       public static void updateMain(Connection conn) throws SQLException {
            Scanner scanner = new Scanner(System.in);
            String toolIDSc;
            System.out.println("Confirm tool ID");
            toolIDSc = scanner.nextLine().toUpperCase().trim();
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
                DBMEthods.calculateRentPrice(conn);
            } else {
                System.out.println("Customer not found!");
                System.out.println("Enter 1 to insert new customer, or 2 to try again to gain out tool:");
                String choice = scanner.nextLine().trim();

                if (choice.equals("1")) {
                    insertCustomer(conn, scanner);
                } else if (choice.equals("2")) {
                    toolIDSearch(conn);
                    updateMain(conn);
                } else {
                    System.out.println("Invalid input. Returning to main menu...");

                    // Šo tekstu parāda, bet neparāda pašu main menu
                    return;
                }


            }
        }

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
                    updateMain(conn);

                } else if (resultSet4.next()) {
                    System.out.println("Handed out to:\n"
                            + resultSet4.getString(6) + ", " + resultSet4.getString(7) + ", " + resultSet4.getString(8));
                }
            } else {
                System.out.println("ID does not exist");
                toolIDSearch(conn);
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

// Mēģināju izlabot to, ko programma dara, ja pie tool reset izvēlas 'n'. Principā strādā, jāiztestē kārtīgi tikai ar izsniegšanu
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
                    }
                    break;
                }
// JAUNS
                // Ja nospiež 'n', instruments joprojām rādās available. Izmēģināju 2 variantus:
                // Lai izmēģinātu while cilpu, nokomentē IF steitmentu, šim noņem //
                // WHILE cilpa:
                // Varētu arī izmantot FOR cilpu, ko divreiz izpilda, pēc tam iestata statusu uz unavailable
              /*  while (confirm == 'n') {
                    System.out.println("NB! The tool will not be available until after the service!");
                    PreparedStatement resetLater = conn.prepareStatement("UPDATE main SET available = 0 WHERE toolID = ?");
                    resetLater.setString(1, newToolID);
                    resetLater.executeUpdate();
                    System.out.println("Send to service now? y/n");
                    confirm = scanner.nextLine().charAt(0);
                }*/
                // 2. IF steitments. Ja nospiež 'n', tad main tabulā available tiek iestatīts uz 0.
                // Nākamreiz, kad mēģina instrumentu izsniegt, vienreiz atkal tiek rādīts jautājums par apkopi y/n un šis if steitments
                // Man personīgi šis variants šķiet mazāk agresīvs :)
// JAUNS
  /*              if (confirm == 'n'){

                    System.out.println("NB! The tool will not be available until after the service!");
                    System.out.println("Send to service now? y/n");
                    confirm = scanner.nextLine().charAt(0);
                    PreparedStatement resetLater = conn.prepareStatement("UPDATE main SET available = 0 WHERE toolID = ?");
                    resetLater.setString(1, newToolID);
                    resetLater.executeUpdate();
                }

            } else {
                System.out.println("Till service " + hours + " hours. All ok");
            }
        }
    }

    // Mainīta koda daļa ar IF steitmentu, kas pārbauda pieejamību
    /*public static void toolIDSearch(Connection conn) throws SQLException {
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

        // Pārbauda, vai nav atlikts service reset
        PreparedStatement laterReset = conn.prepareStatement("SELECT * FROM main WHERE untilService < 24 AND toolID = ?");
        laterReset.setString(1, toolIDSc);
        ResultSet resultSet6 = laterReset.executeQuery();


// JAUNS!
        if (resultSet.next()) {
            System.out.println("ID: " + resultSet.getString(2) + "\t Name: " + resultSet.getString(3));
            if (!resultSet2.next() || resultSet3.next() || !resultSet6.next()) {
                System.out.println("Tool is available");
                updateMain(conn);
            } else if (resultSet6.next()) {

                hoursTillService(conn, toolIDSc, scanner);
            }


            // Pārbauda, vai instruments vispār ir ticis ievadīts main tabulā

            PreparedStatement inMain = conn.prepareStatement("SELECT toolID FROM main WHERE toolID = ?");
            inMain.setString(1, toolIDSc);
            ResultSet resultSet5 = inMain.executeQuery();


        } else if (resultSet4.next()) {
            System.out.println("Handed out to:\n"
                    + resultSet4.getString(6) + ", " + resultSet4.getString(7) + ", " + resultSet4.getString(8));

        } else {
            System.out.println("ID does not exist");
            toolIDSearch(conn);
        }
    }*/

    // Mainīts SQL vaicājums, lai nolasītu pieejamos rīkus arī no tools tabulas
   /* public static void readAvailableTools(Connection conn) throws SQLException {

        String sql = "SELECT * FROM tools WHERE id NOT IN (SELECT toolID FROM main) " +
                "OR id = (SELECT toolID FROM main WHERE available <> 0 OR untilService > 24)";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            String category = resultSet.getString(1);
            String id = resultSet.getString(2);
            String name = resultSet.getString(3);
            String specifications = resultSet.getString(4);
            int serviceHours = resultSet.getInt(5);
            double priceDay = resultSet.getDouble(6);

            String output = ": %s - %s - %s - %s - %s - %s";
            System.out.println(String.format(output, category, id, name, specifications, serviceHours, priceDay));
        }
    }
    }*/



