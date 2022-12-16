-- R1 is lower pointer
-- R2 is upper pointer
-- R3 is pivot pointer
-- R4 is lower value
-- R5 is upper value
-- R6 is pivot value
-- R7 is work reg
-- R30 is SP
-- R8 is left edge of array
Begin Assembly
addi R30, R0, 5000
addi R1, R0, 4000
addi R2, R0, 4000
addi R3, R0, 4040
jal sort
halt
LABEL sort
beq R1, R3, finshed
add R8, R0, R1
lw R4, 0(R1)
lw R5, 0(R2)
lw R6, 0(R3)
LABEL partition
sub R7, R4, R6
bgtz R7, walkUpper
addi R1, R1, 4
beq R1, R2, continue
addi R2, R2, 4
lw R5, 0(R2)
LABEL continue
lw R4, 0(R1)
beq R1, R3, finshIter
j partition
LABEL walkUpper
sub R7, R5, R6
bltz R7, swap
addi R2, R2, 4
beq R2, R3, finshIter
lw R5, 0(R2)
j walkUpper
LABEL swap
sw R4, 0(R2)
sw R5, 0(R1)
addi R1, R1, 4
addi R2, R2, 4
lw R4, 0(R1)
lw R5, 0(R2)
beq R1, R3, finshIter
beq R2, R3, finshIter
j partition
LABEL finshIter
sw R6, 0(R1)
sw R4, 0(R3)
sw R31, 0(R30)
addi R30, R30, 4
sw R2, 0(R30)
addi R30, R30, 4
sw R1, 0(R30)
addi R30, R30, 4
add R2, R0, R8
addi R3, R1, -4
add R1, R0, R8
sub R7, R1, R3
bgtz R7, noLeft
jal sort
LABEL noLeft
addi R30, R30, -4
lw R1, 0(R30)
sw R0, 0(R30)
addi R30, R30, -4
lw R3, 0(R30)
sw R0, 0(R30)
add R2, R0, R1
jal sort
addi R30, R30, -4
lw R31, 0(R30)
sw R0, 0(R30)
LABEL finshed
jr R31
End Assembly
Begin Data 4000 40
195
728
943
955
102
116
747
169
278
34
End Data
Begin Data 5000 1024
End Data