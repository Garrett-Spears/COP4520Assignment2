# COP4520Assignment2
Since I am most familiar with Java and had success with it during the first assignment, I decided to utilize this language again for this assignment.

# Problem 1: Minotaur Birthday Party

## Approach:

In my implementation of problem 1, the program starts at the "MinotaurBirthdayParty" class. At the top of this class, there is a static variable "NUM_GUESTS" which defines the number of guests that are attending the party. This variable can be modified to run problem 1 on different input sizes. The "MinotaurBirthdayParty" class essentially represents the Minotaur's house where the party is being thrown. This means that the class also contains the labyrinth and all of its elements.

In the "MinotaurBirthdayParty" class, there is an AtomicBoolean that keeps track of whether or not the labyrinth is currently empty. This lets the Minotaur know whether or not he needs to choose a new guest to enter the labyrinth. The class also contains a boolean field "cupcakeAvailable" that keeps track of whether or not there is currently a cupcake at the end of the labyrinth. There is no need for this field to be an atomic since only one guest will be able to enter the labyrinth at a time and modify this variable. The "MinotaurBirthdayParty" also contains an AtomicInteger field called "guestChosen" which specifies the current guest that the Minotaur has chosen to enter the labyrinth. 

The guests decide that in order to ensure that all guests have eaten a cupcake, they can each add onto a shared counter the first time they eat a cupcake until this counter becomes equal to the number of guests at the party. The Minotaur can then be notified that all guests have eaten a cupcake at this point. In the birthday party scenario, this counter can be seen as each guest telling the Minotaur that they have eaten their first cupcake after leaving the labyrinth. In the problem description it only states that guests may not talk to each other during the party, but nothing is said about guests being able to notify the Minotaur of what they have done in the labyrinth. 

In my implementation of problem 1, the Minotaur is represented by the main thread and all other threads created are for the guests. To represent each guest at the party, I created a class called "Guest" that extends the base Thread class in Java. Each instance of "Guest" is assigned a unique guest id and has its own boolean that keeps track of whether or not this guest has eaten a cupcake yet. 

At the beginning of the main thread's execution all "Guest" threads are spawned. The Minotaur (main thread) keeps the party running until he finds out that all guests have eaten a cupcake through the shared counter. While the party is running, the Minotaur checks to see if the labyrinth is empty. If it is, then the Minotaur chooses a new guest randomly out of all threads to enter the maze next. If it is not empty, then the Minotaur waits a brief time before checking the labyrinth again.

Each "Guest" continously runs until all guests have gotten a chance to eat a cupcake. While running, a "Guest" thread checks to see if they have been chosen by the Minotaur to enter the labyrinth. If not the guest waits briefly before checking again. However, if the guest has been chosen by the Minotaur, then the guest enters the labyrinth by using a lock to ensure that they are the only guest in the labyrinth at that time. Upon entering the labyrinth, the guest checks to see if there is a cupcake available. If not, the guest requests that the servants bring a new one. If the guest has not had the chance to eat a cupake yet, then the guest eats the cupcake in the labyrinth and increments the shared counter of all guests who have eaten the cupcake in the labyrinth. Otherwise, the guest leaves the cupcake for the next guest who has not had the chance to eat one yet. Finally, the guest leaves the labyrinth. Upon doing so, the guest releases the labyrinth lock and notifies the Minotaur that the labyrinth is now empty and that he must choose the next guest.
Once all guests have eaten a cupcake, all threads finish executing and the runtime of the program is printed out.

## Generating Output:

I calculate the execution time of the program by recording the time right before the Minotaur (main thread) starts choosing guest to enter the labyrinth and right after the Minotaur finishes choosing guests. At the end of the main thread's execution, it is printed out that all guests have had eaten a cupcake along with how long it took for this to occur. 

At the top of the "MinotaurBirthdayParty" class, there is a print flag that enables the printing out of individual events such as each random Minotaur decisions and whether or not each guests eats or leaves the cupcake when they are inside the labyrinth. These print statements help to give a sequential history of what guests are chosen and journey through the labyrinth during the party. However, this print flag can be set to false if only the final print that all guests have eaten a cupcake is desired to be printed out.

## Design Correctness/Efficiency: 

It is difficult to say the average runtime it takes for this problem, since the randomness of the implementation causes the numbers to vary quite a bit. However, my program is able to consistently run under 10 seconds, with or without printing, when dealing with 100 guests. To avoid too much contention between threads for shared resources, I put some very brief waits right after the Minotaur finds out the labyrinth is occupied before trying again and also right after each guest thread finds out they have not currently been chosen by the Minotaur before checking again. These very brief waits hlped to recduce contention and make my program more efficient.

