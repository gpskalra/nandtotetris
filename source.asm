// C_PUSH constant 111
// push constant 111
@111
D=A
@SP
A=M
M=D
@SP
M=M+1
// C_PUSH constant 333
// push constant 333
@333
D=A
@SP
A=M
M=D
@SP
M=M+1
// C_PUSH constant 888
// push constant 888
@888
D=A
@SP
A=M
M=D
@SP
M=M+1
// C_POP static 8
// pop static 8
@SP
M=M-1
@SP
A=M
D=M
@source.8
M=D
// C_POP static 3
// pop static 3
@SP
M=M-1
@SP
A=M
D=M
@source.3
M=D
// C_POP static 1
// pop static 1
@SP
M=M-1
@SP
A=M
D=M
@source.1
M=D
// C_PUSH static 3
// push static 3
@source.3
D=M
@SP
A=M
M=D
@SP
M=M+1
// C_PUSH static 1
// push static 1
@source.1
D=M
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
// C_PUSH static 8
// push static 8
@source.8
D=M
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
