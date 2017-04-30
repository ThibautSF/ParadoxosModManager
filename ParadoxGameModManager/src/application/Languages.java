package application;

public enum Languages {
	ENGLISH ("english"),
	FRENCH ("french"),
	GERMAN ("german"),
	SPANISH ("spanish");
	
	private String name;
	

	private Languages(String name) {
		this.name = name;
	}
	
	public String getCode() {
		return "l_" + name;
	}

	public String getName() {
		return name;
	}
	
	public static Languages getLanguage(String language) {
		for (Languages l : values()) {
			if (l.getName() == language) {
				return l;
			}
		}
		return ENGLISH;
	}
}
