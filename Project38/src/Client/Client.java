package Client;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static final int PORT = 9961;
    public static final String IP = "127.0.0.1";
    public static boolean connectedState = false;

    public static void main(String[] args) {
        while (!connectedState) {
            try {
                connect();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void connect() {
        System.out.println("客户端正在启动...");
        try {
            connectedState = true;
            Socket socket = new Socket(IP, PORT);
            System.out.println("客户端启动成功");
            ObjectInputStream ois = new ObjectInputStream(
                    socket.getInputStream()
            );
            ObjectOutputStream oos = new ObjectOutputStream(
                    socket.getOutputStream()
            );
            new Thread(
                    new Client_Listen(socket, ois)
            ).start();
            new Thread(
                    new Client_Send(socket, oos)
            ).start();
        } catch (IOException e) {
            System.out.println("启动失败，尝试重连...");
            e.printStackTrace();
            connectedState = false;
            reconnect();
        }
    }
    public static void reconnect() {
        while (!connectedState) {
            connect();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
class Client_Listen implements Runnable {
    private Socket socket;
    private ObjectInputStream ois;
    Client_Listen(Socket socket, ObjectInputStream ois) {
        this.socket = socket;
        this.ois = ois;
    }
    public void ClientReceive() {
        while (true) {
            try {
                System.out.println(ois.readObject());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void run() {
        ClientReceive();
    }
}
class Client_Send implements Runnable {
    private Socket socket;
    private ObjectOutputStream oos;
    Client_Send(Socket socket, ObjectOutputStream oos) {
        this.socket = socket;
        this.oos = oos;
    }
    public void ClientReceive() {
        Scanner scan = new Scanner(System.in);
        try {
            while (true) {
                System.out.println("请输入信息:");
                String string = scan.nextLine();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "chat");
                jsonObject.put("msg", string);
                oos.writeObject(jsonObject);
                oos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
                Client.connectedState = false;
                Client.reconnect();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    @Override
    public void run() {
        ClientReceive();
    }
}
