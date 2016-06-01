@256
D=A
@SP
M=D
@RETURN_Sys.init_0
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
@5
D=A
@SP
D=M-D
@ARG
M=D
@SP
D=M
@LCL
M=D
@Sys.init
0;JMP
(RETURN_Sys.init_0)
// file: ..asm
// @256
// D=A
// @SP
// M=D
// @RETURN_Sys.init_0
// D=A
// @SP
// A=M
// M=D
// @SP
// M=M+1
// @LCL
// D=M
// @SP
// A=M
// M=D
// @SP
// M=M+1
// @ARG
// D=M
// @SP
// A=M
// M=D
// @SP
// M=M+1
// @THIS
// D=M
// @SP
// A=M
// M=D
// @SP
// M=M+1
// @THAT
// D=M
// @SP
// A=M
// M=D
// @SP
// M=M+1
// @5
// D=A
// @SP
// D=M-D
// @ARG
// M=D
// @SP
// D=M
// @LCL
// M=D
// @Sys.init
// 0;JMP
// (RETURN_Sys.init_0)
// file: FibonacciElement.cmp
// | RAM[0] |RAM[261]|
// |    262 |      3 |
// file: FibonacciElement.tst
// load FibonacciElement.asm,
// output-file FibonacciElement.out,
// compare-to FibonacciElement.cmp,
// output-list RAM[0]%D1.6.1 RAM[261]%D1.6.1;
// repeat 6000 {
//   ticktock;
// }
// output;
// file: FibonacciElementVME.tst
// load,
// output-file FibonacciElement.out,
// compare-to FibonacciElement.cmp,
// output-list RAM[0]%D1.6.1 RAM[261]%D1.6.1;
// set sp 261,
// repeat 110 {
//   vmstep;
// }
// output;
// file: Icon
// file: Main.vm
// function Main.fibonacci 0
(Main.fibonacci)
// push argument 0
@ARG
D=M
@0
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// push constant 2
@2
D=A
@SP
A=M
M=D
@SP
M=M+1
// lt
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
D=M-D
@PUSH_TRUE_0
D;JLT
@PUSH_FALSE_0
0,JMP
(PUSH_TRUE_0)
@SP
A=M
M=-1
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_0
0;JMP
(PUSH_FALSE_0)
@SP
A=M
M=0
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_0
0;JMP
(LEAVE_COMPARISON_COMMAND_0)
// if-goto IF_TRUE
@SP
M=M-1
@SP
A=M
D=M
@Main.fibonacci$IF_TRUE
D;JNE
// goto IF_FALSE
@Main.fibonacci$IF_FALSE
0;JMP
// label IF_TRUE
(Main.fibonacci$IF_TRUE)
// push argument 0
@ARG
D=M
@0
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// return
@SP
M=M-1
@SP
A=M
D=M
@ARG
A=M
M=D
@ARG
D=M+1
@SP
M=D
@5
D=A
@LCL
A=M-D
D=M
@R13
M=D
@LCL
M=M-1
@LCL
A=M
D=M
@THAT
M=D
@LCL
M=M-1
@LCL
A=M
D=M
@THIS
M=D
@LCL
M=M-1
@LCL
A=M
D=M
@ARG
M=D
@LCL
M=M-1
@LCL
A=M
D=M
@LCL
M=D
@R13
A=M
0;JMP
// label IF_FALSE
(Main.fibonacci$IF_FALSE)
// push argument 0
@ARG
D=M
@0
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// push constant 2
@2
D=A
@SP
A=M
M=D
@SP
M=M+1
// sub
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
M=M-D
@SP
M=M+1
// call Main.fibonacci 1
@RETURN_Main.fibonacci_1
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
@6
D=A
@SP
D=M-D
@ARG
M=D
@SP
D=M
@LCL
M=D
@Main.fibonacci
0;JMP
(RETURN_Main.fibonacci_1)
// push argument 0
@ARG
D=M
@0
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// push constant 1
@1
D=A
@SP
A=M
M=D
@SP
M=M+1
// sub
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
M=M-D
@SP
M=M+1
// call Main.fibonacci 1
@RETURN_Main.fibonacci_2
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
@6
D=A
@SP
D=M-D
@ARG
M=D
@SP
D=M
@LCL
M=D
@Main.fibonacci
0;JMP
(RETURN_Main.fibonacci_2)
// add
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
M=D+M
@SP
M=M+1
// return
@SP
M=M-1
@SP
A=M
D=M
@ARG
A=M
M=D
@ARG
D=M+1
@SP
M=D
@5
D=A
@LCL
A=M-D
D=M
@R13
M=D
@LCL
M=M-1
@LCL
A=M
D=M
@THAT
M=D
@LCL
M=M-1
@LCL
A=M
D=M
@THIS
M=D
@LCL
M=M-1
@LCL
A=M
D=M
@ARG
M=D
@LCL
M=M-1
@LCL
A=M
D=M
@LCL
M=D
@R13
A=M
0;JMP
// file: Sys.vm
// function Sys.init 0
(Sys.init)
// push constant 4
@4
D=A
@SP
A=M
M=D
@SP
M=M+1
// call Main.fibonacci 1
@RETURN_Main.fibonacci_3
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
@6
D=A
@SP
D=M-D
@ARG
M=D
@SP
D=M
@LCL
M=D
@Main.fibonacci
0;JMP
(RETURN_Main.fibonacci_3)
// label WHILE
(Sys.init$WHILE)
// goto WHILE
@Sys.init$WHILE
0;JMP
