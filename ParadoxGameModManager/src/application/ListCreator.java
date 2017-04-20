package application;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author SIMON-FINE Thibaut (alias Bisougai)
 *
 */
public class ListCreator extends Stage {
	//Window Var
	private static int WINDOW_WIDTH = 700;
	private static int WINDOW_HEIGHT = 500;
	private GridPane window = new GridPane();
	
	private VBox titleBox = new VBox();	
	private Label lblListName = new Label("List Name : ");
	private TextField fieldListName = new TextField ();
	
	private VBox helpBox = new VBox();	
	private Label lblHelp = new Label("Primary Click on mod to activate/desactivate\nSecondary Click to open workshop");
	
	private VBox descrBox = new VBox();
	private Label lblListDesc = new Label("Description : ");
	private TextField fieldListDesc = new TextField ();
	
	private VBox yrModsBox = new VBox();
	private String lblYrMods = "Your mods (%d founds)";
	private Label yourMods = new Label(lblYrMods);
	
	private VBox listBox = new VBox();
	TableView<Mod> mods = new TableView<Mod>();
	TableColumn<Mod,String> modNameCol = new TableColumn<Mod,String>("Mod Name");
	TableColumn<Mod,String> fileNameCol = new TableColumn<Mod,String>("File");
    TableColumn<Mod,String> versionCol = new TableColumn<Mod,String>("Version");
    TableColumn<Mod,String> steamPath = new TableColumn<Mod,String>("Workshop");
	
    ObservableList<Mod> listOfMods = FXCollections.observableArrayList();
    ObservableList<Mod> selectedModsList = FXCollections.observableArrayList();
    ObservableList<Mod> missingMods = FXCollections.observableArrayList();
	private HBox cancelListBox = new HBox();
	private Button cancelList = new Button("Cancel");
	private HBox saveListBox = new HBox();
	private Button saveList = new Button("Save");
	private String lblSaveifMissings = "\tMissings mods will be cleared !";
	private Label saveifMissings = new Label(lblSaveifMissings);
	
	//Local Var
	private MyXML userlistsXML;
	private String fileXML = ModManager.xmlDir+File.separator+"UserLists.xml";
	private ModList list;
	private ArrayList<Mod> userMods = new ArrayList<Mod>();
	
	/**
	 * @param path
	 * @param modFiles
	 */
	public ListCreator(String path, String[] modFiles) {
		this(path,modFiles,new ModList(null,null,new ArrayList<Mod>()));
	}


