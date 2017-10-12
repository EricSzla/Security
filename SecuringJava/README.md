# Signing Code and Granting It Permissions 

### 1.
In this scenario, Susan wishes to send code to Ray. Ray wants to ensure that when the</br>
code is received, it has not been tampered with along the way 
 - for instance someone could have intercepted the code exchange e-mail and replaced the code with a virus. </br>
 
- Create two directories named susan and ray. Extract the program Count.java and save it in 'susan'.</br>
- Create a file named data.txt and save it in the susan folder.</br>
- Run the Count program, which will count the number of characters in the file: </br> 

  ```java Count data.txt  (javac before required).```
  
### 2.  
Because Susan wants to send her data with authentication, she must create a public/private</br> 
key pair. The java keytool allows users to do this: </br>

```
keytool -genkey -alias signFiles -keypass kpi135 -keystore susanstore -storepass ab987c 
```

Enter all the required information for Susan. The keytool will create a certificate that </br>
contains Susan's **public key** and all her details. </br>

This certificate will be valid for the default validity period if you don't specify a validity option. </br>
The certificate is associated with the **private key** in a **keystore entry** referred to by the alias </br>
signFiles. The private key is assigned the **password kpi135**.</br>

### 3.
Susan now wants to digitally sign the code to send it to Ray. The first step is to put </br>
the code into a JAR file: 

```jar cvf Count.jar Count.class ```

Then the **jarsigner tool** can be used to sign the JAR: </br>

```jarsigner -keystore susanstore -signedjar sCount.jar Count.jar signFiles ```

You will be prompted for the store password (ab987c) and the private key password (kpi135). </br>

### 4.
Susan can now export a copy of her certificate and send this with the signed JAR to Ray: 

``` keytool -export -keystore susanstore -alias signFiles –file SusanJones.cer ```

The certificate will be in the file ***SusanJones.cer*** - this contains her public key which Ray </br>
can use to authenticate the origin of the file he received. </br>

### 5.    
Copy (simulated e-mail) the file **SusanJones.cer** and the signed JAR **sCount.jar** to the ray folder.</br>
Create or copy a file named data.txt to put in Ray's folder. Try to execute the code with the security manager in place: 

```java -Djava.security.manager -classpath sCount.jar Count data.txt ```

Note the ***AccessControlException*** - you are not permitted access the disk with the Security Manager in operation. 

### 6.
Ray will now create his own keystore (use any password you want), into which he will import Susan's details: 

```keytool -import -alias susan -file SusanJones.cer -keystore raystore ```

### 7.    
Ray can now verify that the code, sCount.jar was signed by Susan: 

```jarsigner -verify -verbose -keystore raystore sCount.jar ```

### 8.

Ray can also give any code signed by Susan permission to access certain files or perform </br>
operations it would not otherwise be permitted to do, by creating the following policy file </br>

```
keystore "raystore"; 
grant signedBy "susan" { 
    permission java.io.FilePermission "data.txt", "read"; 
};
```

And using this when running the code: 

``` java -Djava.security.manager -Djava.security.policy=raypolicy.policy -classpath sCount.jar Count data.txt ```


