// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// Put your code here.

// a = RAM[0]
// b = RAM[1]
// result = 0
// for(i=0;i<R1;i++){
//     result += RAM[0]    
// }
// RAM[2] = result

    @i
    M=0 // i=0
    
    @R0
    D=M
    @a
    M=D // a = RAM[0]
    
    @R1
    D=M
    @b
    M=D // b = RAM[1] 

    @R2
    M=0 // R2 = 0

(LOOP)
    @i
    D=M
    @b
    D=D-M    
    @END
    D;JEQ // if i==b goto END

    @R2
    D=M
    @a
    D=D+M
    @R2
    M=D // R2 = R2 + a
    
    @i
    M=M+1 // i=i+1

    @LOOP
    0;JMP

(END)
    @END
    0;JMP
