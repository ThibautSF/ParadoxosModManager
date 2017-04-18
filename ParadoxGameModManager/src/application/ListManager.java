package application;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

/**
 * @author SIMON-FINE Thibaut (alias Bisougai)
 *
 */
public class ListManager extends Stage {
	
	//Window Var
	private static int WINDOW_WIDTH = 600;
	private static int WINDOW_HEIGHT = 300;
	private BorderPane window = new BorderPane();
	private HBox menu = new HBox();
	private String lblstr = "Path : %s \nMod founds : %d";
	private Label pthMod = new Label(lblstr);
	private VBox content = new VBox();
	private String lblYrLists = "Your lists (%d founds)";
	private Label yourLists = new Label(lblYrLists);
	private ListView<String> lists = new ListView<String>();
	private VBox bottom = new VBox();
	private HBox buttons = new HBox();
	private Button newList = new Button("New");
	private Button modifyList = new Button("Modify");
	private Button delList = new Button("Delete");
	private Button applyList = new Button("Apply");
	private HBox buttons2 = new HBox();
	private Button exportList = new Button("Export");
	private Button importList = new Button("Import");
	
	//Local Var
	private File gameDir;
	private String absolutePath;
	private String[] modFiles;
	private ArrayList<ModList> userListArray = new ArrayList<ModList>();
	private MyXML userlistsXML;
	private String fileXML = ModManager.xmlDir+File.separator+"UserLists.xml";
	
