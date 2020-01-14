import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws Exception {

        BusinessLayer businessLayer = new BusinessLayer();
        //businessLayer.getKeyUsingDiffieHellman();

        Gemalto g=new Gemalto();
        g.initGemalto();
        g.waitCardIn();
        //g.initCard("jonathan",g.PIN1,g.AES1);
        g.emulateUserMode();
        while(true) {
        	g.waitCardIn(); //attendre linsersion de la carte
            //if(g.initCard("fauxPWD",g.PIN0)) { //definir l'id
          	  String s = g.readIdFromCard();
          	  if(s!=null) {
          		System.out.println(s);
        	  	  	businessLayer.checkForAuthentification(s,g);
        	  	    //businessLayer.getKeyUsingDiffieHellman();
          	  }  
          	  else
          		  System.out.println("null");
        g.waitCardOut();    //attendre le retrait de la carte
        }
            
        //g.disconnect(false);
        
    }
}