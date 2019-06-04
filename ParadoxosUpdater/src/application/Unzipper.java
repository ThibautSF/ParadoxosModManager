/**
 * 
 */
package application;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author mkyong (Founder of Mkyong.com), SIMON-FINE Thibaut (alias Bisougai)
 * Updated src : https://stackoverflow.com/a/10634536
 */
public class Unzipper {
	private static final int BUFFER_SIZE = 4096;
	
	/**
	 * Extract zipfile to outdir with complete directory structure
	 * @param zipFile input zip file string
	 * @param output zip file output folder string
	 */
	public static void extract(String zipFile, String outputFolder) {
		extract(new File(zipFile), new File(outputFolder));
	}
	
	/***
	 * Extract zipfile to outdir with complete directory structure
	 * @param zipfile Input .zip file
	 * @param outdir Output directory
	 */
	public static void extract(File zipfile, File outdir) {
		try {
			ZipInputStream zin = new ZipInputStream(new FileInputStream(zipfile));
			ZipEntry entry;
			String name, dir;
			while ((entry = zin.getNextEntry()) != null) {
				name = entry.getName();
				if(entry.isDirectory()) {
					mkdirs(outdir,name);
					continue;
				}
				if (name.equals(Main.UPDATER_NAME)) {
					name = "New_"+name;
				}
				/* this part is necessary because file entry can come before
				 * directory entry where is file located
				 * i.e.:
				 *   /foo/foo.txt
				 *   /foo/
				 */
				dir = dirpart(name);
				if(dir != null)
					mkdirs(outdir,dir);
				
				extractFile(zin, outdir, name);
			}
			zin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void extractFile(ZipInputStream in, File outdir, String name) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outdir,name)));
		int count = -1;
		while ((count = in.read(buffer)) != -1)
			out.write(buffer, 0, count);
		out.close();
	}
	
	private static void mkdirs(File outdir,String path) {
		File d = new File(outdir, path);
		if(!d.exists())
			d.mkdirs();
	}
	
	private static String dirpart(String name) {
		int s = name.lastIndexOf(File.separatorChar);
		return s == -1 ? null : name.substring(0, s);
	}
}
