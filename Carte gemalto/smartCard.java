package card;

public class smartCard {
  
  public static void main(String[] args) {
      Gemalto g=new Gemalto();
      g.initGemalto();
      g.waitCardIn();
      //if(g.initCard("jo",g.PIN1,g.AES1)) {
    	 String s=g.readIdFromCard();
    	  if(s!=null) {
    		  System.out.println(s);
    		  s=g.readAesFromCard(g.PIN1);
        	  if(s!=null)
        		  System.out.println(s);
        	  else
        		  System.out.println("aes null");
    	  }
    	  else
    		  System.out.println("id null");
    	  //System.out.println("ok");
      //}
      g.waitCardOut();
      g.disconnect(false);
      
      
    /* int a=250000;
     byte[] data=g.intToByte(a);
     for(int i=0;i<4;i++)
    	 System.out.println(data[i]);
      System.out.println(g.byteToInt(data));*/
      //System.out.println(new Integer(15).byteValue()); 
      
     /*String p="1234";
      for(byte b:g.stringToByte(p)){
    	  System.out.println(b);
      }
      System.out.println(g.byteToString(g.stringToByte(p)));*/
  }
}

