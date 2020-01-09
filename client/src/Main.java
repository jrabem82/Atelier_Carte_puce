


public class Main {

    public static void main(String[] args) throws Exception {

        BusinessLayer businessLayer = new BusinessLayer();
        //businessLayer.checkForAuthentification("adiShamir");

        Gemalto g=new Gemalto();
        g.initGemalto();

        while(true) {
        	g.waitCardIn(); //attendre linsersion de la carte
            //if(g.initCard("fauxPWD",g.PIN0)) { //definir l'id
          	  String s = g.readIdFromCard(g.PIN0);
          	  if(s!=null) {
          		System.out.println(s);
        	  	  	businessLayer.checkForAuthentification(s);
          	  }  
          	  else
          		  System.out.println("null");
        g.waitCardOut();    //attendre le retrait de la carte
        }
            
       // g.disconnect();
        
        
    }
}