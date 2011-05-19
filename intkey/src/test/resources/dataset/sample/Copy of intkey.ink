*COMMENT Intkey initialization file.

*SET IMAGEPATH images

*SET INFOPATH info

*FILE TAXA 
iitems

*FILE CHARACTERS ichars

*FILE INPUT toolbar.inp

*COMMENT CHANGE
*COMMENT CHARACTERS
*COMMENT CONTENTS
*COMMENT DEFINE
*COMMENT DELETE
*COMMENT DESCRIBE all all
*COMMENT DIAGNOSE 1 all
*COMMENT DIFFERENCES all all
*COMMENT DISPLAY CHARACTERORDER best
*COMMENT EXCLUDE CHARACTERS all
*COMMENT FILE OUTPUT
*COMMENT ILLUSTRATE
*COMMENT INCLUDE
*COMMENT INFORMATION
*COMMENT NEWDATASET
*COMMENT OUTPUT
*COMMENT PREFERENCES
*COMMENT RESTART
*COMMENT SET
*COMMENT SHOW
*COMMENT SIMILARITIES
*COMMENT STATUS
*COMMENT SUMMARY
*COMMENT TAXA
*COMMENT USE 1,foo
*COMMENT QUIT



*SET RBASE 1.2

*DISPLAY UNKNOWNS Off

*DISPLAY INAPPLICABLES Off

*DEFINE CHARACTERS "nomenclature" 1

*DEFINE CHARACTERS "habit" 2-5 13

*DEFINE CHARACTERS "vegetative form" 2-11

*DEFINE CHARACTERS "   culms (form)" 3-6

*DEFINE CHARACTERS "   leaves (form)" 7-11

*DEFINE CHARACTERS "   ligules" 10-11

*DEFINE CHARACTERS "reproductive organization" 12 23-24

*DEFINE CHARACTERS "inflorescence form" 13-24

*DEFINE CHARACTERS "femsterile spikelets" 25

*DEFINE CHARACTERS "femfertile spikelets" 26-63

*DEFINE CHARACTERS "   glumes" 32-38

*DEFINE CHARACTERS "   incomplete florets" 39-43

*DEFINE CHARACTERS "   florets (female-fertile)" 44-63

*DEFINE CHARACTERS "   lemmas (female-fertile)" 45-54

*DEFINE CHARACTERS "   awns of female-fertile lemmas" 47-51

*DEFINE CHARACTERS "   paleas (female-fertile)" 55-56

*DEFINE CHARACTERS "   lodicules (female-fertile florets" 57-59

*DEFINE CHARACTERS "   androecium of female-fertile florets" 60

*DEFINE CHARACTERS "   gynoecium" 61-63

*DEFINE CHARACTERS "fruit" 64-67

*DEFINE CHARACTERS "photosynthetic pathway-related features" 68-70

*DEFINE CHARACTERS "   biochemistry" 69

*DEFINE CHARACTERS "ts anatomy of the leaf blade" 68 70-76

*DEFINE CHARACTERS "diagnostic features of individual taxa" 77

*DEFINE CHARACTERS "classification" 78-84

*DEFINE CHARACTERS "   subfamilies and supertribes" 78-79

*DEFINE CHARACTERS "   tribes" 80-84

*DEFINE CHARACTERS "species number" 85

*DEFINE CHARACTERS "geography" 86

*DEFINE CHARACTERS "references" 87

*DEFINE CHARACTERS "text" 1 25 87

*DEFINE CHARACTERS "nontext" 2-24 26-86

*DEFINE CHARACTERS "morphology" veg repro infl femster femfert glumes incompl
  florets lemmas paleas lodicu androec gynoec fruit diag

*DEFINE CHARACTERS "ident - include for routine identification using `Best'"
  1-77 86-87

*DEFINE CHARACTERS "brief description" nom class spec geog ref

*DEFINE CHARACTERS "illustrated" 6 9-11 13

*DEFINE NAMES cereals Echinochloa, Eleusine, Oryza, Panicum, Zea

*DEFINE INFORMATION "Brief description" "describe ?S /c brief"

*DEFINE INFORMATION "Diagnostic description" "diagnose ?S none"

*DEFINE SUBJECTS "habit" "floret" "spikelet" "inflorescence"

*FILE INPUT act.tax
