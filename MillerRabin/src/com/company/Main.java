package com.company;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/* Code developed by Eryk Szlachetka
*  Implementation of Miller-Rabin algorithm,
*  to check if the number is prime or not.
*  If the algorithm returns Composite then the number is probably prime.
*  If the algorithm returns inconclusive, then it is not prime.
*/

public class Main {

    public static void main(String[] args) {

        String prime = "";
        long n;
        boolean isPrime;

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter a number to check if it is prime: ");
        n = scan.nextLong();
        System.out.println("Checking...\n");
        isPrime = checkIfPrime(n);

        if(isPrime){
            prime = "Composite";
        }else{
            prime = "Inconclusive";
        }

        System.out.println("The number: " + n + " is: " + prime);

    }

    private static boolean checkIfPrime(long n){

        // Return false if n is 0 or 1 as its not prime
        if(n == 0 || n == 1){
            return false;
        }
        // Return true if n is 2, as it is prime
        if(n == 2){
            return true;
        }
        // If its an even number other than 2 then return false.
        if(n % 2 == 0){
            return false;
        }


        // Find integers k, q with k > 0, q is odd so that (n - 1 = 2^k q)
        long q = n-1;
        int k = 0;

        while(q % 2 == 0){ // while q is odd
            q = q/2; // divide q by 2
            k++;     // increment k
        }
        // Select random integer a where 1 < a < n-1
        long a = ThreadLocalRandom.current().nextLong(1, (n-1));

        // If a^q mod n == 1 then return "inconclusive"
        if(((a^q) % n) == 1){
            return false;
        }

        for(int j =0; j < k-1; j++){
            // if a^((2^j)q) mod n == n - 1 then return "inconclusive"
            long power = 2^j * q;
            long res = a^power;

            if(res % n == n-1) {
                return false;
            }
        }
        // Return "composite"
        return true;
    }
}


