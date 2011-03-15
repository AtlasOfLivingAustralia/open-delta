*COMMENT Personal Intkey initialization file.

*COMMENT This illustrates the use of a `personal' Intkey initialization file,
in which you can specify settings that you want to use yourself, but don't want
to include in the standard initialization file, intkey.ink, which will be used
by other users of your data. Note that intkey.ini is invoked by this file,
so you don't have to maintain extra copies of keyword definitions, etc.

*FILE INPUT intkey.ink

*SET IMAGEPATH .;images

*DEFINE BUTTON SPACE

*DEFINE BUTTON numns.bmp
"DISPLAY NUMBERING ?"
"Set displaying of character and taxon numbers on or off"

*DEFINE BUTTON diaglns.bmp
"SET DIAGLEVEL ?"
"Set the diagnostic level"

*DEFINE BUTTON /N set_mats.bmp
"SET MATCH ?"
"Set criteria for matching of attributes"

*DEFINE BUTTON dislogs.bmp
"DISPLAY LOG ON"
"Show log"
