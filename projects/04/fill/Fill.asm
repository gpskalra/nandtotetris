// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, the
// program clears the screen, i.e. writes "white" in every pixel.

// Put your code here.

    @8192
    D=A
    @screen_n_words
    M=D

(LOOP)
    @KBD
    D=M

    // no key pressed, make screen white
    @WHITE
    D;JEQ 

(BLACK)
    @i
    M=0 // i=0
    
(BLACK_LOOP)
    @screen_n_words
    D=M
    @i
    D=D-M
    @LOOP
    D;JEQ // all screen blackened

    @SCREEN
    D=A
    @i
    A=D+M
    M=-1 // SCREEN[i]=-1
    @i
    M=M+1 // i=i+1

    @BLACK_LOOP
    0;JMP
    
(WHITE)
    @i
    M=0 // i=0
    
(WHITE_LOOP)
    @screen_n_words
    D=M
    @i
    D=D-M
    @LOOP
    D;JEQ // all screen whitened

    @SCREEN
    D=A
    @i
    A=D+M
    M=0 // SCREEN[i]=0
    @i
    M=M+1 // i=i+1

    @WHITE_LOOP
    0;JMP
