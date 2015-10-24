package de.tudarmstadt.lt.masterThesis.hypernyms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class IsasSearch {

	public void isasSearch(){

		String inputIsasFile = "/wikipedia.patterns_lemmatized";

		String outputFile = "clustersLMWithIsas.txt";

		Set<String> testSet = new HashSet<String>();
		testSet.add("Python#NP#5");
		testSet.add("Viper#NP#5");
		testSet.add("Snake#NP#5");


		InputStream is = getClass().getResourceAsStream(inputIsasFile);
		BufferedReader isasText = new BufferedReader(new InputStreamReader(is));
		String line = null;

		try {
			File fo = new File(outputFile);
			FileWriter fw = new FileWriter (fo);

			Set<String> isas = new HashSet<String>();
			for (String word : testSet){
				word = word.split("#")[0].toLowerCase();
				String letter = word.substring(0, 1);
				while ( (line = isasText.readLine()) != null){ 
					if (line.toLowerCase().startsWith(letter) && line.split(" ")[0].toLowerCase().equals(word.toLowerCase())){
						isas.add(line.split(" ")[2].split("\t")[0]);
					}
				}
			}
			System.out.println(isas.toString());
			fw.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		


	}

}
