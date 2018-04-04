// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

	//@status
    //M=-1        //status=0xFFFF, whites
	

(CHECK)
	@SCREEN
	D=A
	@Position	//initial to -1
	M=D-1
	@KBD		//keyboard ready
	D=M 	
	@BLACKEN 	//if key value is > 0
	D;JGT
	@WHITEN 	//else whiten
	0;JMP

(BLACKEN)
	//if position is at max of the screen, do nothing
	@24576
	D=M
	@Position
	D=D-M
	@CHECK
	D;JEQ

	//else start blacken
	@Position
	A=M
	M=-1

	//next position
	@Position
	D=M+1
	@Position
	M=D

	//check if user is still hold the key
	@CHECK
	0;JMP

(WHITEN)
	//if position is at max of the screen, do nothing
	@24576
	D=M
	@Position
	D=D-M
	@CHECK
	D;JEQ

	//else start blacken
	@Position
	A=M
	M=0

	//next position
	@Position
	D=M+1
	@Position
	M=D

	//check if user is still hold the key
	@CHECK
	0;JMP







