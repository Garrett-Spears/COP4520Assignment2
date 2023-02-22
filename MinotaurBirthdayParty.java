import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MinotaurBirthdayParty {
    // Number of guests (threads) at the party 
    public static final int NUM_GUESTS = 100;
    public static final boolean PRINT_EVENTS = true;

    public static boolean cupcakeAvailable = true;
    public static AtomicInteger guestChosen = new AtomicInteger(-1);
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

        // // Join all guest threads to ensure all threads finish
        // try {
        //     for (Guest guest : guests) {
        //         guest.join();
        //     }
        // }
        // catch (InterruptedException e) {
        //     e.printStackTrace();
        // }

        Random rand = new Random();

        while (numGuestsEatenCupcake < NUM_GUESTS) {
            if (guestChosen.get() == -1) {
                int randGuestChosen = rand.nextInt(NUM_GUESTS);

                if (PRINT_EVENTS) {
                    System.out.println("Minotaur chose guest " + randGuestChosen + " to enter the maze.\n");
                }

                guestChosen.set(randGuestChosen);
            }
            else {
                try {
                    Thread.sleep(100);
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
        while (MinotaurBirthdayParty.numGuestsEatenCupcake < MinotaurBirthdayParty.NUM_GUESTS) {
            if (MinotaurBirthdayParty.guestChosen.get() != guestId) {
                continue;
            }

            if (MinotaurBirthdayParty.PRINT_EVENTS) {
                System.out.println("Guest " + guestId + " enters the maze.");
            }

            // Use lock since only one guest can enter the maze at a time
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
                    MinotaurBirthdayParty.numGuestsEatenCupcake++;
                }
                // If cupcake is available but guest has already eaten it, the current guest should leave
                // the cupcake there so that other guests can have their chance to eat one
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

                MinotaurBirthdayParty.guestChosen.set(-1);
                // Guest leaves the maze, so release the lock so that the Minotaur may choose other guests to enter
                lock.unlock();
            }
        }
    }
}
