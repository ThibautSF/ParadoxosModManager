# README IMPORTANT
## FAQ : "Mod order don't apply" and also why it takes time

With the new launcher released by Paradox for their games, some changements were mades :
- better UI
- mods not ordered alphabetically by default
- allow user to order all their mods

But when launcher were released, I didn't have the time to work a lot to update PMM, so I updated PMM based on code previously made for Imperator Rome launcher.
So PMM is able to apply modlist and custom order, it is functional.
PMM isn't able (for now, improvement is planed [#52](https://github.com/ThibautSF/ParadoxosModManager/issues/52#issue-506377801)) to update the visual order in the launcher when you apply a list, but it still working. So don't add/remove active mod or change order of mods with the game launcher, just launch the game and the order from PMM should be keeped.

I am (slowly dues to school projects and futur exams) on a rework with a more recent version of Java (and also with java packaged, so no installation needed) in the following new repo https://github.com/ThibautSF/ParadoxosModManagerRework

# Paradoxos Mod Manager
Paradoxos Mod Manager is a java application which can be used to manage your mods in recent Paradox Interactive’s games

I developed this app because i often have lots of mods and games with different lists of mods active on Stellaris, and enabling/disabling a bunch of mods for different savegames before i could launch my game was very boring…

## Game supported
* Crusader Kings II
* Europa Universalis IV
* Stellaris
* Hearts of Iron 4
* Imperator Rome

## Requirements
* OS : Windows, Linux, MacOS
* Java 8u40 (at least)
* Supported game(s) and mods installed (*not really necessary but without these, this tool won't really be useful*)

## Useful links
### Download
* Google Drive : https://drive.google.com/open?id=0B2162Wd9vePmRXdieVc2QzdraFU
* Github release : https://github.com/ThibautSF/ParadoxosModManager/releases

### Documentation
The complete documentation is available on this [Google Doc (https://drive.google.com/open?id=1wThmbZIEGWzDO3rp8-zzJumebXDBE4-q6L6GnzVKmAY)](https://drive.google.com/open?id=1wThmbZIEGWzDO3rp8-zzJumebXDBE4-q6L6GnzVKmAY), the last version (when the app zip was released) of this documentation in pdf is always included in each app archive (for offline read).

### Version Log
On this [Google Doc (https://drive.google.com/open?id=1DFCgmSFUUZ2IRY-ON1bOVZki9LPd-FSTHacR7i2ibUA)](https://drive.google.com/open?id=1DFCgmSFUUZ2IRY-ON1bOVZki9LPd-FSTHacR7i2ibUA)

## How to use
* Download and extract
* Run 'ParadoxosModManager.jar' if you have set your PATH, or use 'LaunchWindows.cmd' on windows, or 'LaunchUnix.sh' on UNIX
* Follow the [Documentation](https://drive.google.com/open?id=1wThmbZIEGWzDO3rp8-zzJumebXDBE4-q6L6GnzVKmAY) part III for detailled procedure (a pdf version is in the archive)
---2.4 instructions---
* Run the manager and apply a mod list
* Launch game via manager and don't change anything in the paradox launcher
mods should load according to the order in the manager, as long as you don't change anything in the paradox launcher


## Additional information
### Team
* [SIMON-FINE Thibaut (alias Bisougai)](https://github.com/ThibautSF) : Author
* [GROSJEAN Nicolas (alias Mouchi)](https://github.com/NicolasGrosjean) : Contributor

### Contact (suggestions, reports...)
**For a bug report : Add all information you can add (OS, java version, image(s), file “DebugLog.txt”...)**
* Use one of the presentation threads on the paradoxplaza forum
* Open an [issue thread](https://github.com/ThibautSF/ParadoxosModManager/issues)
