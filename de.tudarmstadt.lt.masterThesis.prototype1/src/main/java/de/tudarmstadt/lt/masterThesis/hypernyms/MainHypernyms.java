package de.tudarmstadt.lt.masterThesis.hypernyms;

public class MainHypernyms 
{
    public static void main( String[] args )
    {
    	String inputFile = "/ClustersCWFilt.txt";
        HypernymsSearch hs = new HypernymsSearch();
        hs.wordNetSearch(inputFile);
    	//IsasSearch is = new IsasSearch();
    	//is.isasSearch();
    }
}
