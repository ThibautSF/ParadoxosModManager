package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import org.jdom2.DataConversionException;
import settings.MyXML;
import versioning.OnlineVersionChecker;
import debug.ErrorPrint;

/**
 * Main class for Paradoxos Mod Manager
 * 
 * @author SIMON-FINE Thibaut (alias Bisougai)
 * 
 */
public class ModManager extends Application {
	
	private static String OS = System.getProperty("os.name").toLowerCase();
	
	public static String UPDATE_ZIP_NAME = "update.zip";
	public static String UPDATER_NAME = "Updater.jar";
	
	public static ObservableList<String> SUPPORTED_GAMES = FXCollections.observableArrayList("Stellaris", "Europa Universalis IV", "Crusader Kings II", "Hearts of Iron IV");
	public static List<Integer> GAMES_STEAM_ID = Arrays.asList(                                281990,            236850,                203770,             394360);
	public static String APP_NAME = "Paradoxos Mod Manager";
	public static String PATH;
	public static String GAME;
	public static Integer STEAM_ID;
	public static File xmlDir;
	public static Map<String, String> APP_PARAMS = new HashMap<String, String>();
	
	private static MyXML settingsXML = new MyXML();
	private static String SETTINGS_FILE_XML = "settings.xml";
	
	public ModManager() {
		super();
	}
	
