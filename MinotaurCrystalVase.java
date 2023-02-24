import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MinotaurCrystalVase {
    // Number of guests (threads) at the party
    public static final int NUM_GUESTS = 100;

    // Boolean flag that decides whether or not each guest's visit to the vase should be printed out
    public static final boolean PRINT_VASE_VISITORS = true;

    // Thread-safe queue that decides order of guests that will visit the vase one at a time. Capacity of this queue
    // is equal to the total number of guests since there will never be more than the total number of guests at the party in this
    // queue at any time.
    public static BlockingQueue<Guest> queueForVase = new ArrayBlockingQueue<>(NUM_GUESTS);

    // Thread-safe counter that keeps track of how many guests have viewed the vase currently
    public static AtomicInteger numGuestsVisitedVase = new AtomicInteger(0);

    public static void main(String[] args) {
        // Start all guest threads
        for (int i = 0; i < NUM_GUESTS; i++) {
            Guest guest = new Guest(i);
            guest.start();
        }

        long startTime = System.currentTimeMillis();

        // Keep the showroom open for all guests to keep viewing while there are still guests at the party
        while (Thread.activeCount() > 1) {
            // There are currently still guests at the party who have not seen the vase, but no one is currently in line, 
            // so keep the showroom open and continue operation
            if (queueForVase.isEmpty()) {
                continue;
            }

            // Last guest notifies next person in line that showroom is available, so next guest is grabbed from the front of the queue and
            // enters the showroom to see the vase
            Guest currentVaseVisitor = queueForVase.poll();

            // If this is the current guest's first time visiting the vase, then increment the counter keeping track of how
            // many guests have seen the vase by one
            if (currentVaseVisitor.timesVisitedVase == 0) {
                numGuestsVisitedVase.incrementAndGet();
            }

            // Current guest in showroom prints what number visit they are making to see the vase if print flag is enabled
            if (PRINT_VASE_VISITORS) {
                    System.out.println("Guest " + currentVaseVisitor.guestId + " is making their visit number " + (currentVaseVisitor.timesVisitedVase + 1) + " to see the vase.");
            }

            // Guest is done visiting the vase and exits the showroom, so let the guest be free from the line until 
            // they make their decision on whether or not to join the line again
            currentVaseVisitor.inLine.set(false);
        }

        long endTime = System.currentTimeMillis();

        // Calculate Execution Time
        double elapsedSeconds = (endTime - startTime) / 1000.0;

        System.out.println("All " + NUM_GUESTS + " guests got to see the vase in " + elapsedSeconds + " seconds.");
    }
}

class Guest extends Thread {
    // Defines how likely each guest is to join/rejoin the line to see the vase, each time they make a decision
    private static int PERCENT_CHANCE_TO_QUEUE = 50;

    // Each guest is assigned their own unique identifier and keeps track of how many times 
    // they have visited the vase already
    public int guestId;
    public int timesVisitedVase;

    // Tracks whether or not guest is currently in line/showroom
    public AtomicBoolean inLine;

    public Guest(int guestId) {
        this.guestId = guestId;
        this.timesVisitedVase = 0;
        this.inLine = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        // Guest should keep making decisions to join/rejoin the line while there are still some guests that have not seen the vase yet.
        // Once all guests have seen the vase, then all guests can decide it's time to leave.
        while (MinotaurCrystalVase.numGuestsVisitedVase.get() < MinotaurCrystalVase.NUM_GUESTS) {
            // Get a radom integer between 0 and 99 from thread-safe random number generator to represent their decision to
            // initially join the queue to view the vase
            int randInt = ThreadLocalRandom.current().nextInt(100);

            // If guest's random decision is not within their percentage of willingless to join/rejoin the queue, then guest
            // takes a short break before trying again
            if (randInt >= PERCENT_CHANCE_TO_QUEUE) {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                // Have guest join the back of the queue and make guest's state trasition to in line
                inLine.set(true);
                MinotaurCrystalVase.queueForVase.offer(this);

                // Have guest keep waiting until they are finally able to leave the line
                // and are done viewing the vase
                while (inLine.get()) {
                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Guest just left showroom and visited vase, so increment the current guest's number of vase visits by 1
                this.timesVisitedVase++;
            }
        }
    }
}