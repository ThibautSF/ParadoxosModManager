package versioning;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

import application.ModManager;
import debug.BasicDialog;
import debug.ErrorPrint;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

/**
 * An online checker implementation
 * 
 * @author SIMON-FINE Thibaut (alias Bisougai), and GROSJEAN Nicolas (alias Mouchi)
 *
 */
public class OnlineVersionChecker {
	private static String URL_APP_RELEASES = "https://github.com/ThibautSF/ParadoxosModManager/releases";
	private static String URL_APP_INFO_TXT = "https://raw.githubusercontent.com/ThibautSF/ParadoxosModManager/master/AppInfo.txt";
	//TESTS (Will be deleted for 0.6 release !) → see https://gist.github.com/ThibautSF/6e74782a97c4dab63a0cbb01a3a4b7c1/revisions TODO
	//Logs for 0.5.2 and 0.5.1
	//private static String URL_APP_INFO_TXT = "https://gist.githubusercontent.com/ThibautSF/6e74782a97c4dab63a0cbb01a3a4b7c1/raw/a8492d7b8c7450004438d639ea7159e9b077a2e9/AppInfo.test.txt";
	//Logs for 0.5.1 only (0.5.2 is empty)
	//private static String URL_APP_INFO_TXT = "https://gist.githubusercontent.com/ThibautSF/6e74782a97c4dab63a0cbb01a3a4b7c1/raw/e31bb161e064b3bca7d7e1198ed4cf11941e3f0f/AppInfo.test.txt";
	//Logs for 0.5.2 only (0.5.1 is empty)
	//private static String URL_APP_INFO_TXT = "https://gist.githubusercontent.com/ThibautSF/6e74782a97c4dab63a0cbb01a3a4b7c1/raw/a40c33cd6ebcda282fbc48ba730db5375b0deb81/AppInfo.test.txt";
	
	private static String VERSION = "0.5.2";
	
	private String lastestOnlineVersionNumber;

	public OnlineVersionChecker(){
		String changelogOrNothing = newVersionOnline();
		if (changelogOrNothing.length() > 0)
			showUpdateWindow(changelogOrNothing);
	}
	
