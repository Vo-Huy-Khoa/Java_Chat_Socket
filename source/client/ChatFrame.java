import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;


public class ChatFrame extends JFrame {
	
	private JButton btnFile;
	private JButton btnSend;
	private JScrollPane chatPanel;
	private JLabel lbReceiver = new JLabel(" ");
	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextPane chatWindow;
	JComboBox<String> onlineUsers = new JComboBox<String>();
	
	private String username;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	private HashMap<String, JTextPane> chatWindows = new HashMap<String, JTextPane>();
	
	Thread receiver;
	
	private void autoScroll() {
		chatPanel.getVerticalScrollBar().setValue(chatPanel.getVerticalScrollBar().getMaximum());
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	

	private void newEmoji(String username, String emoji, Boolean yourMessage) {
		
		StyledDocument doc;
		if (username.equals(this.username)) {
			doc = chatWindows.get(lbReceiver.getText()).getStyledDocument();
		} else {
			doc = chatWindows.get(username).getStyledDocument();
		}
		
		Style userStyle = doc.getStyle("User style");
		if (userStyle == null) {
			userStyle = doc.addStyle("User style", null);
			StyleConstants.setBold(userStyle, true);
		}
		
	    if (yourMessage == true) {
	    	StyleConstants.setForeground(userStyle, Color.red);
	    } else {
	    	StyleConstants.setForeground(userStyle, Color.BLUE);
	    }
	    
	    // In ra mÃ n hÃ¬nh tÃªn ngÆ°á»�i gá»­i
	    try { doc.insertString(doc.getLength(), username + ": ", userStyle); }
        catch (BadLocationException e){}
	    
	    Style iconStyle = doc.getStyle("Icon style");
	    if (iconStyle == null) {
	    	iconStyle = doc.addStyle("Icon style", null);
	    }
	    
	    StyleConstants.setIcon(iconStyle, new ImageIcon(emoji));
	    
	    // In ra mÃ n hÃ¬nh Emoji
	    try { doc.insertString(doc.getLength(), "invisible text", iconStyle); }
        catch (BadLocationException e){}
	    
	    // Xuá»‘ng dÃ²ng
	    try { doc.insertString(doc.getLength(), "\n", userStyle); }
        catch (BadLocationException e){}
	    
	    autoScroll();
	}
	

	private void newFile(String username, String filename, byte[] file, Boolean yourMessage) {

		StyledDocument doc;
		String window = null;
		if (username.equals(this.username)) {
			window = lbReceiver.getText();
		} else {
			window = username;
		}
		doc = chatWindows.get(window).getStyledDocument();
		
		Style userStyle = doc.getStyle("User style");
		if (userStyle == null) {
			userStyle = doc.addStyle("User style", null);
			StyleConstants.setBold(userStyle, true);
		}
		
		if (yourMessage == true) {
	    	StyleConstants.setForeground(userStyle, Color.red);
	    } else {
	    	StyleConstants.setForeground(userStyle, Color.BLUE);
	    }

	    try { doc.insertString(doc.getLength(), username + ": ", userStyle); }
        catch (BadLocationException e){}
		
	    Style linkStyle = doc.getStyle("Link style");
	    if (linkStyle == null) {
	    	linkStyle = doc.addStyle("Link style", null);
	    	StyleConstants.setForeground(linkStyle, Color.BLUE);
			StyleConstants.setUnderline(linkStyle, true);
			StyleConstants.setBold(linkStyle, true);
			linkStyle.addAttribute("link", new HyberlinkListener(filename, file));
	    }
	    
	    if (chatWindows.get(window).getMouseListeners() != null) {
	    	// Táº¡o MouseListener cho cÃ¡c Ä‘Æ°á»�ng dáº«n táº£i vá»� file
			chatWindows.get(window).addMouseListener(new MouseListener() {
	
				@Override
				public void mouseClicked(MouseEvent e)
		        {
					Element ele = doc.getCharacterElement(chatWindow.viewToModel(e.getPoint()));
		            AttributeSet as = ele.getAttributes();
		            HyberlinkListener listener = (HyberlinkListener)as.getAttribute("link");
		            if(listener != null)
		            {
		                listener.execute();
		            }
		        }
				
				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
	
				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
	
				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
	
				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
			});
		}
	    
	    // In ra Ä‘Æ°á»�ng dáº«n táº£i file
		try {
			doc.insertString(doc.getLength(),"<" + filename + ">", linkStyle);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		
		// Xuá»‘ng dÃ²ng
		try {
			doc.insertString(doc.getLength(), "\n", userStyle);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		
		autoScroll();
	}
	


	private void newMessage(String username, String message, Boolean yourMessage) {

		StyledDocument doc;
		if (username.equals(this.username)) {
			doc = chatWindows.get(lbReceiver.getText()).getStyledDocument();
		} else {
			doc = chatWindows.get(username).getStyledDocument();
		}
		
		Style userStyle = doc.getStyle("User style");
		if (userStyle == null) {
			userStyle = doc.addStyle("User style", null);
			StyleConstants.setBold(userStyle, true);
		}
		
		if (yourMessage == true) {
	    	StyleConstants.setForeground(userStyle, Color.red);
	    } else {
	    	StyleConstants.setForeground(userStyle, Color.BLUE);
	    }
	    
		// In ra tÃªn ngÆ°á»�i gá»­i
	    try { doc.insertString(doc.getLength(), username + ": ", userStyle); }
        catch (BadLocationException e){}
	    
	    Style messageStyle = doc.getStyle("Message style");
		if (messageStyle == null) {
			messageStyle = doc.addStyle("Message style", null);
		    StyleConstants.setForeground(messageStyle, Color.BLACK);
		    StyleConstants.setBold(messageStyle, false);
		}
	   
		// In ra ná»™i dung tin nháº¯n
	    try { doc.insertString(doc.getLength(), message + "\n",messageStyle); }
        catch (BadLocationException e){}
	    
	    autoScroll();
	}
	

	public ChatFrame(String username, DataInputStream dis, DataOutputStream dos) {
		setTitle("CHAT");	
		this.username = username;
		this.dis = dis;
		this.dos = dos;
		receiver = new Thread(new Receiver(dis));
		receiver.start();
		
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 571, 640);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(new Color(173, 216, 230));
		setContentPane(contentPane);
		
		JPanel header = new JPanel();
		header.setBounds(5, 5, 552, 40);
		header.setBackground(new Color(135, 206, 235));
		
		txtMessage = new JTextField();
		txtMessage.setBounds(125, 549, 321, 49);
		txtMessage.setEnabled(false);
		txtMessage.setColumns(10);
		
		btnSend = new JButton("");
		btnSend.setBounds(506, 549, 42, 49);
		btnSend.setBackground(Color.WHITE);
//		btnSend.setEnabled(false);
		btnSend.setIcon(new ImageIcon("C:\\Users\\Admin\\OneDrive\\Hình ảnh\\Ảnh chụp màn hình\\filled-sent.png"));
		
		chatPanel = new JScrollPane();
		chatPanel.setBounds(125, 51, 432, 432);
		chatPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		JPanel leftPanel = new JPanel();
		leftPanel.setBounds(5, 51, 114, 547);
		leftPanel.setBackground(new Color(230, 230, 250));
		
		btnFile = new JButton("");
		btnFile.setBounds(456, 549, 40, 49);
		btnFile.setForeground(Color.WHITE);
		btnFile.setBackground(Color.WHITE);
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				JFileChooser fileChooser = new JFileChooser();
				int rVal = fileChooser.showOpenDialog(contentPane.getParent());
				if (rVal == JFileChooser.APPROVE_OPTION) {
					byte[] selectedFile = new byte[(int) fileChooser.getSelectedFile().length()];
					BufferedInputStream bis;
					try {
						bis = new BufferedInputStream(new FileInputStream(fileChooser.getSelectedFile()));
						
						bis.read(selectedFile, 0, selectedFile.length);
						
						dos.writeUTF("File");
						dos.writeUTF(lbReceiver.getText());
						dos.writeUTF(fileChooser.getSelectedFile().getName());
						dos.writeUTF(String.valueOf(selectedFile.length));
						
						int size = selectedFile.length;
						int bufferSize = 2048;
						int offset = 0;
						
						
						while (size > 0) {
							dos.write(selectedFile, offset, Math.min(size, bufferSize));
							offset += Math.min(size, bufferSize);
							size -= bufferSize;
						} 
						
						dos.flush();
						
						bis.close();
						
						
						newFile(username, fileChooser.getSelectedFile().getName(), selectedFile, true);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		btnFile.setIcon(new ImageIcon("C:\\Users\\Admin\\OneDrive\\Hình ảnh\\Ảnh chụp màn hình\\file (1).png"));
		
		JPanel emojis = new JPanel();
		emojis.setBounds(125, 510, 432, 33);
		emojis.setBackground(new Color(176, 224, 230));
		
		JLabel smileIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\smile.png"));
		smileIcon.addMouseListener(new IconListener(smileIcon.getIcon().toString()));
		emojis.add(smileIcon);
		
		JLabel bigSmileIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\big-smile.png"));
		bigSmileIcon.addMouseListener(new IconListener(bigSmileIcon.getIcon().toString()));
		emojis.add(bigSmileIcon);
		
		JLabel happyIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\happy.png"));
		happyIcon.addMouseListener(new IconListener(happyIcon.getIcon().toString()));
		emojis.add(happyIcon);
		
		JLabel loveIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\love.png"));
		loveIcon.addMouseListener(new IconListener(loveIcon.getIcon().toString()));
		emojis.add(loveIcon);
		
		JLabel sadIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\sad.png"));
		sadIcon.addMouseListener(new IconListener(sadIcon.getIcon().toString()));
		emojis.add(sadIcon);
		
		JLabel madIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\mad.png"));
		madIcon.addMouseListener(new IconListener(madIcon.getIcon().toString()));
		emojis.add(madIcon);
		
		JLabel suspiciousIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\suspicious.png"));
		suspiciousIcon.addMouseListener(new IconListener(suspiciousIcon.getIcon().toString()));
		emojis.add(suspiciousIcon);
		
		JLabel angryIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\angry.png"));
		angryIcon.addMouseListener(new IconListener(angryIcon.getIcon().toString()));
		emojis.add(angryIcon);
		
		JLabel confusedIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\confused.png"));
		confusedIcon.addMouseListener(new IconListener(confusedIcon.getIcon().toString()));
		emojis.add(confusedIcon);
		
		JLabel unhappyIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\unhappy.png"));
		unhappyIcon.addMouseListener(new IconListener(unhappyIcon.getIcon().toString()));
		emojis.add(unhappyIcon);
		
		JLabel appleIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\apple.png"));
		appleIcon.addMouseListener(new IconListener(appleIcon.getIcon().toString()));
		emojis.add(appleIcon);
		
		JLabel orangeIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\orange.png"));
		orangeIcon.addMouseListener(new IconListener(orangeIcon.getIcon().toString()));
		emojis.add(orangeIcon);
		
		JLabel cherryIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\cherry.png"));
		cherryIcon.addMouseListener(new IconListener(cherryIcon.getIcon().toString()));
		emojis.add(cherryIcon);
		
		JLabel cakeIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\cake.png"));
		cakeIcon.addMouseListener(new IconListener(cakeIcon.getIcon().toString()));
		emojis.add(cakeIcon);
		
		JLabel vietnamIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\vietnam.png"));
		vietnamIcon.addMouseListener(new IconListener(vietnamIcon.getIcon().toString()));
		emojis.add(vietnamIcon);
		
		JLabel usIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\us.png"));
		usIcon.addMouseListener(new IconListener(usIcon.getIcon().toString()));
		emojis.add(usIcon);
		
		JLabel ukIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\uk.png"));
		ukIcon.addMouseListener(new IconListener(ukIcon.getIcon().toString()));
		emojis.add(ukIcon);
		
		JLabel canadaIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\canadaIcon.png"));
		canadaIcon.addMouseListener(new IconListener(canadaIcon.getIcon().toString()));
		emojis.add(canadaIcon);
		
		JLabel italyIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\italy.png"));
		italyIcon.addMouseListener(new IconListener(italyIcon.getIcon().toString()));
		emojis.add(italyIcon);
		
		JLabel spainIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\spainIcon.png"));
		spainIcon.addMouseListener(new IconListener(spainIcon.getIcon().toString()));
		emojis.add(spainIcon);
		
		JLabel egyptIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\egyptIcon.png"));
		egyptIcon.addMouseListener(new IconListener(egyptIcon.getIcon().toString()));
		emojis.add(egyptIcon);
		
		JLabel swedenIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\sweden.png"));
		swedenIcon.addMouseListener(new IconListener(swedenIcon.getIcon().toString()));
		emojis.add(swedenIcon);
		
		JLabel australiaIcon = new JLabel(new ImageIcon("jar\\data\\icon\\emoji\\australia.png"));
		australiaIcon.addMouseListener(new IconListener(australiaIcon.getIcon().toString()));
		emojis.add(australiaIcon);
		
		JLabel userImage = new JLabel(new ImageIcon("jar\\data\\icon\\component\\user.png"));
		userImage.setBounds(25, 5, 91, -1);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 135, 121, 34);
		panel.setBackground(new Color(230,240,247));
		JLabel lblNewLabel_1 = new JLabel("CHAT VỚI");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(0, 237, 113, 33);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(0, 5, 113, 163);
		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\Admin\\OneDrive\\Hình ảnh\\Ảnh chụp màn hình\\Screenshot (1).png"));
		onlineUsers.setBounds(0, 280, 113, 28);
		onlineUsers.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					lbReceiver.setText((String) onlineUsers.getSelectedItem());
					if (chatWindow != chatWindows.get(lbReceiver.getText())) {
						txtMessage.setText("");
						chatWindow = chatWindows.get(lbReceiver.getText());
						chatPanel.setViewportView(chatWindow);
						chatPanel.validate();
					}
					
					if (lbReceiver.getText().isBlank()) {
						btnSend.setEnabled(false);
						btnFile.setEnabled(false);
						txtMessage.setEnabled(false);
					} else {
						btnSend.setEnabled(true);
						btnFile.setEnabled(true);
						txtMessage.setEnabled(true);
					}
				}

			}
		});
		
