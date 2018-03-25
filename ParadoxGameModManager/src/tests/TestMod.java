package tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import application.ModManager;
import mod.Mod;

// DOC about Parameterized test : https://github.com/junit-team/junit4/wiki/parameterized-tests
@RunWith(Parameterized.class)
public class TestMod {

	@Parameters(name = "{1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { "FakeMod.mod", "Direct mod" }, { "699235856", "Zipped mod" } });
	}

	@Parameter(0)
	public String modName;

	@Parameter(1)
	public String modType;
	
	@BeforeClass
	public static void setUp()
	{
		ModManager.PATH = System.getProperty("user.dir") + "/testRessources/";
	}

	@Test
	public void modifiedFilestest() {
		Mod mod = new Mod(modName);
		Set<String> modifiedFiles = mod.getModifiedFiles();
		Assert.assertEquals(7, modifiedFiles.size());
		Assert.assertTrue(modifiedFiles.contains("gfx\\interface\\player_counters_toggle.dds"));
		Assert.assertTrue(modifiedFiles.contains("gfx\\interface\\radar_toggle.dds"));
		Assert.assertTrue(modifiedFiles.contains("gfx\\texticons\\air_experience_20x20.dds"));
		Assert.assertTrue(modifiedFiles.contains("gfx\\texticons\\army_experience_20x20.dds"));
		Assert.assertTrue(modifiedFiles.contains("gfx\\texticons\\navy_experience_20x20.dds"));
		Assert.assertTrue(modifiedFiles.contains("localisation\\additional.yml"));
		Assert.assertTrue(modifiedFiles.contains("localisation\\additional_l_french.yml"));
	}
}
