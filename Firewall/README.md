# Firewall for an Enterprise System
<i>Copyright &#169;  2017 [Eryk Szlachetka](https://www.github.com/EricSzla/)</i>

<i>Roles:</i>

- <i>Eryk Szlachetka: 	Research, IpTables, Scripts, Kernel Module</i>
- <i>Pamela Sabio:		Research, IpTables, Scripts, Kernel Module</i>
- <i>Caoimhe Harvey:	Research, IpTables.</i>

## 1. Introduction

Setting up a firewall in the system is a very important security task, if the firewall is not set up correctly the whole enterprise can be compromised, if the system is compromised this could allow potential hacker to threaten the Confidentiality, Integrity and Availability of the system information.</br>
This would allow the attacker for various threats such as information surveillance, data alteration or unavailability of the system. 
Here I am focusing on enterprise that only needs access to Internet and Email using 
- Transmission Control Protocol (**TCP**)
- Simple Mail Transport Protocol (**SMTP**) </br>
</br>

The communication can happen only on specific ports such as 
- Hypertext Transfer Protocol over Transport Layer Security (HTTPS) 
- Secure Shell (SSH) 
- Domain Name Server (DNS) 
</br>

![figure1](https://user-images.githubusercontent.com/15609881/27061070-d54bc9c8-5013-11e7-95a9-824bbca453c7.png)</br>
<i>Figure 1: Enterprise Environment Design Figure, only Mail/Website specified protocols and ports are allowed to communicate through the firewall.</i>

There are two methods that can be used to implement the solution: 
1) Use IpTables that allows system administrator to specify the rules that each packet has to go through before allowing it in the Enterprise System.

2) Write Kernel Module to replace the IpTables and load the code that specifies the rules into the Kernel.
In this project both solutions are explained and implemented using Ubuntu 16.04, Linux.

## 2. IpTables

IPTables are used to implement the rules specified for the enterprise environment. 

### 2.1. Types of packets

There are two types of packets that are considered: 
- Input Packet – an external packet that arrives at the system designated for a client within the enterprise.
- Output Packet – an internal packet that is designated for the web.

### 2.2. Policy

IPTables policy decides what do with packets that do not match the rules specified. </br>
In order to improve overall security of the system by only allowing specific protocols and ports to communicate, the policy of IPTables is set to **STOLEN** for both the input and the output packets. 

### 2.3. Packet Processing

When the packet first goes into/outside the system, it has to follow specific path <i>(see Figure 2)</i>, it has to be checked for the protocol, if it matches protocol **TCP (6)** or protocol **UDP (17)** then it can be accepted. 
</br>Afterwards it has to be checked what port is the packet coming from, the only ports that are accepted are:

- SSH (22), 
- DNS (53), 
- HTTP (80), 
- HTTPS (443),
- SMTP (25).

All the other packets will be **STOLEN**, which means that there is no indication of what is happening with the packet, this approach is used instead of <i>DROP</i> in order to improve the security of the enterprise by avoiding giving clues to potential attacker what happened with the packet which could allow to discover out the rules set up. 
</br>After the port validation, packet is checked for forbidden destinations.

![Figure 2](https://user-images.githubusercontent.com/15609881/27061626-1bf5e3fa-5018-11e7-9767-e55055c310e6.png)

## 3. Kernel Module
The code inserted into the Kernel Module has an objective to replace the IPTables. The rules are the same as the one for IPTables <i>(see Section 2.3)</i>. 

### 3.1 Code structure

The code inserted into **Kernel** as a **Module** consists of the functions that are called depending on the action.</br>
The function called for the **kernel functionality** are: 
- ``init_module`` – called when ``insmod`` is used i.e. when the module is inserted into the kernel. 
- ``cleanup_module`` – called when ``rmmod`` is used i.e. when module is removed from the kernel .

**Note this functions are called only once.**

![Figure 3](https://user-images.githubusercontent.com/15609881/27061794-53012c28-5019-11e7-9a26-a142eece42ca.png)

All the rules that have been set are set in linked list that all functions have access to.
</br>The functions that are called for setting the packet processing rules functionality are:
- ``set_all_packets`` – this is a function which is called from init_module, it sets the packet rules and calls add_rule for each packet rule that have been set. 
- ``add_rule`` – function called by set_all_packets, it adds the rule to the actual rule linked list <i>(see Figure 4)</i>.

![Figure 4](https://user-images.githubusercontent.com/15609881/27061851-adf677dc-5019-11e7-972a-143da50de506.png)

There are two hook ops to which parameters are set in ``init_mod`` function:
- ``nfho_in`` – used for the incoming packet.
- ``nfho_out`` – used for the outgoing packet.

These hook ops are both set to **highest priority**, and are both set to **support IPV4 packets**. </br>
The two <i>hook ops</i> have different parameters set for the **hooknum** (<i>specifies when the hook is called e.g. straight after packet is received</i>):
- ``NF_INET_PRE_ROUTING`` for ``nfho_in``,
- ``NF_INET_POST_ROUTING`` for ``nfho_out``.

and the **hook** (<i>function which is called when the packet is received</i>) </br>
- ``hook_func_in`` for ``nfho_in``,
- ``hook_func_out`` for ``nfho_out``.

The two functions can be configured differently to do different task, in this case the two functions are configured to achieve the same functionality. </br>
The incoming packet is compared to known protocols such as **TCP/UDP**, if the packet uses a known, allowed protocol then the headers and ports are extracted and assigned to variables, otherwise the packet is **STOLEN**.</br>
If the packet gets through the protocol comparison, the iteration through the rules stored in the linked list (<i>see Figure 5</i>), first thing in the loop is to check if the rule is designated to deal with out or in packet, depending on the function call, action will be taken.<br>
**i.e.** If dealing with outgoing function and the rule is incoming then continue on to next rule, second thing to compare in the loop is the IP address to check if its not forbidden, third thing in the loop is to check if the destination port and source port of the packet matches the ports in the rule, if so then the rule is checked to decide on the action.<br>
**e.g.** if the rule specifies that HTTP port has to be accepted and the packet is from/to HTTP port then its accepted, if it specifies that HTTP port has to be stolen then it is stolen (<i>see Figure 6</i>).

![figure 5](https://user-images.githubusercontent.com/15609881/27061719-c7c7c6bc-5018-11e7-8d72-0ef9731e3aca.png)
</br>
![f6-1](https://user-images.githubusercontent.com/15609881/27062092-314e1cd8-501b-11e7-8d2d-a4cf8103c372.png)</br>
![f6-2](https://user-images.githubusercontent.com/15609881/27062090-313303c6-501b-11e7-92b9-3e190ffd50ed.png)</br>
![f6-3](https://user-images.githubusercontent.com/15609881/27062091-3147e3c2-501b-11e7-9f8f-fa9f8ab3a3ee.png)</br>

