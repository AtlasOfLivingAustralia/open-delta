@echo off
echo.
echo Copies miscellaneous files to the WWW directory
echo.
pause

cdtest www
if errorlevel 1 goto e-www
copy ..\ack.ht?
copy ..\ident.ht?
copy ..\intro.ht?
copy ..\refs.ht?
copy ..\word.ht?
copy %delta%\deltlogo.gi?

rem attrib -a *.*
cd ..
goto finish

:e-www
echo Directory www not found.

:finish
echo.
