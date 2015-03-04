package ru.kamil.inno.ml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ExersiceML {
	
	public boolean DEBUG = false;
	
	public List<File> listFilesForFolder(final File folder) {
		List<File> list = new ArrayList<File>();
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            list.addAll(listFilesForFolder(fileEntry));
	        } else {
	        	if(DEBUG)
	        		System.err.println(fileEntry.getName());
	        	list.add(new File(fileEntry.getAbsolutePath()));
	        }
	    }
	    return list;
	}
	
	private HashSet<String> getWords(List<File> list){
		HashSet<String> words = new HashSet<String>();
		for(File f : list){
			String content = readFile(f);
			String[] terms = content.split(" ");
			for(String term : terms)
				words.add(term);
		}
		
		return words;
	}
	
	private String readFile(File f){
	    String res = "";
	    
	    try {
			for(String line : Files.readAllLines(f.toPath())){
				res += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    return res;
	}
	
	private double fiKY1(int k, int v, List<File> files, HashSet<String> words){
		String givenWord = (String)words.toArray()[k];
		int appear = 0;
		int totalWords = 0;
		for (int i = 0; i < files.size(); i++){
			String[] wordsInCurrFile = readFile(files.get(i)).split(" ");
			totalWords += wordsInCurrFile.length;
			for(String str : wordsInCurrFile){
				if(str.equals(givenWord))
					appear++;
			}
		}
		
		double answer = 0;
		answer = (double)(appear + 1)/(double)(totalWords+v);
		return answer;
		
	}
	
	private HashMap<String, Double> downloadProbs(File f){
		 try {
			 	HashMap<String, Double> map = new HashMap<String, Double>();
				for(String line : Files.readAllLines(f.toPath())){
					try{
						String []arr = line.trim().split(" ");
						map.put(arr[0], Double.parseDouble(arr[1]));
					}
					catch(ArrayIndexOutOfBoundsException e){
						
					}
				}
				return map;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
	}
	
	private boolean isSpam(File f, HashMap<String, Double> spamProbs, HashMap<String, Double> nonSpamProbs){
		String content = readFile(f);
		String[] contentArr = content.split(" ");
		double spamMeasure = 0;
		double nonSpamMeasure = 0;
		for(String str : contentArr){
			Double spamProb = spamProbs.get(str);
			if(spamProb == null) continue;
			spamMeasure += spamProb;
			nonSpamMeasure += nonSpamProbs.get(str);
		}
		if(spamMeasure > nonSpamMeasure)
			return true;
		else return false;
	}
	
	public static void main(String[] args) {
		ExersiceML  ml = new ExersiceML();
		List<File> nonSpamFilelist = ml.listFilesForFolder(new File("res/nonspam-train"));
		List<File> spamFilelist = ml.listFilesForFolder(new File("res/spam-train"));
		
		HashSet<String> wordsSet = new HashSet<String>();
		
		wordsSet.addAll(ml.getWords(nonSpamFilelist));
		wordsSet.addAll(ml.getWords(spamFilelist));
		
		double all = 0.0;
		HashMap<String, Double> spamProbsMap = new HashMap<String, Double>();
		HashMap<String, Double> nonSpamProbsMap = new HashMap<String, Double>();
		
		Object[] arrWords = wordsSet.toArray();
		
		File spamProbs = new File("res/probs-spam");
		File nonSpamProbs = new File("res/probs-nonspam");
		
		spamProbsMap = ml.downloadProbs(spamProbs);
		nonSpamProbsMap = ml.downloadProbs(nonSpamProbs);
		
		/* if we wanna generate it, not to download
		try {
			PrintWriter writer = new PrintWriter(spamProbs);
			
			for(int i = 0; i < arrWords.length; i++){
				double prob =  ml.fiKY1(i, 1, spamFilelist, wordsSet);
				probs.put((String)arrWords[i], prob);
				writer.println((String)arrWords[i] + " " + prob);
				if(ml.DEBUG)
					System.out.println((double)i/arrWords.length);
			}
			writer.flush();
			
			writer = new PrintWriter(nonSpamProbs);
			for(int i = 0; i < arrWords.length; i++){
				double prob =  ml.fiKY1(i, 1, nonSpamFilelist, wordsSet);
				probs.put((String)arrWords[i], prob);
				writer.println((String)arrWords[i] + " " + prob);
				if(ml.DEBUG)
					System.out.println((double)i/arrWords.length);
			}
			writer.flush();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		List<File> spamTestList = ml.listFilesForFolder(new File("res/spam-test"));
		List<File> nonSpamTestList = ml.listFilesForFolder(new File("res/nonspam-test"));
		for(File f : spamTestList){
			System.out.println(f.getName() + " is spam: " + ml.isSpam(f, spamProbsMap, nonSpamProbsMap) + ", expexted : true");
		}
		for(File f : nonSpamTestList){
			System.out.println(f.getName() + " is spam: " + ml.isSpam(f, spamProbsMap, nonSpamProbsMap) + ", expexted : false");
		}
	}

}
