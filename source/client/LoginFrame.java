import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout.*;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.awt.*;

public class LoginFrame extends JFrame {
	
	private JPanel contentPane;
	private JTextField txtUsername;
	private JPasswordField txtPassword;

	private String host = "localhost";
	private int port =9999;
	private Socket socket;
	
	private DataInputStream dis;
	private DataOutputStream dos;
	
	private String username;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame frame = new LoginFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public LoginFrame() {
		setTitle("CHAT");
		
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 516, 501);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(143, 188, 143));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel headerPanel = new JPanel();
		headerPanel.setBounds(0, 5, 502, 37);
		headerPanel.setBackground(new Color(160,190,223));
		
		JLabel lbUsername = new JLabel("Tên đăng nhập");
		lbUsername.setBounds(23, 161, 122, 26);
		lbUsername.setFont(new Font("Arial", Font.BOLD, 17));
		
		JLabel lbPassword = new JLabel("Mật khẩu");
		lbPassword.setBounds(32, 225, 113, 24);
		lbPassword.setFont(new Font("Arial", Font.BOLD, 17));
		
		txtUsername = new JTextField();
		txtUsername.setBounds(166, 161, 246, 31);
		txtUsername.setBackground(new Color(176, 224, 230));
		txtUsername.setColumns(10);
		
		txtPassword = new JPasswordField();
		txtPassword.setBounds(166, 222, 246, 34);
		txtPassword.setBackground(new Color(176, 224, 230));
		
		JPanel buttons = new JPanel();
		buttons.setBounds(54, 305, 414, 69);
		buttons.setBackground(new Color(143, 188, 143));
		JPanel notificationContainer = new JPanel();
		notificationContainer.setBounds(76, 266, 367, 37);
		notificationContainer.setBackground(new Color(143, 188, 143));
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(214, 60, 158, 69);
		lblNewLabel.setIcon(new ImageIcon("E:\\icons8-change-user-100.png"));
		
		JLabel notification = new JLabel("");
		notification.setForeground(Color.RED);
		notification.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		notificationContainer.add(notification);
		
		JButton login = new JButton("Đăng nhập");
		login.setFont(new Font("Tahoma", Font.PLAIN, 12));
		login.setHorizontalAlignment(SwingConstants.LEFT);
		login.setBounds(32, 25, 154, 45);
		login.setIcon(new ImageIcon("E:\\icons8-check-inbox-48.png"));
		JButton signup = new JButton("Đăng ký");
		signup.setFont(new Font("Tahoma", Font.PLAIN, 12));
		signup.setBounds(234, 25, 143, 45);
		signup.setIcon(new ImageIcon("E:\\icons8-check-48.png"));
		
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String response = Login(txtUsername.getText(), String.copyValueOf(txtPassword.getPassword()));
				
				
				if ( response.equals("Log in successful") ) {
					username = txtUsername.getText();
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								ChatFrame frame = new ChatFrame(username, dis, dos);
								frame.setVisible(true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					dispose();
				} else {
					login.setEnabled(false);
					signup.setEnabled(false);
					txtPassword.setText("");
					notification.setText(response);
				}
			}
		});
		buttons.setLayout(null);
		buttons.add(login);
		
		signup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JPasswordField confirm = new JPasswordField();
				
				
				
			    int action = JOptionPane.showConfirmDialog(null, confirm,"Nhập lại mật khẩu!",JOptionPane.OK_CANCEL_OPTION);
			    if (action == JOptionPane.OK_OPTION) {
			    	if (String.copyValueOf(confirm.getPassword()).equals(String.copyValueOf(txtPassword.getPassword()))) {
			    		String response = Signup(txtUsername.getText(),String.copyValueOf(txtPassword.getPassword()));
					
			    		
						if ( response.equals("Sign up successful") ) {
							username = txtUsername.getText();
							EventQueue.invokeLater(new Runnable() {
								public void run() {
									try {
										
										int confirm = JOptionPane.showConfirmDialog(null, "Đăng ký thành công!\nMời vào CHAT", "", JOptionPane.DEFAULT_OPTION);
										 
										ChatFrame frame = new ChatFrame(username, dis, dos);
										frame.setVisible(true);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
							dispose();
						} else {
							login.setEnabled(false);
							signup.setEnabled(false);
							txtPassword.setText("");
							notification.setText(response);
						}
					} else {
			    		notification.setText("Nhập mật khẩu bị sai!");
			    	}
			    }
			}
		});
		buttons.add(signup);
		
		JLabel headerContent = new JLabel("LOGIN");
		headerContent.setForeground(new Color(255, 0, 0));
		headerContent.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
		headerPanel.add(headerContent);
		
		txtUsername.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtUsername.getText().isBlank() || String.copyValueOf(txtPassword.getPassword()).isBlank()) {
					login.setEnabled(false);
					signup.setEnabled(false);
				} else {
					login.setEnabled(true);
					signup.setEnabled(true);
				}
			}
		});
		
		txtPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtUsername.getText().isBlank() || String.copyValueOf(txtPassword.getPassword()).isBlank()) {
					login.setEnabled(false);
					signup.setEnabled(false);
				} else {
					login.setEnabled(true);
					signup.setEnabled(true);
				}
			}
		});
		
		this.getRootPane().setDefaultButton(login);
		contentPane.setLayout(null);
		contentPane.add(headerPanel);
		contentPane.add(lbUsername);
		contentPane.add(lbPassword);
		contentPane.add(txtPassword);
		contentPane.add(txtUsername);
		contentPane.add(notificationContainer);
		contentPane.add(buttons);
		contentPane.add(lblNewLabel);
	}
	

	public String Login(String username, String password) {
		try {
			Connect();
			
			dos.writeUTF("Log in");
			dos.writeUTF(username);
			dos.writeUTF(password);
			dos.flush();
			
			String response = dis.readUTF();
			return response;
			
		} catch (IOException e) {
			e.printStackTrace();
			return "Network error: Log in fail";
		}
	}
	

	public String Signup(String username, String password) {
		try {
			Connect();
			
			dos.writeUTF("Sign up");
			dos.writeUTF(username);
			dos.writeUTF(password);
			dos.flush();
			
			String response = dis.readUTF();
			return response;
			
		} catch (IOException e) {
			e.printStackTrace();
			return "Network error: Sign up fail";
		}
	}
	
	
	public void Connect() {
		try {
			if (socket != null) {
				socket.close();
			}
			socket = new Socket(host, port);
			this.dis = new DataInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public String getUsername() {
		return this.username;
	}
}
