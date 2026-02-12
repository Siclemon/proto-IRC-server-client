package serveur.src;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Serveur {

    public static void main(String[] args) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(1415)) {
            Socket socket;
            
            while (!serverSocket.isClosed()) {

                socket = serverSocket.accept();
                System.out.println("\033[38;2;60;130;230m" + "+ Client connecté " + "\033[38;2;220;220;70m" + socket.toString() + "\033[m");

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
        envoi("> " + username + " a rejoint!",false,false);
        String msg;

        try {
            while ((msg = in.readLine()) != null) {
                if (msg.startsWith("/") && msg.length()>1)
                    command(msg);
                else
                    envoi(msg, false, true);
            }
        } catch (IOException e) {
        } finally {
            fermer();
        }

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
        envoi("> " + username + " a quitté :(", false, false);
        System.out.println("\033[38;2;220;70;70m" + "- Client déconnecté " + "\033[38;2;220;220;70m" + socket.toString() + "\033[m" + " [" + username + "]");
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

    private void command(String msg) {
        msg = msg.substring(1);
        String[] arr = msg.split(" ", 2);
        String cmd = arr[0];
        String arg = null;
        if (arr.length>1)
            arg = arr[1];

        switch (cmd) {
            case "help" :
                commandHelp();
                break;

            case "color":
                color = commandColor(arg);
                break;

            case "nick" :
                username = commandNick(arg);
                break;

            case "msg" :
                commandMsg(arg);
                break;
        
            default:
                out.println("\033[4m" + "Unknown command : type \"/help\" to see available commands." + "\033[m");
                break;
        }
    }

    private String color() {
        Random rng = new Random();
        color = "\033[38;2;" + rng.nextInt(70,256) + ";" + rng.nextInt(70,256) + ";" + rng.nextInt(70,256) + "m";
        return color;
    }

    private String color(int r, int g, int b) {
        color = "\033[38;2;" + r + ";" + g + ";" + b + "m";
        return color;
    }


    private Boolean is_color(int color) throws IllegalArgumentException
    {
        if ( 0 <= color && color <= 255)
            return (true);
        throw new IllegalArgumentException();
    }

    private String commandColor(String arg) 
    {
        try 
        {

            if (arg.trim().equals("random")){
                String newColor = color();
                out.println(newColor + "Nouvelle couleur" + "\033[m");
                return newColor;
            }
            else if (arg.trim().equals("info")) {
                String colorCode = color.substring(7,color.length()-1);
                colorCode = colorCode.split(";")[0] + " " + colorCode.split(";")[1] + " " + colorCode.split(";")[2];
                colorCode = color + "Your color : " + "\033[m" + colorCode;
                out.println(colorCode);
                return color;
            } else if (arg.trim().equals("help")) {
                out.println("\033[4m" + "Usage :" + "\033[m" +
                                    " /color help   → to see this\n" +
                            "               info   → to get the RGB values of your current color\n" + 
                            "               random → to get a new random color\n" +
                            "               r g b  → to choose a new color (with r, g and b between 0 and 255)"
                            );
                return color;
            }
            else {
                String[] rgb = arg.split(" ");
                int r = Integer.parseInt(rgb[0]);
                int g = Integer.parseInt(rgb[1]);
                int b = Integer.parseInt(rgb[2]);
                is_color(r);
                is_color(g);
                is_color(b);
                String newColor = color(r, g, b);
                out.println(newColor + "Nouvelle couleur" + "\033[m");
                return newColor;
            }
        } 
        catch (Exception e) 
        {
            out.println("Bad command format. Type \"/color help\" to see usage.");
            return color;
        }
    }

    private void commandHelp() {
        out.println("\033[4m" + "Available commands :" + "\033[m" +
            "\n\t/help                     → to see this" +
            "\n\t/color <arg>              → to change your color, type \"/color help\" to see available arguments" +
            "\n\t/nick <newUsername>       → to change your username" +
            "\n\t/msg <username> <message> → to send a private message to another user"
        );
    }

    private String commandNick(String arg) {
        try {
            if (arg.length()>1){
                out.println("Nouveau nom : " + color + arg + "\033[m");
                envoi("> " + username + " s'appelle maintenant " + arg, false, false);
                System.out.println(username + " s'appelle maintenant " + arg);
                return arg;
            }
        } catch (Exception e) {}
        out.println("Bad command format. Usage is \"/nick <username>\"");
        return username;
    }

    private void commandMsg(String str) {

    }
    
}
