
// JavaObjClientView.java ObjecStram ��� Client
//�������� ä�� â
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.Icon;

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
	private static final int BUF_LEN = 128; // Windows ó�� BUF_LEN �� ����
	private Socket socket; // �������
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	private JLabel[] lblUserName = new JLabel[4];
	private JLabel[] lblUserScore = new JLabel[4];
	private Vector<Integer> score = new Vector<>(4);
	// private JTextArea textArea;
	private JTextPane textArea;

	private Frame frame;
	private FileDialog fd;
	private JButton imgBtn;
	private JLabel btnRed;
	private JLabel btnBlack;
	private JLabel btnGreen;
	private JLabel btnBlue;
	private JLabel btnYellow;
	private JLabel btnPurple;
	private JLabel eraseBtn;
	private JLabel resetBtn;
	private JButton rectangleBtn;
	private JButton circleBtn;
	private JButton fillrectangleBtn;
	private JButton fillcircleBtn;
	private JButton penBtn;
	private JButton sketchBtn;

	JPanel panel;
	private JLabel lblMouseEvent;
	private Graphics gc;
	private int pen_size = 2; // minimum 2
	// �׷��� Image�� �����ϴ� �뵵, paint() �Լ����� �̿��Ѵ�.
	private Image panelImage = null;
	private Image tmpImage = null;
	private Graphics gc2 = null;
	private Graphics gc3 = null;
	private Color c = Color.BLACK; // default = black
	private int mode = 0; // 0:line, 1:rectangle, 2:circle, 3:eraser, 4:fillrect, 5:fillcircle
	Image sketchImg = null;
	private int color = 0;
	String answer;
	private boolean isStarter = true;

	Vector<Point> startV = new Vector<>();
	Vector<Point> pos_v = new Vector<>();

	/**
	 * Create the frame.
	 * 
	 * @throws BadLocationException
	 */
	public JavaGameClientView(String username, String ip_addr, String port_no) {
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
		scrollPane.setBounds(851, 357, 192, 180);
		contentPane.add(scrollPane);

		textArea = new JTextPane();
		textArea.setEditable(true);
		textArea.setOpaque(true);
		textArea.setFont(new Font("����ü", Font.PLAIN, 14));
		scrollPane.setViewportView(textArea);

		txtInput = new JTextField();
		txtInput.setBounds(851, 544, 192, 28);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton(">");
		btnSend.setFont(new Font("����", Font.PLAIN, 14));
		btnSend.setBounds(796, 538, 50, 34);
		contentPane.add(btnSend);

		lblUserName[0] = new JLabel("");
		lblUserName[0].setBackground(Color.WHITE);
		lblUserName[0].setFont(new Font("��������üM", Font.BOLD, 16));
		lblUserName[0].setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName[0].setBounds(31, 202, 102, 50);
		contentPane.add(lblUserName[0]);
		setVisible(true);

		UserName = username;

		imgBtn = new JButton("+");
		imgBtn.setFont(new Font("����", Font.PLAIN, 11));
		imgBtn.setBounds(803, 493, 35, 35);
		contentPane.add(imgBtn);

		JLabel btnNewButton = new JLabel("", new ImageIcon("src/imgsrc/quitBtn.png"), JLabel.CENTER);
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
		gc2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
		gc2.setColor(Color.WHITE);
		gc2.drawRect(0, 0, panel.getWidth() - 1, panel.getHeight() - 1);
		
		tmpImage = createImage(panel.getWidth(), panel.getHeight());
		gc3 = tmpImage.getGraphics();
		gc3.setColor(panel.getBackground());
		gc3.fillRect(0, 0, panel.getWidth(), panel.getHeight());
		gc3.setColor(Color.WHITE);
		gc3.drawRect(0, 0, panel.getWidth() - 1, panel.getHeight() - 1);

		lblMouseEvent = new JLabel("");
		lblMouseEvent.setForeground(Color.BLACK);
		lblMouseEvent.setHorizontalAlignment(SwingConstants.CENTER);
		lblMouseEvent.setFont(new Font("��������üM", Font.BOLD, 14));
		lblMouseEvent.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblMouseEvent.setForeground(c);
		lblMouseEvent.setBounds(815, 270, 234, 40);
		lblMouseEvent.setText("Mode : " + getMode() + "| pen_size = " + pen_size);
		lblMouseEvent.setOpaque(true);
		contentPane.add(lblMouseEvent);

		btnBlack = new JLabel("", new ImageIcon("src/imgsrc/blackBtn.png"), JLabel.CENTER);
		btnBlack.setBounds(870, 0, 42, 72);
		btnBlack.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				c = Color.BLACK;
				color = 0;
				lblMouseEvent.setText("Mode : " + getMode() + "| pen_size = " + pen_size);
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
		contentPane.add(btnBlack);

		btnRed = new JLabel("", new ImageIcon("src/imgsrc/redBtn.png"), JLabel.CENTER);
		btnRed.setBounds(813, 30, 42, 72);
		btnRed.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				c = Color.RED;
				color = 1;
				lblMouseEvent.setText("Mode : " + getMode() + "| pen_size = " + pen_size);
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
		contentPane.add(btnRed);

		btnGreen = new JLabel("", new ImageIcon("src/imgsrc/greenBtn.png"), JLabel.CENTER);
		btnGreen.setBounds(813, 116, 42, 72);
		btnGreen.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				c = Color.GREEN;
				color = 3;
				lblMouseEvent.setForeground(c);
				lblMouseEvent.setText("Mode : " + getMode() + "| pen_size = " + pen_size);
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
				lblMouseEvent.setText("Mode : " + getMode() + "| pen_size = " + pen_size);
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
				lblMouseEvent.setText("Mode : " + getMode() + "| pen_size = " + pen_size);
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
		contentPane.add(btnPurple);
		btnPurple.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				c = new Color(175, 75, 214);
				color = 4;
				lblMouseEvent.setForeground(c);
				lblMouseEvent.setText("Mode : " + getMode() + "| pen_size = " + pen_size);
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
		contentPane.add(eraseBtn);
		eraseBtn.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				mode = 3;
				c = Color.WHITE;
				color = 6;
				lblMouseEvent.setForeground(c);
				lblMouseEvent.setText("Mode : " + getMode() + "| pen_size = " + pen_size);
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

		rectangleBtn = new JButton(new ImageIcon("src/imgsrc/rectangle_btn.png"));
		rectangleBtn.setBounds(467, 550, 45, 45);
		rectangleBtn.setBorderPainted(false);
		rectangleBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mode = 1;
				rectangleBtn.setBackground(Color.GRAY);
				fillrectangleBtn.setBackground(Color.WHITE);
				circleBtn.setBackground(Color.WHITE);
				fillcircleBtn.setBackground(Color.WHITE);
				penBtn.setBackground(Color.WHITE);
				lblMouseEvent.setText("Mode : " + getMode() + "| pen_size = " + pen_size);
			}
		});
		contentPane.add(rectangleBtn);

		fillrectangleBtn = new JButton(new ImageIcon("src/imgsrc/fillrect_btn.png"));
		fillrectangleBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mode = 4;
				rectangleBtn.setBackground(Color.WHITE);
				fillrectangleBtn.setBackground(Color.GRAY);
				circleBtn.setBackground(Color.WHITE);
				fillcircleBtn.setBackground(Color.WHITE);
				penBtn.setBackground(Color.WHITE);
				lblMouseEvent.setText("Mode : " + getMode() + "| pen_size = " + pen_size);
			}
		});
		fillrectangleBtn.setBorderPainted(false);
		fillrectangleBtn.setBounds(524, 550, 45, 45);
		contentPane.add(fillrectangleBtn);

		circleBtn = new JButton(new ImageIcon("src/imgsrc/circle_btn.png"));
		circleBtn.setOpaque(true);
		circleBtn.setBounds(581, 550, 45, 45);
		circleBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mode = 2;
				rectangleBtn.setBackground(Color.WHITE);
				fillrectangleBtn.setBackground(Color.WHITE);
				circleBtn.setBackground(Color.GRAY);
				fillcircleBtn.setBackground(Color.WHITE);
				penBtn.setBackground(Color.WHITE);
				lblMouseEvent.setText("Mode : " + getMode() + "| pen_size = " + pen_size);
			}
		});
		contentPane.add(circleBtn);

		fillcircleBtn = new JButton(new ImageIcon("src/imgsrc/fillcircle_btn.png"));
		fillcircleBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mode = 5;
				rectangleBtn.setBackground(Color.WHITE);
				fillrectangleBtn.setBackground(Color.WHITE);
				circleBtn.setBackground(Color.WHITE);
				fillcircleBtn.setBackground(Color.GRAY);
				penBtn.setBackground(Color.WHITE);
				lblMouseEvent.setText("Mode : " + getMode() + "| pen_size = " + pen_size);
			}
		});
		fillcircleBtn.setOpaque(true);
		fillcircleBtn.setBounds(638, 550, 45, 45);
		contentPane.add(fillcircleBtn);

		penBtn = new JButton(new ImageIcon("src/imgsrc/penBtn.png"));
		penBtn.setBounds(410, 550, 45, 45);
		penBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mode = 0;
				rectangleBtn.setBackground(Color.WHITE);
				fillrectangleBtn.setBackground(Color.WHITE);
				circleBtn.setBackground(Color.WHITE);
				fillcircleBtn.setBackground(Color.WHITE);
				penBtn.setBackground(Color.GRAY);
				lblMouseEvent.setText("Mode : " + getMode() + "| pen_size = " + pen_size);
			}
		});
		contentPane.add(penBtn);
		
		rectangleBtn.setBackground(Color.WHITE);
		fillrectangleBtn.setBackground(Color.WHITE);
		circleBtn.setBackground(Color.WHITE);
		fillcircleBtn.setBackground(Color.WHITE);
		penBtn.setBackground(Color.GRAY);

		sketchBtn = new JButton("sketch");
		sketchBtn.setFont(new Font("Bradley Hand ITC", Font.BOLD, 18));
		sketchBtn.setBounds(293, 550, 91, 45);
		sketchBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == sketchBtn) {
					if (sketchImg == null) {
						frame = new Frame("�ر׸� �̹��� ����");
						fd = new FileDialog(frame, "�ر׸� �̹��� ����", FileDialog.LOAD);

						fd.setVisible(true);
						if (fd.getDirectory().length() > 0 && fd.getFile().length() > 0) {
							sketchImg = new ImageIcon(fd.getDirectory() + fd.getFile()).getImage();
							gc2.drawImage(sketchImg, 0, 0, panel);
							gc.drawImage(sketchImg, 0, 0, panel);

							System.out.println(fd.getDirectory() + fd.getFile());
						}
					} else {
						sketchImg = null;
						panelImage = createImage(panel.getWidth(), panel.getHeight());
						gc2 = panelImage.getGraphics();
						gc2.setColor(panel.getBackground());
						gc2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
						gc2.setColor(Color.WHITE);
						gc2.drawRect(0, 0, panel.getWidth() - 1, panel.getHeight() - 1);
						gc.drawImage(panelImage, 0, 0, panel);
					}
				}
			}
		});
		contentPane.add(sketchBtn);

		resetBtn = new JLabel("", new ImageIcon("src/imgsrc/reset.png"), JLabel.CENTER);
		resetBtn.setBounds(697, 550, 50, 45);
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
				SendGameStart();
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
		lblUserName[1].setFont(new Font("��������üM", Font.BOLD, 16));
		lblUserName[1].setBackground(Color.WHITE);
		lblUserName[1].setBounds(31, 252, 102, 50);
		contentPane.add(lblUserName[1]);

		lblUserName[2] = new JLabel("");
		lblUserName[2].setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName[2].setFont(new Font("��������üM", Font.BOLD, 16));
		lblUserName[2].setBackground(Color.WHITE);
		lblUserName[2].setBounds(31, 301, 102, 50);
		contentPane.add(lblUserName[2]);

		lblUserName[3] = new JLabel("");
		lblUserName[3].setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName[3].setFont(new Font("��������üM", Font.BOLD, 16));
		lblUserName[3].setBackground(Color.WHITE);
		lblUserName[3].setBounds(31, 349, 102, 50);
		contentPane.add(lblUserName[3]);

		lblUserScore[0] = new JLabel("");
		lblUserScore[0].setHorizontalAlignment(SwingConstants.CENTER);
		lblUserScore[0].setFont(new Font("��������üM", Font.BOLD, 16));
		lblUserScore[0].setBackground(Color.WHITE);
		lblUserScore[0].setBounds(132, 202, 102, 50);
		contentPane.add(lblUserScore[0]);

		lblUserScore[1] = new JLabel("");
		lblUserScore[1].setHorizontalAlignment(SwingConstants.CENTER);
		lblUserScore[1].setFont(new Font("��������üM", Font.BOLD, 16));
		lblUserScore[1].setBackground(Color.WHITE);
		lblUserScore[1].setBounds(132, 251, 102, 50);
		contentPane.add(lblUserScore[1]);

		lblUserScore[2] = new JLabel("");
		lblUserScore[2].setHorizontalAlignment(SwingConstants.CENTER);
		lblUserScore[2].setFont(new Font("��������üM", Font.BOLD, 16));
		lblUserScore[2].setBackground(Color.WHITE);
		lblUserScore[2].setBounds(132, 300, 102, 50);
		contentPane.add(lblUserScore[2]);

		lblUserScore[3] = new JLabel("");
		lblUserScore[3].setHorizontalAlignment(SwingConstants.CENTER);
		lblUserScore[3].setFont(new Font("��������üM", Font.BOLD, 16));
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
						if(cm.UserName.equals("SERVER"))
							msg = cm.data;
						else
							msg = String.format("[%s]\n%s", cm.UserName, cm.data);
					} else
						continue;
					switch (cm.code) {
					case "101":
						UpdateUserlist(cm.data);
						break;
					case "200": // chat message
						if (cm.UserName.equals(UserName))
							AppendTextR(msg); // �� �޼����� ������
						else
							AppendText(msg);
						if (cm.data.equals(answer)) {
							SendGameOver(cm);
						}
						break;
					case "300": // Image ÷��
						if (cm.UserName.equals(UserName))
							AppendTextR("[" + cm.UserName + "]");
						else
							AppendText("[" + cm.UserName + "]");
						AppendImage(cm.img);
						break;
					case "500": // Mouse Event ����
						DoMouseEvent(cm, cm.color, cm.mode);
						break;
					case "501":
						ResetCanvas();
						break;
					case "600": // Game Start
						DoGameStart(cm);
						break;
					case "700": //Game Over
						DoGameOver(cm);
						UpdateUserScore(cm);
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

	public void UpdateUserlist(String users) {
		score.removeAllElements();
		for (int i = 0; i < lblUserName.length; i++) {
			lblUserName[i].setText("");
			lblUserScore[i].setText("");
		}

		String[] username = users.split(",");
		for (int i = 0; i < username.length; i++) {
			lblUserName[i].setText(username[i]);
			score.add(0);
			lblUserScore[i].setText(Integer.toString(score.elementAt(i)));
		}
	}

	public void UpdateUserScore(ChatMsg cm) {
		for (int i = 0; i < score.size(); i++) {
			lblUserScore[i].setText(Integer.toString(cm.scoreList.elementAt(i)));
		}
	}

	public void ResetCanvas() {
		gc2.setColor(panel.getBackground());
		gc2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
		gc.drawImage(panelImage, 0, 0, panel);
	}

	public void SendGameStart() {
		ResetCanvas();
		GameAnswer g = new GameAnswer();
		answer = g.getAnswer();
		JOptionPane.showMessageDialog(panel, "������ ������ '" + answer + "' �Դϴ�");

		isStarter = true;
		startBtn.setEnabled(false);
		ChatMsg cm = new ChatMsg(UserName, "600", "GAMESTART");
		cm.answer = answer;
		SendObject(cm);
	}

	public void DoGameStart(ChatMsg cm) {
		if (cm.UserName.matches(UserName))
			return;

		ResetCanvas();
		isStarter = false;
		btnBlack.setEnabled(false);
		btnRed.setEnabled(false);
		btnGreen.setEnabled(false);
		btnYellow.setEnabled(false);
		btnPurple.setEnabled(false);
		btnBlue.setEnabled(false);
		eraseBtn.setEnabled(false);
		rectangleBtn.setEnabled(false);
		circleBtn.setEnabled(false);
		fillrectangleBtn.setEnabled(false);
		fillcircleBtn.setEnabled(false);
		penBtn.setEnabled(false);
		sketchBtn.setEnabled(false);
		startBtn.setEnabled(false);
	}

	public void SendGameOver(ChatMsg cm) {
		for (int i = 0; i < lblUserName.length; i++) {
			if (lblUserName[i].getText().equals(cm.UserName)) {
				score.set(i, score.elementAt(i) + 10);
				break;
			}
		}
		
		ChatMsg cmsg = new ChatMsg(UserName, "700", "GAMEOVER");
		cmsg.winner = cm.UserName;
		cmsg.answer = answer;
		cmsg.scoreList = score;
		if(sketchImg!=null)
			cmsg.img = new ImageIcon(sketchImg);
		SendObject(cmsg);

		JOptionPane.showMessageDialog(panel, cm.UserName + "���� ���߼̽��ϴ�!\n ���� : '" + answer + "'");

		startBtn.setEnabled(true);
		ResetCanvas();
		c = Color.BLACK;
	}

	public void DoGameOver(ChatMsg cm) {
		score = cm.scoreList;
		isStarter = true;
		UpdateUserScore(cm);
		if (cm.UserName.matches(UserName))
			return;

		JLabel lb = new JLabel();
		if(cm.img!=null) {
			ImageIcon img = new ImageIcon(cm.img.getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT));
			lb.setIcon(img);
		}
		lb.setVerticalTextPosition(JLabel.BOTTOM);
		lb.setHorizontalTextPosition(JLabel.CENTER);
		if (cm.winner.equals(UserName)) {
			lb.setText("�����Դϴ�");
			JOptionPane.showMessageDialog(panel, lb, "���", JOptionPane.PLAIN_MESSAGE);
		}
		else {
			lb.setText(cm.winner + "���� ���߼̽��ϴ�!\n\n  ���� : '" + cm.answer + "'");
			JOptionPane.showMessageDialog(panel, lb, "���", JOptionPane.PLAIN_MESSAGE);
		}

		btnBlack.setEnabled(true);
		btnRed.setEnabled(true);
		btnGreen.setEnabled(true);
		btnYellow.setEnabled(true);
		btnPurple.setEnabled(true);
		btnBlue.setEnabled(true);
		eraseBtn.setEnabled(true);
		rectangleBtn.setEnabled(true);
		circleBtn.setEnabled(true);
		fillrectangleBtn.setEnabled(true);
		fillcircleBtn.setEnabled(true);
		penBtn.setEnabled(true);
		sketchBtn.setEnabled(true);
		startBtn.setEnabled(true);
		ResetCanvas();
		c = Color.BLACK;
	}

	// Mouse Event ���� ó��
	public void DoMouseEvent(ChatMsg cm, int color, int mode) {
		if (cm.UserName.matches(UserName)) // ���� ���� �̹� Local �� �׷ȴ�.
			return;

		gc2.setColor(cm.penColor(color));
		Graphics2D g = (Graphics2D) gc2;
		g.setStroke(new BasicStroke(cm.pen_size));
		if (mode == 0 || mode == 3) { // mode:line
			pos_v.add(cm.mouse_e.getPoint());
			if (pos_v.size() == 1) {
				pos_v.add(cm.mouse_e.getPoint());
				return;
			}
			for (int i = 0; i < pos_v.size() - 1; i++) {
				Point start = pos_v.elementAt(i);
				Point end = pos_v.elementAt(i + 1);
				gc2.drawLine((int) start.getX(), (int) start.getY(), (int) end.getX(), (int) end.getY());
			}
			gc.drawImage(panelImage, 0, 0, panel);
			if (cm.mouse_e.getID() == MouseEvent.MOUSE_RELEASED)
				pos_v.removeAllElements();

		} else if (mode == 1) { // mode:rectangle

			pos_v.add(cm.mouse_e.getPoint());

			Point start = pos_v.elementAt(0);
			Point end = pos_v.elementAt(pos_v.size() - 1);
	

			if (cm.mouse_e.getID() == MouseEvent.MOUSE_RELEASED) {
				gc2.drawRect((int) start.getX(), (int) start.getY(), Math.abs((int) start.getX() - (int) end.getX()),
						Math.abs((int) start.getY() - (int) end.getY()));
				gc.drawImage(panelImage, 0, 0, panel);
				pos_v.removeAllElements();
			}
		} else if (mode == 2) {// mode:circle

			pos_v.add(cm.mouse_e.getPoint());

			Point start = pos_v.elementAt(0);
			Point end = pos_v.elementAt(pos_v.size() - 1);

			if (cm.mouse_e.getID() == MouseEvent.MOUSE_RELEASED) {
				gc2.drawOval((int) start.getX(), (int) start.getY(), Math.abs((int) start.getX() - (int) end.getX()),
						Math.abs((int) start.getY() - (int) end.getY()));
				gc.drawImage(panelImage, 0, 0, panel);
				pos_v.removeAllElements();
			}
		} else if (mode == 4) { // mode:fillrectangle

			pos_v.add(cm.mouse_e.getPoint());

			Point start = pos_v.elementAt(0);
			Point end = pos_v.elementAt(pos_v.size() - 1);

			if (cm.mouse_e.getID() == MouseEvent.MOUSE_RELEASED) {
				gc2.fillRect((int) start.getX(), (int) start.getY(), Math.abs((int) start.getX() - (int) end.getX()),
						Math.abs((int) start.getY() - (int) end.getY()));
				gc.drawImage(panelImage, 0, 0, panel);
				pos_v.removeAllElements();
			}
		} else if (mode == 5) {// mode:fillcircle

			pos_v.add(cm.mouse_e.getPoint());

			Point start = pos_v.elementAt(0);
			Point end = pos_v.elementAt(pos_v.size() - 1);

			if (cm.mouse_e.getID() == MouseEvent.MOUSE_RELEASED) {
				gc2.fillOval((int) start.getX(), (int) start.getY(), Math.abs((int) start.getX() - (int) end.getX()),
						Math.abs((int) start.getY() - (int) end.getY()));
				gc.drawImage(panelImage, 0, 0, panel);
				pos_v.removeAllElements();
			}
		}

	}

	public void SendMouseEvent(MouseEvent e) {
		ChatMsg cm = new ChatMsg(UserName, "500", "MOUSE");
		cm.mouse_e = e;
		cm.pen_size = pen_size;
		cm.color = color;
		cm.mode = mode;

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
			lblMouseEvent.setText("Mode : " + getMode() + "| pen_size = " + pen_size);

		}

	}

	// Mouse Event Handler
	class MyMouseEvent implements MouseListener, MouseMotionListener {
		MouseEvent myMouseEvent;

		@Override
		public void mouseDragged(MouseEvent e) {
			if(!isStarter)
				return;
			myMouseEvent = e;
			gc2.setColor(c);

			if (mode == 0 || mode == 3) { // mode:line
				Graphics2D g = (Graphics2D) gc2;
				g.setStroke(new BasicStroke(pen_size));
				startV.add(e.getPoint());
				for (int i = 0; i < startV.size() - 1; i++) {
					Point start = startV.elementAt(i);
					Point end = startV.elementAt(i + 1);
					gc2.drawLine((int) start.getX(), (int) start.getY(), (int) end.getX(), (int) end.getY());
				}
			} else if (mode == 1) { // mode:rectangle
				startV.add(e.getPoint());

			} else if (mode == 2) {// mode:circle
				startV.add(e.getPoint());

			} else if (mode == 4) { // mode:fillrectangle
				startV.add(e.getPoint());

				Point start = startV.elementAt(0);
				for (int i = 0; i < startV.size() - 1; i++) {
					Point end = startV.elementAt(i + 1);
					int x = (int) Math.min(start.getX(), end.getX());
					int y = (int) Math.min(start.getY(), end.getY());
					int w = Math.abs((int) start.getX() - (int) end.getX());
					int h = Math.abs((int) start.getY() - (int) end.getY());
					gc2.fillRect(x, y, w, h);
				}
			} else if (mode == 5) { // mode:fillcircle
				startV.add(e.getPoint());
			}

			// panelImnage�� paint()���� �̿��Ѵ�.
			gc.drawImage(panelImage, 0, 0, panel);
			SendMouseEvent(myMouseEvent);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if(!isStarter)
				return;
			gc2.setColor(c);

			tmpImage = panelImage;
			gc3.drawImage(panelImage, 0, 0, panel);
			startV.add(e.getPoint());
			gc.drawImage(panelImage, 0, 0, panel);
			SendMouseEvent(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if(!isStarter)
				return;
			// �巡���� ����� ����
			Graphics2D g = (Graphics2D) gc2;
			g.setStroke(new BasicStroke(pen_size));

			if (mode == 1) { // mode:rectangle
				Point start = startV.elementAt(0);
				Point end = startV.elementAt(startV.size() - 1);
				int x = (int) Math.min(start.getX(), end.getX());
				int y = (int) Math.min(start.getY(), end.getY());
				int w = Math.abs((int) start.getX() - (int) end.getX());
				int h = Math.abs((int) start.getY() - (int) end.getY());
				gc2.drawRect(x, y, w, h);

			} else if (mode == 2) { // mode:circle
				Point start = startV.elementAt(0);
				Point end = startV.elementAt(startV.size() - 1);
				int x = (int) Math.min(start.getX(), end.getX());
				int y = (int) Math.min(start.getY(), end.getY());
				int w = Math.abs((int) start.getX() - (int) end.getX());
				int h = Math.abs((int) start.getY() - (int) end.getY());
				gc2.drawOval(x, y, w, h);

			} else if (mode == 4) { // mode:fillrectangle

				Point start = startV.elementAt(0);
				Point end = startV.elementAt(startV.size() - 1);
				int x = (int) Math.min(start.getX(), end.getX());
				int y = (int) Math.min(start.getY(), end.getY());
				int w = Math.abs((int) start.getX() - (int) end.getX());
				int h = Math.abs((int) start.getY() - (int) end.getY());
				gc2.fillRect(x, y, w, h);

			} else if (mode == 5) { // mode:fillcircle
				Point start = startV.elementAt(0);
				Point end = startV.elementAt(startV.size() - 1);
				int x = (int) Math.min(start.getX(), end.getX());
				int y = (int) Math.min(start.getY(), end.getY());
				int w = Math.abs((int) start.getX() - (int) end.getX());
				int h = Math.abs((int) start.getY() - (int) end.getY());
				gc2.fillOval(x, y, w, h);

			}
			gc.drawImage(panelImage, 0, 0, panel);
			pos_v.clear();
			SendMouseEvent(e);
			startV.clear();
		}
	}

	// keyboard enter key ġ�� ������ ����
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button�� �����ų� �޽��� �Է��ϰ� Enter key ġ��
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
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
				fd.setVisible(true);
				if (fd.getDirectory().length() > 0 && fd.getFile().length() > 0) {
					ChatMsg obcm = new ChatMsg(UserName, "300", "IMG");
					ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
					obcm.img = img;
					SendObject(obcm);
				}
			}
		}
	}


	public void AppendIcon(ImageIcon icon) {
		int len = textArea.getDocument().getLength();
		// ������ �̵�
		textArea.setCaretPosition(len);
		textArea.insertIcon(icon);
	}

	// ȭ�鿡 ���
	public void AppendText(String msg) {
		msg = msg.trim(); // �յ� blank�� \n�� �����Ѵ�.
		int len = textArea.getDocument().getLength();
		// ������ �̵�

		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.BLACK);
		doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), msg + "\n", left);
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
			doc.insertString(doc.getLength(), msg + "\n", right);
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
		// panelImage = ori_img.getScaledInstance(panel.getWidth(), panel.getHeight(),
		// Image.SCALE_DEFAULT);

		gc2.drawImage(ori_img, 0, 0, panel.getWidth(), panel.getHeight(), panel);
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

	private String getMode() {
		String state = null;
		if (mode == 0)
			state = "Pen";
		else if (mode == 1)
			state = "Rectangle";
		else if (mode == 2)
			state = "Circle";
		else if (mode == 3)
			state = "Eraser";
		else if (mode == 4)
			state = "FillRectangle";
		else if (mode == 5)
			state = "FillCircle";
		return state;
	}
}
