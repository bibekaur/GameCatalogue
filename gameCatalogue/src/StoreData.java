import javax.swing.JButton;
import javax.swing.JTextArea;

public class StoreData {
	private String gameName;
	private String gameGenre;
	private Integer rating;
	private String date;
	private JButton button;
	private JTextArea text;
	
	public StoreData(String gameName, String gameGenre, Integer rating, String date, JButton button, JTextArea text) {
		this.gameName = gameName;
		this.gameGenre = gameGenre;
		this.rating = rating;
		this.date = date;
		this.button = button;
		this.text = text;
	}
	
	public String getGameName() {
		return this.gameName;
	}
	public String getGameGenre() {
		return this.gameGenre;
	}
	public Integer getRating() {
		return this.rating;
	}
	public String getDate() {
		return this.date;
	}
	public JButton getButton() {
		return this.button;
	}
	public JTextArea getText() {
		return this.text;
	}
}
