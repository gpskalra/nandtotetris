# This script is part of project work for the coursera 
# course Nand To Tetris.
#
# This script runs the jack analyser used to tokenise and 
# parse jack files.
#
# Author: gaganpreet1810@gmail.com

#!/bin/sh

JAVAHOME='/usr/bin/java'
CLASSPATH="$HOME/IdeaProjects/nandtotetris/out/production/JackCompiler"
MAINCLASS='com.nandtotetris.jackcompiler.JackAnalyser'

$JAVAHOME -cp $CLASSPATH $MAINCLASS $1
