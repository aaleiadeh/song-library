//By Ahmad Aleiadeh, Kristina Zarudna
package MVC;

public class Song {
	
	private String name;
	private String artist;
	private String album;
	private String year;
	
	public Song(String name, String artist, String album, String year)
	{
		this.name = name;
		this.artist = artist;
		this.album = album;
		this.year = year;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getArtist()
	{
		return artist;
	}
	
	public String getAlbum()
	{
		return album;
	}
	
	public String getYear()
	{
		return year;
	}
	
	public void editName(String name)
	{
		this.name = name;
	}
	
	public void editArtist(String artist)
	{
		this.artist = artist;
	}
	
	public void editAlbum(String album)
	{
		this.album = album;
	}
	
	public void editYear(String year)
	{
		this.year = year;
	}
}
