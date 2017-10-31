# Miller-Rabin algorithm

## Description.
An algorigthm to check if a given number ``n`` is <b>prime</b>.</br>
If the algorithm returns <b>inconclusive</b>, it means that ``n`` is not prime, </b> </br>
otherwise if algorithm returns <b>composite</b>, it means that ``n`` <b>might be</b> prime.

### PseudoCode

```
Test(n)
1. Find integers k, q, with k > 0, q odd, so that (n - 1 = (2^k)q;
2. Select a random integer a, 1 < a < n - 1;
3. If (a^q)mod n == 1 then return ("inconclusive");
4. For j = 0 to k - 1 do
4.1 If (a^(2^j)q)mod n = n - 1 then return ("inconclusive");
5. Return ("composite");
```

