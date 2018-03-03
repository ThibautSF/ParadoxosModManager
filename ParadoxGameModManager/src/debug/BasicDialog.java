/**
 * 
 */
package debug;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * @author SIMON-FINE Thibaut (alias Bisougai)
 * 
 * Source : http://code.makery.ch/blog/javafx-dialogs-official/
 *
 */
public class BasicDialog {
	
	/**
	 * @param header
	 * @param message
	 */
	public static void showGenericDialog(String header, String message, AlertType alert_type){
		showGenericDialog("Error", header, message, alert_type);
	}

	/**
	 * @param title
	 * @param header
	 * @param message
	 */
	public static void showGenericDialog(String title, String header, String message, AlertType alert_type){
		Alert alert = new Alert(alert_type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(message);

		alert.showAndWait();
	}
}
