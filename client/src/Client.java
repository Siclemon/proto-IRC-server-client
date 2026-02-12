package client.src;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private String color;

    public Client(Socket socket, String nom) {
        try {
            this.socket = socket;
            username = nom;
            Random rng = new Random();
            color = "\033[38;2;" + rng.nextInt(70,256) + ";" + rng.nextInt(70,256) + ";" + rng.nextInt(70,256) + "m";
            out = new PrintWriter(socket.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(username);
            out.println(color);

            System.out.println(in.readLine());
        } catch (IOException e) {
            fermer();
            e.printStackTrace();
        } 
    }
    
    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("Adresse IP : ");
            String ip = sc.nextLine();
            Socket socket = new Socket(ip, 1415);
            System.out.print("Pseudo : ");
            String nom = sc.nextLine();
            Client client = new Client(socket,nom);
            client.ecoute();
            client.ecrire();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void ecrire() {
        Scanner sc = new Scanner(System.in);
        String msg;
        while (!socket.isClosed()) {
            try {
                msg = sc.nextLine();
                out.println(msg);
            } catch (Exception e) {
                break;
            }
        }
    }

    public void ecoute() {
        new Thread (new Runnable() {
            @Override
            public void run() {
                String msg;
                while (!socket.isClosed()) {
                    try {
                        //msg = in.readLine();
                        if ((msg = in.readLine()) != null)
                            System.out.println(msg);
                    } catch (IOException e) {
                    }
                }
                fermer();
            }
        }).start();
    }

    private void fermer() {
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