To ensure that only one guest can enter the labyrinth at a time I put a lock() around this section of code. Since the "cupcakeAvailable" boolean is only modified and checked inside of the labyrinth, there was no need to make it an AtomicBoolean since only one guest thread could possibly access it at any time. However, all the other shared resources accross threads, such as the cupcakes eaten counter or the empty labyrinth flag, were made atomic. This ensured that each thread would only be able to modify these shared resources one at a time and have access to the most up to date information on what is happening in the program.
    
## Experimental Evaluation:

To ensure my program was working properly at first, I ran my program on small inputs of number of guests and checked that the history of eac random Minotaur decision and each guest's journey into the labyrinth were valid. I also ensured that each individual guest printed out that they had eaten a cupcake before the program ended. After confirming my output was valid, I decided to run my program on varying numbers of input and seeing how efficiently it performed. However, since the Minotaur's decisions are random, these runtimes vary by quite a bit. I decided to test three input sizes and run 5 trials for each. The results of these trials are listed below.

    NUM_GUESTS = 10:
        With Print Steps:
            Trial 1: 0.153s
            Trial 2: 0.163s
            Trial 3: 0.134s
            Trial 4: 0.193s
            Trial 5: 0.364s
            Average: 0.201s
        Without Print Steps:
            Trial 1: 0.169s
            Trial 2: 0.210s
            Trial 3: 0.162s
            Trial 4: 0.168s
            Trial 5: 0.170s
            Average: 0.175s

    NUM_GUESTS = 50:
        With Print Steps:
            Trial 1: 1.269s
            Trial 2: 2.420s
            Trial 3: 1.565s
            Trial 4: 1.326s
            Trial 5: 2.423s
            Average: 1.800s
        Without Print Steps:
            Trial 1: 1.776s
            Trial 2: 1.966s
            Trial 3: 1.119s
            Trial 4: 0.823s
            Trial 5: 1.933s
            Average: 1.523s

    NUM_GUESTS = 100:
        With Print Steps:
            Trial 1: 3.322s
            Trial 2: 3.916s
            Trial 3: 4.209s
            Trial 4: 3.687s
            Trial 5: 6.902s
            Average: 4.407s
        Without Print Steps:
            Trial 1: 3.088s
            Trial 2: 2.495s
            Trial 3: 3.446s
            Trial 4: 3.984s
            Trial 5: 2.511s
            Average: 3.105s

## To Run Problem 1:

Before running, you can modify the number of guests/threads for the problem by changing the value of the NUM_GUESTS field at the top of the
MinotaurBirthdayParty class. Can also enable/disable printing of each step in the program by changing the value of the PRINT_EVENTS boolean
flag, which is also found at the top of the MinotaurBirthdayParty class. To run the program:
    1. Use the command prompt to navigate to the directory where the MinotaurBirthdayParty.java file is located.
    2. Enter the command "javac MinotaurBirthdayParty.java" on the command line to compile the java source code.
    3. Enter the command "java MinotaurBirthdayParty" on the command line to execute the code.
    4. Output for the program is printed to the command line.


# Problem 2: Minotaur Crystal Vase

## Choosing an Approach

### Approach 1

This approach feels like the most hectic out of the 3 given approaches. One advantage of this approach is that each guest can freely roam the castle until they choose to visit the vase. Another pro is that a guest may be able to visit a vase immediately with no waiting time even if they are not the first ones thery're waiting to get in the showroom. There are quite a few disadvantages to this approach. The first of these being that all the guests will crowd the showroom fighting for access. Additionally, no guest is guaranteed that they'll be able to see the vase and a guest may have to wait a long time to visit the vase even if they are one of the first ones by the showroom waiting for access.

### Approach 2

I believe that the second approach is definitely better than the first one. There will not be the issue of crowding the room and trying to gain access to the showroom since the sign will keep guests from fighting to gain access until the room is available. However, this approach still struggles from many of the same issues as the first approach. In this approach, no guest is guaranteed they'll be able to see the vase since other guest may be able to just keep gaining access to the room repeatedly. Additionally, a guest may keep checking the sign continously, but someone who is checking the sign their first time may be able to get access first which makes things pretty unfair.

### Approach 3

I believe that the third approach is the best all around approach, so I chose to go with this one. The only issue I see with this approach is that guests may have to waste their time in really long lines if the demand is high to visit the vase. However, this approach is a lot more fair since every guest who wants to see the vase will get the chance to do so. Additionally, there is less crowding around the showroom with this approach since the guests are waiting in a well formed line instead of all trying to continously check if the room is open. 

## Chosen Approach

