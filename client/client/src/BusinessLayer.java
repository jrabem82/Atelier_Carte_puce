

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
    
    public void executeBashCommand(String cmd) throws IOException, InterruptedException {
	    	Runtime run = Runtime.getRuntime();
	    	Process pr;
	    	pr = run.exec(cmd);
	    	pr.waitFor();
	    	BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
	    	String line = "";
	    	while ((line=buf.readLine())!=null) {
	    	System.out.println(line);
	    	}
    }
    
    public List<String> recupererHists(String path) {
	    	try {
				RandomAccessFile file = new RandomAccessFile(path, "r");
				String str;
				List<String> hists = new ArrayList();
				while ((str = file.readLine()) != null) {
					if(!str.isEmpty()) {
						hists.add(str);
					}
				}
				file.close();
				return hists;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
    	
    }
    
    public static int NbAleatoire()
    {
        double d = Math.random();
        int n = (int)d;
        n = (int)(Math.random() * 20);   
        return n;
    }
    public static List<Integer> primeNumbersBruteForce(int n) {
    List<Integer> primeNumbers = new LinkedList<>();
    for (int i = 100; i <= n; i++) {
        if (isPrimeBruteForce(i)) {
            primeNumbers.add(i);
        }
    }
    return primeNumbers;
    }
    public static boolean isPrimeBruteForce(int number) {
    for (int i = 2; i < number; i++) {
        if (number % i == 0) {
            return false;
        }
    }
    return true;
}
    public static int PrimeNumber(){
            List<Integer> primeNb = primeNumbersBruteForce(200);
            List<Integer> randomlist = new ArrayList<Integer>();
            List<Integer> intListRandom = new ArrayList<Integer>();
            for (int i = 0; i<20 ; i++){
                int n = NbAleatoire();
                randomlist.add(primeNb.get(n)); 
            }
            return randomlist.get(NbAleatoire()); 
     
        }
    
    public double getKeyUsingDiffieHellman()
    {
        try {
            //connection.getConnection();
            String pstr, gstr, Astr;
            int p = PrimeNumber();//23; 
            int g = PrimeNumber();;//9; 
            int a = NbAleatoire(); 
            double Adash, serverB;
            
            //connection.getFlux_sortie().println ("authentification") ;
            
            pstr = Integer.toString(p);
            connection.getFlux_sortie().println (pstr) ;
            
            gstr = Integer.toString(g);
            connection.getFlux_sortie().println (gstr) ;
            
            double A = ((Math.pow(g, a)) % p); // calculation of A 
            Astr = Double.toString(A);
            
            connection.getFlux_sortie().println (Astr) ;
            
         // Client's Private Key 
            System.out.println("From Client : Private Key = " + a); 
            
            serverB = Double.parseDouble(connection.getFlux_entree().readLine());
            System.out.println("From Server : Public Key = " + serverB);
            
            Adash = ((Math.pow(serverB, a)) % p); // calculation of Adash 

            System.out.println("Secret Key to perform Symmetric Encryption = "
                    + Adash);
            
            //connection.getFlux_sortie().println ("/quit") ;
            
            return Adash;
                

        } catch (IOException e) {
            System.out.println("erreur !!");
            return -1;
        }
    }


    public void checkForAuthentification(String userId, Gemalto g)
    {
        try {
            connection.getConnection();
            String token, reply;
            boolean success = false;
            //while (!success){
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
                            double key = getKeyUsingDiffieHellman();
                            
                            String codePin = "286331153";
                        	
                            String keyAES = g.readAesFromCard(g.PIN1);
                            
                            connection.getFlux_sortie().println (AES.encrypt((keyAES), Double.toString(key)));
                            System.out.println("client : Cle AES = "+keyAES);
                            
                            //
                            
                            List<String> recupererHists = recupererHists("/Users/yanisbaour/Desktop/client/test.txt");
                            
                            connection.getFlux_sortie().println (AES.encrypt((recupererHists.get(0)),Double.toString(key)));
                            System.out.println("client : histR = "+recupererHists.get(0));
                            
                            connection.getFlux_sortie().println (AES.encrypt((recupererHists.get(1)),Double.toString(key)));
                            System.out.println("client : histG = "+recupererHists.get(1));
                            
                            connection.getFlux_sortie().println (AES.encrypt((recupererHists.get(2)),Double.toString(key)));
                            System.out.println("client : histB = "+recupererHists.get(2));
                            
                            reply = connection.getFlux_entree().readLine() ;
                            System.out.println("server : reply = "+reply);
                            
                            
                            
                        }else if(reply.equals("number of authorized attempts exceeded")){
                        	   connection.getFlux_sortie().println ("/quit");
                        	   System.out.println("client : quit");
                            //break;
                        }
                    }
                }else{
                		connection.getFlux_sortie().println ("/quit");
                		System.out.println("client : quit");
                    //break; //if number of authorized attempts exceeded
                    //return "";
                }
            //}

        } catch (IOException e) {
            System.out.println("erreur !!");
            //return "";
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            //return "";
        }
    }
}