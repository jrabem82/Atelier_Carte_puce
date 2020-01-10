package entry;

import jdbc.JdbcConnection;
import jdbc.JdbcPersistence;
import jdbc.JdbcQuery;
import model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class BusinessLayer {
    private Connection connection;

    //ClientsConnection clientsConnection = new ClientsConnection();

    public BusinessLayer(){}

    public static byte[] stringToMD5Bytes(String msg) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(msg.getBytes());

        return md.digest();
    }

    public static String stringToMD5String(String msg) throws NoSuchAlgorithmException {
        StringBuilder sb = new StringBuilder();
        for (byte b : stringToMD5Bytes(msg)) {
            sb.append(String.format("%02X", b));
        }

        return sb.toString();
    }

    public static String hashBuilder(String x,String y,String graine,String pw) throws NoSuchAlgorithmException {
        return stringToMD5String(graine+stringToMD5String(x+stringToMD5String(y+pw)));
    }


    public static void getKeyUsingDiffieHellman(PrintWriter flux_sortie,BufferedReader flux_entree) throws IOException, NoSuchProviderException, NoSuchAlgorithmException {

        // Server Key
        int b = 3;

        // Client p, g, and key
        double clientP, clientG, clientA, B, Bdash;
        String Bstr;

        // Server's Private Key
        System.out.println("From Server : Private Key = " + b);

        clientP = Integer.parseInt(flux_entree.readLine()); // to accept p
        System.out.println("From Client : P = " + clientP);

        clientG = Integer.parseInt(flux_entree.readLine()); // to accept g
        System.out.println("From Client : G = " + clientG);

        clientA = Double.parseDouble(flux_entree.readLine()); // to accept A
        System.out.println("From Client : Public Key = " + clientA);

        B = ((Math.pow(clientG, b)) % clientP); // calculation of B
        Bstr = Double.toString(B);

        flux_sortie.println(Bstr); // Sending B

        Bdash = ((Math.pow(clientA, b)) % clientP); // calculation of Bdash

        System.out.println("Secret Key to perform Symmetric Encryption = "
                + Bdash);

    }

    public static void checkForAuthentification(PrintWriter flux_sortie,BufferedReader flux_entree) throws IOException, NoSuchProviderException, NoSuchAlgorithmException {

        UserControl userControl = new UserControl();
        String graine;
        String userId;
        if ((userId = flux_entree.readLine()) != null) { //recevoir l'id
            System.out.println("client : userId = "+userId);
            if (userControl.userExists(userId)){ //si user exists
                System.out.println("\tUser exists");
                JdbcPersistence jdbcPersistence = new JdbcPersistence();

                if (!jdbcPersistence.getUser(" idUser = '" + userId +"' AND NBAuthEchouees < 3;").isEmpty()) {//si le nombre de tentatives inferieur a 3
                    String query = "idUser = '"+userId+"';";
                    JdbcQuery jdbcQuery = new JdbcQuery(JdbcConnection.getConnection());

                    //generer x, y et g
                    PasswordGenerator passwordGenerator = new PasswordGenerator();
                    graine = passwordGenerator.graineGen();
                    Long x_hmac = jdbcQuery.selectUser(query).get(0).getX_hmac();
                    Long y_hmac = jdbcQuery.selectUser(query).get(0).getY_hmac();
                    String hmac_x_y_pw = jdbcQuery.selectUser(query).get(0).getHmac_x_y_pw();

                    flux_sortie.println(x_hmac+";"+y_hmac+";"+graine);
                    System.out.println("server : x_sel;y_sel;graine = "+x_hmac+";"+y_hmac+";"+graine);

                    String hashRecu = flux_entree.readLine();
                    System.out.println("client : hash recu = "+hashRecu);

                    String hashcalcule = stringToMD5String(graine.toString().trim()+hmac_x_y_pw);

                    System.out.println("\thash calcule : " + hashcalcule);

                    if (hashRecu.equals(hashcalcule)) {
                        flux_sortie.println("success");
                        System.out.println("server : success ");

                        jdbcQuery.setNbAuthEch(userId,0);
                    }else{
                        flux_sortie.println("echec authentification");
                        System.out.println("server : echec authentification ");
                        jdbcQuery.IncNbAuthEchouees(userId);
                    }
                } else{
                    flux_sortie.println("number of authorized attempts exceeded");
                    System.out.println("server : number of authorized attempts exceeded");
                }


            }else{
                flux_sortie.println("echec authentification");
                System.out.println("server : echec authentification ");
                JdbcQuery jdbcQuery = new JdbcQuery(JdbcConnection.getConnection());
                jdbcQuery.IncNbAuthEchouees(userId);
            }

        }
    }

}
