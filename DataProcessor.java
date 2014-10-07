import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataProcessor {

	// add your directory path here
	public final static String fileDir = "";
	
	public static List<List<String>> importData(String fileName){
		List<List<String>> dataSet = new ArrayList<List<String>>();
		try {
			BufferedReader bf = new BufferedReader(new FileReader(fileDir + fileName));
			String line = bf.readLine();
			while (line != null) {
				String[] splits = line.split(" ");
				List<String> newData = new ArrayList<String>();
				newData.add(splits[0]);
				for (int i = 1; i <= 9; i++) {
					newData.add(splits[i].substring(2));
				}
				dataSet.add(newData);
				line = bf.readLine();
			}
			
			bf.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return dataSet;
	}
}
