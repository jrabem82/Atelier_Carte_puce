package entry;

import jdbc.JdbcPersistence;

public class UserControl {

    public UserControl() {}


    public Boolean userExists(String userId){ // demande d'authentification

        JdbcPersistence jdbcPersistence = new JdbcPersistence();

        if (!jdbcPersistence.getUser(" idUser = '" + userId +"';").isEmpty()) {
            return true;
        } else{
            return false;
        }
    }

}
