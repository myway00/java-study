package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    // ������ IP �ּҿ� ��Ʈ�� ����
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9999;
    
    // Ŭ���̾�Ʈ���� ����� ��ɾ� �� �޽��� ���� ����
    private static final String QUIT_COMMAND = "quit";
    private static final String BLANK_COMMAND = "";

    public static void main(String[] args) {
        Socket socket = null;
        Scanner scanner = null;

        try {
            // �Է��� ���� ��ĳ�� ��ü ����
            scanner = new Scanner(System.in);

            // ���� ���� �� ������ ����
            socket = new Socket();
            socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));

            // ���� ������� ���� Reader�� Writer ����
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            // �г��� ���� �� ������ ���� �޽��� ����
            System.out.print("�г���>>");
            String nickname = scanner.nextLine();
            pw.println("JOIN:" + nickname);
            String ack = br.readLine();
            if ("JOIN:OK".equals(ack)) {
                System.out.println(nickname + "���� �����Ͽ����ϴ�.");
            }

            // Ŭ���̾�Ʈ ������ ���� �� ����
            new ChatClientThread(socket).start();

            // ����� �Է� ó��
            while (true) {
                if (!scanner.hasNextLine()) {
                    continue;
                }
                String message = scanner.nextLine();

                // ����� �Է� ó��
                if (QUIT_COMMAND.equals(message)) {
                    sendMessage(pw, "QUIT");
                    break;
                }
                if (!BLANK_COMMAND.equals(message)) {
                    sendMessage(pw, "MESSAGE:" + message);
                }
            }
        } catch (ConnectException ex) {
            consoleLog("����[" + SERVER_IP + ":" + SERVER_PORT + "]�� ������ �� �����ϴ�.");
        } catch (Exception ex) {
            consoleLog("���� ������ ���α׷��� ���� �մϴ� :" + ex);
        } finally {
            // ���� �ݱ�
            closeSocket(socket);
        }
    }

    // �޽��� ���� �޼���
    private static void sendMessage(PrintWriter pw, String message) {
        if (pw != null) {
            pw.println(message);
        }
    }

    // ���� �ݱ� �޼���
    private static void closeSocket(Socket socket) {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException ex) {
                consoleLog("���� ������ ���α׷��� ���� �մϴ� :" + ex);
            }
        }
    }

    // �α� �޽��� ��� �޼���
    public static void consoleLog(String message) {
        System.out.println("\n[chat client]" + message);
    }
}
