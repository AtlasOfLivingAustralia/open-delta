<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE helpset   
PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN"
         "http://java.sun.com/products/javahelp/helpset_2_0.dtd">
<helpset version="2.0">

  <!-- title -->
  <title>Intkey Help</title>

  <!-- maps -->
  <maps>
     <homeID>introduction</homeID>
     <mapref location="default/IntkeyMap.jhm"/>
  </maps>
  
  <presentation default="true" displayviews="true" displayviewimages="true">
  	<name>Intkey</name>
  	<size width="1024" height="768"/>
  	<title>Intkey Help</title>
  	<image>intkey.image.icon</image>
  </presentation>

  <!-- views -->
  <view mergetype="javax.help.AppendMerge">
    <name>TOC</name>
    <label>Table Of Contents</label>
    <type>javax.help.TOCView</type>
    <data>default/IntkeyTOC.xml</data>
  </view>

  <!--<view mergetype="javax.help.SortMerge">
    <name>Index</name>
    <label>Index</label>
    <type>javax.help.IndexView</type>
    <data>DeltaEditorIndex.xml</data>
  </view>-->

  <view mergetype="javax.help.SortMerge">
    <name>Search</name>
    <label>Search</label>
    <type>javax.help.SearchView</type>
    <data engine="com.sun.java.help.search.DefaultSearchEngine">
      JavaHelpSearch
    </data>
  </view>
  
</helpset>

         