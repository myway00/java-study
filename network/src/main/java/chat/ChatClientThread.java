package hw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class ChatClientThread extends Thread {
    private Socket socket;

    // 생성자: 소켓을 전달받아 멤버 변수에 저장
    public ChatClientThread(Socket socket) {
        this.socket = socket;
    }

    // 스레드 실행 메서드
    @Override
    public void run() {
        try {
            // 소켓의 입력 스트림을 읽어오기 위한 BufferedReader 생성
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            // 무한 루프를 통해 서버로부터 받은 메시지 출력
            while (true) {
                String message = br.readLine();
                if (message == null) {
                    break; // 서버가 메시지를 보내지 않으면 루프 종료
                }
                System.out.println(message);
            }
        } catch (SocketException ex) {
            // 소켓 예외 처리: 연결이 끊길 때 예외 메시지 출력
            ChatClient.consoleLog("" + ex);
        } catch (IOException ex) {
            // IO 예외 처리: 프로그램 종료 메시지 출력
            ChatClient.consoleLog("다음 이유로 프로그램을 종료 합니다 :" + ex);
        } finally {
            System.exit(0); // 스레드 종료 시 프로그램 종료
        }
    }
}
