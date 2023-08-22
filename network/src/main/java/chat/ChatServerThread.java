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
            // 클라이언트 소켓 주소 정보 가져오기
            InetSocketAddress remoteSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
            ChatServer.consoleLog(
                    "Connected by client[" +
                            remoteSocketAddress.getAddress().getHostAddress() + ":" +
                            remoteSocketAddress.getPort() +
                            "]");
 
            // 소켓 입력 스트림과 출력 스트림 설정
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
 
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    // 클라이언트가 종료한 경우 처리
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
            // 클라이언트 소켓 비정상 종료 처리
            doQuit(printWriter);
            ChatServer.consoleLog("Abnormal closure by client");
        } catch (IOException e) {
            // I/O 예외 처리
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
            // 소켓 닫기
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
            // 퇴장 메시지 브로드캐스팅
            broadcastMessage(name + "님이 퇴장하였습니다.");
        }
    }

    private void doMessage(String message) {
        // 메시지 브로드캐스팅
        broadcastMessage(name + ":" + message);
    }

    private void doJoin(String name, PrintWriter currentUsers) { 
        this.name = name;
 
        String entryMessage = name + "님이 입장했습니다.";
        // 입장 메시지 브로드캐스팅
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