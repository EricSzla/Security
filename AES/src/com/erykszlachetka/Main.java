package com.erykszlachetka;

import java.io.UnsupportedEncodingException;


/*
*   Advanced Security, Lab 3
*
*   Program Developed By:
*   Name: Eryk Szlachetka
*   Student No: C14386641
*   Course: DT282/4
*
*   Title:
*       Expansion Key for AES algorithm.
*   Description:
*       The purpose of the program is to present my understanding and ability to code the key expansion of the
*       Advanced Encryption Standard (AES) algorithm.
*
*/
public class Main {
    // Rijndael S-box table, with pre-defined values.
    public static final int[] sboxTable = {0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67,
            0x2b, 0xfe, 0xd7, 0xab, 0x76, 0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59,
            0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0, 0xb7,
            0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1,
            0x71, 0xd8, 0x31, 0x15, 0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05,
            0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75, 0x09, 0x83,
            0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29,
            0xe3, 0x2f, 0x84, 0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b,
            0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf, 0xd0, 0xef, 0xaa,
            0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c,
            0x9f, 0xa8, 0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc,
            0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2, 0xcd, 0x0c, 0x13, 0xec,
            0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19,
            0x73, 0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee,
            0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb, 0xe0, 0x32, 0x3a, 0x0a, 0x49,
            0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
            0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4,
            0xea, 0x65, 0x7a, 0xae, 0x08, 0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6,
            0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a, 0x70,
            0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9,
            0x86, 0xc1, 0x1d, 0x9e, 0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e,
            0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf, 0x8c, 0xa1,
            0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0,
            0x54, 0xbb, 0x16};

    private final static int INT_RADIX = 16; // Used for parsing
    private final static int INT_KEYWORDS = 44; // Number of keywords
    private final static int INT_BYTES = 4; // Number of bytes in the word
    private final static int INT_ZERO = 0;
    private final static int INT_ONE = 1;
    private final static int INT_TWO = 2;
    private final static int INT_THREE = 3;

    public static String[] Rcon = {"01 00 00 00", "02 00 00 00", "04 00 00 00", "08 00 00 00", "10 00 00 00", "20 00 00 00",
            "40 00 00 00", "80 00 00 00", "1B 00 00 00", "36 00 00 00"};

    // Main method, called when program is executed.
    public static void main(String[] args) {
            // First convert the string to byte array, in order to pass it to the key expansion function.
            byte keys[] = "0f1571c947d9e8590cb7add6af7f6798".getBytes();
            // Initialize new "words" array of size 44
            String words[] = new String[INT_KEYWORDS];
            // Call the KeyExpansion function, pass byte array and string array as parameters.
            KeyExpansion(keys,words);

    }

    // The core function that deals with the expansion of the key.
    // It takes two parameters, the key byte array and the words array.
    public static void KeyExpansion(byte key[], String wordArray[]){
        // First create temporary string array that will store the converted byte array.
        String temp[] = {""};
        // Try to create new string out of the byte array, and split it on every second character e.g. "0f15" becomes {"0f" "15"}
        // An exception possible for unsupported encoding therefore try and catch.
        try {
            String bla = new String(key,"UTF-8");
            temp = bla.split("(?<=\\G..)");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // Iterate 4 times in order to assign the words out of temporary array to the wordArray (passed as parameter).
        // E.g.
        // wordArray[0] = temp[0] + temp[1] + temp[2] + temp[3];
        // wordArray[1] = temp[4] + temp[5] + temp[6] + temp[7];
        // .... up to temp[15]
        for(int i =INT_ZERO; i < INT_BYTES; i++){
            wordArray[i] = temp[INT_BYTES*i] + " " + temp[INT_BYTES*i+INT_ONE] + " " +  temp[INT_BYTES*i + INT_TWO] + " " + temp[INT_BYTES*i+INT_THREE];
            //System.out.println(wordArray[i]);
        }
        // Declare new string.
        String finalString;

        // Iterate from 4 to 44
        for(int i = INT_BYTES; i < INT_KEYWORDS; i++){
            // Initialize the finalString to wordArray[i-1]
            // e.g. starting wordArray[4-1] and ending wordArray[43-1]
            finalString = wordArray[i - INT_ONE];
            // If i % 4 == 0
            if(i % INT_BYTES == INT_ZERO){
                String rotated = RotWordOperation(finalString); // Call the RotWordOperation
                String subStr = SubWordOperation(rotated);      // Call the SubWordOperation
                finalString = xOrOperation(subStr, Rcon[i/INT_BYTES - 1]); // Call the xOrOperation
            }
            wordArray[i] = xOrOperation(wordArray[i-INT_BYTES], finalString); // Call the xOrOperation
        }

        System.out.println("\n\nAdvanced Security Lab 3\n");
        System.out.println("Program developed by:");
        System.out.println("Name: Eryk Szlachekta");
        System.out.println("Student No: C14386641");
        System.out.println("Course: DT282/4\n");


        // Iterate 44 times to print the wordArray
        for (int i = INT_ZERO; i < INT_KEYWORDS; i++){
            System.out.println("_____________________________");
            System.out.print("KeyWord " + i + " ==> " + wordArray[i] + "\n");
            System.out.println("-----------------------------");
        }
    }

    // RotWordOperation takes one parameter: the string.
    // It splits the string in spaces,
    // then returns a string composed of swapped bytes
    public static String RotWordOperation(String tempFinal){
        String bytes[] = tempFinal.split(" ");
        return (bytes[INT_ONE] + " " + bytes[INT_TWO] + " " + bytes[INT_THREE] + " " + bytes[INT_ZERO]);
    }

    // SubWordOperation takes one parameter: the string.
    // The "S-Box', swaps the values with the values in the S-Box table.
    public static String SubWordOperation(String rotString){
        String bytes[] = rotString.split(" "); // Split the string
        StringBuilder sb = new StringBuilder();      // Declare string builder
        // Iterate 4 times
        for (int i = INT_ZERO; i < INT_BYTES; i++){
            int decimal = Integer.parseInt(bytes[i],INT_RADIX); // Parse the string as int
            int sub = sboxTable[decimal & 0xff]; // Get int out of sboxTable using format 0xff
            sb.append(Integer.toHexString(sub)); // Append the 'sub' int as HexString to the string builder
            // If i < 3
            if(i < INT_THREE){
                sb.append(" ");
            }
        }
        return String.valueOf(sb);
    }

    // xOrOperation takes two parameters, the sub string and the rcon String
    public static String xOrOperation(String sub, String rconString){
        String subbytes[] = sub.split(" "); // Split the sub string
        String rbytes[] = rconString.split(" "); // Split the rcon String
        // Declare new string builder
        StringBuilder sb = new StringBuilder();
        // Iterate 4 times
        for(int i = INT_ZERO; i < INT_BYTES; i++){
            // ParseInt of the subbytes array at position i and xOr ( ^ ) with parse rbytes array at position i
            int res = Integer.parseInt(subbytes[i], INT_RADIX) ^ Integer.parseInt(rbytes[i],INT_RADIX);
            sb.append(Integer.toHexString(res)); // append to the string builder

            // Append space if its less than three
            if(i < INT_THREE){
                sb.append(" ");
            }
        }
        return String.valueOf(sb);
    }
}
