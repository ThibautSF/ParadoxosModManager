package application;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import debug.ErrorPrint;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mod.Mod;
import mod.ModList;
import settings.MyXML;
import window.WorkIndicatorDialog;

/**
 * @author SIMON-FINE Thibaut (alias Bisougai)
 *
 */
public class ListManager extends Stage {
	private static MyXML userlistsXML = new MyXML();
	private static List<String> modFileNames = Arrays.asList("mod","mods");
	
	//Window Var
	private static int WINDOW_WIDTH = 800;
	private static int WINDOW_HEIGHT = 600;
	private GridPane window = new GridPane();
	
	private VBox menu = new VBox();
	private String pthModstr = "Path : %s";
	private Label pthModLbl = new Label(pthModstr);
	private String nbModstr = "Mod(s) found : %d";
	private Label nbModLbl = new Label(nbModstr);
	
	private HBox actionsBox = new HBox(8);
	private Button buttonRefresh = new Button();
	private Button buttonBack = new Button();
	
	private VBox yrListsBox = new VBox();
	private String lblYrLists = "Your lists (%d found)";
	private Label yourLists = new Label(lblYrLists);
	
	private VBox content = new VBox();
	private TableView<ModList> lists = new TableView<ModList>();
	private TableColumn<ModList,String> listNameCol = new TableColumn<ModList,String>("List Name");
	//private TableColumn<ModList,String> listDescrCol = new TableColumn<ModList,String>("Description");
	private TableColumn<ModList,String> languageCol = new TableColumn<ModList,String>("Language");
	private TableColumn<ModList,Integer> nbModCol = new TableColumn<ModList,Integer>("NB");
	
	private ObservableList<ModList> listOfLists = FXCollections.observableArrayList();
	//private ObservableList<ModList> selectedListsList = FXCollections.observableArrayList();
	
	private HBox buttons = new HBox(8);
	private Button newList = new Button("New");
	private Button modifyList = new Button("Modify");
	private Button delList = new Button("Delete");
	private Button applyList = new Button("Apply");
	private HBox buttons2 = new HBox(8);
	private Button exportList = new Button("Export");
	private Button importList = new Button("Import");
	
	//Local Var
	private File gameDir;
	private String absolutePath;
	private String fileXML;
	private Map<String, Mod> availableMods = new HashMap<>();
	private List<ModList> userListArray = new ArrayList<ModList>();
	private WorkIndicatorDialog<String> wd = null;
	
