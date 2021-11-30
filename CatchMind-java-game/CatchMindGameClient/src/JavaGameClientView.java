
// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
	private JLabel startBtn;
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

//	private JLabel lblUserName;
//	private JLabel lblUserName2;
//	private JLabel lblUserName3;
//	private JLabel lblUserName4;
	private JLabel [] lblUserName = new JLabel[4];
	private JLabel [] lblUserScore = new JLabel[4];
	private int []score = {0};
	// private JTextArea textArea;
	private JTextPane textArea;

	private Frame frame;
	private FileDialog fd;
	private JButton imgBtn;
	private JLabel redBtn;
	private JLabel btnGreen;
	private JLabel btnBlue;
	private JLabel btnYellow;
	private JLabel btnPurple;
	private JLabel eraseBtn;
	private JButton rectangeBtn;
	private JButton circleBtn;
	private JButton penBtn;
	private JButton sketchBtn;
	
	JPanel panel;
	private JLabel lblMouseEvent;
	private Graphics gc;
	private int pen_size = 2; // minimum 2
	// 그려진 Image를 보관하는 용도, paint() 함수에서 이용한다.
	private Image panelImage = null; 
	private Graphics gc2 = null;
	private Color c = Color.BLACK;	//default = black
	private int mode = 0; //0:line, 1:rectangle, 2:circle, 3:eraser
	Image sketchImg = null;
	private int color = 0;
	private int ox, oy;
	String answer;

	Vector<Point> startV = new Vector<>();
	Vector<Point> pos_v = new Vector<>();

	
	/**
	 * Create the frame.
	 * @throws BadLocationException 
	 */
	public JavaGameClientView(String username, String ip_addr, String port_no)  {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(50, 50, 1070, 637);
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
		textArea.setFont(new Font("굴림체", Font.PLAIN, 14));
		scrollPane.setRowHeaderView(textArea);

		txtInput = new JTextField();
		txtInput.setBounds(851, 540, 192, 31);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton(">");
		btnSend.setFont(new Font("굴림", Font.PLAIN, 14));
		btnSend.setBounds(796, 538, 50, 34);
		contentPane.add(btnSend);

		lblUserName[0] = new JLabel("");
		lblUserName[0].setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName[0].setBackground(Color.WHITE);
		lblUserName[0].setFont(new Font("양재인장체M", Font.BOLD, 16));
		lblUserName[0].setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName[0].setBounds(31, 202, 102, 50);
		contentPane.add(lblUserName[0]);
		setVisible(true);

		UserName = username;
//		lblUserName[0].setText(username);

		imgBtn = new JButton("+");
		imgBtn.setFont(new Font("굴림", Font.PLAIN, 11));
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
		
		// Image 영역 보관용. paint() 에서 이용한다.
		panelImage = createImage(panel.getWidth(), panel.getHeight());
		gc2 = panelImage.getGraphics();
		gc2.setColor(panel.getBackground());
		gc2.fillRect(0,0, panel.getWidth(),  panel.getHeight());
		gc2.setColor(Color.WHITE);
		gc2.drawRect(0,0, panel.getWidth()-1,  panel.getHeight()-1);
		
		lblMouseEvent = new JLabel("");
		lblMouseEvent.setForeground(Color.BLACK);
		lblMouseEvent.setHorizontalAlignment(SwingConstants.CENTER);
		lblMouseEvent.setFont(new Font("양재인장체M", Font.BOLD, 14));
		lblMouseEvent.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblMouseEvent.setForeground(c);
//		lblMouseEvent.setBackground(Color.WHITE);
		lblMouseEvent.setBounds(803, 274, 241, 40);
		lblMouseEvent.setText("Mode : "+ getMode() + "| pen_size = " + pen_size);
		lblMouseEvent.setOpaque(true);
		contentPane.add(lblMouseEvent);

		redBtn = new JLabel("", new ImageIcon("src/imgsrc/redBtn.png"), JLabel.CENTER);
		redBtn.setBounds(813, 30, 42, 72);
		redBtn.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				c = Color.RED;
				color = 1;
				lblMouseEvent.setText("Mode : "+ getMode() + "| pen_size = " + pen_size);
				lblMouseEvent.setForeground(c);
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

		
		btnGreen = new JLabel("", new ImageIcon("src/imgsrc/greenBtn.png"), JLabel.CENTER);
		btnGreen.setBounds(813, 116, 42, 72);
		btnGreen.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				c = Color.GREEN;
				color = 3;
				lblMouseEvent.setForeground(c);
				lblMouseEvent.setText("Mode : "+ getMode() + "| pen_size = " + pen_size);
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

		btnBlue = new JLabel("", new ImageIcon("src/imgsrc/blueBtn.png"), JLabel.CENTER);
		btnBlue.setBounds(813, 193, 42, 72);
		contentPane.add(btnBlue);
		btnBlue.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				c = Color.BLUE;
				color = 5;
				lblMouseEvent.setForeground(c);
				lblMouseEvent.setText("Mode : "+ getMode() + "| pen_size = " + pen_size);
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

		btnYellow = new JLabel("", new ImageIcon("src/imgsrc/yellowBtn.png"), JLabel.CENTER);
		btnYellow.setBounds(870, 73, 42, 72);
		contentPane.add(btnYellow);
		btnYellow.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				c = new Color(236, 231, 26);
				color = 2;
				lblMouseEvent.setForeground(new Color(242, 217, 1));
				lblMouseEvent.setText("Mode : "+ getMode() + "| pen_size = " + pen_size);
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
		
		btnPurple = new JLabel("", new ImageIcon("src/imgsrc/purpleBtn.png"), JLabel.CENTER);
		btnPurple.setBounds(870, 160, 42, 72);
//		btnPurple.setBorderPainted(false);
//		btnPurple.setContentAreaFilled(false);
//		btnPurple.setFocusPainted(false);
//		btnPurple.setOpaque(true);
		contentPane.add(btnPurple);
		btnPurple.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				c = new Color(175, 75, 214);
				color = 4;
				lblMouseEvent.setForeground(c);
				lblMouseEvent.setText("Mode : "+ getMode() + "| pen_size = " + pen_size);
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
		
		eraseBtn = new JLabel("", new ImageIcon("src/imgsrc/eraserBtn.png"), JLabel.CENTER);
		eraseBtn.setBounds(954, 100, 68, 70);
//		eraseBtn.setBorderPainted(false);
//		eraseBtn.setContentAreaFilled(false);
//		eraseBtn.setFocusPainted(false);
//		eraseBtn.setOpaque(true);
		contentPane.add(eraseBtn);
		eraseBtn.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				mode = 3;
				c = Color.WHITE;
				color = 6;
				lblMouseEvent.setForeground(c);
				lblMouseEvent.setText("Mode : "+ getMode() + "| pen_size = " + pen_size);
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
		lblNewLabel.setFont(new Font("양재인장체M", Font.BOLD, 16));
		lblNewLabel.setBounds(91, 165, 83, 31);
		contentPane.add(lblNewLabel);

		rectangeBtn = new JButton(new ImageIcon("src/imgsrc/rectangle_btn.png"));
//		rectangeBtn.setContentAreaFilled(false);
//		rectangeBtn.setFocusPainted(false);
//		rectangeBtn.setOpaque(true);
		rectangeBtn.setBounds(515, 550, 45, 45);
		rectangeBtn.setBorderPainted(false);
		rectangeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mode = 1;
				lblMouseEvent.setText("Mode : "+ getMode() + "| pen_size = " + pen_size);
			}
		});
		contentPane.add(rectangeBtn);
		
		circleBtn = new JButton(new ImageIcon("src/imgsrc/circle_btn.png"));
		circleBtn.setOpaque(true);
		circleBtn.setBounds(595, 550, 45, 45);
		circleBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mode = 2;
				lblMouseEvent.setText("Mode : "+ getMode() + "| pen_size = " + pen_size);
			}
		});
		contentPane.add(circleBtn);

		
		penBtn = new JButton(new ImageIcon("src/imgsrc/penBtn.png"));
		penBtn.setBounds(435, 550, 45, 45);
		penBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mode = 0;
				lblMouseEvent.setText("Mode : "+ getMode() + "| pen_size = " + pen_size);
			}
		});
		contentPane.add(penBtn);
		
		sketchBtn = new JButton("sketch");
		sketchBtn.setFont(new Font("Bradley Hand ITC", Font.BOLD, 18));
		sketchBtn.setBounds(293, 550, 91, 45);
		sketchBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == sketchBtn) {
					if(sketchImg==null) {
						frame = new Frame("밑그림 이미지 선택");
						fd = new FileDialog(frame, "밑그림 이미지 선택", FileDialog.LOAD);
						// frame.setVisible(true);
						// fd.setDirectory(".\\");
						fd.setVisible(true);
						// System.out.println(fd.getDirectory() + fd.getFile());
						if (fd.getDirectory().length() > 0 && fd.getFile().length() > 0) {
							sketchImg = new ImageIcon(fd.getDirectory() + fd.getFile()).getImage();
//							panelImage = new ImageIcon(fd.getDirectory() + fd.getFile()).getImage();
							gc2.drawImage(sketchImg, 0, 0, panel);
							gc.drawImage(sketchImg, 0, 0, panel);
//							panelImage = createImage(panel.getWidth(), panel.getHeight());
//							repaint();
//							gc = sketchImg.getGraphics();
//							gc.drawImage(sketchImg, 0, 0, panel);
//							gc2 = panelImage.getGraphics();
//							gc2.setColor(panel.getBackground());
//							gc2.fillRect(0,0, panel.getWidth(),  panel.getHeight());
//							gc2.setColor(Color.WHITE);
//							gc2.drawRect(0,0, panel.getWidth()-1,  panel.getHeight()-1);

							System.out.println(fd.getDirectory() + fd.getFile());
						}						
					}else {
						sketchImg = null;
						panelImage = createImage(panel.getWidth(), panel.getHeight());
						gc2 = panelImage.getGraphics();
						gc2.setColor(panel.getBackground());
						gc2.fillRect(0,0, panel.getWidth(),  panel.getHeight());
						gc2.setColor(Color.WHITE);
						gc2.drawRect(0,0, panel.getWidth()-1,  panel.getHeight()-1);
						gc.drawImage(panelImage, 0, 0, panel);
					}
				}
			}
		});
		contentPane.add(sketchBtn);
		
		JLabel resetBtn = new JLabel("", new ImageIcon("src/imgsrc/reset.png"), JLabel.CENTER);
		resetBtn.setBounds(690, 550, 50, 45);
		resetBtn.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				ChatMsg cm = new ChatMsg(UserName, "501", "RESET");
				SendObject(cm);
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
		contentPane.add(resetBtn);

		startBtn = new JLabel("", new ImageIcon("src/imgsrc/start.png"), JLabel.CENTER);
		startBtn.setForeground(Color.BLACK);
		startBtn.setBackground(Color.GRAY);
		startBtn.setBounds(77, 460, 100, 100);
		startBtn.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				ChatMsg cm = new ChatMsg(UserName, "600", "GAMESTART");
				SendObject(cm);
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
		contentPane.add(startBtn);		
		
		lblUserName[1] = new JLabel("");
		lblUserName[1].setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName[1].setFont(new Font("양재인장체M", Font.BOLD, 16));
		lblUserName[1].setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName[1].setBackground(Color.WHITE);
		lblUserName[1].setBounds(31, 252, 102, 50);
		contentPane.add(lblUserName[1]);
		
		lblUserName[2] = new JLabel("");
		lblUserName[2].setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName[2].setFont(new Font("양재인장체M", Font.BOLD, 16));
		lblUserName[2].setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName[2].setBackground(Color.WHITE);
		lblUserName[2].setBounds(31, 301, 102, 50);
		contentPane.add(lblUserName[2]);
		
		lblUserName[3] = new JLabel("");
		lblUserName[3].setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName[3].setFont(new Font("양재인장체M", Font.BOLD, 16));
		lblUserName[3].setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName[3].setBackground(Color.WHITE);
		lblUserName[3].setBounds(31, 349, 102, 50);
		contentPane.add(lblUserName[3]);
		
		lblUserScore[0] = new JLabel("");
		lblUserScore[0].setHorizontalAlignment(SwingConstants.CENTER);
		lblUserScore[0].setFont(new Font("양재인장체M", Font.BOLD, 16));
		lblUserScore[0].setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserScore[0].setBackground(Color.WHITE);
		lblUserScore[0].setBounds(132, 202, 102, 50);
		contentPane.add(lblUserScore[0]);
		
		lblUserScore[1] = new JLabel("");
		lblUserScore[1].setHorizontalAlignment(SwingConstants.CENTER);
		lblUserScore[1].setFont(new Font("양재인장체M", Font.BOLD, 16));
		lblUserScore[1].setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserScore[1].setBackground(Color.WHITE);
		lblUserScore[1].setBounds(132, 251, 102, 50);
		contentPane.add(lblUserScore[1]);
		
		lblUserScore[2] = new JLabel("");
		lblUserScore[2].setHorizontalAlignment(SwingConstants.CENTER);
		lblUserScore[2].setFont(new Font("양재인장체M", Font.BOLD, 16));
		lblUserScore[2].setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserScore[2].setBackground(Color.WHITE);
		lblUserScore[2].setBounds(132, 300, 102, 50);
		contentPane.add(lblUserScore[2]);
		
		lblUserScore[3] = new JLabel("");
		lblUserScore[3].setHorizontalAlignment(SwingConstants.CENTER);
		lblUserScore[3].setFont(new Font("양재인장체M", Font.BOLD, 16));
		lblUserScore[3].setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserScore[3].setBackground(Color.WHITE);
		lblUserScore[3].setBounds(132, 349, 102, 50);
		contentPane.add(lblUserScore[3]);
		



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
		// Image 영역이 가려졌다 다시 나타날 때 그려준다.
		gc.drawImage(panelImage, 0, 0, this);
	}
	
	// Server Message를 수신해서 화면에 표시
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
					case "101":
						UpdateUserlist(cm.data);
					case "200": // chat message
						if (cm.UserName.equals(UserName))
							AppendTextR(msg); // 내 메세지는 우측에
						else
							AppendText(msg);
						if(cm.data.equals(answer)){
							ChatMsg cmsg = new ChatMsg(UserName, "700", "GAMEOVER");
							SendObject(cmsg);
						}
						break;
					case "300": // Image 첨부
						if (cm.UserName.equals(UserName))
							AppendTextR("[" + cm.UserName + "]");
						else
							AppendText("[" + cm.UserName + "]");
						AppendImage(cm.img);
						break;
					case "500": // Mouse Event 수신
						DoMouseEvent(cm, cm.color);
						break;
					case "501":
						ResetCanvas();
						break;
					case "600":	//Game Start
						GameStart(cm);
						break;
					case "700":
						GameOver(cm);
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
					} // catch문 끝
				} // 바깥 catch문끝

			}
		}
	}
	
	public void UpdateUserlist(String users) {
		for(int i=0;i<lblUserName.length;i++)
			lblUserName[i].setText("");
		
		String[] username = users.split(",");
		for(int i=0;i<username.length;i++) {
			lblUserName[i].setText(username[i]);
		}
	}
	
	public void ResetCanvas() {
		gc2.setColor(panel.getBackground());
		gc2.fillRect(0,0, panel.getWidth(),  panel.getHeight());
		gc.drawImage(panelImage, 0, 0, panel);
	}

	public void GameStart(ChatMsg cm) {
		ResetCanvas();
		if (cm.UserName.equals(UserName)) {
			GameAnswer g = new GameAnswer();
			answer = g.getAnswer();
			JOptionPane.showMessageDialog(panel, "출제할 문제는 '"+answer+"' 입니다");			
		}else {
			c = Color.WHITE;
			redBtn.setEnabled(false);
			btnGreen.setEnabled(false);
			btnYellow.setEnabled(false);
			btnPurple.setEnabled(false);
			btnBlue.setEnabled(false);
			eraseBtn.setEnabled(false);
			rectangeBtn.setEnabled(false);
			circleBtn.setEnabled(false);
			penBtn.setEnabled(false);
			sketchBtn.setEnabled(false);
		}
		startBtn.setEnabled(false);
	}
	
	public void GameOver(ChatMsg cm) {
		if (cm.UserName.equals(UserName)) {
			JOptionPane.showMessageDialog(panel, cm.UserName+"님이 맞추셨습니다!\n 정답 : '"+answer+"'");			
			for(int i=0;i<lblUserName.length;i++) {
				if(lblUserName[i].getText().equals(UserName)){
					score[i]+=10;
					lblUserScore[i].setText(Integer.toString(score[i]));
					break;
				}
			}
		}
		else {
			JOptionPane.showMessageDialog(panel, "정답입니다");
			redBtn.setEnabled(true);
			btnGreen.setEnabled(true);
			btnYellow.setEnabled(true);
			btnPurple.setEnabled(true);
			btnBlue.setEnabled(true);
			eraseBtn.setEnabled(true);
			rectangeBtn.setEnabled(true);
			circleBtn.setEnabled(true);
			penBtn.setEnabled(true);
			sketchBtn.setEnabled(true);			
		}
		System.out.println("username:"+cm.UserName+", "+UserName);
		startBtn.setEnabled(true);
		ResetCanvas();
	}
	
	// Mouse Event 수신 처리
	public void DoMouseEvent(ChatMsg cm, int color) {
//		Color c;
		if (cm.UserName.matches(UserName)) // 본인 것은 이미 Local 로 그렸다.
			return;
//		c = new Color(255, 0, 0); // 다른 사람 것은 Red
		gc2.setColor(cm.penColor(color));
//		gc2.setColor(cm.c);
		System.out.println("color : "+color +", pensize:"+cm.pen_size+", mode:"+mode);
//		gc2.fillOval(cm.mouse_e.getX() - pen_size/2, cm.mouse_e.getY() - cm.pen_size/2, cm.pen_size, cm.pen_size);
		if(mode == 0 || mode == 3) {	//mode:line
			Graphics2D g = (Graphics2D)gc2;
			g.setStroke(new BasicStroke(cm.pen_size));

//			if(cm.mouse_e.getID()==MouseEvent.MOUSE_PRESSED) {
//				System.out.println("pressed\n");
//				ox = cm.mouse_e.getX();
//				oy = cm.mouse_e.getY();
//			}else {
//				if(cm.mouse_e.getID()==MouseEvent.MOUSE_RELEASED)
//					System.out.println("released\n");
//				else
//					System.out.println("drag\n");
//
//				int nx = cm.mouse_e.getX();
//				int ny = cm.mouse_e.getY();
//				gc2.drawLine((int)ox, (int)oy, (int)nx, (int)ny);
//				ox = nx;
//				oy = ny;
//			}

			pos_v.add(cm.mouse_e.getPoint());
			if(pos_v.size()==1) {
				pos_v.add(cm.mouse_e.getPoint());
				return;
			}
			for(int i=0;i<pos_v.size()-1;i++) {
				Point start = pos_v.elementAt(i);
				Point end = pos_v.elementAt(i+1);
				gc2.drawLine((int)start.getX(), (int)start.getY(), (int)end.getX(), (int)end.getY());
			}
			gc.drawImage(panelImage, 0, 0, panel);
			if(cm.mouse_e.getID()==MouseEvent.MOUSE_RELEASED)
				pos_v.removeAllElements();
			
		}else if(mode == 1) {	//mode:rectangle

			pos_v.add(cm.mouse_e.getPoint());

			Point start = pos_v.elementAt(0);
			for(int i=1;i<pos_v.size()-1;i++) {
				Point end = pos_v.elementAt(i+1);
				gc2.fillRect((int)start.getX(), (int)start.getY(), Math.abs((int)start.getX()-(int)end.getX()), Math.abs((int)start.getY()-(int)end.getY()));
			}		
			gc.drawImage(panelImage, 0, 0, panel);

			if(cm.mouse_e.getID()==MouseEvent.MOUSE_RELEASED)
				pos_v.removeAllElements();
		}else if(mode == 2) {//mode:circle

//			startV.add(e.getPoint());

//			Point start = startV.elementAt(0);
//			for(int i=1;i<startV.size()-1;i++) {
//				Point end = startV.elementAt(i+1);
//				gc2.fillOval((int)start.getX(), (int)start.getY(), Math.abs((int)start.getX()-(int)end.getX()), Math.abs((int)start.getY()-(int)end.getY()));
//			}
		}

	}

	public void SendMouseEvent(MouseEvent e) {
		ChatMsg cm = new ChatMsg(UserName, "500", "MOUSE");
		cm.mouse_e = e;
		cm.pen_size = pen_size;
		cm.color = color;
		cm.c = c;
		System.out.println(">>color : "+cm.color+", pensize:"+cm.pen_size);

		SendObject(cm);
	}

	class MyMouseWheelEvent implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			// TODO Auto-generated method stub
			if (e.getWheelRotation() < 0) { // 위로 올리는 경우 pen_size 증가
				if (pen_size < 20)
					pen_size++;
			} else {
				if (pen_size > 2)
					pen_size--;
			}
			lblMouseEvent.setText("Mode : "+ getMode() + "| pen_size = " + pen_size);

