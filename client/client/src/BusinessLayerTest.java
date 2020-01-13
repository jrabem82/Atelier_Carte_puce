import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BusinessLayerTest {

    @org.junit.Test
    public void recupererBiometrie() throws IOException {
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

}