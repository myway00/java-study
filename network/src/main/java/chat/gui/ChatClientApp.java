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
    private static final String SERVER_IP = "127.0.0.1"; // 서버 IP 주소
    private static final int SERVER_PORT = 9999; // 서버 포트 번호

    public static void main(String[] args) {
        String nickname = null; // 사용자 닉네임을 저장할 변수
        Scanner scanner = null; // 사용자 입력을 받기 위한 스캐너 객체
        Socket socket = null; // 클라이언트와 서버 간의 소켓 연결을 위한 소켓 객체

        try {
            // 1. 키보드 입력을 받기 위한 스캐너 객체 생성
            scanner = new Scanner(System.in);

            // 사용자로부터 대화명 입력 받기
            while (true) {
                System.out.println("대화명을 입력하세요.");
                System.out.print(">>> ");
                nickname = scanner.nextLine();

                if (!nickname.isEmpty()) {
                    break;
                }

                System.out.println("대화명은 한글자 이상 입력해야 합니다.\n");
            }

            // 1. 소켓 생성
            socket = new Socket();

            // 2. 서버에 연결
            socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));

            // 3. 입출력 스트림 생성
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            // 4. 서버에 대화명 전송 (JOIN 요청)
            pw.println("JOIN:" + nickname);
            String ack = br.readLine(); // 서버로부터의 응답을 받음

            // 대화명 전송에 대한 응답이 "JOIN:OK"인 경우에만 채팅 창을 열어줌
            if ("JOIN:OK".equals(ack)) {
                new ChatWindow(nickname, socket).show(); // 채팅 창 생성 및 표시
            }
        } catch (ConnectException ex) {
            consoleLog("서버[" + SERVER_IP + ":" + SERVER_PORT + "]에 연결할 수 없습니다.");
        } catch (Exception ex) {
            consoleLog("다음 이유로 프로그램을 종료 합니다: " + ex);
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
