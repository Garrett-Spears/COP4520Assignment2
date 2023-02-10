public class MinotaurBirthdayParty {
    // Number of guests (threads) at the party 
    private static final int NUM_GUESTS = 10;

    public static boolean cupcakeAvailable = true;
    public static int numGuestsEatenCupcake = 0;

    public static void main(String[] args) {
        for (int i = 0; i < NUM_GUESTS; i++) {
            Guest guest = new Guest(i);
            guest.start(); 
        }
    }
}

class Guest extends Thread {
    // Unique identifier for each guest thread
    private static int guestId;

    public Guest(int guestId) {
        this.guestId = guestId;
    }

    @Override
    public void run() {
        System.out.println("Guest " + guestId + " ran");
    }
}