	public ModManager(boolean restart) throws Exception {
		start(null,restart);
	}
	
	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		start(primaryStage, false);
	}
	
	public void start(Stage primaryStage, boolean reload) throws Exception {
		
		if(!reload){
			File zip = new File(UPDATE_ZIP_NAME);
			zip.delete();
			
			File new_updater = new File("new_"+UPDATER_NAME);
			File old_updater = new File(UPDATER_NAME);
			new_updater.renameTo(old_updater);
			
			new OnlineVersionChecker();
		}
		
		settingsXML.readSettingFile(SETTINGS_FILE_XML);
		
		if(initApp()){
			settingsXML.modifyGameSettings(STEAM_ID, "docfolderpath", PATH);
			
			//Create a dir to save lists of the selected game
			xmlDir = new File(GAME);
			if(!xmlDir.exists())
				xmlDir.mkdir();
			
			try{
				new ListManager(PATH);
			}catch (FileNotFoundException e){
				ErrorPrint.printError(e);
				
				Text textError = new Text(e.getMessage());
				textError.setWrappingWidth(400);
				
				Alert alertError = new Alert(AlertType.ERROR);
				alertError.setTitle("Error Dialog");
				alertError.setHeaderText("Critical Error");
				alertError.getDialogPane().setContent(textError);
				
				alertError.showAndWait();
				
				start(primaryStage);
			}
		}
	}
	
	/**
	 * @param path
	 */
	public static void setPATH(String path) {
		PATH = path;
	}
	
	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}
	
	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}
	
	public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}
	
	/**
	 * @param game
	 * @return
	 */
	public String generatePath(String game){
		String sep = File.separator;
		if (isWindows()) {
			return System.getProperty("user.home")+sep+"Documents"+sep+"Paradox Interactive"+sep+game+sep;
		} else if (isMac()) {
			return System.getProperty("user.home")+sep+"Documents"+sep+"Paradox Interactive"+sep+game+sep;
		} else if (isUnix()) {
			return System.getProperty("user.home")+sep+".local"+sep+"share"+sep+"Paradox Interactive"+sep+game+sep;
		}
		return "";
	}
	
	private boolean initApp(){
		return initApp(null,null);
	}
	
	/**
	 * @param docUserPath
	 * @param exeUserPath
	 * @return
	 */
	private boolean initApp(String docUserPath, String exeUserPath){
		Dialog<List<String>> dialog = new Dialog<>();
		dialog.setTitle(APP_NAME);
		dialog.setHeaderText("Choose a game");
		
		// Set the button types.
		ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 10, 10, 10));
		
		//grid.setGridLinesVisible(true);
		
		ChoiceBox<String> choiceGame = new ChoiceBox<String>(SUPPORTED_GAMES);
		
		TextField docPath = new TextField();
		docPath.setMinWidth(500);
		DirectoryChooser dirDocChooser = new DirectoryChooser();
		Button openDirDocButton = new Button("...");
		
		TextField gamePath = new TextField();
		docPath.setMinWidth(500);
		//FileChooser exeGameChooser = new FileChooser();
		//Button openExeGameButton = new Button("...");
		
		choiceGame.getSelectionModel().selectedIndexProperty().addListener(
			new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					String newGame = SUPPORTED_GAMES.get((int) newValue);
					Integer newSteamID = GAMES_STEAM_ID.get((int) newValue);
					String docPathParam=null;
					try {
						//ModManager.APP_PARAMS = settingsXML.getGameSettings(newSteamID);
						docPathParam = settingsXML.getOneGameSetting(newSteamID,"docfolderpath");
					} catch (DataConversionException e) {
						ErrorPrint.printError(e, "Error reading the saved params for gameID : '"+newSteamID+"'");
						e.printStackTrace();
					}
					String newPath;
					//String docPathParam = ModManager.APP_PARAMS.get("docfolderpath");
					if(docPathParam!=null){
						newPath = docPathParam;
					} else{
						newPath = generatePath(newGame);
					}
					docPath.setText(newPath);
					gamePath.setText("Steam launch | steam://run/"+newSteamID);
					dirDocChooser.setTitle("Choose document path for "+newGame);
					File f = new File(newPath);
					if(f.isDirectory())
						dirDocChooser.setInitialDirectory(f);
					else
						dirDocChooser.setInitialDirectory(new File(File.separator));
					//exeGameChooser.setTitle("Choose exe path for "+newGame);
					//exeGameChooser.setInitialDirectory(new File(File.separator));
					//"steam://run/"+ModManager.STEAM_ID
				}
			}
		);
		
		openDirDocButton.setOnAction(
			new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent e) {
					File file = dirDocChooser.showDialog(dialog.getOwner());
					if (file!=null && file.isDirectory()){
						//System.out.println(file.toString());
						String newPath = file.getAbsolutePath()+File.separator;
						dirDocChooser.setInitialDirectory(new File(newPath));
						docPath.setText(newPath);
					}
				}
			}
		);
		/*
		openExeGameButton.setOnAction(
			new EventHandler<ActionEvent>() {
				@Override
				public void handle(final ActionEvent e) {
					File file = exeGameChooser.showOpenDialog(dialog.getOwner());
					if (file!=null && !file.isDirectory()){
						//System.out.println(file.toString());
						String newPath = file.getAbsolutePath()+File.separator;
						exeGameChooser.setInitialDirectory(new File(newPath));
						gamePath.setText(newPath);
					}
				}
			}
		);
		*/
		
		Label lbl = new Label("--- Change if different from default ---");
		lbl.setMaxWidth(Double.MAX_VALUE);
		lbl.setAlignment(Pos.CENTER);
		
		//Choice Game
		grid.add(new Label("Game :"), 0, 0);
		grid.add(choiceGame, 1, 0);
		
		//Detect conflicts option
		CheckBox cbConflict = new CheckBox("Active conflict detection (longer loading)");
		//cbConflict.setIndeterminate(false);
		
		if(APP_PARAMS.containsKey("DetectConflict")){
			if(APP_PARAMS.get("DetectConflict").equals("true"))
				cbConflict.setSelected(true);
		}
		
		grid.add(cbConflict, 0, 1, 2, 1);
		
		//Label game config
		grid.add(lbl, 0, 2, 3, 1);
		
		//Game configs
		grid.add(new Label("Doc path :"), 0, 3);
		grid.add(docPath, 1, 3);
		grid.add(openDirDocButton, 2, 3);
		/*
		grid.add(new Label("Game (exe) path :"), 0, 4);
		grid.add(gamePath, 1, 4);
		grid.add(openExeGameButton, 2, 4);
		*/

		dialog.getDialogPane().setContent(grid);
		
		choiceGame.getSelectionModel().selectFirst();
		if(docUserPath!=null){
			//System.out.println(userPath);
			docPath.setText(docUserPath);
			File f = new File(docUserPath);
			if(f.isDirectory())
				dirDocChooser.setInitialDirectory(f);
			else
				dirDocChooser.setInitialDirectory(new File(File.separator));
		}
		Platform.runLater(() -> choiceGame.requestFocus());
		
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == okButtonType) {
				return Arrays.asList(choiceGame.getSelectionModel().getSelectedItem(), docPath.getText(), gamePath.getText(),""+cbConflict.isSelected());
			}
			return null;
		});
		
		Optional<List<String>> result = dialog.showAndWait();
		
		if(result.isPresent()){
			List<String> result_list = result.get();
			GAME = result_list.get(0);
			STEAM_ID = GAMES_STEAM_ID.get(SUPPORTED_GAMES.indexOf(GAME));
			
			String docPathStr = result_list.get(1);
			String exePathStr = result_list.get(2);
			String detectConflict = result_list.get(3);
			APP_PARAMS.put("DetectConflict", detectConflict);
			
			//In case the user write wrong separator
			docPathStr = docPathStr.replaceAll("(\\\\+|/+)", Matcher.quoteReplacement(File.separator));
			if(!(docPathStr.lastIndexOf(File.separator)==docPathStr.length()-1)) docPathStr+=File.separator;
			
			if(checkPath(docPathStr)){
				PATH = docPathStr;
				return true;
			}else{
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Dialog");
				alert.setHeaderText(null);
				alert.setContentText("The document path is not correct !");
				alert.showAndWait();
				
				return initApp(docPathStr,exePathStr);
			}
		}
		return false;
	}
	
	/**
	 * @param path
	 * @return
	 */
	public boolean checkPath(String path){
		File docFolder = new File(path);
		
		if(docFolder.exists()){
			if(docFolder.isDirectory()){
				String[] docList = docFolder.list();
				for (String file : docList) {
					if(file.equals("settings.txt")) return true;
				}
			}
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		//Clean debug log file
		File debugFile = new File("DebugLog.txt");
		if(debugFile.exists())
			debugFile.delete();
		
		Application.launch(args);
	}
}