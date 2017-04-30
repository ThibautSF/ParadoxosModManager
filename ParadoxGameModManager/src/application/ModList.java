package application;

import java.util.ArrayList;

/**
 * @author SIMON-FINE Thibaut (alias Bisougai)
 *
 */
public class ModList {
	//
	//Fields and Constructors
	//
	private String name;
	private String description;
	private Languages language;
	private ArrayList<Mod> modlist;

	/**
	 * @param name
	 * @param description
	 * @param modlist
	 */
	public ModList(String name, String description, Languages language, ArrayList<Mod> modlist) {
		this.name=name;
		this.description=description;
		this.language=language;
		this.modlist=modlist;
	}
	
	//
	// Getters and Setters
	//
	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description
	 */
	public void setDescription(String description){
		this.description=description;
	}
	
	public Languages getLanguage() {
		return this.language;
	}
	
	public String getLanguageName() {
		return this.language.getName();
	}
	
	public String getLanguageCode() {
		return this.language.getCode();
	}

	public void setLanguage(Languages language) {
		this.language = language;
	}

	/**
	 * @return
	 */
	public ArrayList<Mod> getModlist() {
		return modlist;
	}
	
	/**
	 * @param modList
	 */
	public void setModlist(ArrayList<Mod> modList) {
		this.modlist=modList;
	}
	//
	//Methods
	//
	/**
	 * @param mod
	 * @return
	 */
	public int isModInList(Mod mod){
		for (int i = 0; i < modlist.size(); i++) {
			Mod one_mod = modlist.get(i);
			if(one_mod.equals(mod))
				return i;
		}
		return 0;
	}
	
	/**
	 * @param mod
	 * @return
	 */
	public boolean addMod(Mod mod){
		if(isModInList(mod)==0){
			modlist.add(mod);
			return true;
		}
		return false;
	}
	
	/**
	 * @param mods
	 */
	public void addAllMod(ArrayList<Mod> mods){
		for (Mod one_mod : mods) {
			this.addMod(one_mod);
		}
	}
	
	/**
	 * @param mod
	 * @return
	 */
	public boolean removeMod(Mod mod){
		int index = isModInList(mod);
		if(index!=0){
			modlist.remove(index);
		}
		return false;
	}
}
