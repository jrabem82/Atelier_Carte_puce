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

    @Test
    public void authentificationBiometrique() {
        JdbcQuery jdbcQuery = new JdbcQuery(JdbcConnection.getConnection());
        assertEquals(true, jdbcQuery.authentificationBiometrique("master2CerAdiShamir","5ec367180662502b2b7d37313d10daa4","87f1ddcadb2d4244295f5170bfc5515a","48d71e12771f1ff9731956c14d21d66d"));
    }
}