/**
 * 
 */
package application;

import java.io.File;

/**
 * @author Thibaut SIMON-FINE
 *
 */
public class Main {
	private static String UPDATE_ZIP_NAME = "update.zip";
	private static String EXTRACT_DIR = ".";
	public static String APP_NAME = "ParadoxosModManager.jar";
	public static String UPDATER_NAME = "Updater.jar";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Unzipper.unZipIt(UPDATE_ZIP_NAME, EXTRACT_DIR);
		
		File zip = new File(UPDATE_ZIP_NAME);
		zip.delete();
		
		String[] run = {"java","-jar","ParadoxosModManager.jar"};
		try {
			Runtime.getRuntime().exec(run);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
