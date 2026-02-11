import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Serveur {

    public static void main(String[] args) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(1415)) {
            Socket socket;
            
            while (!serverSocket.isClosed()) {

                socket = serverSocket.accept();
                System.out.println("\033[38;2;60;130;230m" + "Client connecté " + "\033[38;2;220;220;20m" + socket.toString() + "\033[m");

                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    new Thread(new Polichombr(socket, out, in)).start();
                } catch (IOException e) {}
            }
        }

        

    }
}

class Polichombr implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    static final  List<Polichombr> liste = Collections.synchronizedList(new ArrayList<>());
    private String username;
    private String color;

    public Polichombr(Socket socket, PrintWriter out, BufferedReader in) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        try {
            username = in.readLine();
            color = in.readLine();
        } catch (IOException e) {
        }
        liste.add(this);
    }

    @Override
    public void run() {
        out.println("Bienvenue " + color + username + "\033[m" + "\n");
        envoi(username + " a rejoint!",false,false);
        String msg;

        try {
            while ((msg = in.readLine()) != null) {
            envoi(msg, false, true);
            }
        } catch (IOException e) {
        } finally {
            fermer();
        }

        fermer();
    }

    /**
    * Broadcasts a String to all users.
    * 
    * @param msg        The String to broadcast
    * @param toEveryone A boolean, if true the current user will also receive the message.
    * @param message    A boolean, if true the broadcasted String will be formatted as a message (username : msg)
    */
    public void envoi(String msg, boolean toEveryone, boolean message) {
        try {

            if (message) {
                msg = color + username + " : " + "\033[m" + msg;
                System.out.println(username + " a envoyé un message.");
            }

            synchronized (liste) {
                for (Polichombr usr : liste) {
                    
                    if (usr.socket!=socket || toEveryone) {
                        usr.out.println(msg);
                    }

                }
            }
        } catch (Exception e) {
            fermer();
            System.out.println("erreur jsp trop");
        }
    }

    public void retirerUser() {
        liste.remove(this);
    }

    private void fermer() {
        synchronized (liste) {
            retirerUser();
        }
        try {
            if (socket != null)
                socket.close();
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}
