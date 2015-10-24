package dict;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class FrequentWords {

	public Map<String,Integer> buildFreqMap(String fileName) throws IOException {

		
		System.out.print("Building frequency map...");
		InputStream is = getClass().getResourceAsStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String[] lineSplit = new String[4];
		Map<String,Integer> freqMap = new HashMap<String,Integer>();
		br.readLine();
		String line = null;
		String[] split = null;
		int i = 0;
		int linectr=0,linectr2 = 0;
		while ( (line = br.readLine()) != null){ 
			if ((split = line.split("\t"))!=null && split.length>1){
				freqMap.put(split[0], Integer.valueOf(split[1]));				
			}
		}
		System.out.println("OK");
	return freqMap;
	}
}
