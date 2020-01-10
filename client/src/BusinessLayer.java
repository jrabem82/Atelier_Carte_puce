

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;


public class BusinessLayer extends Observable {

    private  PrintWriter flux_sortie = null ;
    private  BufferedReader flux_entree = null ;
    Connection connection = new Connection();

    public BusinessLayer() {}

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
    
    public void recupererBiometrie() throws IOException {
/*
    	Process p;
        BufferedReader is;

        p = new ProcessBuilder("ls").start();
        System.out.println(p);
        //is = new BufferedReader(new InputStreamReader(p.getInputStream()));
*/
    	   try {
    	      ProcessBuilder pb = new ProcessBuilder("./hello");
    	      pb = pb.redirectErrorStream(true); // on mélange les sorties du processus
    	      Process p = pb.start();
    	      InputStream is = p.getInputStream(); 
    	      InputStreamReader isr = new InputStreamReader(is);
    	      BufferedReader br = new BufferedReader(isr);
    	      String ligne; 

    	      while (( ligne = br.readLine()) != null) { 
    	         // ligne contient une ligne de sortie normale ou d'erreur
    	    	  	System.out.println(ligne);
    	      }
    	      } catch (IOException e) {

    	      } 
    }


    public void checkForAuthentification(String userId)
    {
        try {
            connection.getConnection();
            String token, reply;
            boolean success = false;
            while (!success){
                connection.getFlux_sortie().println ("authentification") ;
                connection.getFlux_sortie().println (userId) ;
                System.out.println("client : idUser = "+userId);
                token =  connection.getFlux_entree().readLine() ;
                if(token.equals("number of authorized attempts exceeded") || token.equals("echec authentification")) {
                	System.out.println("server : " + token);
                }else {
                	System.out.println("server : x_sel;y_sel;graine = "+token);
                }
                
                String[] parts = token.split(";");
                String x = parts[0];
                if (!x.equals("number of authorized attempts exceeded")){//si nb tentatives < 3
                    if (!x.equals("echec authentification")){
                        String y = parts[1];
                        String graine = parts[2];

                        // lire le mdp
                        Scanner sc = new Scanner(System.in);
                        System.out.println("Saisir un pwd :");
                        String pw = sc.nextLine();

                        String hash = hashBuilder(x,y,graine,pw);//(x,y,graine,pw);
                        connection.getFlux_sortie().println (hash);
                        System.out.println("client : hash = "+hash);

                        reply = connection.getFlux_entree().readLine() ;
                        System.out.println("server : reply = "+reply);

                        if (reply.equals("success")){
                            success = true;
                            //connection.getFlux_sortie().println ("/quit");
                        }else if(reply.equals("number of authorized attempts exceeded")){
                        	   connection.getFlux_sortie().println ("/quit");
                        	   System.out.println("client : quit");
                            break;
                        }
                    }
                }else{
                		connection.getFlux_sortie().println ("/quit");
                		System.out.println("client : quit");
                    break; //if number of authorized attempts exceeded
                    //return "";
                }
            }

        } catch (IOException e) {
            System.out.println("erreur !!");
            //return "";
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            //return "";
        }
    }
}