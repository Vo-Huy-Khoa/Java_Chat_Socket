import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class Server {
	private Object lock;
	
	private ServerSocket s;
	private Socket socket;
	static ArrayList<Handler> clients = new ArrayList<Handler>();
	
	private String dataFile = "jar\\data\\accounts.txt";
	

	private void loadAccounts() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf8"));
			
			String info = br.readLine();
			while (info != null && !(info.isEmpty())) {
				clients.add(new Handler(info.split(",")[0], info.split(",")[1], false, lock));
				info = br.readLine();
			}
			
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private void saveAccounts() {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(dataFile), "utf8");
		} catch (Exception ex ) {
			System.out.println(ex.getMessage());
		}
		for (Handler client : clients) {
			pw.print(client.getUsername() + "," + client.getPassword() + "\n");
		}
		pw.println("");
		if (pw != null) {
			pw.close();
		}
	}
	
	public Server() throws IOException {
		try {
			// Object dÃ¹ng Ä‘á»ƒ synchronize cho viá»‡c giao tiáº¿p vá»›i cÃ¡c ngÆ°á»�i dÃ¹ng
			lock = new Object();
			
			// Ä�á»�c danh sÃ¡ch tÃ i khoáº£n Ä‘Ã£ Ä‘Äƒng kÃ½
			this.loadAccounts();
			// Socket dÃ¹ng Ä‘á»ƒ xá»­ lÃ½ cÃ¡c yÃªu cáº§u Ä‘Äƒng nháº­p/Ä‘Äƒng kÃ½ tá»« user
			s = new ServerSocket(9999);
			
			while (true) {
				// Ä�á»£i request Ä‘Äƒng nháº­p/Ä‘Äƒng xuáº¥t tá»« client
				socket = s.accept();
				
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				
				// Ä�á»�c yÃªu cáº§u Ä‘Äƒng nháº­p/Ä‘Äƒng xuáº¥t
				String request = dis.readUTF();
				
				if (request.equals("Sign up")) {
					// YÃªu cáº§u Ä‘Äƒng kÃ½ tá»« user
					
					String username = dis.readUTF();
					String password = dis.readUTF();
					
					// Kiá»ƒm tra tÃªn Ä‘Äƒng nháº­p Ä‘Ã£ tá»“n táº¡i hay chÆ°a
					if (isExisted(username) == false) {
						
						// Táº¡o má»™t Handler Ä‘á»ƒ giáº£i quyáº¿t cÃ¡c request tá»« user nÃ y
						Handler newHandler = new Handler(socket, username, password, true, lock);
						clients.add(newHandler);
						
						// LÆ°u danh sÃ¡ch tÃ i khoáº£n xuá»‘ng file vÃ  gá»­i thÃ´ng bÃ¡o Ä‘Äƒng nháº­p thÃ nh cÃ´ng cho user
						this.saveAccounts();
						dos.writeUTF("Sign up successful");
						dos.flush();
						
						// Táº¡o má»™t Thread Ä‘á»ƒ giao tiáº¿p vá»›i user nÃ y
						Thread t = new Thread(newHandler);
						t.start();
						
						// Gá»­i thÃ´ng bÃ¡o cho cÃ¡c client Ä‘ang online cáº­p nháº­t danh sÃ¡ch ngÆ°á»�i dÃ¹ng trá»±c tuyáº¿n
						updateOnlineUsers();
					} else {
						
						// ThÃ´ng bÃ¡o Ä‘Äƒng nháº­p tháº¥t báº¡i
						dos.writeUTF("This username is being used");
						dos.flush();
					}
				} else if (request.equals("Log in")) {
					// YÃªu cáº§u Ä‘Äƒng nháº­p tá»« user
					
					String username = dis.readUTF();
					String password = dis.readUTF();
					
					// Kiá»ƒm tra tÃªn Ä‘Äƒng nháº­p cÃ³ tá»“n táº¡i hay khÃ´ng
					if (isExisted(username) == true) {
						for (Handler client : clients) {
							if (client.getUsername().equals(username)) {
								// Kiá»ƒm tra máº­t kháº©u cÃ³ trÃ¹ng khá»›p khÃ´ng
								if (password.equals(client.getPassword())) {
									
									// Táº¡o Handler má»›i Ä‘á»ƒ giáº£i quyáº¿t cÃ¡c request tá»« user nÃ y
									Handler newHandler = client;
									newHandler.setSocket(socket);
									newHandler.setIsLoggedIn(true);
									
									// ThÃ´ng bÃ¡o Ä‘Äƒng nháº­p thÃ nh cÃ´ng cho ngÆ°á»�i dÃ¹ng
									dos.writeUTF("Log in successful");
									dos.flush();
									
									// Táº¡o má»™t Thread Ä‘á»ƒ giao tiáº¿p vá»›i user nÃ y
									Thread t = new Thread(newHandler);
									t.start();
									
									// Gá»­i thÃ´ng bÃ¡o cho cÃ¡c client Ä‘ang online cáº­p nháº­t danh sÃ¡ch ngÆ°á»�i dÃ¹ng trá»±c tuyáº¿n
									updateOnlineUsers();
								} else {
									dos.writeUTF("Password is not correct");
									dos.flush();
								}
								break;
							}
						}
						
					} else {
						dos.writeUTF("This username is not exist");
						dos.flush();
					}
				}
				
			}
			
		} catch (Exception ex){
			System.err.println(ex);
		} finally {
			if (s != null) {
				s.close();
			}
		}
	}
	

	public boolean isExisted(String name) {
		for (Handler client:clients) {
			if (client.getUsername().equals(name)) {
				return true;
			}
		}
		return false;
	}
	

	public static void updateOnlineUsers() {
		String message = " ";
		for (Handler client:clients) {
			if (client.getIsLoggedIn() == true) {
				message += ",";
				message += client.getUsername();
			}
		}
		for (Handler client:clients) {
			if (client.getIsLoggedIn() == true) {
				try {
					client.getDos().writeUTF("Online users");
					client.getDos().writeUTF(message);
					client.getDos().flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}


class Handler implements Runnable{

	private Object lock;
	
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private String username;
	private String password;
	private boolean isLoggedIn;
	
	public Handler(Socket socket, String username, String password, boolean isLoggedIn, Object lock) throws IOException {
		this.socket = socket;
		this.username = username;
		this.password = password;
		this.dis = new DataInputStream(socket.getInputStream());
		this.dos = new DataOutputStream(socket.getOutputStream());
		this.isLoggedIn = isLoggedIn;
		this.lock = lock;
	}
	
	public Handler(String username, String password, boolean isLoggedIn, Object lock) {
		this.username = username;
		this.password = password;
		this.isLoggedIn = isLoggedIn;
		this.lock = lock;
	}
	
	public void setIsLoggedIn(boolean IsLoggedIn) {
		this.isLoggedIn = IsLoggedIn;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
		try {
			this.dis = new DataInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public void closeSocket() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean getIsLoggedIn() {
		return this.isLoggedIn;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public DataOutputStream getDos() {
		return this.dos;
	}
	
	@Override
	public void run() {
		
		while (true) {
			try {
				String message = null;
				

				message = dis.readUTF();
				

				if (message.equals("Log out")) {
					
	
					dos.writeUTF("Safe to leave");
					dos.flush();
					
			
					socket.close();
					this.isLoggedIn = false;
					

					Server.updateOnlineUsers();
					break;
				}
				

				else if (message.equals("Text")){
					String receiver = dis.readUTF();
					String content = dis.readUTF();
					
					for (Handler client: Server.clients) {
						if (client.getUsername().equals(receiver)) {
							synchronized (lock) {
								client.getDos().writeUTF("Text");
								client.getDos().writeUTF(this.username);
								client.getDos().writeUTF(content);
								client.getDos().flush();
								break;
							}
						}
					}
				}
				
				// YÃªu cáº§u gá»­i tin nháº¯n dáº¡ng Emoji
				else if (message.equals("Emoji")) {
					String receiver = dis.readUTF();
					String emoji = dis.readUTF();
					
					for (Handler client: Server.clients) {
						if (client.getUsername().equals(receiver)) {
							synchronized (lock) {
								client.getDos().writeUTF("Emoji");
								client.getDos().writeUTF(this.username);
								client.getDos().writeUTF(emoji);
								client.getDos().flush();
								break;
							}
						}
					}
				}
				
				// YÃªu cáº§u gá»­i File
				else if (message.equals("File")) {
					
					// Ä�á»�c cÃ¡c header cá»§a tin nháº¯n gá»­i file
					String receiver = dis.readUTF();
					String filename = dis.readUTF();
					int size = Integer.parseInt(dis.readUTF());
					int bufferSize = 2048;
					byte[] buffer = new byte[bufferSize];
					
					for (Handler client: Server.clients) {
						if (client.getUsername().equals(receiver)) {
							synchronized (lock) {
								client.getDos().writeUTF("File");
								client.getDos().writeUTF(this.username);
								client.getDos().writeUTF(filename);
								client.getDos().writeUTF(String.valueOf(size));
								while (size > 0) {
									// Gá»­i láº§n lÆ°á»£t tá»«ng buffer cho ngÆ°á»�i nháº­n cho Ä‘áº¿n khi háº¿t file
									dis.read(buffer, 0, Math.min(size, bufferSize));
									client.getDos().write(buffer, 0, Math.min(size, bufferSize));
									size -= bufferSize;
								}
								client.getDos().flush();
								break;
							}
						}
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}