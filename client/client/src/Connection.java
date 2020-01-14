
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {
    //private  String ip ="127.0.0.1";
    private  String ip ="192.168.43.93";
	//private  String ip ="192.168.0.12";
	private  int port=2020;
    private  Socket socket = null ;
    private  PrintWriter flux_sortie = null ;
    private  BufferedReader flux_entree = null ;

    public Connection(String ip,int port,Socket socket,PrintWriter flux_sortie,BufferedReader flux_entree){

        this.ip=ip;
        this.port=port;
        this.socket=socket;
        this.flux_sortie=flux_sortie;
        this.flux_entree=flux_entree;
    }


    public  void getConnection() throws IOException {
        try {
            socket = new Socket (ip, port) ;
            flux_sortie = new PrintWriter (socket.getOutputStream (), true) ;
            flux_entree = new BufferedReader (new InputStreamReader(
                    socket.getInputStream ())) ;
        }
        catch (UnknownHostException e) {
            System.err.println ("Hote inconnu") ;
            System.exit (1) ;
        }
    }

    public Connection() {}

    public void closeConnection() throws IOException
    {
        getFlux_sortie().println("/quit");
        flux_sortie.close () ;
        flux_entree.close () ;
        socket.close () ;
        System.out.println("connection closed");
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public PrintWriter getFlux_sortie() {
        return flux_sortie;
    }

    public void setFlux_sortie(PrintWriter flux_sortie) {
        this.flux_sortie = flux_sortie;
    }

    public BufferedReader getFlux_entree() {
        return flux_entree;
    }

    public void setFlux_entree(BufferedReader flux_entree) {
        this.flux_entree = flux_entree;
    }

}

