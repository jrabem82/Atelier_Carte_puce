package jdbc;

import entry.AES;
import model.*;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;


public class JdbcQuery {
    private Connection connection;

    public JdbcQuery(Connection connection) {
        this.connection = connection;
    }

    // SELECT User
    public ArrayList<User> selectUser(String where) {
        ArrayList<User> users;
        users = new ArrayList<User>();
        try {
            User tmpUser;
            String selectUserQuery;

            if (where.length() != 0) {
                selectUserQuery = "SELECT * FROM public.ab_user WHERE " + where + ";";
            } else {
                selectUserQuery = "SELECT * FROM public.ab_user";
            }

            PreparedStatement preparedStatement = connection.prepareStatement(selectUserQuery);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {

                tmpUser = new User();
                tmpUser.setIdUser(result.getString("idUser"));
                tmpUser.setX_hmac(result.getLong("x_hmac"));
                tmpUser.setY_hmac(result.getLong("y_hmac"));
                tmpUser.setHmac_x_y_pw(result.getString("hmac_x_y_pw"));
                users.add(tmpUser);
            }

            preparedStatement.close();

        } catch (SQLException se) {
            System.err.println(se.getMessage());
        }
        return users;
    }

    public void setNbAuthEch(String idUser, int nb) {
        try {
            String checkForPlaceQuery;
            checkForPlaceQuery = "UPDATE public.ab_user SET nbauthechouees = "+nb+" WHERE idUser = '"+idUser+"';";
            PreparedStatement preparedStatement = connection.prepareStatement(checkForPlaceQuery);
            preparedStatement.executeQuery();
            preparedStatement.close();
        } catch (SQLException se) {
            //System.err.println(se.getMessage());
        }
    }

    // check for place
    public void IncNbAuthEchouees(String idUser) {

        User user = new User();
        try {
            String checkForPlaceQuery;

            checkForPlaceQuery = "SELECT NBAuthEchouees FROM public.ab_user WHERE idUser ='"+idUser+"';";

            PreparedStatement preparedStatement = connection.prepareStatement(checkForPlaceQuery);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                setNbAuthEch(idUser, result.getInt("NBAuthEchouees")+1);
            }
            preparedStatement.close();

        } catch (SQLException se) {
            //System.err.println(se.getMessage());
        }
    }

    // SELECT User
    public boolean authentificationBiometrique(String cleClair, String histR, String histG, String histB) {
        ArrayList<User> users;
        users = new ArrayList<User>();
        try {
            User tmpUser;
            String selectUserQuery;
            histR = AES.encrypt(histR,cleClair);
            System.out.println("\t histR avant requete = "+histR);

            histG = AES.encrypt(histG,cleClair);
            System.out.println("\t histB = "+histG);

            histB = AES.encrypt(histB,cleClair);
            System.out.println("\t histB = "+histB);



                selectUserQuery = "SELECT * FROM public.ab_user WHERE  histoR = '"+histR+"' AND histoG = '"+histG+"' AND histoB = '"+histB+"';";
            PreparedStatement preparedStatement = connection.prepareStatement(selectUserQuery);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                tmpUser = new User();
                tmpUser.setHistoR(AES.decrypt((result.getString("histoR")),cleClair));
                tmpUser.setHistoG(AES.decrypt((result.getString("histoG")),cleClair));
                tmpUser.setHistoB(AES.decrypt((result.getString("histoB")),cleClair));

                String tmpHistR = AES.decrypt(histR,cleClair);
                String tmpHistG = AES.decrypt(histG,cleClair);
                String tmpHistB = AES.decrypt(histB,cleClair);

                if (tmpHistR.equals(tmpUser.getHistoR()) && tmpHistG.equals(tmpUser.getHistoG()) && tmpHistB.equals(tmpUser.getHistoB()))
                    return true;
                return false;
            }

            preparedStatement.close();

        } catch (SQLException se) {
            System.err.println(se.getMessage());
        }
        return false;
    }


}
