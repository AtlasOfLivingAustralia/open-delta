; example1.nsi
;
; This script is perhaps one of the simplest NSIs you can make. All of the
; optional settings are left to their default settings. The installer simply 
; prompts the user asking them where to install, and drops a copy of example1.nsi
; there. 

;--------------------------------


; The name of the installer
Name "Open Delta Suite"

; The file to write
OutFile "..\target\DeltaInstaller.exe"

; The default installation directory
InstallDir $DESKTOP\DELTA

; Request application privileges for Windows Vista
RequestExecutionLevel user

LicenseBkColor FFFFFF
LicenseData "..\src\main\resources\au\org\ala\delta\resources\MPL-1.1.txt"
LicenseForceSelection radiobuttons

;--------------------------------

; Pages

Page license
Page directory
Page instfiles

;--------------------------------

; The stuff to install
Section "" ;No components page, name is not important

  ; Set output path to the installation directory.
  SetOutPath $INSTDIR
  
  ; Put file there
  File "..\target\DeltaEditor.exe"
  File /r "$%JAVA_HOME%\jre"
  
  WriteUninstaller $INSTDIR\uninstaller.exe
  
SectionEnd ; end the section

Section "Uninstall"
  Delete $INSTDIR\uninstaller.exe ; delete self (see explanation below why this works)
  Delete $INSTDIR\DeltaEditor.exe
  RMDir /r $INSTDIR\jre
  RMDir $INSTDIR
SectionEnd



