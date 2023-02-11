import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MinotaurBirthdayParty {
    // Number of guests (threads) at the party 
    public static final int NUM_GUESTS = 1000;

    public static boolean cupcakeAvailable = true;
    public static int numGuestsEatenCupcake = 0;

    public static void main(String[] args) {
        for (int i = 0; i < NUM_GUESTS; i++) {
            Guest guest = new Guest(i);
            guest.start(); 
        }

        while (numGuestsEatenCupcake < NUM_GUESTS) {
            ;
        }

        System.out.println("All " + NUM_GUESTS + " have eaten a cupcake.");
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