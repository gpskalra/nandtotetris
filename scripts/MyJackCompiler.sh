#!/bin/sh

JAVAHOME='/usr/bin/java'
CLASSPATH="$HOME/IdeaProjects/nandtotetris/out/production/JackCompiler"
MAINCLASS='com.nandtotetris.jackcompiler.JackCompiler'

$JAVAHOME -cp $CLASSPATH $MAINCLASS $1
