package chat.gui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ChatClientApp {
    private static final String SERVER_IP = "127.0.0.1"; // ���� IP �ּ�
    private static final int SERVER_PORT = 9999; // ���� ��Ʈ ��ȣ

    public static void main(String[] args) {
        String nickname = null; // ����� �г����� ������ ����
        Scanner scanner = null; // ����� �Է��� �ޱ� ���� ��ĳ�� ��ü
        Socket socket = null; // Ŭ���̾�Ʈ�� ���� ���� ���� ������ ���� ���� ��ü

        try {
            // 1. Ű���� �Է��� �ޱ� ���� ��ĳ�� ��ü ����
            scanner = new Scanner(System.in);

            // ����ڷκ��� ��ȭ�� �Է� �ޱ�
            while (true) {
                System.out.println("��ȭ���� �Է��ϼ���.");
                System.out.print(">>> ");
                nickname = scanner.nextLine();

                if (!nickname.isEmpty()) {
                    break;
                }

                System.out.println("��ȭ���� �ѱ��� �̻� �Է��ؾ� �մϴ�.\n");
            }

            // 1. ���� ����
            socket = new Socket();

            // 2. ������ ����
            socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));

            // 3. ����� ��Ʈ�� ����
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            // 4. ������ ��ȭ�� ���� (JOIN ��û)
            pw.println("JOIN:" + nickname);
            String ack = br.readLine(); // �����κ����� ������ ����

            // ��ȭ�� ���ۿ� ���� ������ "JOIN:OK"�� ��쿡�� ä�� â�� ������
            if ("JOIN:OK".equals(ack)) {
                new ChatWindow(nickname, socket).show(); // ä�� â ���� �� ǥ��
            }
        } catch (ConnectException ex) {
            consoleLog("����[" + SERVER_IP + ":" + SERVER_PORT + "]�� ������ �� �����ϴ�.");
        } catch (Exception ex) {
            consoleLog("���� ������ ���α׷��� ���� �մϴ�: " + ex);
        } finally { 
            closeScanner(scanner); 
        }
    }

    public static void consoleLog(String message) {
        System.out.println("\n[chat client]" + message);
    }
    
    private static void closeScanner(Scanner scanner) {
        if (scanner != null) {
            scanner.close();
        }
    }
    
}
