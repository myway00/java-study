# JAVA - 소켓 다중 유저 채팅 프로그램 
<details>
<summary>ChatServer.java</summary>
<div markdown="1">
 
| 단계 | 코드 블록 또는 기능                  | 설명                                                         |
|------|--------------------------------------|--------------------------------------------------------------|
| 1    | `public static void main(String[] args)` | 프로그램의 시작점. `ChatServer` 클래스의 인스턴스를 생성하고 `runServer()` 메서드 호출. |
| 2    | `private void runServer()`           | 서버의 주요 로직이 실행되는 메서드.                            |
| 2.1  | 생성 및 초기화                        | `ServerSocket` 생성 및 속성 설정.                            |
| 2.2  | 서버 소켓 바인딩 및 포트 설정               | 서버 소켓을 특정 IP 주소와 포트에 바인딩.                    |
| 2.3  | 무한 루프                            | 클라이언트 연결 수락을 위한 무한 루프 시작.                |
| 2.3.1| `Socket socket = serverSocket.accept();` | 클라이언트의 연결 요청을 대기하고, 연결이 수락되면 소켓 생성. |
| 2.3.2| `Thread thread = new ChatServerThread(socket, currentUsers);` | 연결된 클라이언트와 통신하기 위한 스레드 생성.              |
| 2.3.3| `thread.start();`                    | 스레드 시작, 클라이언트와의 통신 시작.                     |
| 2.4  | `private void closeServerSocket()`   | 서버 소켓을 닫는 메서드.                                    |
| 2.4.1| 서버 소켓 닫기                        | 서버 소켓이 닫혀 있지 않은 경우 소켓 닫음.                 |
| 3    | `public static void consoleLog(String message)` | 로그 메시지를 출력하는 정적 메서드.                          |
 
</div>
</details>

<details>
<summary>ChatServerThread.java</summary>
<div markdown="1">

| 단계 | 코드 블록 또는 기능                            | 설명                                                         |
|------|----------------------------------------------|--------------------------------------------------------------|
| 1    | `public class ChatServerThread extends Thread` | `ChatServerThread` 클래스를 선언하고 `Thread` 클래스를 확장.   |
| 2    | `private Socket socket;`                      | 클라이언트 소켓을 담을 멤버 변수 선언.                       |
| 3    | `private String name;`                        | 클라이언트 이름을 담을 멤버 변수 선언.                      |
| 4    | `private List<PrintWriter> currentUsers;`     | 현재 사용자 목록을 담을 리스트 멤버 변수 선언.              |
| 5    | 상수 선언: `JOIN_COMMAND`, `MESSAGE_COMMAND`, `QUIT_COMMAND` | 각각 'JOIN', 'MESSAGE', 'QUIT' 명령어를 상수로 선언.    |
| 6    | 생성자: `public ChatServerThread(Socket socket, List<PrintWriter> currentUsers)` | 소켓과 사용자 목록을 받아 멤버 변수 초기화.            |
| 7    | `@Override` 메서드: `public void run()`       | `Thread`의 `run()` 메서드 오버라이딩. 클라이언트와의 통신 시작. |
| 8    | `InetSocketAddress remoteSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();` | 클라이언트 소켓 주소 정보 가져오기.       |
| 9    | 소켓 입력 및 출력 스트림 설정                    | 클라이언트와의 입출력 스트림 설정.                           |
| 10   | 무한 루프: `while (true)`                    | 클라이언트와 계속 통신을 위한 무한 루프 시작.               |
| 11   | 클라이언트로부터 메시지 읽어들임                   | 클라이언트가 보낸 메시지를 읽어들임.                        |
| 12   | 수신한 메시지 확인 및 처리                        | 메시지가 `null`인지 확인하고 처리.                         |
| 13   | 메시지 분할 및 명령어 처리                        | 메시지를 `:`로 분할하여 명령어와 인자로 분리하여 처리.      |
| 14   | 예외 처리: `SocketException`                  | 클라이언트 소켓 비정상 종료 처리 및 로그 출력.              |
| 15   | 예외 처리: `IOException`                      | I/O 예외 처리 및 로그 출력.                               |
| 16   | 소켓 닫기 및 정리                              | 소켓 닫기 및 연관된 작업 정리.                            |
| 17   | 명령어 처리: `doQuit(PrintWriter user)`        | 사용자 제거 및 퇴장 메시지 브로드캐스팅.                  |
| 18   | 명령어 처리: `doMessage(String message)`      | 메시지 브로드캐스팅.                                       |
| 19   | 명령어 처리: `doJoin(String name, PrintWriter currentUsers)` | 사용자 입장 처리, 메시지 브로드캐스팅 및 사용자 추가. |
| 20   | 사용자 추가: `addUser(PrintWriter user)`      | 사용자 목록에 사용자 추가.                                |
| 21   | 사용자 제거: `deleteUser(PrintWriter user)`   | 사용자 목록에서 사용자 제거.                              |
| 22   | 메시지 브로드캐스팅: `broadcastMessage(String message)` | 모든 사용자에게 메시지 브로드캐스팅.            |

 
</div>

