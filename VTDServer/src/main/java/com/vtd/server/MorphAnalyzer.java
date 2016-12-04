package com.vtd.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class MorphAnalyzer {

	private Rengine rengine;
	private REXP exp;
	private Map<String, String> nounDic;
	private Map<String, String> verbDic;
	private Map<String, ArrayList<String>> commands;
	private ArrayList<String> nouns;
	private ArrayList<String> verbs;
	private boolean isNoun;
	private boolean isFind;
	
	public MorphAnalyzer() {
		nounDic = new HashMap<String, String>();
		verbDic = new HashMap<String, String>();
		
		try {
			FileReader fileReader = new FileReader("C:\\Users\\J.H LEE\\git\\VTDServer\\VTDServer\\src\\main\\java\\com\\vtd\\server\\wordDic.txt");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line;
			
			line = bufferedReader.readLine();
			while((line = bufferedReader.readLine()) != null) {
				String[] splitedLine = line.split(" ");
				
				if(splitedLine[0].equals("n")) {
					nounDic.put(splitedLine[1], splitedLine[2]);
				} else {
					verbDic.put(splitedLine[1], splitedLine[2]);
				}
			}
			
			bufferedReader.close();
			fileReader.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		this.rengine = new Rengine(new String[]{"--vanilla"}, false, null);
		inputEval("library(KoNLP)");
		inputEval("useSejongDic()");
	}
	
	public REXP inputEval(String command) {
		return this.rengine.eval(command);
	}
	
	public Map<String, ArrayList<String>> analyze(String message) {
		commands = new HashMap<String, ArrayList<String>>();
		nouns = new ArrayList<String>();
		verbs = new ArrayList<String>();
		isNoun = true;
		isFind = false;

		String morphMessage = "MorphAnalyzer(\"" + message + "\")";
		this.exp = inputEval(morphMessage);

		for(int inx = 0; inx < this.exp.asVector().size(); inx++) {
			isFind = false;
			String element = this.exp.asVector().elementAt(inx).toString();

			System.out.println(element);

			StringTokenizer contentST = new StringTokenizer(element, "\"");
			contentST.nextToken();
			
			while(contentST.hasMoreTokens() && isFind == false) {
				StringBuilder sb = new StringBuilder();
				sb.append(contentST.nextToken());
				
				if(sb.toString().indexOf("+") != -1) {	//여러 형태소로 나눠지는 경우
					StringTokenizer wordST = new StringTokenizer(sb.toString(), "+");
					while(wordST.hasMoreTokens()) {
						StringBuilder sb2 = new StringBuilder();
						sb2.append(wordST.nextToken());
						
						if(isNoun == true) {
							if(sb2.toString().indexOf("/ncn") != -1) {
								modifyWord(sb2.toString().split("/ncn")[0]);
							}
						} else {
							if(sb2.toString().indexOf("/pvg") != -1) {
								modifyWord(sb2.toString().split("/pvg")[0]);
							} else if(sb2.toString().indexOf("/jcs") != -1) {
								modifyWord(sb2.toString().split("/jcs")[0]);
							} else if(sb2.toString().indexOf("/jcc") != -1) {
								modifyWord(sb2.toString().split("/jcc")[0]);
							} else if(sb2.toString().indexOf("/ncpa") != -1) {
								modifyWord(sb2.toString().split("/ncpa")[0]);
							}
						}
					}
				} else {	//단일 형태소
					if(isNoun == true) {
						if(sb.toString().indexOf("/ncn") != -1) {
							modifyWord(sb.toString().split("/ncn")[0]);
						}
					} else {
						if(sb.toString().indexOf("/pvg") != -1) {
							modifyWord(sb.toString().split("/pvg")[0]);
						} else if(sb.toString().indexOf("/jcs") != -1) {
							modifyWord(sb.toString().split("/jcs")[0]);
						} else if(sb.toString().indexOf("/jcc") != -1) {
							modifyWord(sb.toString().split("/jcc")[0]);
						} else if(sb.toString().indexOf("/ncpa") != -1) {
							modifyWord(sb.toString().split("/ncpa")[0]);
						}
					}
				}
				
				contentST.nextToken();
			}
		}
		
		commands.put("nouns", nouns);
		commands.put("verbs", verbs);
		
		System.out.println(commands);
		
		return commands;
	}
	
	public void modifyWord(String word) {
		if(isNoun == true) {
			if(nounDic.get(word) != null) {
				nouns.add(nounDic.get(word));
				
				isFind = true;
				isNoun = false;
			}
		} else {
			if(verbDic.get(word) != null) {
				verbs.add(verbDic.get(word));
				
				isFind = true;
				isNoun = true;
			}
		}
	}
}
