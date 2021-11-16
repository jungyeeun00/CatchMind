
// JavaObjClientView.java ObjecStram ��� Client
//�������� ä�� â
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class JavaGameClientView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtInput;
	private String UserName;
	private JButton btnSend;
	private static final int BUF_LEN = 128; // Windows ó�� BUF_LEN �� ����
	private Socket socket; // �������
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	private JLabel lblUserName;
	// private JTextArea textArea;
	private JTextPane textArea;

	private Frame frame;
	private FileDialog fd;
	private JButton imgBtn;

	JPanel panel;
	private JLabel lblMouseEvent;
	private Graphics gc;
	private int pen_size = 2; // minimum 2
	// �׷��� Image�� �����ϴ� �뵵, paint() �Լ����� �̿��Ѵ�.
	private Image panelImage = null; 
	private Graphics gc2 = null;
	private Color c = null;
	private int mode = 0; //0:line, 1:rectangle, 2:circle

	Vector<Point> startV = new Vector<>();


	
	/**
	 * Create the frame.
	 * @throws BadLocationException 
	 */
	public JavaGameClientView(String username, String ip_addr, String port_no)  {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1070, 637);
		contentPane = new JPanel() {
			Image bg = new ImageIcon("src/imgsrc/main_bg_resize.png").getImage();
			
			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(bg, 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(851, 337, 192, 198);
		contentPane.add(scrollPane);

		textArea = new JTextPane();
		textArea.setEditable(true);
		textArea.setOpaque(true);
		Color backgroundColor = getBackground();
		int r = backgroundColor.getRed();
		int g = backgroundColor.getGreen();
		int b = backgroundColor.getBlue();
		textArea.setBackground(new Color(r,g,b,25));
		textArea.setFont(new Font("����ü", Font.PLAIN, 14));
		scrollPane.setRowHeaderView(textArea);

		txtInput = new JTextField();
		txtInput.setBounds(851, 540, 192, 31);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton(">");
		btnSend.setFont(new Font("����", Font.PLAIN, 14));
		btnSend.setBounds(796, 538, 50, 34);
		contentPane.add(btnSend);

		lblUserName = new JLabel("Name");
		lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName.setBackground(Color.WHITE);
		lblUserName.setFont(new Font("��������üM", Font.BOLD, 16));
		lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName.setBounds(31, 202, 102, 50);
		contentPane.add(lblUserName);
		setVisible(true);

		AppendText("User " + username + " connecting " + ip_addr + " " + port_no);
		UserName = username;
		lblUserName.setText(username);

		imgBtn = new JButton("+");
		imgBtn.setFont(new Font("����", Font.PLAIN, 11));
		imgBtn.setBounds(803, 493, 35, 35);
		contentPane.add(imgBtn);

		JLabel btnNewButton = new JLabel("", new ImageIcon("src/imgsrc/quitBtn.png"), JLabel.CENTER);
//		btnNewButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
//				SendObject(msg);
//				System.exit(0);
//			}
//		});
		btnNewButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
				SendObject(msg);
				System.exit(0);
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		btnNewButton.setBounds(12, 21, 69, 40);
		contentPane.add(btnNewButton);

		panel = new JPanel();
		panel.setForeground(Color.WHITE);
		panel.setBorder(new LineBorder(Color.WHITE));
		panel.setBackground(Color.WHITE);
		panel.setBounds(273, 100, 521, 431);
		contentPane.add(panel);
		gc = panel.getGraphics();
		
		// Image ���� ������. paint() ���� �̿��Ѵ�.
		panelImage = createImage(panel.getWidth(), panel.getHeight());
		gc2 = panelImage.getGraphics();
		gc2.setColor(panel.getBackground());
		gc2.fillRect(0,0, panel.getWidth(),  panel.getHeight());
		gc2.setColor(Color.WHITE);
		gc2.drawRect(0,0, panel.getWidth()-1,  panel.getHeight()-1);
		
		lblMouseEvent = new JLabel("<dynamic>");
		lblMouseEvent.setHorizontalAlignment(SwingConstants.CENTER);
		lblMouseEvent.setFont(new Font("����", Font.BOLD, 14));
		lblMouseEvent.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblMouseEvent.setBackground(Color.WHITE);
		lblMouseEvent.setBounds(12, 550, 345, 40);
		contentPane.add(lblMouseEvent);

		JLabel redBtn = new JLabel("", new ImageIcon("src/imgsrc/redBtn.png"), JLabel.CENTER);
		redBtn.setBounds(813, 30, 35, 60);
		redBtn.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				c = Color.RED;
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		contentPane.add(redBtn);

		
		JLabel btnGreen = new JLabel("", new ImageIcon("src/imgsrc/greenBtn.png"), JLabel.CENTER);
		btnGreen.setBounds(813, 116, 35, 60);
		btnGreen.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				c = Color.GREEN;
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		contentPane.add(btnGreen);

		JLabel btnBlue = new JLabel("", new ImageIcon("src/imgsrc/blueBtn.png"), JLabel.CENTER);
		btnBlue.setBounds(813, 193, 35, 60);
		contentPane.add(btnBlue);
		btnBlue.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				c = Color.BLUE;
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
		});

		JLabel btnYellow = new JLabel("", new ImageIcon("src/imgsrc/yellowBtn.png"), JLabel.CENTER);
		btnYellow.setBounds(870, 73, 35, 60);
		contentPane.add(btnYellow);
		btnYellow.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				c = new Color(236, 231, 26);
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		
		JLabel btnPurple = new JLabel("", new ImageIcon("src/imgsrc/purpleBtn.png"), JLabel.CENTER);
		btnPurple.setBounds(870, 160, 35, 60);
