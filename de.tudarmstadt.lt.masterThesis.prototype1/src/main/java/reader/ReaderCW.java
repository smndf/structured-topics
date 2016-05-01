package reader;

import graph.ArrayBackedGraph;
import graph.BuildGraph;
import graph.ClusterMetrics;
import graph.ResultatBuildGraph;
import graph.ClusterMetrics.TrianglesAndTriplets;
import graph.Graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cw.CW;
import util.FileUtil;
import util.MapUtil;
import util.MonitoredFileReader;

public class ReaderCW {

	@SuppressWarnings({ "deprecation", "static-access" })
	public static void main(String[] args) throws IOException {

		CommandLineParser clParser = new BasicParser();
		Options options = new Options();
		options.addOption(OptionBuilder.withArgName("file").hasArg()
				.withDescription("input ddt-file: word0#POS#0	word1#POS#0:0.54,word2#POS#3:0.44,word3#POS#1:0.41").isRequired().create("in"));
		options.addOption(OptionBuilder.withArgName("file").hasArg()
				.withDescription("name of output file, default = \"CW\"+input file ")
				.create("out"));
		options.addOption(
				OptionBuilder.withArgName("integer").hasArg()
				.withDescription(
						"max. number of edges to process for each word (word subgraph connectivity)")
						.create("n"));
		options.addOption(OptionBuilder.withArgName("float").hasArg().withDescription("min. edge weight").create("ew"));
		CommandLine cl = null;
		boolean success = false;
		try {
			cl = clParser.parse(options, args);
			success = true;
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		if (!success) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar cw.jar", options, true);
			System.exit(1);
		}
		String inputFile = cl.getOptionValue("in");
		String outputFile = cl.hasOption("out") ? cl.getOptionValue("out") : 
			"CW" + inputFile.split("/")[inputFile.split("/").length-1].substring(0, inputFile.length());
		double minEdgeWeight = cl.hasOption("ew") ? Double.parseDouble(cl.getOptionValue("ew")) : 0.0;
		int nbMaxEdgesPerNode = cl.hasOption("n") ? Integer.parseInt(cl.getOptionValue("n")) : Integer.MAX_VALUE;


		System.out.println("fileName = " + inputFile);
		ResultatBuildGraph graph = BuildGraph.buildGraph(inputFile, nbMaxEdgesPerNode, minEdgeWeight);
		CW<Integer> cw = new CW<Integer>();
		Map<Integer, Set<Integer>> clusters = cw.findClusters(graph.getG());
		System.out.println(clusters.size() + " clusters.");
		writeClustersIntoFile(clusters, graph, outputFile);


		//System.out.println("Use:\n - 1st argument (mandatory) = ddt file name\n - 2nd argument (facultative) = max degree of nodes (default infinite)");

	}

	private static void writeClustersIntoFile(
			Map<Integer, Set<Integer>> clusters, ResultatBuildGraph graph,
			String outputFile) {
		File fo = new File(outputFile);
		try{
			FileWriter fw = new FileWriter (fo);
			//fw.write (MapUtil.toString(clusters,trianglesTriplets,nodesMapItoS,eigenvectorCentralityScores,"\t","\n"));
			fw.write (MapUtil.toString(graph,clusters,"\t","\n"));
			fw.close();
			System.out.println("results written in " + outputFile);
		}
		catch (IOException exception)
		{
			System.out.println (exception.getMessage());
		}		
	}
}