As I previously mentioned, I decided to use the third approach to solve problem 2. In my implementation of problem 2, the program starts at the "MinotaurCrystalVase" class. At the top of this class, there is a static variable "NUM_GUESTS" which defines the number of guests that are attending the party. This variable can be modified to run problem 2 on different input sizes. The "MinotaurCrystalVase" class essentially represents the showroom that the guests are trying to visit.

To follow the design of approach 3, I created a BlockingQueue that holds guests/threads that are waiting to visit the vase. This queue is thread safe such that the main thread can safely remove guests from the front of the queue while each guest thread can add themselves to the back of the queue if they so choose to. In the "MinotaurCrystalVase" class, there is also an AtomicInteger counter called "numGuestsVisitedVase" that keeps track of how many guests have currrently visited the vase at least once.

At the beginning of my main thread's execution, all "Guest" threads are spawned and start deciding on whether or not to join the queue to see the vase. The main thread keeps the showroom open while there are still active "Guest" threads, meaning that there still might be people at the party that want to visit the vase.
On each iteration of the main thread's while loop, it checks if the queue is empty and takes a brief break before checking again if so. Otherwise the Guest at the front of the queue is pulled out of the queue and given the chance to visit the showroom. If it is the guest's first time in the showroom, then he/she increments the shared counter of how many guests have seen the vase so far. Once the guest is done visiting the vase, the guest's inLine state is reset to false so that they can choose whether or not they'd like to rejoin the queue again on their own will. Then, the guest notifies, the next guest in line that it's their turn on the next iteration of the loop. Once all guest threads, leave the party, the program prints how long it took all guests to visit the vase.

Each guest thread is assigned their own unique identifier and keeps track of how many times they have visited the vase so far. A "Guest" thread keeps executing while there are still other guests that have not seen the vase yet. The guest continously makes a random decision on whether or not they would like to join/rejoin the queue which is evaluated based on the percentage chance that all guests are assigned in the Guest class. If the guest decides not to join the queue, they take a brief break before making a random decision again. Otherwise the guest adds themselves to the back of the BlockingQueue and sets their state inLine to true. The guest then continously keeps waiting until their inLine state is set back to false. Then, the guest can decide whether or not to rejoin the queue.

## Design Correctness/Efficiency: 

The runtime for this problem seems to be a lot faster in comparison to problem 1, but I guarantee this largely depends on the percentage set for each guest's willingness to join/rejoin the queue. However, the randomness of this problem still causes the runtime to vary. Ultimately, my implementation for this problem seems to be very efficient since it is able to even handle relatively larger input cases like 1000 while still running very fase.

I made sure to use an ArrayBlockingQueue instead of a regular queue for my program, so that multiple threads can safely access the queue at the same time. I also utilized an AtomicInteger for the counter that keeps track of how many guests have seen the vase, so that all threads have access to the most up to date information on when all guest visit the vase so they can stop executing. In my program, I also print when each thread visits the vase, so I ran the program with some small input cases and verified that each unique guest thread was able to visit the vase before the program finished execution.

## Experimental Evaluation:

To ensure my program was working properly at first, I ran my program on small inputs of number of guests and checked that each unique guest printed out that they visited the vase before all threads finish their execution. After confirming my output was valid, I decided to evaluate how efficiently it is able to perform. However, since each guest makes random decisions, these runtimes vary a little bit. I decided to test one input size and ran 5 trials for it. I only chose to test for one input size since there was less variation in runtimes for this problem in comparison to problem 1. The results of these trials are listed below.

    NUM_GUESTS = 100:
        With Print Steps:
            Trial 1: 0.114s
            Trial 2: 0.103s
            Trial 3: 0.083s
            Trial 4: 0.080s
            Trial 5: 0.069
            Average: 0.088s
        Without Print Steps:
            Trial 1: 0.070s
            Trial 2: 0.105s
            Trial 3: 0.092s
            Trial 4: 0.069s
            Trial 5: 0.117s
            Average: 0.091s

## To Run Problem 2:

Before running, you can modify the number of guests/threads for the problem by changing the value of the NUM_GUESTS field at the top of the
MinotaurCrystalVase class. You can also enable/disable printing of each guest's visit to the showroom in by changing the value of the PRINT_VASE_VISITORS boolean
flag, which is also found at the top of the MinotaurCrystalVase class. To run the program:
    1. Use the command prompt to navigate to the directory where the MinotaurCrystalVase.java file is located.
    2. Enter the command "javac MinotaurCrystalVase.java" on the command line to compile the java source code.
    3. Enter the command "java MinotaurCrystalVase " on the command line to execute the code.
    4. Output for the program is printed to the command line.
