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
    // 서버의 IP 주소와 포트를 설정
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9999;
    
    // 클라이언트에서 사용할 명령어 및 메시지 형식 정의
    private static final String QUIT_COMMAND = "quit";
    private static final String BLANK_COMMAND = "";

    public static void main(String[] args) {
        Socket socket = null;
        Scanner scanner = null;

        try {
            // 입력을 위한 스캐너 객체 생성
            scanner = new Scanner(System.in);

            // 소켓 생성 및 서버에 연결
            socket = new Socket();
            socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));

            // 소켓 입출력을 위한 Reader와 Writer 생성
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            // 닉네임 설정 및 서버에 입장 메시지 전송
            System.out.print("닉네임>>");
            String nickname = scanner.nextLine();
            pw.println("JOIN:" + nickname);
            String ack = br.readLine();
            if ("JOIN:OK".equals(ack)) {
                System.out.println(nickname + "님이 입장하였습니다.");
            }

            // 클라이언트 스레드 생성 및 시작
            new ChatClientThread(socket).start();

            // 사용자 입력 처리
            while (true) {
                if (!scanner.hasNextLine()) {
                    continue;
                }
                String message = scanner.nextLine();

                // 사용자 입력 처리
                if (QUIT_COMMAND.equals(message)) {
                    sendMessage(pw, "QUIT");
                    break;
                }
                if (!BLANK_COMMAND.equals(message)) {
                    sendMessage(pw, "MESSAGE:" + message);
                }
            }
        } catch (ConnectException ex) {
            consoleLog("서버[" + SERVER_IP + ":" + SERVER_PORT + "]에 연결할 수 없습니다.");
        } catch (Exception ex) {
            consoleLog("다음 이유로 프로그램을 종료 합니다 :" + ex);
        } finally {
            // 소켓 닫기
            closeSocket(socket);
        }
    }

    // 메시지 전송 메서드
    private static void sendMessage(PrintWriter pw, String message) {
        if (pw != null) {
            pw.println(message);
        }
    }

    // 소켓 닫기 메서드
    private static void closeSocket(Socket socket) {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException ex) {
                consoleLog("다음 이유로 프로그램을 종료 합니다 :" + ex);
            }
        }
    }

    // 로그 메시지 출력 메서드
    public static void consoleLog(String message) {
        System.out.println("\n[chat client]" + message);
    }
}
