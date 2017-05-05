package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.SimpleStringProperty;

/**
 * @author SIMON-FINE Thibaut (alias Bisougai)
 *
 */
public class Mod {
	private SimpleStringProperty fileName;
	private SimpleStringProperty name;
	private SimpleStringProperty versionCompatible;
	private SimpleStringProperty remoteFileID;
	private SimpleStringProperty steamPath;
	private boolean missing;
	
	/**
	 * @param filename
	 */
	public Mod(String filename){
		this(filename,null);
	}
	
	/**
	 * @param filename
	 * @param remoteFileID
	 */
	public Mod(String filename, String remoteFileID) {
		try{
			Integer.parseInt(filename);
			this.fileName = new SimpleStringProperty("ugc_"+filename+".mod");
		} catch(Exception e) {
			this.fileName = new SimpleStringProperty(filename);
		}
		
		if(remoteFileID!=null){
			this.remoteFileID = new SimpleStringProperty(remoteFileID);
			this.steamPath = new SimpleStringProperty("https://steamcommunity.com/sharedfiles/filedetails/?id="+this.remoteFileID.get());
		}else{
			this.remoteFileID = new SimpleStringProperty("");
			this.steamPath = new SimpleStringProperty("No remote ID found");
		}
		
		this.versionCompatible = new SimpleStringProperty("?");
		this.name = this.fileName;
		
		try {
			readFileMod();
			this.missing = false;
		} catch (IOException e) {
			this.missing = true;
			this.name = new SimpleStringProperty("MOD MISSING");
			this.versionCompatible = new SimpleStringProperty("");
			ErrorPrint.printError("Unable to open "+ModManager.PATH+"mod"+File.separator+filename+" ! File is missing or corrupted !");
			e.printStackTrace();
		}
	}
	
	/**
	 * @throws IOException
	 */
	private void readFileMod() throws IOException {
		String sep = File.separator;
		Pattern p = Pattern.compile("\\\".*?\\\"");
		Matcher m;
		
		Path pth = Paths.get(ModManager.PATH+"mod"+sep+fileName.get());
		List<String> lines = Files.readAllLines(pth);
		for (String line : lines) {
			if(line.contains("name=")) {
				m = p.matcher(line);
				if(m.find())
				    name = new SimpleStringProperty((String) m.group().subSequence(1, m.group().length()-1));
			}else if (line.contains("supported_version=")) {
				m = p.matcher(line);
				if(m.find())
				    versionCompatible = new SimpleStringProperty((String) m.group().subSequence(1, m.group().length()-1));
			}else if (line.contains("remote_file_id=")){
				m = p.matcher(line);
				if(m.find()){
				    remoteFileID = new SimpleStringProperty((String) m.group().subSequence(1, m.group().length()-1));
				    this.steamPath = new SimpleStringProperty("https://steamcommunity.com/sharedfiles/filedetails/?id="+this.remoteFileID.get());
				}
			}
		}
		
	}
	
	//
	// Getters and Setters
	//
	
	/**
	 * @return
	 */
	public String getFileName(){
		return fileName.get();
	}
	
	/**
	 * @return
	 */
	public String getName(){
		return name.get();
	}
	
	/**
	 * @return
	 */
	public String getVersionCompatible(){
		return versionCompatible.get();
	}
	
	/**
	 * @return
	 */
	public String getRemoteFileID(){
		return remoteFileID.get();
	}
	
	/**
	 * @return
	 */
	public String getSteamPath(){
		return steamPath.get();
	}
	
	//
	// Methods
	//
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mod mod = (Mod) obj;
		return (fileName.get().equals(mod.getFileName()));
	}
	
	/**
	 * @return
	 */
	public boolean isMissing(){
		return missing;
	}
}