</details>

<details>
<summary>ChatClient.java</summary>
<div markdown="1">

| 단계 | 코드 블록 또는 기능                            | 설명                                                         |
|------|----------------------------------------------|--------------------------------------------------------------|
| 1    | `public class ChatClient`                    | `ChatClient` 클래스를 선언.                                  |
| 2    | 상수 선언: `SERVER_IP`, `SERVER_PORT`          | 서버의 IP 주소와 포트를 상수로 선언.                        |
| 3    | 상수 선언: `QUIT_COMMAND`, `BLANK_COMMAND`    | 사용할 명령어와 빈 메시지 형식을 상수로 선언.              |
| 4    | `public static void main(String[] args)`      | 프로그램의 시작점. 클라이언트 동작을 정의.                  |
| 5    | 소켓 및 스캐너 객체 생성                        | 소켓과 스캐너 객체를 초기화.                                 |
| 6    | 소켓 생성 및 서버 연결                          | 소켓 생성 및 서버에 연결.                                   |
| 7    | 소켓 입출력을 위한 `PrintWriter` 및 `BufferedReader` 생성 | 소켓 입출력 스트림을 생성.                                |
| 8    | 닉네임 설정 및 입장 메시지 전송                   | 닉네임 설정 후 입장 메시지 전송.                            |
| 9    | 클라이언트 스레드 생성 및 시작                   | `ChatClientThread` 스레드 생성 및 시작.                    |
| 10   | 사용자 입력 처리                               | 사용자 입력 처리 및 메시지 전송.                            |
| 11   | 메시지 전송 메서드                              | 메시지를 전송하는 메서드 정의.                              |
| 12   | 소켓 닫기 메서드                               | 소켓을 닫는 메서드 정의.                                    |
| 13   | 로그 메시지 출력 메서드                         | 로그 메시지를 출력하는 메서드 정의.                         |


</div>
</details>

<details>

<summary>ChatClientThread.java</summary>
<div markdown="1">

| 단계 | 코드 블록 또는 기능                            | 설명                                                      |
|------|----------------------------------------------|-----------------------------------------------------------|
| 1    | `public class ChatClientThread extends Thread` | `ChatClientThread` 클래스를 선언하며 `Thread` 상속.       |
| 2    | 멤버 변수 `socket` 선언 및 생성자 정의           | 생성자를 통해 소켓을 멤버 변수에 저장.                   |
| 3    | `public void run()`                          | 스레드 실행 메서드를 오버라이딩하여 정의.                 |
| 3.1  | 소켓의 입력 스트림 읽기 및 `BufferedReader` 생성 | 소켓의 입력 스트림을 읽어오기 위한 `BufferedReader` 생성. |
| 3.2  | 메시지 출력 루프                                | 서버로부터 받은 메시지를 출력하는 무한 루프 시작.         |
| 3.2.1| `String message = br.readLine();`          | 서버에서 메시지 읽어오기.                                |
| 3.2.2| `if (message == null) break;`             | 읽어온 메시지가 없으면 루프 종료.                       |
| 3.2.3| `System.out.println(message);`            | 서버로부터 받은 메시지를 출력.                          |
| 3.3  | `catch (SocketException ex)`                | 소켓 예외 처리: 연결이 끊길 때 예외 메시지 출력.        |
| 3.4  | `catch (IOException ex)`                   | I/O 예외 처리: 프로그램 종료 메시지 출력.               |
| 3.5  | `finally` 블록                              | 스레드 종료 시 프로그램 종료.                           |


</div>
</details>
