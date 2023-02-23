import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MinotaurBirthdayParty {
    // Number of guests (threads) at the party 
    public static final int NUM_GUESTS = 100;

    // Boolean flag that decides whether on not to print which guest is chosen to enter the labyrinth each time
    // and what each guest does while in the labyrinth (may slow down performance slightly)
    public static final boolean PRINT_EVENTS = true;

    // Represents whether or not cupcake is currently present in the labryinth. At the beginning
    // of the party there should be a cupcake available. Does not need to be atomic since only
    // one guest can be in the layrinth at a time to eat the cupcake.
    public static boolean cupcakeAvailable = true;

    // Boolean flag that notifies Minotaur of whether or not labyrinth is empty so that he 
    // knows whether or not to choose a new guest to enter. Whenever a guest leaves the labyrinth, they notify
    // the Minotaur by setting this flag to true.
    public static AtomicBoolean mazeIsEmpty = new AtomicBoolean(true);
    
    // Represents the random guest that the Minotaur decides to send into the labyrinth each time. Each guest will only
    // enter the labyrinth if they see that this integer is equal to their guest number.
    public static AtomicInteger guestChosen = new AtomicInteger(-1);

    // Before the party starts, all the guests decide to collectively add to a counter whenever each guest
    // is able to eat a cupcake in the labyrinth. Once the Mintaur is notified that this counter equals the number of 
    // guests at the party, he can stop choosing guests to send into the labyrinth.
    public static AtomicInteger numGuestsEatenCupcake = new AtomicInteger(0);

    public static void main(String[] args) {
        // Start all guest threads
        for (int i = 0; i < NUM_GUESTS; i++) {
            Guest guest = new Guest(i);
            guest.start(); 
        }

        Random rand = new Random();
        long startTime = System.currentTimeMillis();

        // Minotaur should keep picking guests at random until notified that all guests have gotten the chance to eat a cupcake
        while (numGuestsEatenCupcake.get() < NUM_GUESTS) {
            // No one is currently in the labyrinth so time to pick a guest to go next
            if (mazeIsEmpty.get()) {
                // Generates a random value on the range of 0 to (NUM_GUESTS - 1)
                int randGuestChosen = rand.nextInt(NUM_GUESTS);

                if (PRINT_EVENTS) {
                    System.out.println("Minotaur chose guest " + randGuestChosen + " to enter the maze.\n");
                }

                // Guest was chosen, so notify guests of this decision and set the
                // labyrinth to non-empty
                mazeIsEmpty.set(false);
                guestChosen.set(randGuestChosen);
            }
            else {
                // Labyrinth is currently occupied, so Mintaur can take a short break before checking again
                try {
                    Thread.sleep(5);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        long endTime = System.currentTimeMillis();

        // Calculate Execution Time
        double elapsedSeconds = (endTime - startTime) / 1000.0;

        System.out.println("All " + NUM_GUESTS + " have eaten a cupcake within " +  elapsedSeconds + " seconds.");
    }
}

class Guest extends Thread {
    // Each guest is assigned their own unique identifier and keeps track of whether or not they have
    // eaten a cupcake yet
    private int guestId;
    private boolean eatenCupcake;
    private Lock lock;

    public Guest(int guestId) {
        this.guestId = guestId;
        this.eatenCupcake = false;
        this.lock = new ReentrantLock();
    }

    @Override
    public void run() {
        // Guest should keep trying to see if its their turn to enter the labyrinth until announcement is 
        // made that all guests have eaten a cupcake
        while (MinotaurBirthdayParty.numGuestsEatenCupcake.get() < MinotaurBirthdayParty.NUM_GUESTS) {
            // If Minotaur has not currently chosen this guest to enter the labyrinth, then
            // this guest can take a short break before checking back again if its their turn 
            if (MinotaurBirthdayParty.guestChosen.get() != guestId) {
                try {
                    Thread.sleep(5);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                continue;
            }

            // Otherwise, it is now the guest's turn to enter the labyrinth
            if (MinotaurBirthdayParty.PRINT_EVENTS) {
                System.out.println("Guest " + guestId + " enters the maze.");
            }

            // Use a lock, just in case, since only one guest can enter the labyrinth at a time
            lock.lock();
            try {
                // If cupcake is not at the end of the maze, get servants to refill the cupcake immediately
                if (!MinotaurBirthdayParty.cupcakeAvailable) {

                    if (MinotaurBirthdayParty.PRINT_EVENTS) {
                        System.out.println("Cupcake was not available so guest requested refill from servants.");
                    }

                    MinotaurBirthdayParty.cupcakeAvailable = true;
                }

                // This guest has not eaten a cupcake yet and the cupcake is available, so guest should eat the
                // cupcake and increment the counter for the number of guests that have eaten a cupcake
                if (!eatenCupcake) {
                    if (MinotaurBirthdayParty.PRINT_EVENTS) {
                        System.out.println("Guest has not had a cupcake yet, so decided to eat it.");
                    }

                    this.eatenCupcake = true;
                    MinotaurBirthdayParty.cupcakeAvailable = false;
                    MinotaurBirthdayParty.numGuestsEatenCupcake.incrementAndGet();
                }
                // If cupcake is available but guest has already eaten it, the current guest should leave
                // the cupcake there so that other guests can have their chance to eat one without having 
                // to ask a servant to refill it
                else {
                    if (MinotaurBirthdayParty.PRINT_EVENTS) {
                        System.out.println("Guest has already had cupcake, so decided not to eat it.");
                    }
                }
            }
            finally {
                if (MinotaurBirthdayParty.PRINT_EVENTS) {
                    System.out.println("Guest " + guestId + " has left the maze.\n");
                }

                // Guest left the labyrinth, so release the lock so that the Minotaur may choose other guests to enter
                // and reset the Minotaur's decision so that the same guest doesn't re-enter the layrinth immediately
                MinotaurBirthdayParty.guestChosen.set(-1);
                lock.unlock();

                // After unlocking the labyrinth, guest can notify the minotaur that the maze is officially empty and
                // ready for another guest to be chosen
                MinotaurBirthdayParty.mazeIsEmpty.set(true);
            }
        }
    }
}
