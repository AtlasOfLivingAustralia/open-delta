@echo off
echo.
echo Generates and zips self-extracting file word.exe for distribution of
echo Word documents.
echo Before running this file, run Confor printcr and Confor tonatr,
echo and combine descrip.rtf and chars.rtf into descrip.doc.
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

rem The name of the dataset (maximum length 7 characters).
set dataname=sample
rem The data directory.
set datadir=d:\delta\%dataname%
rem The name of the self-extracting file.
set sxf=word
rem The directory containing winzipse.exe.
set winzipse=c:\winzipse
to %datadir%
if errorlevel 1 goto nodatadir

set icon=%delta%\delta2.ico
set i=%icon%
if not exist %i% goto nofile

echo DELTA Sample Data > message.tmp
echo Descriptions >> message.tmp

rem This is the default directory into which the end user can unpack the files.
echo -d c:\%dataname% > options.tmp
echo -i %icon% >> options.tmp
echo -m %datadir%\message.tmp >> options.tmp
echo -standard >> options.tmp
echo -nooverwrite >> options.tmp

echo Files to be put in %sxf%.exe. > dir.tmp
echo. > dirb.tmp
set i=rtf\descrip.doc
if not exist %i% goto nofile
dir %i% >> dir.tmp
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
start /wait %winzipse%\winzipse.exe %sxf%.zip @%datadir%\options.tmp
touch -f %sxf%.zip %sxf%.exe
if exist %sxf%.zip del %sxf%.zip

rem The zip file created here is transmitted to the server, then unzipped
rem and deleted. As well as bundling the files for easier transmission,
rem this ensures the integrity of the files and retains their date stamps.
if exist %dataname%d.zip del %dataname%d.zip > nul
pkzip -o %dataname%d.zip %sxf%.exe
goto finish

:wrongos
echo Must be run under Windows 95/NT or later.
goto abort

:nodatadir
echo Could not move to %datadir%
goto abort

:nofile
echo The required distribution file %i% is missing.

:abort
echo.
echo Job aborted.

:finish
set i=
set dataname=
set datadir=
set icon=
set sxf=

:end
