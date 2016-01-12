package de.tudarmstadt.lt.masterThesis.fileFiltering;

import graph.BuildGraph;
import graph.ResultatBuildGraph;

import java.io.IOException;
import java.util.Map;

import reader.ReaderCW;
import dict.FrequentWords;


public class MainFiltering 
{
	public static void main( String[] args ) throws IOException
	{
		if (args.length<2){
			System.out.println("Not enough arguments : \nTypeOfFilter InputFileName\nor\nTypeOfFilter InputFileName BaseFileName");
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
				f2.filter(fileName,freqMap,50);
				break;
			case "fa1":  
				if (args.length<3){
					System.out.println("Not enough arguments : TypeOfFilter InputFileName BaseFileName");
				} else {
					fw = new FrequentWords();
					freqMap = fw.buildFreqMap("/news100M_stanford_cc_word_count");
					ResultatBuildGraph res = BuildGraph.buildGraph(args[2]);
					FilterAfterAlgo1 f3 = new FilterAfterAlgo1();
					f3.filter(fileName, res, freqMap, 50);
				}
				break;
			case "fa2":  
				if (args.length<3){
					System.out.println("Not enough arguments : TypeOfFilter InputFileName BaseFileName");
				} else {
					ResultatBuildGraph res = BuildGraph.buildGraph(args[2]);
					FilterAfterAlgo2 f4 = new FilterAfterAlgo2();
					f4.filter(fileName, res);
				}
				break;
			default : System.out.println("No correct argument (fb1,fb2,fa1 or fa2)");
			}
		}

	}
}
