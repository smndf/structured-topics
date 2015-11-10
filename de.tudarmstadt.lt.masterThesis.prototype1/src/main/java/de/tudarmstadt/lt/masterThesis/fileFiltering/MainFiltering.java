package de.tudarmstadt.lt.masterThesis.fileFiltering;

import java.io.IOException;
import java.util.Map;

import reader.ReaderCW;
import dict.FrequentWords;


public class MainFiltering 
{
	public static void main( String[] args ) throws IOException
	{
		if (args.length<2){
			System.out.println("Not enough arguments : TypeOfFilter InputFileName");
		} else {
			Map<String,Integer> freqMap ;
			FrequentWords fw;
			String fileName = args[1];
			switch (args[0]) {
			case "fb1":  
				FilterBeforeAlgo1 f1 = new FilterBeforeAlgo1();
				f1.filter(fileName);
				break; 
			case "fb2":  
				fw = new FrequentWords();
				freqMap = fw.buildFreqMap("/news100M_stanford_cc_word_count");
				FilterBeforeAlgo2 f2 = new FilterBeforeAlgo2();
				f2.filter(fileName,freqMap,10);
				break;
			case "fa1":  
				fw = new FrequentWords();
				freqMap = fw.buildFreqMap("/news100M_stanford_cc_word_count");
				FilterAfterAlgo1 f3 = new FilterAfterAlgo1();
				f3.filter(fileName, freqMap, 10);
				break;
			default : System.out.println("No correct argument (fb1,fb2 or fa1)");
			}
		}

	}
}
