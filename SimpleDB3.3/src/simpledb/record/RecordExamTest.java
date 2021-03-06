package simpledb.record;

import simpledb.server.SimpleDB;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import simpledb.file.BlockId;
import simpledb.tx.Transaction;

public class RecordExamTest {
   public static void main(String[] args) throws Exception {

      SimpleDB db = new SimpleDB("recordtest", 2000, 500);
      Transaction tx = db.newTx();

      Schema sch = new Schema();
      sch.addIntField("A");
      sch.addStringField("B", 12);
      
      Layout layout = new Layout(sch);
      for (String fldname : layout.schema().fields()) {
          int offset = layout.offset(fldname);
          System.out.println(fldname + " has offset " + offset);
       }
      System.out.println("RL=" + layout.slotSize());

           
      System.out.println("Filling the page with 10000 random records.");
      TableScan ts = new TableScan(tx, "T", layout);
      for (int i=1; i<100000;  i++) {
         ts.insert();
         String randomString = generateRandomString();
         ts.setInt("A", i);
         ts.setString("B", randomString);
         //System.out.println("inserting into slot " + ts.getRid()  + ": {" + i + ", " + randomString+"}");
      }
      //System.out.println("USED "+ ts.getRid().blockNumber()+" blocks");
      System.out.println(db.fileMgr().getBlockStats());
      db.fileMgr().resetBlockStats();
      
      System.out.println("Deleting these records, whose A-values are less than 20000");
      int count = 0;
      ts.beforeFirst();
      while (ts.next()) {
         int a = ts.getInt("A");
         String b = ts.getString("B");
         if (a <= 20000) {
            count++;
            //System.out.println("slot " + ts.getRid() + ": {" + a + ", " + b + "}");
            ts.delete();
         }
      }
      System.out.println(count + " values under 20000 were deleted.\n");
      System.out.println(db.fileMgr().getBlockStats());
      db.fileMgr().resetBlockStats();
      //System.out.println(ts.getNumberOfFreeBlock() + " free blocks");

      //Set<Integer> blockwritten = new HashSet<Integer>();
      System.out.println("Filling the page with other 10000 random records.");
      ts.beforeFirst();
      for (int i=1; i<10000;  i++) {
          ts.insert();
          String randomString = generateRandomString();
          int n = (int) Math.round(Math.random() * 50);
          ts.setInt("A", n);
          ts.setString("B", randomString);
          //System.out.println("inserting into slot " + ts.getRid()  + ": {" + n + ", " + randomString+"}");
          //blockwritten.add(ts.getRid().blockNumber());
       }
      //System.out.println(ts.getNumberOfFreeBlock() + " free block");
      //System.out.println(blockwritten.size() + " blocks written");
      System.out.println(db.fileMgr().getBlockStats());
      db.fileMgr().resetBlockStats();
      
      
      //blockwritten.clear();
      Set<Integer> random_numbers = new HashSet<Integer>();
      while(random_numbers.size()!=500)
    	  random_numbers.add(((int) Math.round(Math.random() * 1000)));
      System.out.println("count rows with A-values in random_numbers");

      ts.beforeFirst();
      count = 0;
      while (ts.next()) {
          int a = ts.getInt("A");
          if(random_numbers.contains(a))
        	  count++;
          //blockwritten.add(ts.getRid().blockNumber());
       }
      
      //System.out.println(blockwritten.size() + " blocks read");
      System.out.println(count + " rows with A-values in random_numbers");
      System.out.println(db.fileMgr().getBlockStats());
      db.fileMgr().resetBlockStats();

  }   
   
   public static String generateRandomString() {
	   String candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	   int length=10;
	   
	   StringBuilder sb = new StringBuilder();
	   Random random = new Random();
	   for (int i = 0; i < length; i++)
		   sb.append(candidateChars.charAt(random.nextInt(candidateChars.length())));

	   return sb.toString();
   }
}
