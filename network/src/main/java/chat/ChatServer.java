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
        // 기존의 ArrayList를 동기화된 리스트로 변환
        //  currentUsers 리스트에 대한 접근은 내부적으로 동기화되어 여러 쓰레드가 안전하게 사용
    	List<PrintWriter> currentUsers = List<PrintWriter> currentUsers = Collections.synchronizedList(new ArrayList<>());

        try {
            // 1. 서버 소켓 생성 
            serverSocket = new ServerSocket();

            // 1-1. SO_REUSEADDR 소켓이 사용하는 포트를 더 빨리 재사용하도록 허용하는 역할
            serverSocket.setReuseAddress(true);

            // 2. binding - 백로그(backlog) 큐의 크기
            String localhost = InetAddress.getLocalHost().getHostAddress();
            serverSocket.bind(new InetSocketAddress("0.0.0.0", PORT), ALLOWED_CONNECTION_SIZE);
            consoleLog("ChatServer Starts at " + PORT);

            while (true) { 
                // 서버가 계속해서 클라이언트의 연결을 받아들이기 위한 무한 루프입니다.
                // 이 루프는 서버가 실행 중일 동안 계속해서 클라이언트의 연결을 처리합니다.
                
                Socket socket = serverSocket.accept();
                // 새로운 클라이언트의 연결을 수락하고 해당 클라이언트와 통신하기 위한 소켓을 생성합니다.
                // accept() 메서드는 클라이언트의 연결이 올 때까지 블로킹됩니다.
                
                Thread thread = new ChatServerThread(socket, currentUsers);
                // 새로운 클라이언트와 통신하기 위한 쓰레드를 생성합니다.
                // ChatServerThread 클래스는 클라이언트와의 실제 통신을 처리하는 로직이 담겨있는 클래스입니다.
                // 해당 클래스는 생성자에 소켓과 현재 사용자 목록을 전달받아 초기화됩니다.
                
                thread.start();
                // 쓰레드를 시작하고, ChatServerThread 내의 run() 메서드가 실행되게 합니다.
                // 이렇게 함으로써 서버는 여러 클라이언트들과 동시에 병렬적으로 통신할 수 있습니다.
            }

        } catch (IOException e) {
			consoleLog( "error:" + e );
        } finally {
            closeServerSocket();
        }
    }

    private void closeServerSocket() {
        try {
            // 서버 소켓이 null이 아니고 이미 닫혀있지 않은 경우에만 아래 코드를 실행합니다.
            if (serverSocket != null && !serverSocket.isClosed()) {
                // 서버 소켓을 닫습니다. 클라이언트의 연결 수락을 중단하고 리소스를 해제합니다.
                serverSocket.close();
            }
        } catch (IOException ex) {
            // 서버 소켓을 닫는 과정에서 발생한 예외를 처리합니다.
            // 예외 내용을 출력하여 로그에 기록합니다.
            consoleLog("error:" + ex);
        }
    }


    public static void consoleLog(String message) {
        System.out.println("[chat server]" + message);
    }
}

