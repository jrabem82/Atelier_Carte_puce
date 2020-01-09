package smartcards;

import java.util.List;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
/**
 *
 * @author Guillaume
 */
public class smartCards {
    private static CardTerminal terminal;
    private static Card carte;
    private static int i;
    private static String texte=new String();
    
    static public List<CardTerminal> getTerminals() throws CardException {
        return TerminalFactory.getDefault().terminals().list();

    }
    
    static public String toString(byte[] byteTab){
        String texte="";
        String hexNombre;
        for(i=0;i<byteTab.length;i++){
                hexNombre="";
                hexNombre=Integer.toHexString(byteTab[i]);
                if(hexNombre.length()==1){
                    texte+=" 0"+hexNombre;
                }
                else{
                    texte+=" "+hexNombre;
                }
        }
        return texte;
    }
    
    public static void main(String[] args) throws CardException {
        List<CardTerminal> terminauxDispos = smartCards.getTerminals();
        //Premier terminal dispo
        terminal = terminauxDispos.get(0); //recuperer le contenu du premier terminal
        System.out.println(terminal.toString());
        //Connexion à la carte
        carte = terminal.connect("T=0"); // T=0 (protocole d'echange)
        //ATR (answer To Reset)
        System.out.println(toString(carte.getATR().getBytes()));
        System.out.println(carte.getProtocol());
        
        CardChannel channel = carte.getBasicChannel(); //creer un circuit de communication
        CommandAPDU commande ;
        ResponseAPDU r;
        
        commande = new CommandAPDU(0x80,0xBE,0x00,0x01,0x10);//lecture (nombre d'octets == 5)
        r = channel.transmit(commande);
        System.out.println(toString(r.getData()));
        
        byte apdu[]={0x00,(byte) 0x20,(byte)0x00,(byte)0x07,(byte)0x04,(byte)0xAA,(byte)0xAA,(byte)0xAA,(byte)0xAA};
        commande = new CommandAPDU(apdu);//test code pin
        r = channel.transmit(commande);
        System.out.println("reponse : " + "SW1 : 0x"+Integer.toHexString(r.getSW1())+" SW2 : 0x"+Integer.toHexString(r.getSW2())+" data : "+toString(r.getData()));
        System.out.println(texte);
        carte.disconnect(false);
        
        

    }
}
