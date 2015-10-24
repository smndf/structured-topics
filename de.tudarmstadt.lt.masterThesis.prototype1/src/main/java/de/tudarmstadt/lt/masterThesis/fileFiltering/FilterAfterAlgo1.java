package de.tudarmstadt.lt.masterThesis.fileFiltering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class FilterAfterAlgo1 {

	public void filter(String inputFile, Map<String, Integer> freqMap, int freqThreshold) {

		String outputFile = inputFile.substring(1, inputFile.length() - 4) + "Filtered.txt";
		File fo = new File(outputFile);

		InputStream is = getClass().getResourceAsStream(inputFile);
		BufferedReader input = new BufferedReader(new InputStreamReader(is));

		String[] lineSplit = new String[2];
		String line = null;

		try {

			FileWriter fw = new FileWriter (fo);
			StringWriter sw = new StringWriter();
			int ctr = 0;
			int nbWords = 0;
			while ( (line = input.readLine()) != null){ 
				lineSplit = line.split("\t");
				nbWords = 0;
				if (lineSplit.length>1){
					//System.out.println("dsfs");
					ArrayList<String> neighboursSplit = new ArrayList<String>(Arrays.asList(lineSplit[1].split(",")));
					sw.write(ctr + "\t");
					ctr++;
					for (String neighbour : neighboursSplit){
						//neighbours.add(neighbourSp.split(":")[0]);
						// if node already added							
						if (neighbour.split("#").length>1){
							String pos = neighbour.split("#")[1];		
							if (pos.equals("NN") || pos.equals("NP") || pos.equals("JJ") || pos.equals("RB")){	
								String term = neighbour.split("#")[0] + "#" + neighbour.split("#")[1];
								if (freqMap.containsKey(term) && freqMap.get(term) > freqThreshold){
									sw.write(neighbour + ",");	
									nbWords++;
								}
							}
						}
					}
				}
				if (nbWords>2){
					sw.getBuffer().setLength(sw.toString().length() - 2);
					fw.write (sw.toString() + "\n");
				} else {
					ctr--;
				}
				sw.getBuffer().setLength(0);
			}

			fw.close();
			sw.close();
			System.out.println("file modified, new file : " + outputFile);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}
