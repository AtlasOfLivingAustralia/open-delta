!include .\DeltaEditorMain.nsi

OutFile "..\target\Open-DELTA-${VERSION}-Installer.exe"

Function InstallAddFiles
    SetOutPath "$INSTDIR"
    ; Put file there
    File "/oname=${EXEOUTPUTNAME}" "..\delta-editor\target\${EXENAME}" 
    
    ; Include the JRE
    File /r "$%JAVA_HOME%\jre"
  
    ; Output sample dlt into sample subdirectory
    SetOutPath "$INSTDIR\sample"
    File "..\delta-editor\sampledata\sample.dlt"
    
    ; Output JAR files to lib subdirectory
    SetOutPath "$INSTDIR\lib"
    File "..\delta-editor\target\${JARNAME}"
FunctionEnd

Function un.UninstallRemoveFiles
    Delete /REBOOTOK "$INSTDIR\*.exe"
    Delete /REBOOTOK "$INSTDIR\lib\*.jar"
    RmDir /REBOOTOK "$INSTDIR\lib"
    
    ; Remove the JRE
    RmDir /r /REBOOTOK "$INSTDIR\jre"
    
    Delete /REBOOTOK "$INSTDIR\sample\sample.dlt"
    RmDir /REBOOTOK "$INSTDIR\sample"
    
    FlushINI "$INSTDIR\uninstall.ini"
    Delete /REBOOTOK "$INSTDIR\uninstall.ini"
FunctionEnd