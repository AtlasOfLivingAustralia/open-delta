!include .\DeltaEditorMain.nsi

OutFile "..\target\OpenDelta-${VERSION}-Installer-NOJRE.exe"

Function InstallAddFiles
    SetOutPath $INSTDIR
    ; Put file there
    File "..\target\${EXENAME}"
  
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
    
    Delete /REBOOTOK $INSTDIR\sample\sample.dlt
    RmDir /REBOOTOK $INSTDIR\sample
FunctionEnd