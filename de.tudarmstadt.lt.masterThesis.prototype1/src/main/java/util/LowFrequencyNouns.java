package util;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import frequency.FrequentWords;

public class LowFrequencyNouns {

	public static void main(String[] args) {

		Map<String,Integer> freqMap;
		Integer minFreq = 10;
		Integer maxFreq = 20;
		try {
			freqMap = FrequentWords.buildFreqMap("/news100M_stanford_cc_word_count");
			Set<String> lowFrequencyNouns = new HashSet<String>();
			for (Entry<String, Integer> entry : freqMap.entrySet()){
				String word = entry.getKey();
				Integer freq = entry.getValue();
				String pos = word.split("#")[word.split("#").length - 1];
				if (pos.equals("NN") || pos.equals("NP")){
					if (freq < maxFreq && freq > minFreq){
						lowFrequencyNouns.add(word.substring(0, word.length() - 1 - pos.length()));
					}
				}
			}
			
			for (String word : lowFrequencyNouns){
				System.out.println(word);
			}
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