//		btnPurple.setBorderPainted(false);
//		btnPurple.setContentAreaFilled(false);
//		btnPurple.setFocusPainted(false);
//		btnPurple.setOpaque(true);
		contentPane.add(btnPurple);
		btnPurple.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				c = new Color(175, 75, 214);
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		
		JLabel eraseBtn = new JLabel("", new ImageIcon("src/imgsrc/eraserBtn.png"), JLabel.CENTER);
		eraseBtn.setBounds(954, 100, 68, 70);
//		eraseBtn.setBorderPainted(false);
//		eraseBtn.setContentAreaFilled(false);
//		eraseBtn.setFocusPainted(false);
//		eraseBtn.setOpaque(true);
		contentPane.add(eraseBtn);
		eraseBtn.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				c = Color.WHITE;
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
		});		
		
		JLabel lblNewLabel = new JLabel("\uCC38\uAC00\uC790\uB4E4");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("��������üM", Font.BOLD, 16));
		lblNewLabel.setBounds(91, 165, 83, 31);
		contentPane.add(lblNewLabel);

		JButton rectangeBtn = new JButton(new ImageIcon("src/imgsrc/rectangle_btn.png"));
//		rectangeBtn.setContentAreaFilled(false);
//		rectangeBtn.setFocusPainted(false);
//		rectangeBtn.setOpaque(true);
		rectangeBtn.setBounds(515, 550, 45, 45);
		rectangeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mode = 1;
			}
		});
		contentPane.add(rectangeBtn);
		
		JButton circleBtn = new JButton(new ImageIcon("src/imgsrc/circle_btn.png"));
		circleBtn.setOpaque(true);
		circleBtn.setBounds(595, 550, 45, 45);
		circleBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mode = 2;
			}
		});
		contentPane.add(circleBtn);

		
		JButton penBtn = new JButton(new ImageIcon("src/imgsrc/penBtn.png"));
		penBtn.setBounds(435, 550, 45, 45);
		penBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mode = 0;
			}
		});
		contentPane.add(penBtn);


		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));
