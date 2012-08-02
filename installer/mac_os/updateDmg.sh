#!/bin/bash
cp Open\ DELTA.dmg /tmp
hdiutil attach /tmp/Open\ DELTA.dmg
cd ../../target
unzip open-delta-1.0-RC2-mac_os_bundle.zip
cd open-delta-1.0-RC2
cp -R .open-delta /Volumes/Open\ DELTA/
cp -R Open\ DELTA /Volumes/Open\ DELTA/
hdiutil detach /Volumes/Open\ DELTA
hdiutil convert -format UDCO -o ../Open\ DELTA.dmg /tmp/Open\ DELTA.dmg