	/**
	 * @throws Exception 
	 * 
	 */
	public ListManager(String path) {
		this.userlistsXML = new MyXML();
		gameDir = new File(path);
		absolutePath = gameDir.getAbsolutePath();
		
		setTitle(ModManager.APP_NAME+" : "+ModManager.GAME);
		
		//Top
		window.setTop(menu);
		menu.setStyle("-fx-background-color: #EAE795;");
		menu.getChildren().add(pthMod);
		pthMod.setText(String.format(lblstr,absolutePath,getModNumbers()));
		//launchGame.setStyle("-fx-alignment: right;");
		
		//Center
		window.setCenter(content);
		content.getChildren().addAll(yourLists,lists);
		content.setStyle("-fx-alignment: center;");
		yourLists.setText(lblYrLists);
		yourLists.setStyle("-fx-font: bold 20 serif;");
		try {
			updateList();
		} catch (Exception eCreate) {
			ErrorPrint.printError(eCreate,"When update ListView of ModLists on window creation");
			eCreate.printStackTrace();
		}
		
		lists.setPrefHeight(200);
		
		//fixed width for buttons
		newList.setPrefWidth(75);
		modifyList.setPrefWidth(75);
		delList.setPrefWidth(75);
		applyList.setPrefWidth(75);
		importList.setPrefWidth(75);
		exportList.setPrefWidth(75);
		
		//Bottom
		window.setBottom(bottom);
		buttons.setStyle("-fx-alignment: center;");
		buttons.getChildren().addAll(newList,modifyList,delList,applyList);
		buttons2.setStyle("-fx-alignment: center;");
		buttons2.getChildren().addAll(importList,exportList);
		bottom.getChildren().addAll(buttons,buttons2);
		
	    Scene sc = new Scene(window, WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setScene(sc);
		this.setMinHeight(WINDOW_HEIGHT);
		this.setMinWidth(WINDOW_WIDTH);
	    this.show();
	    
	    Stage stage = (Stage) window.getScene().getWindow();
	    stage.focusedProperty().addListener(new ChangeListener<Boolean>(){
	    	@Override
	    	public void changed(ObservableValue<? extends Boolean> ov, Boolean oldB, Boolean newB){
	    		if (newB.booleanValue()) {
	    			//Window focus
	    			try {
	    				updateList();
	    			} catch (Exception e) {
	  					ErrorPrint.printError(e,"When update ListView of ModLists on window focus");
	  					e.printStackTrace();
	  				}
	    		} else {
	    			//Window unfocus
                }
	    	}
	    });
	    
	    newList.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent t) {
	            new ListCreator(path,modFiles);
	        }//end action
	    });
	    
	    modifyList.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent t) {
	        	int pos = lists.getSelectionModel().getSelectedIndex();
	        	try{
	        		ModList toModify = userListArray.get(pos);
	        		new ListCreator(path, modFiles, toModify);
	        	} catch (Exception e) {
					if(pos==-1) ErrorPrint.printError(e,"User try to enter in list modification without selecting a list");
					else ErrorPrint.printError(e,"When enter in modification of a list");
					e.printStackTrace();
				}
	        }//end action
	    });
	    
	    Alert alertConfirm = new Alert(AlertType.CONFIRMATION);
    	alertConfirm.setTitle("Confirmation");
    	alertConfirm.setHeaderText("Confirm !");
	    
	    delList.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent t) {
	        	int pos = lists.getSelectionModel().getSelectedIndex();
	        	ModList toDelete = userListArray.get(pos);
	        	alertConfirm.setContentText("Are you ok to delete '"+toDelete.getName()+"' ?");

	        	Optional<ButtonType> result = alertConfirm.showAndWait();
	        	if (result.get() == ButtonType.OK){
	        		try {
	        			userlistsXML.readFile(fileXML);
	        			userlistsXML.removeList(toDelete.getName());
	        			updateList();
	        		} catch (Exception e) {
	        			if(pos==-1) ErrorPrint.printError(e,"User try to delete a list without selecting a list");
	        			else ErrorPrint.printError(e,"When trying to delete a list");
	        			e.printStackTrace();
	        		}
	        	}
	        }//end action
	    });
	    
	    applyList.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent t) {
	        	int pos = lists.getSelectionModel().getSelectedIndex();
	        	alertConfirm.setContentText("Are you ok to apply '"+userListArray.get(pos).getName()+"' ?");

	        	Optional<ButtonType> result = alertConfirm.showAndWait();
	        	if (result.get() == ButtonType.OK){
		        	try {
						if(applyModList(pos)){
							Alert alertInfo = new Alert(AlertType.CONFIRMATION);
							alertInfo.setTitle("Success");
							alertInfo.setHeaderText(null);
							alertInfo.setContentText("The list was successfully applied !");

							ButtonType buttonTypeLaunchGame = new ButtonType("Launch Game");
							ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

							alertInfo.getButtonTypes().setAll(buttonTypeLaunchGame, buttonTypeCancel);

							Optional<ButtonType> resultInfo = alertInfo.showAndWait();
							if (resultInfo.get() == buttonTypeLaunchGame){
								if(Desktop.isDesktopSupported()){
				        			new Thread(() -> {
				        		           try {
				        		        	   URI uri = new URI("steam://run/"+ModManager.STEAM_ID);
				        		               Desktop.getDesktop().browse(uri);
				        		           } catch (IOException | URISyntaxException e) {
				        		        	   ErrorPrint.printError(e);
				        		               e.printStackTrace();
				        		           }
				        		    }).start();
								}
							}
						}else{
							Alert alertError = new Alert(AlertType.ERROR);
							alertError.setTitle("Error");
							alertError.setHeaderText("Ooops, there was an error !");
							alertError.setContentText("Sorry but the list apply failed :(\nA debug file should be generated :)");

							alertError.showAndWait();
						}
					} catch (IOException e) {
						ErrorPrint.printError(e,"When list application");
						e.printStackTrace();
					}
	        	}
	        }//end action
	    });
	    
	    importList.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent t) {
	        	FileChooser importChooser = new FileChooser();
	        	FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
	        	importChooser.setTitle("Choose an exported list xml file for "+ModManager.GAME);
	        	importChooser.setInitialDirectory(new File(File.separator));
	        	importChooser.getExtensionFilters().add(extFilter);
	        	File file = importChooser.showOpenDialog(stage.getOwner());
				if (file!=null && !file.isDirectory()){
					try {
						String strResult = userlistsXML.importList(file.getAbsolutePath());
						
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Import result");
						alert.setHeaderText(null);
						alert.setContentText(strResult);

						alert.showAndWait();
					} catch (Exception e) {
						ErrorPrint.printError(e, "When import list");
						e.printStackTrace();
					}
				}
	        }//end action
	    });
	    
	    exportList.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent t) {
	        	int pos = lists.getSelectionModel().getSelectedIndex();
	        	try{
	        		userlistsXML.exportList(userListArray.get(pos).getName());
	        		
	        		Alert a = new Alert(AlertType.INFORMATION);
					a.setTitle("Import result");
					a.setHeaderText(null);
					a.setContentText("List exported");

					a.showAndWait();
	        	} catch (Exception e) {
					if(pos==-1) ErrorPrint.printError(e,"User try to export a list without selecting a list");
					else ErrorPrint.printError(e,"When export a list");
					e.printStackTrace();
				}
	        }//end action
	    });
	}
	
	/**
	 * @throws Exception
	 */
	public void updateList() throws Exception{
		userlistsXML.readFile(fileXML);
		userListArray = userlistsXML.getSavedList();
		yourLists.setText(String.format(lblYrLists, userListArray.size()));
		
		ArrayList<String> userListArrayStr = new ArrayList<String>();
		
		for (ModList oneModList : userListArray) {
			userListArrayStr.add(oneModList.getName()+" ("+oneModList.getModlist().size()+" mods)");
		}
		
		ObservableList<String> items = FXCollections.observableList(userListArrayStr);
		
		lists.setItems(items);
	}

	/**
	 * @param selected
	 * @return
	 * @throws IOException
	 */
	public boolean applyModList(int selected) throws IOException {
		ModList applyList = userListArray.get(selected);
		ArrayList<Mod> applyMods = applyList.getModlist();
		
		String sep = File.separator;
		File inputFile = new File(ModManager.PATH+sep+"settings.txt");
		File tempFile = new File(ModManager.PATH+sep+"new_setting.tmp");

		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

		String startLineRemove = "last_mods";
		String currentLine;
		boolean startEdit = false, startCopy = true, noLast_Mods = true, hasEqual = false, waitEqual = false;

		while ((currentLine = reader.readLine()) != null) {
			// trim newline when comparing with lineToRemove
			String trimmedLine = currentLine.trim();
			if (hasEqual && trimmedLine.contains("{")) {
				hasEqual = false;
				startEdit = true;
				writer.write(currentLine.substring(0, currentLine.indexOf("{") + 1) + System.getProperty("line.separator"));
			}
			if (waitEqual && trimmedLine.contains("=")) {
				waitEqual = false;
				if (trimmedLine.contains("{")) {
					startEdit = true;
					writer.write(
							currentLine.substring(0, currentLine.indexOf("{") + 1) + System.getProperty("line.separator"));
				} else {
					hasEqual = true;
				}
			}
			if (trimmedLine.contains(startLineRemove)) {
				String toWrite;
				if (trimmedLine.contains(startLineRemove + "={")) {
					startEdit = true;
					toWrite = currentLine.substring(0, currentLine.indexOf("{") + 1);
				} else if (trimmedLine.contains(startLineRemove + "=")) {
					hasEqual = true;
					toWrite = currentLine.substring(0, currentLine.indexOf("=") + 1);
				} else {
					waitEqual = true;
					toWrite = currentLine.substring(0,
							currentLine.indexOf(startLineRemove.charAt(startLineRemove.length() - 1)));
				}
				noLast_Mods = false;
				startCopy = false;
				writer.write(toWrite + System.getProperty("line.separator"));
			}
			if (startEdit) {
				modPrint(applyMods, writer);
				startEdit = false;
			} else {
				if (startCopy)
					writer.write(currentLine + System.getProperty("line.separator"));
				if (!startCopy && !hasEqual && !waitEqual) {
					if (trimmedLine.contains("}")) {
						startCopy = true;
						writer.write(currentLine.substring(currentLine.indexOf("}"), currentLine.length())
								+ System.getProperty("line.separator"));
					}
				}
			}
		}
		if(noLast_Mods){
			writer.write("last_mods={" + System.getProperty("line.separator"));
			modPrint(applyMods,writer);
			writer.write("}" + System.getProperty("line.separator"));
		}
		writer.close();
		reader.close();
		inputFile.delete();
		boolean successful = tempFile.renameTo(inputFile);
		return successful;
	}
	
	/**
	 * @param applyMods
	 * @param writer
	 * @throws IOException
	 */
	private void modPrint(ArrayList<Mod> applyMods, BufferedWriter writer) throws IOException{
		for (Mod mod : applyMods) {
    		String addLine="\t\"mod/"+mod.getFileName()+"\"";
			writer.write(addLine + System.getProperty("line.separator"));
		}
	}
	
	/**
	 * @return
	 */
	private int getModNumbers(){
		int modNumbers;
		String sep = File.separator;
		File modFile = new File(absolutePath+sep+"mod");
		modFiles = modFile.list(new FilenameFilter(){
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".mod");
		    }
		    });
		
		modNumbers = modFiles.length;
		return modNumbers;
	}
}