	/**
	 * @throws FileNotFoundException 
	 * @throws Exception 
	 * 
	 */
	public ListManager(String path) throws FileNotFoundException {
		gameDir = new File(path);
		absolutePath = gameDir.getAbsolutePath();
		fileXML = ModManager.xmlDir+File.separator+"UserLists.xml";
		
		setTitle(ModManager.APP_NAME+" : "+ModManager.GAME);
		
		window.setHgap(8);
		window.setVgap(8);
		window.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		window.setPadding(new Insets(0, 0, 5, 0));
		
		//Uncomment when editing window to see cells
		//window.setGridLinesVisible(true);
		
		RowConstraints row1 = new RowConstraints(40, 40, 40);
		RowConstraints row2 = new RowConstraints(25, 25, 25);
		RowConstraints row3 = new RowConstraints();
		row3.setMaxHeight(Double.MAX_VALUE);
		row3.setVgrow(Priority.ALWAYS);
		content.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		VBox.setVgrow(lists, Priority.ALWAYS);
		RowConstraints row4 = new RowConstraints(30, 30, 30);
		RowConstraints row5 = new RowConstraints(30, 30, 30);
		window.getRowConstraints().addAll(row1,row2,row3,row4,row5);
		
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(0);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(25);
		ColumnConstraints col3 = new ColumnConstraints();
		col3.setPercentWidth(25);
		ColumnConstraints col4 = new ColumnConstraints();
		col4.setPercentWidth(25);
		ColumnConstraints col5 = new ColumnConstraints();
		col5.setPercentWidth(25);
		ColumnConstraints col6 = new ColumnConstraints();
		col6.setPercentWidth(0);
		window.getColumnConstraints().addAll(col1,col2,col3,col4,col5,col6);
		
		
		//ListManager Top
		window.add(menu, 0, 0, 6, 1);
		menu.setStyle("-fx-background-color: #EAE795;");
		menu.getChildren().addAll(pthModLbl,nbModLbl);
		pthModLbl.setText(String.format(pthModstr,absolutePath));
		
		refreshTexts();
		
		window.add(actionsBox, 4, 0);
		buttonRefresh.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
		buttonBack.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.LONG_ARROW_LEFT));
		actionsBox.setAlignment(Pos.CENTER_RIGHT);
		actionsBox.getChildren().addAll(buttonRefresh,buttonBack);
		
		buttonRefresh.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				try {
					loadModFilesArray();
				} catch (Exception e) {
					ErrorPrint.printError(e, "Refresh");
				}
			}//end action
		});
		
		buttonBack.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				Node  source = (Node)  t.getSource(); 
				Stage stage  = (Stage) source.getScene().getWindow();
				stage.close();
				try {
					new ModManager(true);
				} catch (Exception e) {
					ErrorPrint.printError(e, "Reload Game");
				}
			}//end action
		});
		
		//ModList "Your mods" field
		window.add(yrListsBox, 1, 1, 4, 1);
		yrListsBox.getChildren().add(yourLists);
		yrListsBox.setStyle("-fx-alignment: center;");
		yourLists.setStyle("-fx-font: bold 20 serif;");
		
		//Center
		window.add(content, 1, 2, 4, 1);
		content.getChildren().add(lists);
		content.setStyle("-fx-alignment: center;");
		
		listNameCol.setSortable(false);
		languageCol.setSortable(false);
		nbModCol.setSortable(false);
		lists.getColumns().add(listNameCol);
		lists.getColumns().add(languageCol);
		lists.getColumns().add(nbModCol);
		
		listNameCol.setCellValueFactory(
			new PropertyValueFactory<ModList,String>("name")
		);
		listNameCol.setMinWidth(300);
		
		languageCol.setCellValueFactory(
			cell -> new SimpleStringProperty(cell.getValue().getLanguageName().toUpperCase(Locale.ENGLISH))
		);
		
		nbModCol.setCellValueFactory(
			cell -> new SimpleIntegerProperty(cell.getValue().getModlist().size()).asObject()
		);
		
		lists.setRowFactory(tv -> {
			TableRow<ModList> row = new TableRow<ModList>() {
				/* *
				@Override
				protected void updateItem(ModList item, boolean empty) {
					super.updateItem(item, empty) ;
					if (item == null)
						setStyle("");
					else if (selectedListsList.contains(item))
						setStyle("-fx-text-fill: white; -fx-background-color: #4CAF50;");
					else
						setStyle("");
				}
				/* */
			};
			
			row.setOnMouseClicked(event -> {
				int pos = row.getIndex();
				
				//Enable/Disable buttons which need a selected list
				if (!row.isEmpty() && event.getButton()==MouseButton.PRIMARY){
					if(pos>=0){
						modifyList.setDisable(false);
						delList.setDisable(false);
						applyList.setDisable(false);
						exportList.setDisable(false);
					} else {
						modifyList.setDisable(true);
						delList.setDisable(true);
						applyList.setDisable(true);
						exportList.setDisable(true);
					}
				}
			});
			
			return row;
		});
		
		//fixed width for buttons
		newList.setPrefWidth(75);
		modifyList.setPrefWidth(75);
		delList.setPrefWidth(75);
		applyList.setPrefWidth(75);
		importList.setPrefWidth(75);
		exportList.setPrefWidth(75);
		
		//Buttons line 1
		window.add(buttons, 1, 3, 4, 1);
		buttons.setStyle("-fx-alignment: bottom-center;");
		buttons.getChildren().addAll(newList,modifyList,delList,applyList);
		modifyList.setDisable(true);
		delList.setDisable(true);
		applyList.setDisable(true);
		
		//Buttons line 2
		window.add(buttons2, 1, 4, 4, 1);
		buttons2.setStyle("-fx-alignment: top-center;");
		buttons2.getChildren().addAll(importList,exportList);
		exportList.setDisable(true);
		
		Scene sc = new Scene(window, WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setScene(sc);
		this.setMinHeight(WINDOW_HEIGHT);
		this.setMinWidth(WINDOW_WIDTH);
		this.show();
		
		//Load the list of mod files
		loadModFilesArray();
		
		Stage stage = (Stage) window.getScene().getWindow();
		stage.focusedProperty().addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean oldB, Boolean newB){
				if (newB.booleanValue()) {
					//Window focus
					try {
						updateList();
						refreshTexts();
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
				new ListCreator(path, availableMods);
			}//end action
		});
		
		modifyList.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				int pos = lists.getSelectionModel().getSelectedIndex();
				ModList toModify = lists.getSelectionModel().getSelectedItem();
				try{
					new ListCreator(path, availableMods, toModify);
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
				ModList toDelete = lists.getSelectionModel().getSelectedItem();
				alertConfirm.setContentText("Are you ok to delete '"+toDelete.getName()+"' ?");
				
				Optional<ButtonType> result = alertConfirm.showAndWait();
				if (result.get() == ButtonType.OK){
					try {
						userlistsXML.readFile(fileXML);
						userlistsXML.removeList(toDelete.getName());
						updateList();
						refreshTexts();
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
				ModList toApply = lists.getSelectionModel().getSelectedItem();
				alertConfirm.setContentText("Are you ok to apply '"+toApply.getName()+"' ?");
				
				Optional<ButtonType> result = alertConfirm.showAndWait();
				if (result.get() == ButtonType.OK){
					try {
						if(applyOneModList(toApply)){
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
						String strResult = userlistsXML.importList(file.getAbsolutePath(), availableMods);
						updateList();
						refreshTexts();
						
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
				ModList toExport = lists.getSelectionModel().getSelectedItem();
				try{
					userlistsXML.exportList(toExport.getName());
					
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
	private void updateList() throws Exception {
		userlistsXML.readFile(fileXML);
		userListArray = userlistsXML.getSavedList(availableMods);
		
		
		listOfLists.clear();
		listOfLists.addAll(userListArray);
		
		lists.setItems(listOfLists);
		lists.refresh();
		
		//Loose selection after refresh
		modifyList.setDisable(true);
		delList.setDisable(true);
		applyList.setDisable(true);
		exportList.setDisable(true);
	}
	
	private void refreshTexts() {
		nbModLbl.setText(String.format(nbModstr, getModNumber()));
		yourLists.setText(String.format(lblYrLists, getListNumber()));
	}
	
	/**
	 * @param selected
	 * @return
	 * @throws IOException
	 */
	private boolean applyOneModList(ModList applyList) throws IOException {
		List<Mod> applyMods = applyList.getModlist();
		
		if(applyList.isCustomOrder()) {
			generateCustomModFiles(applyMods);
		}
		
		String sep = File.separator;
		File inputFile = new File(ModManager.PATH+sep+"settings.txt");
		File tempFile = new File(ModManager.PATH+sep+"new_setting.tmp");

		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

		String startLineRemove = "gui";
		String aloneLineRemove = "language";
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
				if (startLineRemove.equals("last_mods")) {
					noLast_Mods = false;
				}
				startCopy = false;
				writer.write(toWrite + System.getProperty("line.separator"));
			}
			if (startEdit) {
				if (startLineRemove.equals("gui")) {
					printLanguageBloc(applyList.getLanguageCode(), writer);
					startLineRemove = "last_mods";
				} else {
					if(applyList.isCustomOrder())
						modPrint(applyMods, writer, "pmm_");
					else
						modPrint(applyMods, writer);
				}
				startEdit = false;
			} else {
				if (startCopy) {
					if (trimmedLine.contains(aloneLineRemove)) {
						writer.write(aloneLineRemove + "=\"" + applyList.getLanguageCode()
								+ "\"" + System.getProperty("line.separator"));
						startLineRemove = "last_mods";
					} else {
						writer.write(currentLine + System.getProperty("line.separator"));
					}
				}
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
			if(applyList.isCustomOrder())
				modPrint(applyMods, writer, "pmm_");
			else
				modPrint(applyMods, writer);
			writer.write("}" + System.getProperty("line.separator"));
		}
		writer.close();
		reader.close();
		inputFile.delete();
		boolean successful = tempFile.renameTo(inputFile);
		return successful;
	}
	
	private void generateCustomModFiles(List<Mod> applyMods) {
		// TODO Logic seems good, but the customMod/ folder idea don't work ! Need to use mod/ → done
		// TODO add clean custom .mod button OR clean when load .mod files → done
		
		//Check if 'customMod/' exist in doc game folder (if not → create it)
		//UPDATE : is useless with mod/
		String sep = File.separator;
		File modDir = new File(ModManager.PATH+sep+"mod");
		/*
		if(!(customModFolder.exists() || customModFolder.isDirectory())) {
			customModFolder.mkdir();
		}
		*/
		
		//Make 'cutomMod/' empty
		//UPDATE : clean 'mod/' of pmm_xxxxxxx.mod files
		/*
		File[] content = customModFolder.listFiles();
		for (File file : content) {
			file.delete();
		}
		*/
		File[] content = modDir.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().startsWith("pmm_") && name.toLowerCase().endsWith(".mod");
			}
		});
		for (File file : content) {
			file.delete();
		}
		
		//Create the custom .mod file for each mod (XXX_idorname.mod)
		int n = applyMods.size();
		int digits = 0;
		do {
			n = n/10;
			digits++;
		} while (n!=0);
		String numberFormat = "%0"+digits+"d";
		String.format("%03d", 1);
		for (int i = 0; i < applyMods.size(); i++) {
			Mod mod = applyMods.get(i);
			
			String customModName = String.format(numberFormat, i)+"_"+mod.getName();
			
			File modFile = new File(ModManager.PATH+sep+"mod"+sep+mod.getFileName());
			File customModFile = new File(ModManager.PATH+sep+"mod"+sep+"pmm_"+mod.getFileName());
			try {
				Files.copy(Paths.get(modFile.getAbsolutePath()), Paths.get(customModFile.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
				
				File inputFile = customModFile;
				File tempFile = new File(ModManager.PATH+sep+"mod"+sep+String.format(numberFormat, i)+mod.getFileName()+".tmp");
				
				BufferedReader reader = new BufferedReader(new FileReader(inputFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
				
				//String startLineRemove = "name";
				String aloneLineRemove = "name";
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
					/*
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
						if (startLineRemove.equals("last_mods")) {
							noLast_Mods = false;
						}
						startCopy = false;
						writer.write(toWrite + System.getProperty("line.separator"));
					}
					*/
					if (startEdit) {
						/*
						if (startLineRemove.equals("gui")) {
							printLanguageBloc(applyList.getLanguageCode(), writer);
							startLineRemove = "last_mods";
						} else {
							if(applyList.isCustomOrder())
								modPrint(applyMods, writer, "customMods/");
							else
								modPrint(applyMods, writer);
						}
						*/
						startEdit = false;
					} else {
						if (startCopy) {
							if (trimmedLine.contains(aloneLineRemove)) {
								writer.write(aloneLineRemove + "=\"" + customModName
										+ "\"" + System.getProperty("line.separator"));
								//startLineRemove = "last_mods";
							} else {
								writer.write(currentLine + System.getProperty("line.separator"));
							}
						}
						if (!startCopy && !hasEqual && !waitEqual) {
							if (trimmedLine.contains("}")) {
								startCopy = true;
								writer.write(currentLine.substring(currentLine.indexOf("}"), currentLine.length())
										+ System.getProperty("line.separator"));
							}
						}
					}
				}
				/*
				if(noLast_Mods){
					writer.write("last_mods={" + System.getProperty("line.separator"));
					if(applyList.isCustomOrder())
						modPrint(applyMods, writer, "customMods/");
					else
						modPrint(applyMods, writer);
					writer.write("}" + System.getProperty("line.separator"));
				}
				*/
				writer.close();
				reader.close();
				inputFile.delete();
				boolean successful = tempFile.renameTo(inputFile);
				
			} catch (IOException e) {
				// TODO Bloc catch généré automatiquement
				e.printStackTrace();
			}
			
			
			
		}
	}
	
	/**
	 * @param applyMods
	 * @param writer
	 * @throws IOException
	 */
	private void modPrint(List<Mod> applyMods, BufferedWriter writer) throws IOException {
		modPrint(applyMods, writer, "", "mod/");
	}
	
	private void modPrint(List<Mod> applyMods, BufferedWriter writer, String prefix) throws IOException {
		modPrint(applyMods, writer, prefix, "mod/");
	}
	
	private void modPrint(List<Mod> applyMods, BufferedWriter writer, String prefix, String modfolder) throws IOException {
		if(!(modfolder.lastIndexOf("/")==modfolder.length()-1)) modfolder+="/";
		for (Mod mod : applyMods) {
			String addLine="\t\""+modfolder+prefix+mod.getFileName()+"\"";
			writer.write(addLine + System.getProperty("line.separator"));
		}
	}
	
	private void printLanguageBloc(String languageCode, BufferedWriter writer) throws IOException {
		writer.write("\tlanguage=" + languageCode + System.getProperty("line.separator") +
				"\thas_set_language=yes" + System.getProperty("line.separator"));
	}
	
	/**
	 * @return
	 */
	private int getModNumber(){
		return availableMods.size();
	}
	
	/**
	 * @return
	 */
	private int getListNumber(){
		return userListArray.size();
	}
	
	private void loadModFilesArray() {
		String workLabel = ModManager.isConflictComputed() ? "Generate mods and conflicts..." : "Generate mods...";

		wd = new WorkIndicatorDialog<String>(window.getScene().getWindow(), workLabel);
		
		wd.addTaskEndNotification(result -> {
			try {
				updateList();
			} catch (Exception eCreate) {
				ErrorPrint.printError(eCreate,"When update ListView of ModLists on window creation");
				eCreate.printStackTrace();
			}
			
			refreshTexts();
			
			wd=null; // don't keep the object, cleanup
		});
		
		wd.exec("LoadMods", inputParam -> {
			//String sep = File.separator;
			File userDir = new File(absolutePath);
			
			File[] childs = userDir.listFiles();
			
			for (int i = 0; i < childs.length; i++) {
				File modDir = childs[i];
				
				if (modDir.isDirectory() && ListManager.modFileNames.contains(modDir.getName().toLowerCase())) {
					//Clean customModFiles
					File[] content = modDir.listFiles(new FilenameFilter(){
						@Override
						public boolean accept(File dir, String name) {
							return name.toLowerCase().startsWith("pmm_") && name.toLowerCase().endsWith(".mod");
						}
					});
					for (File file : content) {
						file.delete();
					}
					
					String[] modFiles = modDir.list(new FilenameFilter(){
						@Override
						public boolean accept(File dir, String name) {
							return name.toLowerCase().endsWith(".mod");
						}
					});
					wd.maxProgress = modFiles.length;
					int j = 0;
					for (String modFile : modFiles) {
						availableMods.put(modFile, new Mod(modFile, ModManager.isConflictComputed()));
						j++;
						wd.currentProgress = j;
					}
				} else {
					//throw new FileNotFoundException("The folder '"+modFile.getAbsolutePath()+"' is missing, please check the path.\nBe sure to have started the game launcher once !");
				}
			}
			
			return new Integer(1);
		});
	}
}
