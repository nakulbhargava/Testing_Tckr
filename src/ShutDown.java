import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ShutDown {
	static List<String> eodOrders = new ArrayList<String>();
	public static void main(String[] args) throws InterruptedException, IOException {
		//		Properties properties = new Properties();
		//		File file = new File("something.properties");
		//		FileInputStream fis = new FileInputStream(file);
		//		properties.load(fis);
		//		Iterator itr = properties.entrySet().iterator();
		//		while(itr.hasNext()){
		//			System.out.println(itr.next());
		//		};
		//		fis.close();	
		Thread[] tickerActivityThreads = new Thread[3];
		for(int j = 0 ; j < 3; j++) {
			tickerActivityThreads[j] = new Thread(String.valueOf(j)) {
				public void run(){
					try {
						List<String> tickerEODOrders = new ArrayList<String>();
						tickerEODOrders.add(String.valueOf(Integer.parseInt(this.getName()) + 1 ));
						tickerEODOrders.add(String.valueOf(Integer.parseInt(this.getName())*100));
						System.out.println(tickerEODOrders.toString());
						eodOrders.addAll(tickerEODOrders);	
					}
					catch(Exception e) {
						e.printStackTrace();
						System.out.println("Need to Handle Thread Exception!");
					}
				}
			};
		}	
		for(int j = 0 ; j < 3; j++) {
			tickerActivityThreads[j].start();
		}

		for(int j = 0 ; j < 3; j++) {
			tickerActivityThreads[j].join();
		}
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				System.out.println("Running Shutdown Thread Service!");
					for(String eodOrder : eodOrders) {
						System.out.println(eodOrder);
					}

			}
		});

		System.out.println(eodOrders.toString());
	}
}
