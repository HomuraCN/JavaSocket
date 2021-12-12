package Server;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerMain {
    public static final int PORT = 9961;
    public static void main(String[] args) {
        System.out.println("服务器正在启动...");
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("服务器启动成功");
            while (true){
                Socket socket = serverSocket.accept();
                System.out.println("客户端连接成功");
                new Thread(
                        new Server_Listen(socket)
                ).start();
                new Thread(
                        new Server_Send(socket)
                ).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class Server_Listen implements Runnable {
    private Socket socket;
    Server_Listen(Socket socket) {
        this.socket = socket;
    }
    public void ServerReceive() {
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    socket.getInputStream()
            );
            while(true) {
                System.out.println(ois.readObject());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void run() {
        ServerReceive();
    }
}
class Server_Send implements Runnable {
    private Socket socket;
    Server_Send(Socket socket) {
        this.socket = socket;
    }
    public void ServerSend() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    socket.getOutputStream()
            );
            Scanner scan = new Scanner(System.in);
            while(true) {
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
        }
    }
    @Override
    public void run() {
        ServerSend();
    }
}