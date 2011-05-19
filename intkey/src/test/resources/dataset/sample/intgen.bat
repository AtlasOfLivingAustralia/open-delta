@echo off
echo.
echo Generates and zips self-extracting file for distribution of Intkey package.
echo Confor toint and Confor tonatsr must be run first.
echo Requires the programs Pkzip and Winzipse.
echo.
echo This file is included as an example only. It will need to be
echo modified to suit your requirements.
echo.
goto end
rem Remove the above statement to allow the rest of the file to run.
echo Press CTRL/C to abort . . .
pause

ted /line /ex="i'sver'j(s;7.;=)'q,q"
if errorlevel 1 goto wrongos

rem Include the following two lines, without the "rem ", in autoexec.bat.
rem set space=123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
rem set spacx=123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890

set space=
set spacx=

rem The name of the dataset (used in various file names).
set dataname=sample
rem The full name of the data source folder.
set datasrc=d:\delta\%dataname%
rem The default name of the destination folder (when the file is unpacked).
set datadest=c:\%dataname%
rem The name of the self-extracting  file.
set sxf=%dataname%i
rem The name of the startup  file.
set startup=%dataname%.ink
rem The directory containing winzipse.exe.
set winzipse=c:\winzipse

to %datasrc%
if errorlevel 1 goto nodatasrc

echo DELTA Sample Data > message.tmp
echo Interactive identification and information retrieval >> message.tmp

set icon=%delta%\delta2.ico
set i=%icon%
if not exist %i% goto nofile

set i=readme.txt
if not exist %i% goto nofile
set i=%startup%
if not exist %i% goto nofile

echo -d %datadest% > options.tmp
echo -i %icon% >> options.tmp
echo -m %datasrc%\message.tmp >> options.tmp
echo -standard >> options.tmp
echo -nooverwrite >> options.tmp

echo Files to be put in %sxf%.exe. > dir.tmp
echo. > dirb.tmp
set i=ichars
if not exist %i% goto nofile
dir %i% >> dir.tmp
echo %i% >> dirb.tmp
set i=iitems
if not exist %i% goto nofile
dir %i% >> dir.tmp
echo %i% >> dirb.tmp
set i=intkey.ink
if not exist %i% goto nofile
dir %i% >> dir.tmp
echo %i% >> dirb.tmp
set i=*.ind
if not exist %i% goto nofile
dir /od %i% >> dir.tmp
echo %i% >> dirb.tmp
set i=toolbar.inp
if not exist %i% goto nofile
dir /od %i% >> dir.tmp
echo %i% >> dirb.tmp
set i=*.tax
if not exist %i% goto nofile
dir /od %i% >> dir.tmp
echo %i% >> dirb.tmp
set i=intro.rtf
if not exist %i% goto nofile
dir /od %i% >> dir.tmp
echo %i% >> dirb.tmp
set i=ack.rtf
if not exist %i% goto nofile
dir /od %i% >> dir.tmp
echo %i% >> dirb.tmp
set i=refs.rtf
if not exist %i% goto nofile
dir /od %i% >> dir.tmp
echo %i% >> dirb.tmp

ted dir.tmp /ex="@('f'e)o@(s;bytes free;e)o?;Enter Q to continue;"
if errorlevel 1 goto abort
echo.
echo Press CTRL/C to abort . . .
pause

if exist %sxf%.zip del %sxf%.zip
if exist %sxf%.exe del %sxf%.exe
pkzip -o -P- %sxf%.zip @dirb.tmp
echo.
echo Creating self-extracting file ...
start /wait %winzipse%\winzipse %sxf%.zip @%datasrc%\options.tmp
if exist %sxf%.zip del %sxf%.zip

rem The zip file created here is transmitted to the server, then unzipped
rem and deleted. As well as bundling the files for easier transmission,
rem this ensures the integrity of the files and retains their date stamps.
if exist %sfx%.zip del %sfx%.zip > nul
pkzip -Po %sxf%.zip readme.txt %startup% %sxf%.exe info\*.*
goto finish

:wrongos
echo Must be run under Windows 95/NT or later.
goto abort

:nodatasrc
echo Could not move to %datasrc%
goto abort

:nofile
echo The required distribution file %i% is missing.

:abort
echo.
echo Job aborted.

:finish
echo.
set dataname=
set datasrc=
set datadest=
set startup=
set i=
set icon=
set sxf=

:end