//			txtpenSize.setText("pen size = "+pen_size);
		}
		
	}
	
	
	// Mouse Event Handler
	class MyMouseEvent implements MouseListener, MouseMotionListener {
		MouseEvent myMouseEvent;
		@Override
		public void mouseDragged(MouseEvent e) {
			myMouseEvent = e;
//			lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// 좌표출력가능
			gc2.setColor(c);
			
			if(mode == 0 || mode == 3) {	//mode:line
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

				Point start = startV.elementAt(0);
				for(int i=1;i<startV.size()-1;i++) {
					Point end = startV.elementAt(i+1);
					gc2.fillOval((int)start.getX(), (int)start.getY(), Math.abs((int)start.getX()-(int)end.getX()), Math.abs((int)start.getY()-(int)end.getY()));
				}
			}

			// panelImnage는 paint()에서 이용한다.
			gc.drawImage(panelImage, 0, 0, panel);
			SendMouseEvent(myMouseEvent);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
//			lblMouseEvent.setText(e.getButton() + " mouseMoved " + e.getX() + "," + e.getY());
		}

		@Override
		public void mouseClicked(MouseEvent e) {
//			lblMouseEvent.setText(e.getButton() + " mouseClicked " + e.getX() + "," + e.getY());
			gc2.setColor(c);
			
			startV.add(e.getPoint());
			gc.drawImage(panelImage, 0, 0, panel);
			SendMouseEvent(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
//			lblMouseEvent.setText(e.getButton() + " mouseEntered " + e.getX() + "," + e.getY());
			// panel.setBackground(Color.YELLOW);

		}

		@Override
		public void mouseExited(MouseEvent e) {
//			lblMouseEvent.setText(e.getButton() + " mouseExited " + e.getX() + "," + e.getY());
			// panel.setBackground(Color.CYAN);

		}

		@Override
		public void mousePressed(MouseEvent e) {
//			lblMouseEvent.setText(e.getButton() + " mousePressed " + e.getX() + "," + e.getY());

//			startV.add(e.getPoint());
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// 드래그중 멈출시 보임			
			if(mode == 2) {
				Point start = startV.elementAt(0);
				Point end = startV.elementAt(startV.size()-1);
				gc2.fillOval((int)start.getX(), (int)start.getY(), Math.abs((int)start.getX()-(int)end.getX()), Math.abs((int)start.getY()-(int)end.getY()));
			}
			gc.drawImage(panelImage, 0, 0, panel);
			pos_v.clear();
			SendMouseEvent(e);
//			lblMouseEvent.setText(e.getButton() + " mouseReleased " + e.getX() + "," + e.getY());
			startV.clear();		
		}
	}

	// keyboard enter key 치면 서버로 전송
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button을 누르거나 메시지 입력하고 Enter key 치면
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
				// msg = String.format("[%s] %s\n", UserName, txtInput.getText());
				msg = txtInput.getText();
				SendMessage(msg);
//				IsAnswer(msg);
				txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				if (msg.contains("/exit")) // 종료 처리
					System.exit(0);
			}
		}
	}

	class ImageSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
			if (e.getSource() == imgBtn) {
				frame = new Frame("이미지첨부");
				fd = new FileDialog(frame, "이미지 선택", FileDialog.LOAD);
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

//	public void IsAnswer(String word) {
//		System.out.println(word+","+answer);
//		if(word.equals(answer)){
//			ChatMsg cmsg = new ChatMsg(UserName, "700", "GAMEOVER");
//			SendObject(cmsg);
//		}
//	}
	
	public void AppendIcon(ImageIcon icon) {
		int len = textArea.getDocument().getLength();
		// 끝으로 이동
		textArea.setCaretPosition(len);
		textArea.insertIcon(icon);
	}

	// 화면에 출력
	public void AppendText(String msg) {
		// textArea.append(msg + "\n");
		// AppendIcon(icon1);
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
		int len = textArea.getDocument().getLength();
		// 끝으로 이동
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
	// 화면 우측에 출력
	public void AppendTextR(String msg) {
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.	
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
		// Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다.
		if (width > 200 || height > 200) {
			if (width > height) { // 가로 사진
				ratio = (double) height / width;
				width = 200;
				height = (int) (width * ratio);
			} else { // 세로 사진
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
		// new_icon.addActionListener(viewaction); // 내부클래스로 액션 리스너를 상속받은 클래스로
		// panelImage = ori_img.getScaledInstance(panel.getWidth(), panel.getHeight(), Image.SCALE_DEFAULT);

		gc2.drawImage(ori_img,  0,  0, panel.getWidth(), panel.getHeight(), panel);
		gc.drawImage(panelImage, 0, 0, panel.getWidth(), panel.getHeight(), panel);
	}

	// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
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

	// Server에게 network으로 전송
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

	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("메세지 송신 에러!!\n");
			AppendText("SendObject Error");
		}
	}
	
	private String getMode() {
		String state = null;
		if(mode==0)
			state = "Pen";
		else if(mode==1)
			state = "Rectangle";
		else if(mode==2)
			state = "Circle";
		else if(mode==3)
			state = "Eraser";
		return state;
	}
}
