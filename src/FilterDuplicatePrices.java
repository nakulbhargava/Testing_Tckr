import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class FilterDuplicatePrices {
	public static void main(String[] args) throws FileNotFoundException {
		List<String> ticker = null;
		List<Integer> tickerWithDuplicates = new ArrayList<Integer>();
		List<String> tickerWithoutDuplicates = new ArrayList<String>();
		FilterDuplicatePrices demo = new FilterDuplicatePrices();
		ticker = demo.readFileAsList();
	}
	private List<String> readFileAsList() {
		String fileName = "/test/US1.SNAP_210504_210504.txt";

		try {
			URI uri = this.getClass().getResource(fileName).toURI();
			List<String> lines = Files.readAllLines(Paths.get(uri),
					Charset.defaultCharset());
			List<String> ticker = new ArrayList();
			String priceToCompare = "0.0";
			for (String line : lines) {
				List<String> splitLine = Arrays.asList(line.split(",", -1));
				if(!priceToCompare.equals(splitLine.get(3))) {
					ticker.add(splitLine.get(3));
					priceToCompare = splitLine.get(3);
				}
			}
			return ticker;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
