package chat.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ChatWindow {
    private Frame frame;
    private Panel panel;
    private Button buttonSend;
    private TextField textField;
    private TextArea textArea;

    private Socket socket;

    private PrintWriter writer;
    private BufferedReader reader;

    // 생성자
    public ChatWindow(String nickname, Socket socket) {
        frame = new Frame(nickname);
        panel = new Panel();
        buttonSend = new Button("Send");
        textField = new TextField();
        textArea = new TextArea(30, 80);

        this.socket = socket;
    }

    // 대화창을 화면에 보여주는 메서드
    public void show() throws IOException {
    		
    		// 1. 채팅 스타일 
    	    initializeUI();

    		// 2. IOStream 받아오기
    	    writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
    	    reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

    	    // 3. ChatClientThread 생성하고 실행
    	    new ChatClientThread().start();
    	}

private void initializeUI() {
    // Send 버튼
    buttonSend.setBackground(Color.GRAY);
    buttonSend.setForeground(Color.WHITE);
    buttonSend.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            sendMessage();
        }
    });

    // 입력 필드
    textField.setColumns(80);
    textField.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent event) {
            char keyCode = event.getKeyChar();
            if (keyCode == KeyEvent.VK_ENTER) {
                sendMessage();
            }
        }
    });

    // 패널
    panel.setBackground(Color.LIGHT_GRAY);
    panel.add(textField);
    panel.add(buttonSend);
    frame.add(BorderLayout.SOUTH, panel);

    // 대화 내용 표시 영역
    textArea.setEditable(false);
    frame.add(BorderLayout.CENTER, textArea);

    // 프레임 종료 이벤트
    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            finish();
        }
    });

    frame.setVisible(true);
    frame.pack();
}

    
    // 대화 종료 메서드
    private void finish() {
        try {
            // 소켓 닫기
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            // 어플리케이션 종료
            System.exit(0);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 대화 내용을 업데이트하는 메서드
    private void updateTextArea(String message) {
        textArea.append(message);
        textArea.append("\n");
    }

    // 메시지 전송 메서드
    private void sendMessage() {
        String message = textField.getText();
        writer.println("MESSAGE:" + message);

        textField.setText("");
        textField.requestFocus();
    }

    // 대화를 처리하는 쓰레드 클래스
    private class ChatClientThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    String message = reader.readLine();
                    if (message == null) {
                        break;
                    }

                    Thread.sleep(1);
                    updateTextArea(message);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (SocketException ex) {
                ChatClientApp.consoleLog("" + ex);
            } catch (IOException ex) {
                ChatClientApp.consoleLog("" + ex);
            } finally {
                finish();
            }
        }
    }
}
