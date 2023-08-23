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

    // ������
    public ChatWindow(String nickname, Socket socket) {
        frame = new Frame(nickname);
        panel = new Panel();
        buttonSend = new Button("Send");
        textField = new TextField();
        textArea = new TextArea(30, 80);

        this.socket = socket;
    }

    // ��ȭâ�� ȭ�鿡 �����ִ� �޼���
    public void show() throws IOException {
    		
    		// 1. ä�� ��Ÿ�� 
    	    initializeUI();

    		// 2. IOStream �޾ƿ���
    	    writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
    	    reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

    	    // 3. ChatClientThread �����ϰ� ����
    	    new ChatClientThread().start();
    	}

private void initializeUI() {
    // Send ��ư
    buttonSend.setBackground(Color.GRAY);
    buttonSend.setForeground(Color.WHITE);
    buttonSend.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            sendMessage();
        }
    });

    // �Է� �ʵ�
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

    // �г�
    panel.setBackground(Color.LIGHT_GRAY);
    panel.add(textField);
    panel.add(buttonSend);
    frame.add(BorderLayout.SOUTH, panel);

    // ��ȭ ���� ǥ�� ����
    textArea.setEditable(false);
    frame.add(BorderLayout.CENTER, textArea);

    // ������ ���� �̺�Ʈ
    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            finish();
        }
    });

    frame.setVisible(true);
    frame.pack();
}

    
    // ��ȭ ���� �޼���
    private void finish() {
        try {
            // ���� �ݱ�
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            // ���ø����̼� ����
            System.exit(0);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ��ȭ ������ ������Ʈ�ϴ� �޼���
    private void updateTextArea(String message) {
        textArea.append(message);
        textArea.append("\n");
    }

    // �޽��� ���� �޼���
    private void sendMessage() {
        String message = textField.getText();
        writer.println("MESSAGE:" + message);

        textField.setText("");
        textField.requestFocus();
    }

    // ��ȭ�� ó���ϴ� ������ Ŭ����
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
