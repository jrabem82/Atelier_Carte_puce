package tests;

import jdbc.*;
import model.*;

import static org.junit.Assert.*;
import org.junit.Test;

public class JdbcQueryTest {

    @Test
    public void selectUser() {
        JdbcQuery jdbcQuery = new JdbcQuery(JdbcConnection.getConnection());
        String query = "idUser = 'myIdentifier';";
        assertEquals("myIdentifier", jdbcQuery.selectUser(query).get(0).getIdUser());
    }


    @Test
    public void setNbAuthEch() {
        JdbcQuery jdbcQuery = new JdbcQuery(JdbcConnection.getConnection());
        String idUser = "alanTuring";
        jdbcQuery.setNbAuthEch(idUser,0);
    }

    @Test
    public void IncNbAuthEchouees() {
        JdbcQuery jdbcQuery = new JdbcQuery(JdbcConnection.getConnection());
        jdbcQuery.IncNbAuthEchouees("alanTuring");
    }
}