	/**
	 * @param path
	 * @param modFiles
	 * @param list
	 */
	public ListCreator(String path, String[] modFiles, ModList list) {
		this.userlistsXML = new MyXML();
		this.list = list;
		
		for (String oneModFiles : modFiles) {
			String oneMod = oneModFiles;
			Mod m = new Mod(oneMod);
			userMods.add(m);
		}
		Collections.sort(userMods, new Comparator<Mod>() {
		    @Override
		    public int compare(Mod m1, Mod m2) {
		        return m1.getName().compareTo(m2.getName());
		    }
		});
		
		//stelDir = new File(path);
		//absolutePath = stelDir.getAbsolutePath();
		
		setTitle(ModManager.APP_NAME+" : "+ModManager.GAME);
		
		window.setHgap(8);
		window.setVgap(8);
		window.setMinSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		window.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		window.setPadding(new Insets(0, 0, 5, 0));
		//window.setGridLinesVisible(true);
		
		RowConstraints row1 = new RowConstraints();
	    row1.setPercentHeight(10);
	    RowConstraints row2 = new RowConstraints();
	    row2.setPercentHeight(10);
	    RowConstraints row3 = new RowConstraints();
	    row3.setPercentHeight(5);
	    RowConstraints row4 = new RowConstraints();
	    row4.setPercentHeight(70);
	    RowConstraints row5 = new RowConstraints();
	    row5.setPercentHeight(5);
	    window.getRowConstraints().addAll(row1,row2,row3,row4,row5);
	    
	    ColumnConstraints col1 = new ColumnConstraints();
	    col1.setPercentWidth(0);
	    ColumnConstraints col2 = new ColumnConstraints();
	    col2.setPercentWidth(50);
	    ColumnConstraints col3 = new ColumnConstraints();
	    col3.setPercentWidth(50);
	    ColumnConstraints col4 = new ColumnConstraints();
	    col4.setPercentWidth(0);
	    window.getColumnConstraints().addAll(col1,col2,col3,col4);
		
		
		//ModList title fields
		window.add(titleBox, 1, 0);
		titleBox.getChildren().add(lblListName);
		titleBox.getChildren().add(fieldListName);
		titleBox.setStyle("-fx-alignment: center-left;");
		fieldListName.setText(list.getName());
		
		//ModList help/info fields
		window.add(helpBox, 2, 0);
		helpBox.getChildren().add(lblHelp);
		helpBox.setStyle("-fx-alignment: center;");
		
		//ModList Descr fields
		window.add(descrBox, 1, 1, 2, 1);
		descrBox.getChildren().add(lblListDesc);
		descrBox.getChildren().add(fieldListDesc);
		descrBox.setStyle("-fx-alignment: center-left;");
		fieldListDesc.setText(list.getDescription());
		
		//ModList "Your mods" field
		window.add(yrModsBox, 1, 2, 2, 1);
		yrModsBox.getChildren().add(yourMods);
		yrModsBox.setStyle("-fx-alignment: center;");
		yourMods.setText(String.format(lblYrMods,modFiles.length));
		yourMods.setStyle("-fx-font: bold 20 serif;");
		
		//ModList list of mods
		window.add(listBox, 1, 3, 2, 1);
		listBox.getChildren().add(mods);
		modNameCol.setSortable(false);
		fileNameCol.setSortable(false);
		versionCol.setSortable(false);
        mods.getColumns().add(modNameCol);
        mods.getColumns().add(fileNameCol);
        mods.getColumns().add(versionCol);
        mods.getColumns().add(steamPath);
        
        modNameCol.setCellValueFactory(
        	new PropertyValueFactory<Mod,String>("name")
        );
        fileNameCol.setCellValueFactory(
        	new PropertyValueFactory<Mod,String>("fileName")
        );
        versionCol.setCellValueFactory(
        	new PropertyValueFactory<Mod,String>("versionCompatible")
        );
        steamPath.setCellValueFactory(
            new PropertyValueFactory<Mod,String>("steamPath")
        );
		
		mods.setRowFactory(tv -> {
	        TableRow<Mod> row = new TableRow<Mod>() {
	        	@Override
	            protected void updateItem(Mod item, boolean empty) {
	        		super.updateItem(item, empty) ;
			        if (item == null)
			            setStyle("");
			        else if (selectedModsList.contains(item))
			            setStyle("-fx-text-fill: white; -fx-background-color: #4CAF50;");
			        else if (missingMods.contains(item))
			        	setStyle("-fx-background-color: red; -fx-font-weight: bold;");
			        else
			            setStyle("");
	            }
	        };
	        
	        row.setOnMouseClicked(event -> {
	        	Mod mod = row.getItem();
        		mods.getSelectionModel().clearSelection();
	        	if (!row.isEmpty() && event.getButton()==MouseButton.PRIMARY){
	        		if(!mod.isMissing()){
						if(selectedModsList.contains(mod)){
							selectedModsList.remove(mod);
							row.setStyle("");
						}else{
							selectedModsList.add(mod);
							row.setStyle("-fx-text-fill: white; -fx-background-color: #4CAF50;");
						}
					}
	        	}else if(event.getButton()==MouseButton.SECONDARY){
	        		if(Desktop.isDesktopSupported()){
	        			new Thread(() -> {
	        		           try {
	        		        	   URI uri = new URI(mod.getSteamPath());
	        		               Desktop.getDesktop().browse(uri);
	        		           } catch (IOException | URISyntaxException e) {
	        		        	   ErrorPrint.printError(e);
	        		               e.printStackTrace();
	        		           }
	        		    }).start();
					}
	        	}
			});
	        
	        return row;
	    });
		
		printModList();
			
		//Buttons Cancel & Apply
		window.add(cancelListBox,1,4);
		cancelListBox.setStyle("-fx-alignment: center-right;");
		cancelListBox.getChildren().add(cancelList);
		
		window.add(saveListBox,2,4);
		saveListBox.setStyle("-fx-alignment: center-left;");
		saveListBox.getChildren().add(saveList);
		
		if(missingMods.size()>0){
			saveifMissings.setStyle("-fx-text-fill: red;");
			saveListBox.getChildren().add(saveifMissings);
		}
		
		Scene sc = new Scene(window, WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setScene(sc);
		this.setMinHeight(WINDOW_HEIGHT);
		this.setMinWidth(WINDOW_WIDTH);
		this.show();
		   
		cancelList.setOnAction(new EventHandler<ActionEvent>() {
			@Override
				public void handle(ActionEvent t) {
					Node  source = (Node)  t.getSource(); 
					Stage stage  = (Stage) source.getScene().getWindow();
					stage.close();
				}//end action
			});
		saveList.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				String listOldName = list.getName();
				list.setName(fieldListName.getText());
				list.setDescription(fieldListDesc.getText());
				ArrayList<Mod> saveSelectedMods = new ArrayList<Mod>();
				for (Mod mod : selectedModsList) {
					saveSelectedMods.add(mod);
				}
				list.setModlist(saveSelectedMods);
				try {
					userlistsXML.readFile(fileXML);
					if(listOldName!=null)
						userlistsXML.modifyList(list,listOldName);
					else
						userlistsXML.modifyList(list);
				} catch (Exception e) {
					ErrorPrint.printError(e,"When save list in mod");
					e.printStackTrace();
				}
				Node  source = (Node)  t.getSource(); 
				Stage stage  = (Stage) source.getScene().getWindow();
				stage.close();
			}//end action
		});
	}

	/**
	 * 
	 */
	private void printModList() {
		ArrayList<Mod> modsFromList = list.getModlist();
		
		for (Mod oneMod : userMods) {
			if(modsFromList.contains(oneMod))
				selectedModsList.add(oneMod);
		}
		
		for (Mod onemod : modsFromList) {
			if(onemod.isMissing())
				missingMods.add(onemod);
		}
		
		listOfMods.addAll(missingMods);
		listOfMods.addAll(userMods);
		
		mods.setItems(listOfMods);
	}
}
