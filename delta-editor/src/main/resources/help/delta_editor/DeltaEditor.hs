<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE helpset   
PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN"
         "http://java.sun.com/products/javahelp/helpset_2_0.dtd">
<helpset version="2.0">

  <!-- title -->
  <title>Delta Editor Help</title>

  <!-- maps -->
  <maps>
     <homeID>introduction</homeID>
     <mapref location="DeltaEditorMap.jhm"/>
  </maps>
  
  <presentation default="true" displayviews="true" displayviewimages="true">
  	<name>DELTAEditor</name>
  	<size width="1024" height="768"/>
  	<title>DELTA Editor</title>
  	<image>editor.image.icon</image>
  </presentation>

  <!-- views -->
  <view mergetype="javax.help.AppendMerge">
    <name>TOC</name>
    <label>Table Of Contents</label>
    <type>javax.help.TOCView</type>
    <data>DeltaEditorTOC.xml</data>
  </view>

  <view mergetype="javax.help.SortMerge">
    <name>Index</name>
    <label>Index</label>
    <type>javax.help.IndexView</type>
    <data>DeltaEditorIndex.xml</data>
  </view>

  <view mergetype="javax.help.SortMerge">
    <name>Search</name>
    <label>Search</label>
    <type>javax.help.SearchView</type>
    <data engine="com.sun.java.help.search.DefaultSearchEngine">
      JavaHelpSearch
    </data>
  </view>
  
</helpset>

         