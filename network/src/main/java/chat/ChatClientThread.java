package hw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class ChatClientThread extends Thread {
    private Socket socket;

    // ������: ������ ���޹޾� ��� ������ ����
    public ChatClientThread(Socket socket) {
        this.socket = socket;
    }

    // ������ ���� �޼���
    @Override
    public void run() {
        try {
            // ������ �Է� ��Ʈ���� �о���� ���� BufferedReader ����
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            // ���� ������ ���� �����κ��� ���� �޽��� ���
            while (true) {
                String message = br.readLine();
                if (message == null) {
                    break; // ������ �޽����� ������ ������ ���� ����
                }
                System.out.println(message);
            }
        } catch (SocketException ex) {
            // ���� ���� ó��: ������ ���� �� ���� �޽��� ���
            ChatClient.consoleLog("" + ex);
        } catch (IOException ex) {
            // IO ���� ó��: ���α׷� ���� �޽��� ���
            ChatClient.consoleLog("���� ������ ���α׷��� ���� �մϴ� :" + ex);
        } finally {
            System.exit(0); // ������ ���� �� ���α׷� ����
        }
    }
}
