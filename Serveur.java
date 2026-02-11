import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Serveur {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1415);
        //Serveur serv = new Serveur(serverSocket);
        Socket socket;
        

        while (true)
            try {
                socket = serverSocket.accept();
                System.out.println("\033[38;2;60;130;230m" + "Client connecté " + "\033[38;2;220;220;20m" + socket.toString() + "\033[m");
                PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                Polichombr jsp = new Polichombr(socket, out, in);
                jsp.start();
            } catch (Exception e) {
                serverSocket.close();
            }

        

    }
}

class Polichombr extends Thread {
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    static ArrayList<Polichombr> liste = new ArrayList<>();
    boolean stop = false;
    String username;
    String color;

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
        out.println("Bienvenue " + color + username + "\n");
        envoi(color);
        String msg;
        while (!stop) {
            
            try {
                msg = in.readLine();
                if (msg != null)
                    envoi(msg);
                else
                    fermer();

            } catch (Exception e) {
                stop = true;
                e.printStackTrace();
            }

        }
        fermer();
    }

    public synchronized void envoi(String msg) {
        try {
            System.out.println(username + " a envoyé un message.");
            for (Polichombr usr : liste) {
                
                if (usr.socket!=socket)
                    usr.out.println(color + username + " : " + msg);
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
        stop = true;
        retirerUser();
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

    
    public String toString() {
        return socket.toString();
    }
    
}
