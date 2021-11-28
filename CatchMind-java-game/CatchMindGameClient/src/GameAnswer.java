import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class GameAnswer {
	
	Vector<String> answer = new Vector<String>();
	
	public GameAnswer() {
		File file = new File("src/imgsrc/answer.txt");
		FileReader fr;
		try {
			fr = new FileReader(file);
		
		BufferedReader br = new BufferedReader(fr);
		String line = "";

		while((line = br.readLine())!=null) {
			answer.add(line);
		}			
			
		}catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public String getAnswer() {
		return answer.elementAt((int) Math.floor(Math.random()*answer.size()));
	}
}
