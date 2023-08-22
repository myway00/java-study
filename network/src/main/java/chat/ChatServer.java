package chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private static final int PORT = 9999; 
    private static final int ALLOWED_CONNECTION_SIZE = 10;
    
    private ServerSocket serverSocket;

    public static void main(String[] args) {
        new ChatServer().runServer();
    }

    private void runServer() {
        // ������ ArrayList�� ����ȭ�� ����Ʈ�� ��ȯ
        //  currentUsers ����Ʈ�� ���� ������ ���������� ����ȭ�Ǿ� ���� �����尡 �����ϰ� ���
    	List<PrintWriter> currentUsers = new ArrayList<PrintWriter>();

        try {
            // 1. ���� ���� ���� 
            serverSocket = new ServerSocket();

            // 1-1. SO_REUSEADDR ������ ����ϴ� ��Ʈ�� �� ���� �����ϵ��� ����ϴ� ����
            serverSocket.setReuseAddress(true);

            // 2. binding - ��α�(backlog) ť�� ũ��
            String localhost = InetAddress.getLocalHost().getHostAddress();
            serverSocket.bind(new InetSocketAddress("0.0.0.0", PORT), ALLOWED_CONNECTION_SIZE);
            consoleLog("ChatServer Starts at " + PORT);

            while (true) { 
                // ������ ����ؼ� Ŭ���̾�Ʈ�� ������ �޾Ƶ��̱� ���� ���� �����Դϴ�.
                // �� ������ ������ ���� ���� ���� ����ؼ� Ŭ���̾�Ʈ�� ������ ó���մϴ�.
                
                Socket socket = serverSocket.accept();
                // ���ο� Ŭ���̾�Ʈ�� ������ �����ϰ� �ش� Ŭ���̾�Ʈ�� ����ϱ� ���� ������ �����մϴ�.
                // accept() �޼���� Ŭ���̾�Ʈ�� ������ �� ������ ���ŷ�˴ϴ�.
                
                Thread thread = new ChatServerThread(socket, currentUsers);
                // ���ο� Ŭ���̾�Ʈ�� ����ϱ� ���� �����带 �����մϴ�.
                // ChatServerThread Ŭ������ Ŭ���̾�Ʈ���� ���� ����� ó���ϴ� ������ ����ִ� Ŭ�����Դϴ�.
                // �ش� Ŭ������ �����ڿ� ���ϰ� ���� ����� ����� ���޹޾� �ʱ�ȭ�˴ϴ�.
                
                thread.start();
                // �����带 �����ϰ�, ChatServerThread ���� run() �޼��尡 ����ǰ� �մϴ�.
                // �̷��� �����ν� ������ ���� Ŭ���̾�Ʈ��� ���ÿ� ���������� ����� �� �ֽ��ϴ�.
            }

        } catch (IOException e) {
			consoleLog( "error:" + e );
        } finally {
            closeServerSocket();
        }
    }

    private void closeServerSocket() {
        try {
            // ���� ������ null�� �ƴϰ� �̹� �������� ���� ��쿡�� �Ʒ� �ڵ带 �����մϴ�.
            if (serverSocket != null && !serverSocket.isClosed()) {
                // ���� ������ �ݽ��ϴ�. Ŭ���̾�Ʈ�� ���� ������ �ߴ��ϰ� ���ҽ��� �����մϴ�.
                serverSocket.close();
            }
        } catch (IOException ex) {
            // ���� ������ �ݴ� �������� �߻��� ���ܸ� ó���մϴ�.
            // ���� ������ ����Ͽ� �α׿� ����մϴ�.
            consoleLog("error:" + ex);
        }
    }


    public static void consoleLog(String message) {
        System.out.println("[chat server]" + message);
    }
}

