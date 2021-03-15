package simpledb.buffer;

import java.io.IOException;
import java.util.Arrays;

import simpledb.file.BlockId;
import simpledb.file.Page;
import simpledb.server.SimpleDB;

public class BufferExamTest {
	public static void main(String[] args) throws IOException {		  
	      SimpleDB db = new SimpleDB("bufferfiletest", 400, 4);
	      BufferMgr bm = db.bufferMgr();
	      
	      BlockId blk70 = new BlockId("testfile", 70);
	      BlockId blk33 = new BlockId("testfile", 33);
	      BlockId blk35 = new BlockId("testfile", 35);
	      BlockId blk47 = new BlockId("testfile", 47);
	      BlockId blk60 = new BlockId("testfile", 60);
	      
	      bm.pin(blk70);
	      bm.pin(blk33);
	      bm.pin(blk33);
	      bm.pin(blk35);
	      bm.pin(blk47);
	      bm.unpin((bm.getBufferpool())[2]);
	      (bm.getBufferpool()[0]).setModified(1, 0);

	      
	      for(Buffer buffer : bm.getBufferpool())
	    	  buffer.setIstant_load(System.currentTimeMillis());
	      
	      
		  ReplaceStategy strategy=bm.strategy;  
	      
	      if(strategy==ReplaceStategy.NAIVE) {
		      bm.unpin(bm.getBufferpool()[0]); //unpin70
		      bm.pin(blk60); //pin60
		      (bm.getBufferpool()[0]).setModified(1, 0); //setXXX(60,...)
		      bm.unpin(bm.getBufferpool()[0]); //unpin60
		      bm.flushAll(0);//flushAll
		      (bm.getBufferpool()[3]).setModified(1, 0); //setXXX(47,...)
		      bm.unpin(bm.getBufferpool()[3]); //unpin47
		      bm.pin(blk70); //pin70
		      (bm.getBufferpool()[0]).setModified(1, 0); //setXXX(70,...)
		      bm.unpin(bm.getBufferpool()[0]); //unpin70
		      bm.pin(blk60); //pin(60)
		      bm.unpin(bm.getBufferpool()[0]); //unpin60
		      bm.pin(blk70); //pin(70)
	      }
	      
	      if(strategy==ReplaceStategy.FIFO) {
		      bm.unpin(bm.getBufferpool()[0]); //unpin70
		      bm.pin(blk60); //pin60
		      (bm.getBufferpool()[0]).setModified(1, 0); //setXXX(60,...)
		      bm.unpin(bm.getBufferpool()[0]); //unpin60
		      bm.flushAll(0);//flushAll
		      (bm.getBufferpool()[3]).setModified(1, 0); //setXXX(47,...)
		      bm.unpin(bm.getBufferpool()[3]); //unpin47
		      bm.pin(blk70); //pin70
		      (bm.getBufferpool()[2]).setModified(1, 0); //setXXX(70,...)
		      bm.unpin(bm.getBufferpool()[2]); //unpin70
		      bm.pin(blk60); //pin(60)
		      bm.unpin(bm.getBufferpool()[0]); //unpin60
		      bm.pin(blk70); //pin(70)
	      }
	      
	      if(strategy==ReplaceStategy.LRU) {
		      bm.unpin(bm.getBufferpool()[0]); //unpin70
		      bm.pin(blk60); //pin60
		      (bm.getBufferpool()[2]).setModified(1, 0); //setXXX(60,...)
		      bm.unpin(bm.getBufferpool()[2]); //unpin60
		      bm.flushAll(0);//flushAll
		      (bm.getBufferpool()[3]).setModified(1, 0); //setXXX(47,...)
		      bm.unpin(bm.getBufferpool()[3]); //unpin47
		      bm.pin(blk70); //pin70
		      (bm.getBufferpool()[0]).setModified(1, 0); //setXXX(70,...)
		      bm.unpin(bm.getBufferpool()[0]); //unpin70
		      bm.pin(blk60); //pin(60)
		      bm.unpin(bm.getBufferpool()[2]); //unpin60
		      bm.pin(blk70); //pin(70)
	      }
	      
	      if(strategy==ReplaceStategy.CLOCK) {
	    	  bm.setLastBufferModified(-1);
		      bm.unpin(bm.getBufferpool()[0]); //unpin70
		      bm.pin(blk60); //pin60
		      (bm.getBufferpool()[0]).setModified(1, 0); //setXXX(60,...)
		      bm.unpin(bm.getBufferpool()[0]); //unpin60
		      bm.flushAll(0);//flushAll
		      (bm.getBufferpool()[3]).setModified(1, 0); //setXXX(47,...)
		      bm.unpin(bm.getBufferpool()[3]); //unpin47
		      bm.pin(blk70); //pin70
		      (bm.getBufferpool()[0]).setModified(1, 0); //setXXX(70,...)
		      bm.unpin(bm.getBufferpool()[0]); //unpin70
		      bm.pin(blk60); //pin(60)
		      bm.unpin(bm.getBufferpool()[2]); //unpin60
		      bm.pin(blk70); //pin(70)
	      }
	      
	   }
}
