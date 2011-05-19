@echo off
echo.
echo Zips distribution files for WWW.
echo Confor printch, Confor tonath, and Wwwcopy must be run first.
echo Requires the program Pkzip.
echo.
echo This file is included as an example only. It will need to be
echo modified to suit your requirements.
echo.
rem goto end
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
to %datadir%
if errorlevel 1 goto nodatadir

rem The zip file created here is transmitted to the server, then unzipped
rem and deleted. As well as bundling the files for easier transmission,
rem this ensures the integrity of the files and retains their date stamps.
if exist %dataname%w.zip del %dataname%w.zip > nul
pkzip -o -P %dataname%w.zip index.htm www\*.*
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
set dataname=
set datadir=

:end
