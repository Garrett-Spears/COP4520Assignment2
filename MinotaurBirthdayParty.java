import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MinotaurBirthdayParty {
    // Number of guests (threads) at the party 
    public static final int NUM_GUESTS = 100;

    public static boolean cupcakeAvailable = true;
    public static int numGuestsEatenCupcake = 0;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        List<Guest> guests = new ArrayList<>();

        // Start all guest threads
        for (int i = 0; i < NUM_GUESTS; i++) {
            Guest guest = new Guest(i);
            guests.add(guest);
            guest.start(); 
        }

        // Join all guest threads to ensure all threads finish
        try {
            for (Guest guest : guests) {
                guest.join();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();

        // Calculate Execution Time
        double elapsedSeconds = (endTime - startTime) / 1000.0;

        System.out.println("All " + NUM_GUESTS + " have eaten a cupcake within " +  elapsedSeconds + " seconds.");
    }
}

class Guest extends Thread {
    private boolean eatenCupcake;
    private Lock lock;

    public Guest(int guestId) {
        this.eatenCupcake = false;
        this.lock = new ReentrantLock();
    }

    @Override
    public void run() {
        while (MinotaurBirthdayParty.numGuestsEatenCupcake < MinotaurBirthdayParty.NUM_GUESTS) {
            // Use lock since only one guest can enter the maze at a time
            lock.lock();
            try {
                // If cupcake is not at the end of the maze, get servants to refill the cupcake immediately
                if (!MinotaurBirthdayParty.cupcakeAvailable) {
                    MinotaurBirthdayParty.cupcakeAvailable = true;
                }
                // This guest has not eaten a cupcake yet and the cupcake is available, so guest should eat the
                // cupcake and increment the counter for the number of guests that have eaten a cupcake
                else if (!eatenCupcake) {
                    this.eatenCupcake = true;
                    MinotaurBirthdayParty.numGuestsEatenCupcake++;
                }
                // If cupcake is available but guest has already eaten it, the current guest should leave
                // the cupcake there so that other guests can have their chance to eat one
            }
            finally {
                // Guest leaves the maze, so release the lock so that the Minotaur may choose other guests to enter
                lock.unlock();
            }
        }
    }
}