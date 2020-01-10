package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

public class JdbcConnection {
    private static String host = "postgresql-synthesis-project-m2.alwaysdata.net";
    private static String base = "synthesis-project-m2_carteapuce";
    private static String user = "synthesis-project-m2";
    private static String password = "carteapuce";
    private static int port = 5432;
    private static String url = "jdbc:postgresql://postgresql-synthesis-project-m2.alwaysdata.net:5432/synthesis-project-m2_carteapuce";// + host + ":" + port + "/" + base;


    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(url, user, password);
            } catch (Exception e) {
                System.err.println("Connection failed : " + e.getMessage());
            }
        }
        return connection;
    }




}
