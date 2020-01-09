package jdbc;

import model.*;
import java.util.ArrayList;

public class JdbcPersistence {

    private JdbcQuery query;

    public JdbcPersistence() {
        this.query = new JdbcQuery(JdbcConnection.getConnection());
    }

    // SELECT User
    public ArrayList<User> getUser(String where) {
        return query.selectUser(where);
    }

    // SELECT User
    public ArrayList<User> getUser() {return query.selectUser("");}

    // SELECT Place
    public void restNbAuth(String idUser, int nb) {
        query.setNbAuthEch(idUser,nb);
    }
}