		JLabel lbUsername = new JLabel(this.username);
		lbUsername.setFont(new Font("Arial", Font.BOLD, 15));
		panel.add(lbUsername);
		leftPanel.setLayout(null);
		leftPanel.add(onlineUsers);
		leftPanel.add(lblNewLabel_1);
		leftPanel.add(panel);
		leftPanel.add(lblNewLabel);
		leftPanel.add(userImage);
		
		JLabel headerContent = new JLabel("ỨNG DỤNG CHAT NỘI BỘ");
		headerContent.setForeground(Color.RED);
		headerContent.setFont(new Font("Leelawadee UI Semilight", Font.BOLD, 24));
		header.add(headerContent);
		
		JPanel usernamePanel = new JPanel();
		usernamePanel.setBackground(new Color(230,240,247));
		chatPanel.setColumnHeaderView(usernamePanel);
		
		lbReceiver.setFont(new Font("Arial", Font.BOLD, 16));
		usernamePanel.add(lbReceiver);
		
		chatWindows.put(" ", new JTextPane());
		chatWindow = chatWindows.get(" ");
		chatWindow.setFont(new Font("Arial", Font.PLAIN, 14));
		chatWindow.setEditable(false);
		
		chatPanel.setViewportView(chatWindow);
		
		txtMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txtMessage.getText().isBlank() || lbReceiver.getText().isBlank()) {
					btnSend.setEnabled(false);
				} else {
					btnSend.setEnabled(true);
				}
			}
		});
		
		// Set action perform to send button.
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					dos.writeUTF("Text");
					dos.writeUTF(lbReceiver.getText());
					dos.writeUTF(txtMessage.getText());
					dos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
					newMessage("ERROR" , "Lỗi kết nối!" , true);
				}
				

				newMessage(username , txtMessage.getText() , true);
				txtMessage.setText("");
			}
		});
		
		this.getRootPane().setDefaultButton(btnSend);
		contentPane.setLayout(null);
		contentPane.add(header);
		contentPane.add(leftPanel);
		contentPane.add(emojis);
		contentPane.add(txtMessage);
		contentPane.add(btnFile);
		contentPane.add(btnSend);
		contentPane.add(chatPanel);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				
				try {
					dos.writeUTF("Log out");
					dos.flush();
					
					try {
						receiver.join();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					
					if (dos != null) {
						dos.close();
					}
					if (dis != null) {
						dis.close();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
	}
	


	class Receiver implements Runnable{

		private DataInputStream dis;
		
		public Receiver(DataInputStream dis) {
			this.dis = dis;
		}
		
		@Override
		public void run() {
			try {		
					
				while (true) {
					
					String method = dis.readUTF();
					
					if (method.equals("Text")) {
						
						String sender =	dis.readUTF();
						String message = dis.readUTF();
						
						
						newMessage(sender, message, false);
					}
					
					else if (method.equals("Emoji")) {
						
						String sender = dis.readUTF();
						String emoji = dis.readUTF();
						
						
						newEmoji(sender, emoji, false);
					}
					
					else if (method.equals("File")) {
						// Nháº­n má»™t file
						String sender = dis.readUTF();
						String filename = dis.readUTF();
						int size = Integer.parseInt(dis.readUTF());
						int bufferSize = 2048;
						byte[] buffer = new byte[bufferSize];
						ByteArrayOutputStream file = new ByteArrayOutputStream();
						
						while (size > 0) {
							dis.read(buffer, 0, Math.min(bufferSize, size));
							file.write(buffer, 0, Math.min(bufferSize, size));
							size -= bufferSize;
						}
						
						// In ra mÃ n hÃ¬nh file Ä‘Ã³
						newFile(sender, filename, file.toByteArray(), false);
						
					}
					
					else if (method.equals("Online users")) {
						
						String[] users = dis.readUTF().split(",");
						onlineUsers.removeAllItems();
						
						String chatting = lbReceiver.getText();
						
						boolean isChattingOnline = false;
						
						for (String user: users) {
							if (user.equals(username) == false) {
								
								onlineUsers.addItem(user);
								if (chatWindows.get(user) == null) {
									JTextPane temp = new JTextPane();
									temp.setFont(new Font("Arial", Font.PLAIN, 14));
									temp.setEditable(false);
									chatWindows.put(user, temp);
								}
							}
							if (chatting.equals(user)) {
								isChattingOnline = true;
							}
						}
						
						if (isChattingOnline == false) {
							
							onlineUsers.setSelectedItem(" ");
							JOptionPane.showMessageDialog(null, chatting + " đã thoát!");
						} else {
							onlineUsers.setSelectedItem(chatting);
						}
						
						onlineUsers.validate();
					}
					
					else if (method.equals("Safe to leave")) {
						
						break;
					}
					
				}
				
			} catch(IOException ex) {
				System.err.println(ex);
			} finally {
				try {
					if (dis != null) {
						dis.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * MouseListener cho cÃ¡c Ä‘Æ°á»�ng dáº«n táº£i file.
	 */
	class HyberlinkListener extends AbstractAction {
		String filename;
		byte[] file;
		
		public HyberlinkListener(String filename, byte[] file) {
			this.filename = filename;
			this.file = Arrays.copyOf(file, file.length);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			execute();
		}
		
		public  void execute() {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setSelectedFile(new File(filename));
			int rVal = fileChooser.showSaveDialog(contentPane.getParent());
			if (rVal == JFileChooser.APPROVE_OPTION) {
				
				
				File saveFile = fileChooser.getSelectedFile();
				BufferedOutputStream bos = null;
				try {
					bos = new BufferedOutputStream(new FileOutputStream(saveFile));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				
				int nextAction = JOptionPane.showConfirmDialog(null, "Lưu file " + saveFile.getAbsolutePath() + "\nBạn có muốn mở file?", "Mở thành công!", JOptionPane.YES_NO_OPTION);
				if (nextAction == JOptionPane.YES_OPTION) {
					try {
						Desktop.getDesktop().open(saveFile);
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}
				
				if (bos != null) {
					try {
						bos.write(this.file);
						bos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		    }
		}
	}
	
	/**
	 * MouseAdapter cho cÃ¡c Emoji.
	 */
	class IconListener extends MouseAdapter {
		String emoji;
		
		public IconListener(String emoji) {
			this.emoji = emoji;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if (txtMessage.isEnabled() == true) {
				
				try {
					dos.writeUTF("Emoji");
					dos.writeUTF(lbReceiver.getText());
					dos.writeUTF(this.emoji);
					dos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
					newMessage("ERROR" , "Network error!" , true);
				}
				
				// In Emoji lÃªn mÃ n hÃ¬nh chat vá»›i ngÆ°á»�i nháº­n
				newEmoji(username, this.emoji, true);
			}
		}
	}
}
