package de.tudarmstadt.lt.masterThesis.fileFiltering;

import java.io.IOException;
import java.util.Map;

import dict.FrequentWords;


public class MainFiltering 
{
    public static void main( String[] args ) throws IOException
    {
		String inputFile = "/ddt-news-n50-485k-closure.txt";
		//String inputFile = "/testReader.txt";
		
		/*
		FilterBeforeAlgo1 f1 = new FilterBeforeAlgo1();
		f1.filter(inputFile);
		*/

		FrequentWords fw = new FrequentWords();
		Map<String,Integer> freqMap = fw.buildFreqMap("/news100M_stanford_cc_word_count");
		
		FilterBeforeAlgo2 f2 = new FilterBeforeAlgo2();
		f2.filter(inputFile,freqMap,10);

		/*
		FilterAfterAlgo1 f3 = new FilterAfterAlgo1();
		f3.filter(inputFile, freqMap, 10);
		 */
    }
}
