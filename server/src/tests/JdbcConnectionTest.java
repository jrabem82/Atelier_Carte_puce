package tests;


import jdbc.JdbcConnection;
import org.junit.Test;

public class JdbcConnectionTest {

    @Test
    public void construct() {
        JdbcConnection jdbcConnection = new JdbcConnection();
        jdbcConnection.getConnection();
    }



}