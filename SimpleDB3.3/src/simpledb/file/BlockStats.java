package simpledb.file;

import java.util.HashMap;
import java.util.Map;

public class BlockStats {
	Map<String, Integer> statsRead = new HashMap<String,Integer>();
	Map<String, Integer> statsWrite = new HashMap<String,Integer>();
	
	public void reset() {
		statsRead.clear();
		statsWrite.clear();
	}
	
	public void logWrittenBlock(BlockId block) {
		String filename = block.fileName();
		if(statsWrite.containsKey(filename))
			statsWrite.put(filename, statsWrite.get(filename)+1);
		else
			statsWrite.put(filename, 1);
	}
	
	public void logReadBlock(BlockId block) {
		String filename = block.fileName();
		if(statsRead.containsKey(filename))
			statsRead.put(filename, statsRead.get(filename)+1);
		else
			statsRead.put(filename, 1);
	}
	
	@Override
	public String toString(){
		StringBuilder sb  = new StringBuilder();
		sb.append("WRITE\n");
		for (String filename : statsWrite.keySet()) {
			sb.append(filename + " written "+ statsWrite.get(filename) + " times\n");
		}
		
		sb.append("\nREAD\n");
		for (String filename : statsRead.keySet()) {
			sb.append(filename + " read "+ statsRead.get(filename) + " times\n");
		}
		
		return sb.toString();
	}
}
