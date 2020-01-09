package card;

public class smartCard {
  
  public static void main(String[] args) {
      Gemalto g=new Gemalto();
      g.initGemalto();
      g.waitCard();
      if(g.initCard("jonathan",g.PIN0)) {
    	  String s=g.readIdFromCard(g.PIN0);
    	  if(s!=null)
    		  System.out.println(s);
    	  else
    		  System.out.println("null");
      }
      g.disconnect();
      
      
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
