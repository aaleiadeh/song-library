//By Ahmad Aleiadeh, Kristina Zarudna
package MVC;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class Controller {

	@FXML Button add;
	@FXML Button edit;
	@FXML Button delete;
	@FXML Button cancel;
	@FXML Button save;
	@FXML TextField titleText;
	@FXML TextField artistText;
	@FXML TextField albumText;
	@FXML TextField yearText;
	@FXML ListView<String> songList;
	ObservableList<String> songNames = FXCollections.observableArrayList(); //Song data converted to strings for display
	ArrayList<Song> songArray = new ArrayList<Song>(); //Used to store song object data in background
	public boolean editMode = false; //To determine whether savechanges is from add or edit

	public void start() throws FileNotFoundException//Just used to bind ListView to ObsList in SongLib app
	{
		songList.setItems(songNames);
		readFromFile();
		if(!(songArray.isEmpty()))
		{
			enableFields();
		}
	}
	
	public void updateSelection() {//Updates listview details on click
		if(!(songArray.isEmpty()))
		{
			displaySelectedDetails();
		}
	}
	
	public void addSong(ActionEvent e) { //Just handles aesthetic logistics
		add.setDisable(true);
		edit.setDisable(true);
		delete.setDisable(true);
		cancel.setDisable(false);
		save.setDisable(false);
		enableFields();
		deleteText();
		enableText();
	}
	public void editSong(ActionEvent e) {//enables text and toggles appropriate states for buttons
		edit.setDisable(true);
		enableText();
		cancel.setDisable(false);
		save.setDisable(false);
		add.setDisable(true);
		delete.setDisable(true);
		editMode = true;
		
	}
	
	public void deleteSong(ActionEvent e) throws IOException {
		int index = songList.getSelectionModel().getSelectedIndex();
		songArray.remove(index);
		songNames.remove(index);
		if(songNames.isEmpty()) //Cant edit or delete in an empty list so disables those features, wipes text, and disables textfields
		{
			edit.setDisable(true);
			delete.setDisable(true);
			deleteText();
			disableText();
			disableFields();
		}
		else if(index == songArray.size()) //Note to self. Size shrinks before this check by 1
		{
			songList.getSelectionModel().select(index-1);
			displaySelectedDetails();
		}
		else
		{
			songList.getSelectionModel().select(index);
			displaySelectedDetails();
		}
		writeToFile();
	}
	
	public void cancelEdit(ActionEvent e) {
		cancel.setDisable(true);
		save.setDisable(true);
		add.setDisable(false);
		disableText();
		deleteText(); //Removes edited text
		if(songArray.isEmpty())
		{
			disableFields();
		}
		else
		{
			edit.setDisable(false);
			delete.setDisable(false);//we cancelled an action and the list has items so must reenable delete and edit
			displaySelectedDetails(); //Resets text to highlighted song
		}
	}
	
	public void saveChanges(ActionEvent e) throws IOException {
		if(titleText.getText().equals("") || artistText.getText().equals(""))
		{
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setHeaderText("Input not valid");
			errorAlert.setContentText("You must enter the Song and Artist's name at the very least");
			errorAlert.showAndWait();
			if(!(songArray.isEmpty()))//Resets details to last displayed song if last wasnt empty before error and reenables appropriate buttons
			{
				displaySelectedDetails();
				delete.setDisable(false);
				edit.setDisable(false);
			}
			else//list is empty so wipe and disable text fields.
			{
				disableFields();
				deleteText();
			}
		}
		else if(!(isValidYear(yearText.getText()))) 
		{
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setHeaderText("Input not valid");
			errorAlert.setContentText("Year must be an integer");
			errorAlert.showAndWait();
			if(!(songArray.isEmpty()))//Resets details to last displayed song if last wasnt empty before error and reenables appropriate buttons
			{
				displaySelectedDetails();
				delete.setDisable(false);
				edit.setDisable(false);
			}
			else//list is empty so wipe and disable text fields.
			{
				disableFields();
				deleteText();
			}
		}
		else
		{	
			if(editMode == false)
			{
				int index = addSorted(createSong());
				edit.setDisable(false);
				delete.setDisable(false);
				songList.getSelectionModel().select(index);
				displaySelectedDetails();
				writeToFile();
			}
			else //Pressed save changes from edit
			{
				int index = songList.getSelectionModel().getSelectedIndex();
				Song song = createSong();
				Song temp = songArray.get(index);
				if(!(song.getName().toLowerCase().equals(temp.getName().toLowerCase())) || !(song.getArtist().toLowerCase().equals(temp.getArtist().toLowerCase())))//At the very minimum, there is a name
				{																																					//or artist change
					if(contains(songArray, song))//contains only checks if another song in the list contains same name and artist. in this case, there is no way the song its compared to is itself
					{
						Alert errorAlert = new Alert(AlertType.ERROR);
						errorAlert.setHeaderText("Input not valid");
						errorAlert.setContentText("Song with same Title and Artist already exists");
						errorAlert.showAndWait();
					}
					else
					{
						songArray.remove(index);
						songNames.remove(index);
						index = addSorted(song);
						writeToFile();
					}
				}
				else //Neither name or artist changed. Meaning either album changed, year changed, or nothing changed. Regardless updating the fields will achieve desired result
				{
					songArray.remove(index);
					songNames.remove(index);
					index = addSorted(song);
					writeToFile();
				}
				songList.getSelectionModel().select(index);
				displaySelectedDetails();
				edit.setDisable(false);
				delete.setDisable(false);
				editMode = false;
			}
		}
		add.setDisable(false);
		cancel.setDisable(true);
		save.setDisable(true);
		disableText();
	}
	
	private void disableText() {
		titleText.setEditable(false);
		artistText.setEditable(false);
		albumText.setEditable(false);
		yearText.setEditable(false); 
	}
	
	private void disableFields() {
		titleText.setDisable(true); 
		artistText.setDisable(true);
		albumText.setDisable(true); 
		yearText.setDisable(true);	
	}
	
	private void enableText() {
		titleText.setEditable(true);
		artistText.setEditable(true);
		albumText.setEditable(true);
		yearText.setEditable(true);
	}
	
	private void enableFields() {
		titleText.setDisable(false); 
		artistText.setDisable(false);
	    albumText.setDisable(false); 
		yearText.setDisable(false);
	}
	
	private void deleteText() {
		titleText.setText("");
		artistText.setText("");
		albumText.setText("");
		yearText.setText("");
	}
	
	private int addSorted(Song a)
	{
		int index = 0;
		for(Song song: songArray)
		{
			if(song.getName().toLowerCase().equals(a.getName().toLowerCase()))
			{
				if(song.getArtist().toLowerCase().equals(a.getArtist().toLowerCase()))
				{
					Alert errorAlert = new Alert(AlertType.ERROR);
					errorAlert.setHeaderText("Input not valid");
					errorAlert.setContentText("Cannot add duplicate songs");
					errorAlert.showAndWait();
					return index;
				}
				else
				{
					if(song.getArtist().toLowerCase().compareTo(a.getArtist().toLowerCase()) > 0)
					{
						songArray.add(index, a);
						songNames.add(index, a.getName() + " By: " + a.getArtist());
						return index;
					}
				}
			}
			if(song.getName().toLowerCase().compareTo(a.getName().toLowerCase()) > 0)
			{
				songArray.add(index, a);
				songNames.add(index, a.getName() + " By: " + a.getArtist());
				return index;
			}
			index++;
		}
		songArray.add(index, a);
		songNames.add(index, a.getName() + " By: " + a.getArtist());
		return index;
	}
	
	private void displaySelectedDetails()
	{
		int index = songList.getSelectionModel().getSelectedIndex();
		Song temp = songArray.get(index);
		titleText.setText(temp.getName());
		artistText.setText(temp.getArtist());
		albumText.setText(temp.getAlbum());
		yearText.setText(temp.getYear());
	}
	
	private boolean contains(ArrayList<Song> arr, Song a)
	{
		for(Song song: arr)
		{
			if(song.getName().toLowerCase().equals(a.getName().toLowerCase()) && song.getArtist().toLowerCase().equals(a.getArtist().toLowerCase()))
			{
				return true;
			}
		}
		return false;
	}
	
	public void readFromFile() throws FileNotFoundException  {
		int index = 0;
		Song song = null;
		Scanner s = new Scanner(new File("Songs.txt"));
	    while (s.hasNext()) {
	    	String str1 = s.nextLine();
	        String str2 = s.nextLine();
	        String str3 = s.nextLine();
	        String str4 = s.nextLine();
	            
	        str1 = str1.substring(6);
	        str2 = str2.substring(7);
	        str3 = str3.substring(6);
	        str4 = str4.substring(5);
	            
	        song = new Song(str1, str2,str3, str4);
	        index = addSorted(song);
			songList.getSelectionModel().select(index);
			displaySelectedDetails();
	    }	
		s.close();
		if(!(songArray.isEmpty()))
		{
			edit.setDisable(false);
			delete.setDisable(false);
		}
	}
	
	public void writeToFile() throws IOException  {
		File file = new File("Songs.txt");
		file.delete();
		file.createNewFile();
		FileWriter filewriter = new FileWriter(file);
		for(Song song: songArray)
		{
			filewriter.write("Title:"+song.getName()+"\n");
			filewriter.write("Artist:"+song.getArtist()+"\n");
			filewriter.write("Album:"+song.getAlbum()+"\n");
			filewriter.write("Year:"+song.getYear()+"\n");
		}
		filewriter.close();
	}
	
	private Song createSong()
	{
		Song song = new Song(titleText.getText(), artistText.getText(), albumText.getText(), yearText.getText());
		return song;
	}
	
	private boolean isValidYear(String s)
	{
		if(s.equals(""))
			return true;
		try {
			Integer.parseInt(s);
			return true;
		}catch(Exception e) {
			return false;
		}
	}
}
