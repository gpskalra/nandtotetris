// push constant 17
@17
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 17
@17
D=A
@SP
A=M
M=D
@SP
M=M+1
// eq
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
D=M-D
@PUSH_TRUE_1
D;JEQ
@PUSH_FALSE_1
0,JMP
(PUSH_TRUE_1)
@SP
A=M
M=-1
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_1
0;JMP
(PUSH_FALSE_1)
@SP
A=M
M=0
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_1
0;JMP
(LEAVE_COMPARISON_COMMAND_1)
// push constant 17
@17
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 16
@16
D=A
@SP
A=M
M=D
@SP
M=M+1
// eq
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
D=M-D
@PUSH_TRUE_2
D;JEQ
@PUSH_FALSE_2
0,JMP
(PUSH_TRUE_2)
@SP
A=M
M=-1
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_2
0;JMP
(PUSH_FALSE_2)
@SP
A=M
M=0
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_2
0;JMP
(LEAVE_COMPARISON_COMMAND_2)
// push constant 16
@16
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 17
@17
D=A
@SP
A=M
M=D
@SP
M=M+1
// eq
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
D=M-D
@PUSH_TRUE_3
D;JEQ
@PUSH_FALSE_3
0,JMP
(PUSH_TRUE_3)
@SP
A=M
M=-1
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_3
0;JMP
(PUSH_FALSE_3)
@SP
A=M
M=0
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_3
0;JMP
(LEAVE_COMPARISON_COMMAND_3)
// push constant 892
@892
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 891
@891
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
@PUSH_TRUE_4
D;JLT
@PUSH_FALSE_4
0,JMP
(PUSH_TRUE_4)
@SP
A=M
M=-1
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_4
0;JMP
(PUSH_FALSE_4)
@SP
A=M
M=0
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_4
0;JMP
(LEAVE_COMPARISON_COMMAND_4)
// push constant 891
@891
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 892
@892
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
@PUSH_TRUE_5
D;JLT
@PUSH_FALSE_5
0,JMP
(PUSH_TRUE_5)
@SP
A=M
M=-1
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_5
0;JMP
(PUSH_FALSE_5)
@SP
A=M
M=0
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_5
0;JMP
(LEAVE_COMPARISON_COMMAND_5)
// push constant 891
@891
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 891
@891
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
@PUSH_TRUE_6
D;JLT
@PUSH_FALSE_6
0,JMP
(PUSH_TRUE_6)
@SP
A=M
M=-1
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_6
0;JMP
(PUSH_FALSE_6)
@SP
A=M
M=0
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_6
0;JMP
(LEAVE_COMPARISON_COMMAND_6)
// push constant 32767
@32767
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 32766
@32766
D=A
@SP
A=M
M=D
@SP
M=M+1
// gt
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
D=M-D
@PUSH_TRUE_7
D;JGT
@PUSH_FALSE_7
0,JMP
(PUSH_TRUE_7)
@SP
A=M
M=-1
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_7
0;JMP
(PUSH_FALSE_7)
@SP
A=M
M=0
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_7
0;JMP
(LEAVE_COMPARISON_COMMAND_7)
// push constant 32766
@32766
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 32767
@32767
D=A
@SP
A=M
M=D
@SP
M=M+1
// gt
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
D=M-D
@PUSH_TRUE_8
D;JGT
@PUSH_FALSE_8
0,JMP
(PUSH_TRUE_8)
@SP
A=M
M=-1
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_8
0;JMP
(PUSH_FALSE_8)
@SP
A=M
M=0
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_8
0;JMP
(LEAVE_COMPARISON_COMMAND_8)
// push constant 32766
@32766
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 32766
@32766
D=A
@SP
A=M
M=D
@SP
M=M+1
// gt
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
D=M-D
@PUSH_TRUE_9
D;JGT
@PUSH_FALSE_9
0,JMP
(PUSH_TRUE_9)
@SP
A=M
M=-1
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_9
0;JMP
(PUSH_FALSE_9)
@SP
A=M
M=0
@SP
M=M+1
@LEAVE_COMPARISON_COMMAND_9
0;JMP
(LEAVE_COMPARISON_COMMAND_9)
// push constant 57
@57
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 31
@31
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 53
@53
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
// push constant 112
@112
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
// neg
@SP
M=M-1
A=M
M=-M
@SP
M=M+1
// and
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
M=D&M
@SP
M=M+1
// push constant 82
@82
D=A
@SP
A=M
M=D
@SP
M=M+1
// or
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
M=D|M
@SP
M=M+1
// not
@SP
M=M-1
A=M
M=!M
@SP
M=M+1
