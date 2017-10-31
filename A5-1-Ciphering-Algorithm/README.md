# A5/1 Ciphering-Algorithm

<a href="https://en.wikipedia.org/wiki/A5/1">A5/1</a> is a stream cipher used to provide over-the-air communication privacy in the GSM cellular telephone standard.</br>
It is one of seven algorithms which were specified for GSM use. It was initially kept secret, but became public knowledge through leaks and reverse engineering.

![Algo](https://upload.wikimedia.org/wikipedia/commons/5/5e/A5-1_GSM_cipher.svg)

## Table of Contents
1. <a href="#goal">The goal</a>
2. <a href="#imp">Implementation</a>
	- <a href="#lib">Libraries and defines</a>
 	- <a href="#reg">Registers</a>
 	- <a href="#fun">Function Prototypes</a>
 	- <a href="#mai">Main Function</a>
 	- <a href="#maj">Majority Function</a>
 	- <a href="#shi">Shift Function</a>
 	- <a href="#out">Output Function</a>
3. <a href="#output">Output</a>
 	- <a href="#com">Compiling</a>
 	- <a href="#run">Running</a>
 	- <a href="#exp">Expected Output</a>

<a id ="goal"> </a>

## The Goal

The goal is simple, to implement the A5/1 algorithm. </br>
</br>
Suppose that, after a particular step, the values in the registers are: </br>
```C
X = (x0, x1, . . . , x18) = (1010101010101010101)</br>
Y = (y0, y1, . . . , y21) = (1100110011001100110011)</br>
Z = (z0, z1, . . . , z22) = (11100001111000011110000)</br></br>
```

What the program does is, generating and printing the next 8 keystream bits.</br>
Also printing the contents of X, Y and Z after the 8 keystream bits have been generated.

![a5/1](https://cloud.githubusercontent.com/assets/15609881/26023858/3afaca3c-37f8-11e7-81b0-bf61102492d8.png)

<a id ="imp"> </a>

## Implementation
In this section I will explain how I have implemented the A5/1 Ciphering Algorithm.

<a id ="lib"> </a>

### Libraries and defines
First of all, libraries have to be imported, after the includes I have defined values that are used in the program.</br>

```C
// Include libraries
#include <stdio.h>  // Standard Input Output Library
#include <stdlib.h> // Standard Library 

// Defines for register's size
#define SIZE1 19
#define SIZE2 22
#define SIZE3 23
#define SIZE4 8
// Define clock bit
#define REG1CLOCK 8
#define REGCLOCK 10
// Define byte
#define BYTE 8
// Other defines
#define ZERO 0
#define ONE 1
#define TWO 2
```

So, the libraries that are imported is nothing complicated, just standard C libraries.</br>
The first four defines are used to define the size of each register i.e. </br>```SIZE1``` = register X,</br> ```SIZE2``` = register Y,</br> ```SIZE3``` = register Z, </br>```SIZE4``` = register OUT.</br></br>
The clock bit defines are used to store the position of bit clock on each register i.e. </br>
```REG1CLOCK``` = clock bit of reg X,</br>```REGCLOCK``` = clock bit of REG Y,</br> ```REGCLOCK``` = also clock bit of REG Z.</br></br>
```BYTE``` is used for the for loop, to iterate through the BYTE (1 BYTE = 8 Bits, therefore 8 iterations).</br>

<a id ="reg"> </a>

### Registers
First thing after the defines is to define the struct that will store information for each register, including X, Y, Z and OUT.</br>

```C
struct Registers{
    int x[SIZE1];
    int y[SIZE2]; 
    int z[SIZE3]; 
    int out[SIZE4];
};
```
The Struct holds 3 registers:
```Register X``` - 18 bits (clock bit = 8)</br>
```Register Y``` - 21 bits (clock bit = 10)</br>
```Register Z``` - 22 bits (clock bit = 10)</br>
```OUT```        - The struct also holds the Keystream Byte (8 bits).</br>

<a id ="fun"> </a>

### Function Prototypes
This is the time define the prototypes of the functions to be used in the program, so they can be precompiled, and so that the compiler would know that we are going to use them, what they should return and what parameters should be passed.</br>

```C
int majority(int x, int y, int z);
void shift(struct Registers *regs, int size);
void output(int size, int counter,struct Registers *ptr);
```

```majority``` is a function which checks if specified register is in the majority by comparing the clock bite, **returns an integer value**, takes three parameters (also Integers) that are the **values from the registers** in each iteration through the Byte.</br></br>

```shift``` is a function that shifts the values in the registers, doesn't return anything, takes two parameters, **Registers struct** and **size of the register** (integer).</br></br>

```output``` is a cuntion which outputs appropriate data, i.e. register values and keystream values to the screen. Doesn't return anything, takes three parameters: **size of the register** (integer), **counter** (integer), and finaly **pointer to the Registers** struct.</br>

<a id ="mai"> </a>

### Main Function
The main function is executed first, where other functions are called. </br>

```C
int main(){
	struct Registers registers = {  
		{1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},
		{1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1},
		{1,1,1,0,0,0,0,1,1,1,1,0,0,0,0,1,1,1,1,0,0,0,0}};
    	struct Registers *registersPointer = &registers; // Pointer to the 'registers' struct.
    
	output(ZERO,ZERO,registersPointer);
    
	// Variable for for loop
	int i = ZERO;
	// Iterate through the byte (8 times)
	for(i = 0; i <  BYTE; i++){
		// Store the majority value from the clock bit in m (x[8], y[10], z[10]).
		int m = majority(registers.x[REG1CLOCK],registers.y[REGCLOCK],registers.z[REGCLOCK]);
			
		// Compare and shift the x register only if part of the majority.
		if(registers.x[REG1CLOCK] == m){
			shift(registersPointer,SIZE1);
		}
        // Compare and shift the y register only if part of the majority.
		if(registers.y[REGCLOCK] ==m){
			shift(registersPointer,SIZE2);
		}
        // Compare and shift the z register only if part of the majority.
		if(registers.z[REGCLOCK] ==m){
			shift(registersPointer,SIZE3);
		}
        // Set the keystream bit (start from the back).
		registersPointer -> out[SIZE4-i] = 
			registers.x[SIZE1-1] ^ 
			registers.y[SIZE2-1] ^ 
			registers.z[SIZE3-1];
		output(SIZE4-i,ONE,registersPointer); // Output the values including the keystream.
	}
    	// Output the final value.
	output(ZERO,TWO,registersPointer);
	return 0;
}
```

First I have defined the struct ```registers``` with pre-defined set of data and a pointer ```*registersPointer``` that <i>'points'</i> to that struct.
```C
struct Registers registers = {  {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},{1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1},{1,1,1,0,0,0,0,1,1,1,1,0,0,0,0,1,1,1,1,0,0,0,0}};
struct Registers *registersPointer = &registers; // Pointer to the 'registers' struct.
```
Then ```output``` function is called that outputs the data, that are initialized to the registers at the start, then declare and initialize variable for the loop.</br>
```C
output(ZERO,ZERO,registersPointer);
int i = ZERO;
```
Then the loop is started to iterate through the byte</br>
```C
for(i = 0; i <  BYTE; i++){
```
Inside the foor loop, first the majority value is stored in ``int m``, this is either ``1`` or ``0``, an actual value from the clock bit ``m (x[8], y[10], z[10])``.

```C
int m = majority(registers.x[REG1CLOCK],registers.y[REGCLOCK],registers.z[REGCLOCK]);	
```
Once the majority value is stored in ``int m``, its start to find out which register is the once with majority value in it's clock bit.</br>
```C
if(registers.x[REG1CLOCK] == m){...} // Compare x register if is part of the majority.
if(registers.y[REGCLOCK]  == m){...} // Compare y register if is part of the majority.
if(registers.z[REGCLOCK]  == m){...} // Compare z register if is part of the majority.
```
If any of the registers is in the majority, then **shift** it.  

```C
// Compare and shift the x register only if part of the majority.
if(registers.x[REG1CLOCK] == m){
    shift(registersPointer,SIZE1);
}
// Compare and shift the y register only if part of the majority.
if(registers.y[REGCLOCK] ==m){
    shift(registersPointer,SIZE2);
}
// Compare and shift the z register only if part of the majority.
if(registers.z[REGCLOCK] ==m){
    shift(registersPointer,SIZE3);
}
```
The last part is to set the keystream bit (start from the back) and output the values including the keystream.</br>
```C
registersPointer -> out[SIZE4-i] = registers.x[SIZE1-1] ^ registers.y[SIZE2-1] ^ registers.z[SIZE3-1];
output(SIZE4-i,ONE,registersPointer); 
```

<a id ="maj"> </a>

### Majority Function
This function checks the majority of clock bit.</br>
e.g. if register values at that position are as follow:</br>
```C
X = 1
Y= 0
Z = 1
```
Then the majority will be ```1```.
```C
int majority(int x, int y, int z){
	if( x == y){
		return x;
	}else if(y == z){
		return y;
	}else if(x == z){
		return z;
	}
	return 0;
}
```

<a id ="shi"> </a>

### Shift Function
Function to compare and swap register's bit using XOR operation.</br>
```C
void shift(struct Registers *registers,int registerSize){
	int swap = 0;
	if(registerSize == SIZE1){
		swap = 
			registers -> x[SIZE1 - 1] ^ registers -> x[SIZE1 - 2] ^
			registers -> x[SIZE1 - 3] ^ registers -> x[SIZE1 - 6];
	}else if(registerSize == SIZE2){
		swap = registers -> y[SIZE2-1] ^ registers -> y[SIZE2-2];
	}else if(registerSize == SIZE3){
		swap = 
			registers -> z[SIZE3-1] ^ registers -> z[SIZE3-2] ^ 
			registers -> z[SIZE3-3] ^ registers -> z[SIZE3-15];
	}
	
	for(int f = (registerSize-1); f >= 0;f--){
		if(registerSize == SIZE1){
			if(f == 0){
				registers -> x[f] = swap;
			}else{
				registers -> x[f] = registers -> x[f-1];
			}

		}else if(registerSize == SIZE2){
			if(f == 0){
                		registers -> y[f] = swap;
            		}else{
                		registers -> y[f] = registers -> y[f-1];
            		}
		}else if(registerSize == SIZE3){
			if(f == 0){
                		registers -> z[f] = swap;
            		}else{
                		registers -> z[f] = registers -> z[f-1];
            		}
		}
	}
}
```

First, check which register are we dealing with (SIZE1 = register X, SIZE2 = register Y, SIZE3 = register Z), after that compare using XOR operation the last register's bits to find out the swap bit.
```C
if(registerSize == SIZE1){
	swap = registers -> x[SIZE1 - 1] ^ registers -> x[SIZE1 - 2] ^ registers -> x[SIZE1 - 3] ^ registers -> x[SIZE1 - 6];
}else if(registerSize == SIZE2){
	swap = registers -> y[SIZE2-1] ^ registers -> y[SIZE2-2];
}else if(registerSize == SIZE3){
	swap = registers -> z[SIZE3-1] ^ registers -> z[SIZE3-2] ^ registers -> z[SIZE3-3] ^ registers -> z[SIZE3-15];
}
```
After the swap value is determined, terate through the register and check which register we are dealing with.
```C
if(registerSize == SIZE1){...}
else if(registerSize == SIZE2){...}
else if(registerSize == SIZE3){...}
```
check if we are at element 0, if so then put the swap value. Otherwise change current element to element-1.

```C
if(registerSize == SIZE1){
	if(f == 0){
		registers -> x[f] = swap;
	}else{
		registers -> x[f] = registers -> x[f-1];
	}

}else if(registerSize == SIZE2){
	if(f == 0){
		registers -> y[f] = swap;
	}else{
		registers -> y[f] = registers -> y[f-1];
	}
}else if(registerSize == SIZE3){
	if(f == 0){
		registers -> z[f] = swap;
	    }else{
		registers -> z[f] = registers -> z[f-1];
	    }
}	
```

<a id ="out"> </a>

### Output Function
Function to output the data to the screen. 

```C
// Function to output the data.
void output(int outvalue, int go, struct Registers *registers){
	int b = 0;
	// If the first or second time outputing, output appropriate message.
	if(go == ZERO){
		printf("ORGINAL CONTENT OF X, Y and Z.");
	}
	if(go == TWO){
		printf("\nLAST CONTENT OF X, Y and Z.");
	}
	// Output values for register X.
	printf("\nValue for x: ");
	for(b =0; b < SIZE1 ;b++){
		printf("%d,",registers->x[b]);
	}
	// Output values for register Y.
	printf("\nValue for y: ");
	for(b = 0; b < SIZE2 ;b++){
		printf("%x,",registers->y[b]);
	}	
	// Output values for register Z.
	printf("\nValue for z: ");
	for(b = 0; b < SIZE3;b++){
		printf("%d,",registers->z[b]);
	}

	// Output values for the KeyStream.
	if(go != ZERO && go != TWO){
		printf("\nKeystream (X ^ Y ^ Z): ");
		for(int i = outvalue ; i <= SIZE4;i++){
		     printf("%d,",registers->out[i]);
		}
	}
    
	printf("\n");
	
}
```

<a id ="output"> </a>

## Output
In this section I will show how to compile the program, how to run it and what should be the expected output.

<a id ="com"> </a>

### Compiling
To compile the program, navigate to the program directory using </i>Terminal on Mac</i> or <i>CMD on Windows</i> and use ```gcc -o algo a51.c``` to compile.

<a id ="run"> </a>

### Running
To run the program use ```./algo``` or 'click' on the generated file.

<a id ="exp"> </a>

### Expected Output
![a51](https://cloud.githubusercontent.com/assets/15609881/26023855/3146dfb2-37f8-11e7-87a3-18101ae256b5.png)
