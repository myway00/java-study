package chat;

import java.io.*;
import java.net.*;
import java.util.List;

public class ChatServerThread extends Thread {
    private Socket socket;
    private String name;
    private List<PrintWriter> currentUsers;

    private static final String JOIN_COMMAND = "JOIN";
    private static final String MESSAGE_COMMAND = "MESSAGE";
    private static final String QUIT_COMMAND = "QUIT";
    
    public ChatServerThread(Socket socket, List<PrintWriter> currentUsers) {
        this.socket = socket;
        this.currentUsers = currentUsers;
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = null;
        PrintWriter printWriter = null;

        try { 
            // Ŭ���̾�Ʈ ���� �ּ� ���� ��������
            InetSocketAddress remoteSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
            ChatServer.consoleLog(
                    "Connected by client[" +
                            remoteSocketAddress.getAddress().getHostAddress() + ":" +
                            remoteSocketAddress.getPort() +
                            "]");
 
            // ���� �Է� ��Ʈ���� ��� ��Ʈ�� ����
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
 
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    // Ŭ���̾�Ʈ�� ������ ��� ó��
                    doQuit(printWriter);
                    ChatServer.consoleLog("Closed by client");
                    break;
                }
                String[] tokens = line.split(":");
                if (tokens.length >= 2) {
                    String command = tokens[0];
                    String argument = tokens[1];
                    processCommand(command, argument, printWriter);
                }
            }
        } catch (SocketException e) {
            // Ŭ���̾�Ʈ ���� ������ ���� ó��
            doQuit(printWriter);
            ChatServer.consoleLog("Abnormal closure by client");
        } catch (IOException e) {
            // I/O ���� ó��
            doQuit(printWriter);
            ChatServer.consoleLog("Error: " + e);
        } finally {
            closeSocketQuietly();
        }
    }
    
    private void processCommand(String command, String argument, PrintWriter user) {
        if (JOIN_COMMAND.equals(command)) {
            doJoin(argument, user);
        } else if (MESSAGE_COMMAND.equals(command)) {
            doMessage(argument);
        } else if (QUIT_COMMAND.equals(command)) {
            doQuit(user);
        }
    }

    private void closeSocketQuietly() {
        try {
            // ���� �ݱ�
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            ChatServer.consoleLog("Error: " + e);
        }
    }

    private void doQuit(PrintWriter user) {
        deleteUser(user);
        if (name != null) {
            // ���� �޽��� ��ε�ĳ����
            broadcastMessage(name + "���� �����Ͽ����ϴ�.");
        }
    }

    private void doMessage(String message) {
        // �޽��� ��ε�ĳ����
        broadcastMessage(name + ":" + message);
    }

    private void doJoin(String name, PrintWriter currentUsers) { 
        this.name = name;
 
        String entryMessage = name + "���� �����߽��ϴ�.";
        // ���� �޽��� ��ε�ĳ����
        broadcastMessage(entryMessage); 
        addUser(currentUsers); 
        currentUsers.println("JOIN:OK");
    }

    private void addUser(PrintWriter user) {
        synchronized (currentUsers) {
            currentUsers.add(user);
        }
    }

    private void deleteUser(PrintWriter user) {
        synchronized (currentUsers) {
            currentUsers.remove(user);
        }
    }

    private void broadcastMessage(String message) {
        synchronized (currentUsers) {
            for (PrintWriter user : currentUsers) {
                user.println(message);
            }
        }
    }
}