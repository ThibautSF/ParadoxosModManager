package application;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.output.*;

/**
 * @author SIMON-FINE Thibaut (alias Bisougai)
 *
 */
public class MyXML {
	private static Element root;
	private static org.jdom2.Document document;
	private static Element root_exported;
	private static org.jdom2.Document document_exported;
	private String file;
	   
	//public MyXML(){}
	
	/**
	 * @param file
	 * @throws Exception
	 */
	public void readFile(String file) throws Exception{
		SAXBuilder sxb = new SAXBuilder();
		File xml = new File(file);
		if(xml.exists()){
			document = sxb.build(xml);
			root = document.getRootElement();
		}
		else{
			root = new Element("userlists");
			document = new Document(root);
		}
		
		//Init for export lists
		root_exported = new Element("exportedlist");
		root_exported.setAttribute("gameID", ModManager.STEAM_ID.toString());
		document_exported = new Document(root_exported);
		
		this.file = file;
	}
	
	/**
	 * @throws Exception
	 */
	public void saveFile() throws Exception{
		XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
		sortie.output(document, new FileOutputStream(file));
	}
	
	
	/**
	 * @return
	 */
	public ArrayList<ModList> getSavedList(){
		ArrayList<ModList> userLists = new ArrayList<ModList>();
		List<Element> modLists = root.getChildren("list");
		Iterator<Element> i = modLists.iterator();
		while(i.hasNext()){
			ArrayList<Mod> listMods = new ArrayList<Mod>();
			
			Element oneListElement = (Element) i.next();
			String listName = oneListElement.getAttribute("name").getValue();
			String listDescr = oneListElement.getChild("descr").getText();
			List<Element> modsElements = oneListElement.getChildren("mod");
			for (Element modElement : modsElements) {
				List<Attribute> modElementAttr = modElement.getAttributes();
				String fileName="",remoteFileId=null;
				for (Attribute attribute : modElementAttr) {
					switch (attribute.getName()) {
					case "id":
					case "fileName":
						fileName = attribute.getValue();
						break;
					
					case "remoteID":
						remoteFileId = attribute.getValue();
						break;
					
					default:
						break;
					}
				}
					
				Mod oneMod = new Mod(fileName, remoteFileId);
				listMods.add(oneMod);
			}
			
			ModList oneList = new ModList(listName, listDescr, listMods);
			userLists.add(oneList);
		}
		return userLists;
	}
	
	/**
	 * @param listName
	 * @throws Exception
	 */
	public void removeList(String listName) throws Exception{
		List<Element> modLists = root.getChildren("list");
		Iterator<Element> iE = modLists.iterator();
		while(iE.hasNext()){
			Element oneListElement = (Element) iE.next();
			String listElementName = oneListElement.getAttribute("name").getValue();
			if(listElementName.equals(listName)){
				root.removeContent(oneListElement);
				break;
			}
		}
		this.saveFile();
	}
	
	/**
	 * @param list
	 * @throws Exception
	 */
	public void modifyList(ModList list) throws Exception { modifyList(list, null); }
			
	/**
	 * @param list
	 * @param listName
	 * @throws Exception
	 */
	public void modifyList(ModList list, String listName) throws Exception{
		Element oneListElement,listDescrElement,listModElement;
		ArrayList<Mod> listMods;
		if(listName!=null){
			List<Element> modLists = root.getChildren("list");
			Iterator<Element> i = modLists.iterator();
			while(i.hasNext()){
				oneListElement = (Element) i.next();
				String listElementName = oneListElement.getAttribute("name").getValue();
				if(listElementName.equals(listName)){
					oneListElement.setAttribute("name", list.getName());
					
					listDescrElement = oneListElement.getChild("descr");
					listDescrElement.setText(list.getDescription());
					
					oneListElement.removeChildren("mod");
					listMods = list.getModlist();
					for (Mod mod : listMods) {
						listModElement = new Element("mod");
						listModElement.setAttribute("modName", mod.getName());
						listModElement.setAttribute("fileName", mod.getFileName());
						listModElement.setAttribute("remoteID", mod.getRemoteFileID());
						oneListElement.addContent(listModElement);
					}
					break;
				}
			}
		}
		else{
			oneListElement = new Element("list");
			oneListElement.setAttribute("name", list.getName());
			root.addContent(oneListElement);
			
			listDescrElement = new Element("descr");
			listDescrElement.setText(list.getDescription());
			oneListElement.addContent(listDescrElement);
			
			listMods = list.getModlist();
			for (Mod mod : listMods) {
				listModElement = new Element("mod");
				listModElement.setAttribute("fileName", mod.getFileName());
				listModElement.setAttribute("remoteID", mod.getRemoteFileID());
				oneListElement.addContent(listModElement);
			}
		}
		this.saveFile();
	}
	
	/**
	 * @param listName
	 * @throws Exception
	 */
	public void exportList(String listName) throws Exception{
		List<Element> modLists = root.getChildren("list");
		Iterator<Element> iE_export = modLists.iterator();
		while(iE_export.hasNext()){
			Element oneListElement = (Element) iE_export.next();
			String listElementName = oneListElement.getAttribute("name").getValue();
			if(listElementName.equals(listName)){
				root_exported.addContent(oneListElement.detach());
				XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
				String exportFileName = "Export_"+ModManager.GAME+"_"+listName+".xml";
				sortie.output(document_exported, new FileOutputStream(ModManager.xmlDir+File.separator+exportFileName));
				break;
			}
		}
	}
	
	/**
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public String importList(String xml) throws Exception{
		SAXBuilder sxb = new SAXBuilder();
		Document importDocument = sxb.build(xml);
		Element importRoot = importDocument.getRootElement();
		if(importRoot.getAttribute("gameID").getValue().equals(ModManager.STEAM_ID.toString())){
			List<Element> modLists = importRoot.getChildren("list");
			Iterator<Element> i = modLists.iterator();
			while(i.hasNext()){
				ArrayList<Mod> listMods = new ArrayList<Mod>();
				
				Element oneListElement = (Element) i.next();
				String listName = oneListElement.getAttribute("name").getValue();
				String listDescr = oneListElement.getChild("descr").getText();
				List<Element> modsElements = oneListElement.getChildren("mod");
				for (Element modElement : modsElements) {
					List<Attribute> modElementAttr = modElement.getAttributes();
					String fileName="",remoteFileId=null;
					for (Attribute attribute : modElementAttr) {
						switch (attribute.getName()) {
						case "id":
						case "fileName":
							fileName = attribute.getValue();
							break;
						
						case "remoteID":
							remoteFileId = attribute.getValue();
							break;
						
						default:
							break;
						}
					}
						
					Mod oneMod = new Mod(fileName, remoteFileId);
					listMods.add(oneMod);
				}
				
				ModList oneList = new ModList("[Imported]"+listName, listDescr, listMods);
				modifyList(oneList);
			}
			return "Import done.";
		}
		return "Import procedure aborted, this list is not for the current game !";
	}
}