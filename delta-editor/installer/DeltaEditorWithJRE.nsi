!include .\DeltaEditorMain.nsi

OutFile "..\target\OpenDelta-${VERSION}-Installer.exe"

Function InstallAddFiles
    SetOutPath $INSTDIR
    ; Put file there
    File "..\target\${EXENAME}"
    
    ; Include the JRE
    File /r "$%JAVA_HOME%\jre"
  
    ; Output sample dlt into sample subdirectory
    SetOutPath $INSTDIR\sample
    File "..\sampledata\sample.dlt"
    
    ; Output JAR files to lib subdirectory
    SetOutPath $INSTDIR\lib
    File "..\target\${JARNAME}"
FunctionEnd

Function un.UninstallRemoveFiles
    Delete /REBOOTOK $INSTDIR\*.exe
    Delete /REBOOTOK $INSTDIR\lib\*.jar
    RmDir /REBOOTOK $INSTDIR\lib
    
    ; Remove the JRE
    RmDir /r /REBOOTOK $INSTDIR\jre
    
    Delete /REBOOTOK $INSTDIR\sample\sample.dlt
    RmDir /REBOOTOK $INSTDIR\sample
FunctionEnd