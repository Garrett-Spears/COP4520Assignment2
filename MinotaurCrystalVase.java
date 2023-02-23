import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class MinotaurCrystalVase {
    // Number of guests (threads) at the party
    public static final int NUM_GUESTS = 100;

    // Boolean flag that decides whether or not each guest's visit to the vase should be printed out
    public static final boolean PRINT_VASE_VISITORS = true;

    // Thread-safe queue that decides order of guests that will visit the vase one at a time
    public static BlockingQueue<Guest> queueForVase = new ArrayBlockingQueue<>(NUM_GUESTS);

    public static void main(String[] args) {
        // Start all guest threads
        for (int i = 0; i < NUM_GUESTS; i++) {
            Guest guest = new Guest(i);
            guest.start();
        }

        long startTime = System.currentTimeMillis();

        // Keep the showroom open for all guests to keep viewing while there are still guests at the party (main thread is not counted as a guest)
        while (Thread.activeCount() > 1) {
            // There are currently still guests at the party, but no one is currently in line, so keep the showroom open and continue operation
            if (queueForVase.isEmpty()) {
                continue;
            }

            // Last guest notifies next person in line that showroom is available, so next guest is grabbed from the front of the queue and
            // enters the showroom to see the vase
            Guest currentVaseVisitor = queueForVase.poll();

            // Current guest in showroom prints what number visit they are making to see the vase if print flag is enabled
            if (PRINT_VASE_VISITORS) {
                if (currentVaseVisitor.timesVisitedVase == 0) {
                    System.out.println("Guest " + currentVaseVisitor.guestId + " is visiting the vase for the first time.");
                }
                else {
                    System.out.println("Guest " + currentVaseVisitor.guestId + " is making their visit number " + (currentVaseVisitor.timesVisitedVase + 1) + " to see the vase.");
                }
            }

            // Guest is done visiting the vase and exits the showroom, so let the guest be free from the line until 
            // they make their decision on whether or not to join the line again
            currentVaseVisitor.inLine.set(false);
        }

        long endTime = System.currentTimeMillis();

        // Calculate Execution Time
        double elapsedSeconds = (endTime - startTime) / 1000.0;

        System.out.println("All " + NUM_GUESTS + " guests got to see the vase as many times as they wanted in " + elapsedSeconds + " seconds.");
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
        // Get a radom integer between 0 and 99 from thread-safe random number generator to represent their decision to
        // initially join the queue to view the vase
        int randInt = ThreadLocalRandom.current().nextInt(100);

        // If guest's random decision is within their percentage of willingless to join/rejoin the queue, then guest
        // stays at the party and joins the queue
        while (randInt < PERCENT_CHANCE_TO_QUEUE) {
            // Have guest joing the back of the queue and make guest's state trasition to in line
            inLine.set(true);
            MinotaurCrystalVase.queueForVase.offer(this);

            // Have guest keep waiting until they are finally able to leave the line
            // and view the vase
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

            // Now that guest is out of line, let guest make a random decision on whether to join the line to see the vase again.
            // If not, the guest will decide to leave the party and the thread will stop executing for this guest.
            randInt = ThreadLocalRandom.current().nextInt(100);
        }
    }
}