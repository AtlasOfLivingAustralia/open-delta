@echo off

rem Generate descriptions with a different character order (Windows 95/NT).

rem If necessary, create directory 'nat1' for reordered files.
if not exist nat1\nul md nat1

rem Reorder files.
start /wait delforqw nat1ord
if errorlevel 1 goto error

rem Move reordered files to 'nat1' directory.
move specs.new nat1\specs
move chars.new nat1\chars
move items.new nat1\items
move empchari.new nat1\empchari
move empcharm.new nat1\empcharm
move layout.new nat1\layout

rem Copy other necessary files to 'nat1' directory.
copy tonatr nat1
copy markrtf nat1

rem Move to 'nat1' directory and generate descriptions.
cd nat1
conforqw tonatr
if errorlevel 1 goto error
goto finish

:error
echo Job aborted.

:finish

