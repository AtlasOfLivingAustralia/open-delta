!include "MUI2.nsh"

!define /date DATE "%Y%m%d"

; The name of the installer
Name "OpenDelta Suite"

; The file to write
OutFile "..\target\OpenDelta-${VERSION}-Installer.exe"

; The default installation directory
InstallDir $PROGRAMFILES\OpenDelta

;Get installation folder from registry if available
InstallDirRegKey HKLM "Software\OpenDelta" ""

; Request application privileges for Windows Vista
RequestExecutionLevel admin

;--------------------------------

; Pages

	!insertmacro MUI_DEFAULT MUI_ICON ".\resources\installer.ico"
	!insertmacro MUI_DEFAULT MUI_UNICON ".\resources\installer.ico"
	
	!define MUI_HEADERIMAGE
  	!define MUI_HEADERIMAGE_BITMAP ".\resources\InstallerHeaderImage.bmp"
	!define MUI_ABORTWARNING

	!insertmacro MUI_PAGE_LICENSE "..\src\main\resources\au\org\ala\delta\resources\MPL-1.1.txt"
  	!insertmacro MUI_PAGE_DIRECTORY
  	!insertmacro MUI_PAGE_INSTFILES
  	
  	!insertmacro MUI_UNPAGE_CONFIRM
  	!insertmacro MUI_UNPAGE_INSTFILES
  	
  	!insertmacro MUI_LANGUAGE "English"

;--------------------------------

; The stuff to install
Section "" ;No components page, name is not important

  ; Set output path to the installation directory.
  SetOutPath $INSTDIR
  
  ; Put file there
  File "..\target\DeltaEditor.exe"
  File "..\target\delta-editor-${VERSION}-jar-with-dependencies.jar"
  File /r "$%JAVA_HOME%\jre"
  
  createDirectory "$SMPROGRAMS\OpenDelta Suite"
  createShortCut "$SMPROGRAMS\OpenDelta Suite\DeltaEditor.lnk" "$INSTDIR\DeltaEditor.exe"
  createShortCut "$SMPROGRAMS\OpenDelta Suite\Uninstall.lnk" "$INSTDIR\Uninstall.exe"
  #createShortCut "$DESKTOP\DeltaEditor.lnk" "$INSTDIR\DeltaEditor.exe"
  
  WriteUninstaller $INSTDIR\Uninstall.exe
  
  ;store uninstallation data in registry
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\OpenDelta" "DisplayName" "OpenDelta Suite"	
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\OpenDelta" "UninstallString" "$INSTDIR\Uninstall.exe"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\OpenDelta" "DisplayIcon" "$INSTDIR\Uninstall.exe"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\OpenDelta" "Version" "${VERSION}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\OpenDelta" "DisplayVersion" "${VERSION}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\OpenDelta" "URLInfoAbout" "http://code.google.com/p/open-delta/"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\OpenDelta" "InstallDate" "${DATE}"
  
  ;Store installation folder
  WriteRegStr HKLM "Software\OpenDelta" "" $INSTDIR
  
SectionEnd ; end the section

Section "Uninstall"
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\OpenDelta"
  RMDir /r $INSTDIR
  RMDir /r "$SMPROGRAMS\OpenDelta Suite"
  
  DeleteRegKey /ifempty HKLM "Software\OpenDelta"
SectionEnd



