#!/bin/bash
if [ $# -eq 0 ] # no argument
then
	echo "need the file name"
else
	fileName=$1 
	echo "$fileName"
	suff1=.txt
	add=LM.txt
	fileNameRad=${fileName%$suff1}
	fileNameLM=${fileName%$suff1}$add

	cd
	cd Documents/workspace/structured-topics/

#echo "${fileNameRad}graph.bin"

	echo "java -jar /Users/simondif/Documents/workspace/structured-topics/lmConversion.jar bef $fileName"
	java -jar /Users/simondif/Documents/workspace/structured-topics/lmConversion.jar bef $fileName
	echo "./gen-louvain/convert -i $fileNameLM  -o ${fileNameRad}graph.bin -w ${fileNameRad}graph.weights"
	./gen-louvain/convert -i $fileNameLM  -o ${fileNameRad}graph.bin -w ${fileNameRad}graph.weights
	echo "./gen-louvain/louvain ${fileNameRad}graph.bin -l -1 -w ${fileNameRad}graph.weights > ${fileNameRad}graph.tree"
	./gen-louvain/louvain ${fileNameRad}graph.bin -l -1 -w ${fileNameRad}graph.weights > ${fileNameRad}graph.tree
	echo "./gen-louvain/hierarchy ${fileNameRad}graph.tree -l 1 > ${fileNameRad}clusters.txt"
	./gen-louvain/hierarchy ${fileNameRad}graph.tree -l 1 > ${fileNameRad}clusters.txt
	echo "java -jar /Users/simondif/Documents/workspace/structured-topics/lmConversion.jar aft ${fileNameRad}clusters.txt mapItoS$fileName LM$fileName"
	java -jar /Users/simondif/Documents/workspace/structured-topics/lmConversion.jar aft ${fileNameRad}clusters.txt mapItoS$fileName LM$fileName

	rm ${fileNameRad}graph.tree
	rm ${fileNameRad}graph.bin
	rm ${fileNameRad}graph.weights
	rm mapItoS$fileName
	rm $fileNameLM
	rm ${fileNameRad}clusters.txt
	
	scp LM$fileName simondif@frink.lt.informatik.tu-darmstadt.de:structured-topics/LM/

fi
