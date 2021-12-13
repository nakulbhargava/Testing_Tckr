package Backtesting;
public class AnotherTestShutDownHook {
    static int        a          = 0;           /* should end up 0 */
    static boolean    go         = true;        /* signaled to stop */
    static Thread     mainThread = Thread.currentThread();

    private static void trySleep ( int msecs ) {
        try { Thread.sleep(msecs); } catch (InterruptedException e) {}
    }

    private static void tryJoin ( Thread t ) {
        try { t.join(); } catch (InterruptedException e) {}
    }

    /*
     * this simulates a process that we want to do completely
     * or not at all.
     */
    private static void doitall () {
        System.out.println("start");
        ++a;
        trySleep(5000);
        System.out.println("end");
        --a;
    }

    /*
     * starts a thread to wait for a shutdown signal,
     * then does units of work until told to stop
     */
    public static void main(String[] args) {  
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Shutdown coming...");
                go = false; 
                tryJoin(mainThread);        // wait for mainThread to exit
                System.out.println("Shutdown hook! " + a);
            }       
        }); 
        while (go) {
            doitall();
        }   
        System.out.println("goodbye");
    }
}