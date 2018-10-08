set _CURRENT_FILE_DIR=%~dp0
set _CURRENT_FILE_DIR=%_CURRENT_FILE_DIR:~0,-1%

cd /D %_CURRENT_FILE_DIR%
java -jar %_CURRENT_FILE_DIR%\ParadoxosModManager.jar