//			is = socket.getInputStream();
//			dis = new DataInputStream(is);
//			os = socket.getOutputStream();
//			dos = new DataOutputStream(os);

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			// SendMessage("/login " + UserName);
			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello");
			SendObject(obcm);

			ListenNetwork net = new ListenNetwork();
			net.start();
			TextSendAction action = new TextSendAction();
			btnSend.addActionListener(action);
			txtInput.addActionListener(action);
			txtInput.requestFocus();
			ImageSendAction action2 = new ImageSendAction();
			imgBtn.addActionListener(action2);
			MyMouseEvent mouse = new MyMouseEvent();
			panel.addMouseMotionListener(mouse);
			panel.addMouseListener(mouse);
			MyMouseWheelEvent wheel = new MyMouseWheelEvent();
			panel.addMouseWheelListener(wheel);


		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AppendText("connect error");
		}

	}

	public void paint(Graphics g) {
		super.paint(g);
		// Image ������ �������� �ٽ� ��Ÿ�� �� �׷��ش�.
		gc.drawImage(panelImage, 0, 0, this);
	}
	
	// Server Message�� �����ؼ� ȭ�鿡 ǥ��
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				try {

					Object obcm = null;
					String msg = null;
					ChatMsg cm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						msg = String.format("[%s]\n%s", cm.UserName, cm.data);
					} else
						continue;
					switch (cm.code) {
					case "200": // chat message
						if (cm.UserName.equals(UserName))
							AppendTextR(msg); // �� �޼����� ������
						else
							AppendText(msg);
						break;
					case "300": // Image ÷��
						if (cm.UserName.equals(UserName))
							AppendTextR("[" + cm.UserName + "]");
						else
							AppendText("[" + cm.UserName + "]");
						AppendImage(cm.img);
						break;
					case "500": // Mouse Event ����
						DoMouseEvent(cm);
						break;
					}
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
//						dos.close();
//						dis.close();
						ois.close();
						oos.close();
						socket.close();

						break;
					} catch (Exception ee) {
						break;
					} // catch�� ��
				} // �ٱ� catch����

			}
		}
	}

	// Mouse Event ���� ó��
	public void DoMouseEvent(ChatMsg cm) {
		Color c;
		if (cm.UserName.matches(UserName)) // ���� ���� �̹� Local �� �׷ȴ�.
			return;
		c = new Color(255, 0, 0); // �ٸ� ��� ���� Red
		gc2.setColor(c);
		gc2.fillOval(cm.mouse_e.getX() - pen_size/2, cm.mouse_e.getY() - cm.pen_size/2, cm.pen_size, cm.pen_size);
		gc.drawImage(panelImage, 0, 0, panel);
	}

	public void SendMouseEvent(MouseEvent e) {
		ChatMsg cm = new ChatMsg(UserName, "500", "MOUSE");
		cm.mouse_e = e;
		cm.pen_size = pen_size;
		SendObject(cm);
	}

	class MyMouseWheelEvent implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			// TODO Auto-generated method stub
			if (e.getWheelRotation() < 0) { // ���� �ø��� ��� pen_size ����
				if (pen_size < 20)
					pen_size++;
			} else {
				if (pen_size > 2)
					pen_size--;
			}
			lblMouseEvent.setText("mouseWheelMoved Rotation=" + e.getWheelRotation() 
				+ " pen_size = " + pen_size + " " + e.getX() + "," + e.getY());

