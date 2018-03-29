package tests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import mod.Languages;
import mod.Mod;
import mod.ModConflict;
import mod.ModList;

/**
 * Test the ModList class
 * 
 * @author GROSJEAN Nicolas (alias Mouchi)
 *
 */
public class TestModList {
	private ModList modList;
	private Mod mod1;
	private Mod mod2;
	private Mod mod3;

	private static String GFX_FILE_1 = "gfx\\interface\\1.dds";
	private static String GFX_FILE_2 = "gfx\\interface\\2.dds";
	private static String INTERFACE_FILE = "interface\\1.dds";

	@Before
	public void setUp() {
		modList = new ModList("", "", Languages.ENGLISH, new ArrayList<>());

		MockitoAnnotations.initMocks(this);

		mod1 = Mockito.mock(Mod.class);
		Mockito.when(mod1.getName()).thenReturn("mod1");
		Mockito.when(mod1.getModifiedFiles()).thenReturn(getModifiedFiles(true, true));

		mod2 = Mockito.mock(Mod.class);
		Mockito.when(mod2.getName()).thenReturn("mod2");
		Mockito.when(mod2.getModifiedFiles()).thenReturn(getModifiedFiles(true, false));

		MockitoAnnotations.initMocks(this);
		mod3 = Mockito.mock(Mod.class);
		Mockito.when(mod3.getName()).thenReturn("mod3");
		Mockito.when(mod3.getModifiedFiles()).thenReturn(getModifiedFiles(false, true));
	}

	@Test
	public void computeConflictsTest() {
		modList.setModlist(create3ModList());

		List<ModConflict> conflicts = modList.getModConflicts();
		Assert.assertEquals(2, conflicts.size());
		assertConflictBetweenMod1AndMod2(conflicts.get(0));
		assertConflictBetweenMod1AndMod3(conflicts.get(1));
	}

	@Test
	public void addConflictsTest() {
		List<Mod> mods = new ArrayList<>(1);
		mods.add(mod1);
		modList.setModlist(mods);

		modList.addMod(mod2);
		List<ModConflict> conflicts = modList.getModConflicts();
		Assert.assertEquals(1, conflicts.size());
		assertConflictBetweenMod1AndMod2(conflicts.get(0));

		modList.addMod(mod3);
		conflicts = modList.getModConflicts();
		Assert.assertEquals(2, conflicts.size());
		assertConflictBetweenMod1AndMod2(conflicts.get(0));
		assertConflictBetweenMod1AndMod3(conflicts.get(1));
	}

	@Test
	public void removeConflicts1Test() {
		modList.setModlist(create3ModList());

		modList.removeMod(mod1);
		Assert.assertEquals(0, modList.getModConflicts().size());
	}

	@Test
	public void removeConflicts2Test() {
		modList.setModlist(create3ModList());

		modList.removeMod(mod2);
		List<ModConflict> conflicts = modList.getModConflicts();
		Assert.assertEquals(1, conflicts.size());
		assertConflictBetweenMod1AndMod3(conflicts.get(0));
	}

	private List<Mod> create3ModList() {
		List<Mod> mods = new ArrayList<>(3);
		mods.add(mod1);
		mods.add(mod2);
		mods.add(mod3);
		return mods;
	}

	private void assertConflictBetweenMod1AndMod2(ModConflict conflict) {
		Assert.assertEquals(mod1, conflict.getMod1());
		Assert.assertEquals(mod2, conflict.getMod2());
		List<String> conflictDetail = conflict.getConflictFiles();
		Assert.assertEquals(2, conflictDetail.size());
		Assert.assertTrue(conflictDetail.contains(GFX_FILE_1));
		Assert.assertTrue(conflictDetail.contains(GFX_FILE_2));
	}

	private void assertConflictBetweenMod1AndMod3(ModConflict conflict) {
		Assert.assertEquals(mod1, conflict.getMod1());
		Assert.assertEquals(mod3, conflict.getMod2());
		List<String> conflictDetail = conflict.getConflictFiles();
		Assert.assertEquals(1, conflictDetail.size());
		Assert.assertTrue(conflictDetail.contains(INTERFACE_FILE));
	}

	private Set<String> getModifiedFiles(boolean gfxFile, boolean interfaceFile) {
		Set<String> res = new HashSet<>();
		if (gfxFile) {
			res.add(GFX_FILE_1);
			res.add(GFX_FILE_2);
		}
		if (interfaceFile) {
			res.add(INTERFACE_FILE);
		}
		return res;
	}
}
