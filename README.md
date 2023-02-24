# COP4520Assignment2
Since I am most familiar with Java and had success with it during the first assignment, I decided to utilize this language again for this assignment.

# Problem 1: MinotaurBirthdayParty
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