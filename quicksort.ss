      -- R1 is lower pointer
      -- R2 is upper pointer
      -- R3 is pivot pointer
      -- R4 is lower value
      -- R5 is upper value
      -- R6 is pivot value
      -- R7 is work reg
      -- R30 is SP
      -- R8 is left edge of array
0:  addi R30, R0, 5000
4:  addi R1, R0, 4000
8:  addi R2, R0, 4000
12:  addi R3, R0, 4040
16:  jal sort
20:  halt
LABEL sort
24:  beq R1, R3, finshed
28:  add R8, R0, R1
32:  lw R4, 0(R1)
36:  lw R5, 0(R2)
40:  lw R6, 0(R3)
LABEL partition
44:  sub R7, R4, R6
48:  bgtz R7, walkUpper
52:  addi R1, R1, 4
56:  beq R1, R2, continue
60:  addi R2, R2, 4
64:  lw R5, 0(R2)
LABEL continue
68:  lw R4, 0(R1)
72:  beq R1, R3, finshIter
76:  j partition
LABEL walkUpper
80:  sub R7, R5, R6
84:  bltz R7, swap
88:  addi R2, R2, 4
92:  beq R2, R3, finshIter
96:  lw R5, 0(R2)
100:  j walkUpper
LABEL swap
104:  sw R4, 0(R2)
108:  sw R5, 0(R1)
112:  addi R1, R1, 4
116:  addi R2, R2, 4
120:  lw R4, 0(R1)
124:  lw R5, 0(R2)
128:  beq R1, R3, finshIter
132:  beq R2, R3, finshIter
136:  j partition
LABEL finshIter
140:  sw R6, 0(R1)
144:  sw R4, 0(R3)
148:  sw R31, 0(R30)
152:  addi R30, R30, 4
156:  sw R2, 0(R30)
160:  addi R30, R30, 4
164:  sw R1, 0(R30)
168:  addi R30, R30, 4
172:  add R2, R0, R8
176:  addi R3, R1, -4
180:  add R1, R0, R8
184:  sub R7, R1, R3
188:  bgtz R7, noLeft
192:  jal sort
LABEL noLeft
196:  addi R30, R30, -4
200:  lw R1, 0(R30)
204:  sw R0, 0(R30)
208:  addi R30, R30, -4
212:  lw R3, 0(R30)
216:  sw R0, 0(R30)
220:  add R2, R0, R1
224:  jal sort
228:  addi R30, R30, -4
232:  lw R31, 0(R30)
236:  sw R0, 0(R30)
LABEL finshed
240:  jr R31
