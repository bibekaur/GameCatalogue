
public class ReviewData {
	private String username;
	private String description;
	private Integer rating;
	private Integer userId;
	private Integer gameId;

	
	public ReviewData (String username, String description, Integer rating, Integer userId, Integer gameId){
		this.username = username;
		this.description= description;
		this.rating = rating;
		this.userId = userId;
		this.gameId = gameId;
		
	}
	
}
