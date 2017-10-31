/*
    Program developed by: Eryk Szlachetka
    Chinese name: 林殊
    Student number: PJ1725110
    Class: Introduction to Information Systems Security.
*/

// Include libraries
#include <stdio.h>
#include <stdlib.h>
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

// Struct that holds 3 registers:
// Register X - 18 bits (clock bit = 8)
// Register Y - 21 bits (clock bit = 10)
// Register Z - 22 bits (clock bit = 10)
// The struct also holds the Keystream Byte (8 bits).
struct Registers{
    int x[SIZE1];
    int y[SIZE2]; 
    int z[SIZE3]; 
    int out[SIZE4];
};

// Function Prototypes
int majority(int x, int y, int z); // Function which checks if specified register is in the majority by comparing the clock bite.
void shift(struct Registers *regs, int size); // Function that shifts the values in the registers
void output(int size, int counter,struct Registers *ptr); // Function to output appropriate data. (Register values and keystream).

int main(){
    // Define the struct with pre-defined set of data.
	struct Registers registers = {  {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1},{1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1},{1,1,1,0,0,0,0,1,1,1,1,0,0,0,0,1,1,1,1,0,0,0,0}};
    struct Registers *registersPointer = &registers; // Pointer to the 'registers' struct.
    
    // First output the data, that are initialized to the registers at the start.
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
		registersPointer -> out[SIZE4-i] = registers.x[SIZE1-1] ^ registers.y[SIZE2-1] ^ registers.z[SIZE3-1];
		output(SIZE4-i,ONE,registersPointer); // Output the values including the keystream.
	}
    // Output the final value.
	output(ZERO,TWO,registersPointer);
	return 0;
}
// Function to check if the majority of clock bit.
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

// Function to compare register's bit using XOR operation and swap.
void shift(struct Registers *registers,int registerSize){
	int swap = 0;
    
    // Check which register are we dealing with (SIZE1 = register X, SIZE2 = register Y, SIZE3 = register Z).
    // After that compare using XOR operation the last register's bits to find out the swap bit.
    if(registerSize == SIZE1){
		swap = registers -> x[SIZE1 - 1] ^ registers -> x[SIZE1 - 2] ^ registers -> x[SIZE1 - 3] ^ registers -> x[SIZE1 - 6];
	}else if(registerSize == SIZE2){
		swap = registers -> y[SIZE2-1] ^ registers -> y[SIZE2-2];
	}else if(registerSize == SIZE3){
		swap = registers -> z[SIZE3-1] ^ registers -> z[SIZE3-2] ^ registers -> z[SIZE3-3] ^ registers -> z[SIZE3-15];
	}
    // Iterate through the register,
    // Check which register we are dealing with,
    // Check if we are at element 0, if so then put the swap value.
    // Otherwise change current element to element-1.
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
