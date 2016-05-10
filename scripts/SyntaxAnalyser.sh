#!/bin/sh

JAVAHOME='/usr/bin/java'
CLASSPATH='../out/production/SyntaxAnalyser/'
MAINCLASS='com.nandtotetris.syntaxanalyser.SyntaxAnalyser'

$JAVAHOME -cp $CLASSPATH $MAINCLASS $1