//			txtpenSize.setText("pen size = "+pen_size);
		}
		
	}
	
	
	// Mouse Event Handler
	class MyMouseEvent implements MouseListener, MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// ��ǥ��°���
			gc2.setColor(c);
			
			if(mode == 0) {	//mode:line
				Graphics2D g = (Graphics2D)gc2;
				g.setStroke(new BasicStroke(pen_size));
				startV.add(e.getPoint());
				for(int i=0;i<startV.size()-1;i++) {
					Point start = startV.elementAt(i);
					Point end = startV.elementAt(i+1);
					gc2.drawLine((int)start.getX(), (int)start.getY(), (int)end.getX(), (int)end.getY());
				}				
			}else if(mode == 1) {	//mode:rectangle
				startV.add(e.getPoint());

				Point start = startV.elementAt(0);
				for(int i=1;i<startV.size()-1;i++) {
					Point end = startV.elementAt(i+1);
					gc2.fillRect((int)start.getX(), (int)start.getY(), Math.abs((int)start.getX()-(int)end.getX()), Math.abs((int)start.getY()-(int)end.getY()));
				}				
			}else if(mode == 2) {//mode:circle
				startV.add(e.getPoint());

//				Point start = startV.elementAt(0);
//				for(int i=1;i<startV.size()-1;i++) {
//					Point end = startV.elementAt(i+1);
//					gc2.fillOval((int)start.getX(), (int)start.getY(), Math.abs((int)start.getX()-(int)end.getX()), Math.abs((int)start.getY()-(int)end.getY()));
//				}
			}

			// panelImnage�� paint()���� �̿��Ѵ�.
			gc.drawImage(panelImage, 0, 0, panel);
			SendMouseEvent(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseMoved " + e.getX() + "," + e.getY());
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseClicked " + e.getX() + "," + e.getY());
			gc2.setColor(c);
			
			startV.add(e.getPoint());
			
			gc.drawImage(panelImage, 0, 0, panel);
			SendMouseEvent(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseEntered " + e.getX() + "," + e.getY());
			// panel.setBackground(Color.YELLOW);

		}

		@Override
		public void mouseExited(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseExited " + e.getX() + "," + e.getY());
			// panel.setBackground(Color.CYAN);

		}

		@Override
		public void mousePressed(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mousePressed " + e.getX() + "," + e.getY());
			
			startV.clear();				

//			startV.add(e.getPoint());
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseReleased " + e.getX() + "," + e.getY());
			// �巡���� ����� ����			
			
			if(mode == 2) {
				Point start = startV.elementAt(0);
				Point end = startV.elementAt(startV.size()-1);
				gc2.fillOval((int)start.getX(), (int)start.getY(), Math.abs((int)start.getX()-(int)end.getX()), Math.abs((int)start.getY()-(int)end.getY()));
			}
			gc.drawImage(panelImage, 0, 0, panel);
			SendMouseEvent(e);
		}
	}

	// keyboard enter key ġ�� ������ ����
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button�� �����ų� �޽��� �Է��ϰ� Enter key ġ��
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
				// msg = String.format("[%s] %s\n", UserName, txtInput.getText());
				msg = txtInput.getText();
				SendMessage(msg);
				txtInput.setText(""); // �޼����� ������ ���� �޼��� ����â�� ����.
				txtInput.requestFocus(); // �޼����� ������ Ŀ���� �ٽ� �ؽ�Ʈ �ʵ�� ��ġ��Ų��
				if (msg.contains("/exit")) // ���� ó��
					System.exit(0);
			}
		}
	}

	class ImageSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// �׼� �̺�Ʈ�� sendBtn�϶� �Ǵ� textField ���� Enter key ġ��
			if (e.getSource() == imgBtn) {
				frame = new Frame("�̹���÷��");
				fd = new FileDialog(frame, "�̹��� ����", FileDialog.LOAD);
				// frame.setVisible(true);
				// fd.setDirectory(".\\");
				fd.setVisible(true);
				// System.out.println(fd.getDirectory() + fd.getFile());
				if (fd.getDirectory().length() > 0 && fd.getFile().length() > 0) {
					ChatMsg obcm = new ChatMsg(UserName, "300", "IMG");
					ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
					obcm.img = img;
					SendObject(obcm);
				}
			}
		}
	}

	ImageIcon icon1 = new ImageIcon("src/icon1.jpg");
	private JButton eraseBtn;

	public void AppendIcon(ImageIcon icon) {
		int len = textArea.getDocument().getLength();
		// ������ �̵�
		textArea.setCaretPosition(len);
		textArea.insertIcon(icon);
	}

	// ȭ�鿡 ���
	public void AppendText(String msg) {
		// textArea.append(msg + "\n");
		// AppendIcon(icon1);
		msg = msg.trim(); // �յ� blank�� \n�� �����Ѵ�.
		int len = textArea.getDocument().getLength();
		// ������ �̵�
		//textArea.setCaretPosition(len);
		//textArea.replaceSelection(msg + "\n");
		
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.BLACK);
	    doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), msg+"\n", left );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	// ȭ�� ������ ���
	public void AppendTextR(String msg) {
		msg = msg.trim(); // �յ� blank�� \n�� �����Ѵ�.	
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet right = new SimpleAttributeSet();
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
		StyleConstants.setForeground(right, Color.BLUE);	
	    doc.setParagraphAttributes(doc.getLength(), 1, right, false);
		try {
			doc.insertString(doc.getLength(),msg+"\n", right );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void AppendImage(ImageIcon ori_icon) {
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len); // place caret at the end (with no selection)
		Image ori_img = ori_icon.getImage();
		Image new_img;
		ImageIcon new_icon;
		int width, height;
		double ratio;
		width = ori_icon.getIconWidth();
		height = ori_icon.getIconHeight();
		// Image�� �ʹ� ũ�� �ִ� ���� �Ǵ� ���� 200 �������� ��ҽ�Ų��.
		if (width > 200 || height > 200) {
			if (width > height) { // ���� ����
				ratio = (double) height / width;
				width = 200;
				height = (int) (width * ratio);
			} else { // ���� ����
				ratio = (double) width / height;
				height = 200;
				width = (int) (height * ratio);
			}
			new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			new_icon = new ImageIcon(new_img);
			textArea.insertIcon(new_icon);
		} else {
			textArea.insertIcon(ori_icon);
			new_img = ori_img;
		}
		len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		textArea.replaceSelection("\n");
		// ImageViewAction viewaction = new ImageViewAction();
		// new_icon.addActionListener(viewaction); // ����Ŭ������ �׼� �����ʸ� ��ӹ��� Ŭ������
		// panelImage = ori_img.getScaledInstance(panel.getWidth(), panel.getHeight(), Image.SCALE_DEFAULT);

		gc2.drawImage(ori_img,  0,  0, panel.getWidth(), panel.getHeight(), panel);
		gc.drawImage(panelImage, 0, 0, panel.getWidth(), panel.getHeight(), panel);
	}

	// Windows ó�� message ������ ������ �κ��� NULL �� ����� ���� �Լ�
	public byte[] MakePacket(String msg) {
		byte[] packet = new byte[BUF_LEN];
		byte[] bb = null;
		int i;
		for (i = 0; i < BUF_LEN; i++)
			packet[i] = 0;
		try {
			bb = msg.getBytes("euc-kr");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		for (i = 0; i < bb.length; i++)
			packet[i] = bb[i];
		return packet;
	}

	// Server���� network���� ����
	public void SendMessage(String msg) {
		try {
			// dos.writeUTF(msg);
//			byte[] bb;
//			bb = MakePacket(msg);
//			dos.write(bb, 0, bb.length);
			ChatMsg obcm = new ChatMsg(UserName, "200", msg);
			oos.writeObject(obcm);
		} catch (IOException e) {
			// AppendText("dos.write() error");
			AppendText("oos.writeObject() error");
			try {
//				dos.close();
//				dis.close();
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}

	public void SendObject(Object ob) { // ������ �޼����� ������ �޼ҵ�
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("�޼��� �۽� ����!!\n");
			AppendText("SendObject Error");
		}
	}
}
