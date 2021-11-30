
// ChatMsg.java 채팅 메시지 ObjectStream 용.
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import javax.swing.ImageIcon;

class ChatMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	public String code; // 100:로그인, 400:로그아웃, 200:채팅메시지, 300:Image, 500: Mouse Event
	public String UserName;
	public String data;
	public ImageIcon img;
	public MouseEvent mouse_e;
	public int pen_size; // pen size
	public int color;
	public int mode;
	public Color c;

	public ChatMsg(String UserName, String code, String msg) {
		this.code = code;
		this.UserName = UserName;
		this.data = msg;
	}
	
	public Color penColor(int color) {
		Color c = null;
		if(color==0)
			c = Color.BLACK;
		else if(color==1)
			c = Color.RED;
		else if(color==2)
			c = new Color(236, 231, 26);
		else if(color==3)
			c = Color.GREEN;
		else if(color==4)
			c = new Color(175, 75, 214);
		else if(color==5)
			c = Color.BLUE;
		else if(color==6)
			c = Color.WHITE;
		return c;
	}
}