	private String newVersionOnline(){
		StringBuilder changelog = new StringBuilder();
		boolean updateExist = true;
		boolean versionChangelog = false;
		boolean firstRead = true;
		
		String[] aLocalV = VERSION.split("\\.");
		try{
			URL appInfoTxt = new URL(URL_APP_INFO_TXT);
			BufferedReader in = new BufferedReader(new InputStreamReader(appInfoTxt.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null){
				if (inputLine.contains("AppVersion=")){
					String onlineVersion = inputLine.substring(inputLine.indexOf("=") + 1, inputLine.length());
					String[] aOnlineV = onlineVersion.split("\\.");
					
					if (firstRead){
						lastestOnlineVersionNumber = onlineVersion;
					}
					
					updateExist = checkIsNewerVersion(aLocalV, aOnlineV);
					
					if(!updateExist){
						//We reach an inferior or equal app version number → End
						if(!versionChangelog && changelog.length()>0)
							changelog.append("No changelog available");
						return changelog.toString();
					} else {						
						if(!firstRead){
							if(!versionChangelog)
								changelog.append("No changelog available\n");
							
							changelog.append("\n");
						}
						
						changelog.append(onlineVersion + ":\n");
						versionChangelog = false;
					}
					
					firstRead = false;
				} else {
					if (inputLine.length() > 0) {
						versionChangelog = true;
						changelog.append(inputLine + "\n");
					}
				}
			}
			in.close();
		} catch (Exception e) {
			ErrorPrint.printError(e, "Check Online Version");
			
			BasicDialog.showGenericDialog("Version checking error", "Unable to check online version", AlertType.ERROR);
		}
		
		//We reach end of file (case final line was an AppVersion)
		if(!versionChangelog && changelog.length()>0)
			changelog.append("No changelog available");
		
		return changelog.toString();
	}
	
	/**
	 * Compare local version to online.
	 * Both arrays must have the same number of values (raise IllegalArguementException if local.length!=online.length)
	 * 
	 * @param local an array of string which contains integer and represent the local version of the app (ex; for "1.0" input should be ["1","0"])
	 * @param online an array of string which contains integer and represent the online version of the app
	 * @return
	 */
	private boolean checkIsNewerVersion(String[] local, String[] online){
		if(local.length!=online.length)
			throw new IllegalArgumentException("The local and online array must have the same length");
		
		int i = 0;
		while (i<local.length) {
			if(Integer.parseInt(online[i]) > Integer.parseInt(local[i])) {
				return true;
			} else if(Integer.parseInt(online[i]) == Integer.parseInt(local[i])) {
				i++;
			} else {
				break;
			}
		}
		
		return false;
	}
	
	/**
	 * Get the download GitHub URL.
	 * EX : https://github.com/NicolasGrosjean/Translate_helper/releases/download/v2.1/TranslateHelper_v2-1.rar
	 * 
	 * @return string of the url to download the last version of the software
	 */
	private String getGithHubDownloadUrl(){
		//Paradoxos Example : https://github.com/ThibautSF/ParadoxosModManager/releases/download/0.5.2/ParadoxosModManager0.5.2.zip
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("https://github.com/ThibautSF/ParadoxosModManager/releases/download/");
		builder.append(lastestOnlineVersionNumber);
		builder.append("/ParadoxosModManager");
		builder.append(lastestOnlineVersionNumber);
		builder.append(".zip");
		return builder.toString();
	}
	
	/**
	 * Get the download GitHub release tag page URL.
	 * EX : https://github.com/ThibautSF/ParadoxosModManager/releases/tag/0.5.2
	 * 
	 * @return string of the url to see infos about the last version of the software (on GitHub)
	 */
	private String getGithHubReleaseUrl(){	
		StringBuilder builder = new StringBuilder();
		
		builder.append("https://github.com/ThibautSF/ParadoxosModManager/releases/tag/");
		builder.append(lastestOnlineVersionNumber);
		return builder.toString();
	}
	
	/**
	 * Generate a javafx alert and confirmation window to inform about a new version (and ask what he want to do)
	 * 
	 * @param changelog the content of the scrollable textarea
	 */
	private void showUpdateWindow(String changelog){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(ModManager.APP_NAME);
		alert.setHeaderText("A new version is available !");
		
		Text contentText = new Text(String.format("A new version of %s is available online !\nLocal : %s\nOnline : %s\n", ModManager.APP_NAME, VERSION, lastestOnlineVersionNumber));

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.autosize();
		expContent.add(contentText, 0, 0);
		
		if(changelog.length() > 0 && !changelog.equals("No changelog available")){
			Label label = new Label("CHANGELOG:");
	
			TextArea textArea = new TextArea(changelog);
			textArea.setEditable(false);
			textArea.setWrapText(true);
	
			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);
			expContent.add(label, 0, 1);
			expContent.add(textArea, 0, 2);
		}
		
		alert.getDialogPane().setContent(expContent);
		
		ButtonType buttonWebAll = new ButtonType("All versions\n(with source)");
		//ButtonType buttonWebRelease = new ButtonType("See "+lastestOnlineVersionNumber+"\n(with source)");
		ButtonType buttonWebDownload = new ButtonType("Get Update\n(zip archive)");
		ButtonType buttonCancel = new ButtonType("Continue\n(Stay "+VERSION+")", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonWebAll, buttonWebDownload, buttonCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonWebAll){
			goURL(URL_APP_RELEASES);
		//} else if (result.get() == buttonWebRelease) {
		//	goURL(getGithHubReleaseUrl());
		} else if (result.get() == buttonWebDownload) {
			goURL(getGithHubDownloadUrl());
		}
	}
	
	/** 
	 * Open the web browser with the target url
	 * If not available copy url to clipboard
	 * 
	 * @param url
	 */
	private void goURL(String url){
		if(Desktop.isDesktopSupported()){
			new Thread(() -> {
				try {
					URI uri = new URI(url);
					Desktop.getDesktop().browse(uri);
					System.exit(0);
				} catch (IOException | URISyntaxException e) {
					ErrorPrint.printError(e,"Open URL ( "+url+" )");
					e.printStackTrace();
				}
			}).start();
		} else {
			StringSelection selection = new StringSelection(getGithHubDownloadUrl());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);
			
			BasicDialog.showGenericDialog("Unable to open Web Browser", "Url was copied in your clipboard.", AlertType.ERROR);
		}
	}
}
