@256
D=A
@SP
M=D
@0
D=A
@1
D=D-A
@LCL
M=D
@0
D=A
@2
D=D-A
@ARG
M=D
@0
D=A
@3
D=D-A
@THIS
M=D
@0
D=A
@4
D=D-A
@THAT
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
// file: Sys.vm
// function Sys.init 0
(Sys.init)
// call Sys.main 0
@RETURN_Sys.main_1
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
@Sys.main
0;JMP
(RETURN_Sys.main_1)
// pop temp 1
@R5
D=A
@1
D=D+A
@R13
M=D
@SP
M=M-1
@SP
A=M
D=M
@R13
A=M
M=D
// label LOOP
(Sys.init$LOOP)
// goto LOOP
@Sys.init$LOOP
0;JMP
// function Sys.main 0
(Sys.main)
// push constant 123
@123
D=A
@SP
A=M
M=D
@SP
M=M+1
// call Sys.add12 1
@RETURN_Sys.add12_2
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
@Sys.add12
0;JMP
(RETURN_Sys.add12_2)
// pop temp 0
@R5
D=A
@0
D=D+A
@R13
M=D
@SP
M=M-1
@SP
A=M
D=M
@R13
A=M
M=D
// push constant 246
@246
D=A
@SP
A=M
M=D
@SP
M=M+1
// return
@5
D=A
@LCL
A=M-D
D=M
@R13
M=D
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
// function Sys.add12 3
(Sys.add12)
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
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
// push constant 12
@12
D=A
@SP
A=M
M=D
@SP
M=M+1
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
@5
D=A
@LCL
A=M-D
D=M
@R13
M=D
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
