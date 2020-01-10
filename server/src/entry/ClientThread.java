package entry;
import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static entry.BusinessLayer.*;


// For every client's connection we call this class
public class ClientThread extends Thread{
    private String clientName = null;
    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final ClientThread[] threads;
    private int maxClientsCount;

    public ClientThread(Socket clientSocket, ClientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
        BusinessLayer businessLayer = new BusinessLayer();
    }

    public void run() {
        int maxClientsCount = this.maxClientsCount;
        ClientThread[] threads = this.threads;

        try {
            /*
             * Create input and output streams for this client.
             */
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());

            PrintWriter flux_sortie = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader flux_entree = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String name;

            /* Start the conversation. */
            while (true) {
                String line = is.readLine();
                if ("/quit".startsWith(line)) {
                    break;
                }
                switch(line) {
                    case "authentification":
                        //System.out.println("authentification");
                        checkForAuthentification(flux_sortie,flux_entree);
                        //getKeyUsingDiffieHellman(flux_sortie,flux_entree);
                        break;
                    default:
                        break;
                }
            }

            /*
             * Clean up. Set the current thread variable to null so that a new client
             * could be accepted by the server.
             */
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }
            /*
             * Close the output stream, close the input stream, close the socket.
             */
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException | NoSuchProviderException | NoSuchAlgorithmException e) {
        }
    }
}