package simpledb.buffer;

import java.util.ArrayList;
import java.util.Arrays;

import simpledb.file.*;
import simpledb.log.LogMgr;

/**
 * Manages the pinning and unpinning of buffers to blocks.
 * @author Edward Sciore
 *
 */
public class BufferMgr {
   private Buffer[] bufferpool;
   private int numAvailable;
   private static final long MAX_TIME = 10000; // 10 seconds
   
   ReplaceStategy strategy= ReplaceStategy.NAIVE; 
   int lastBufferModified=-1;
   
   /**
    * Creates a buffer manager having the specified number 
    * of buffer slots.
    * This constructor depends on a {@link FileMgr} and
    * {@link simpledb.log.LogMgr LogMgr} object.
    * @param numbuffs the number of buffer slots to allocate
    */
   public BufferMgr(FileMgr fm, LogMgr lm, int numbuffs) {
      bufferpool = new Buffer[numbuffs];
      numAvailable = numbuffs;
      for (int i=0; i<numbuffs; i++)
         bufferpool[i] = new Buffer(fm, lm);
   }
   
   /**
    * Returns the number of available (i.e. unpinned) buffers.
    * @return the number of available buffers
    */
   public synchronized int available() {
      return numAvailable;
   }
   
   /**
    * Flushes the dirty buffers modified by the specified transaction.
    * @param txnum the transaction's id number
    */
   public synchronized void flushAll(int txnum) {
      for (Buffer buff : bufferpool)
         if (buff.modifyingTx() == txnum)
         buff.flush();
   }
   
   
   /**
    * Unpins the specified data buffer. If its pin count
    * goes to zero, then notify any waiting threads.
    * @param buff the buffer to be unpinned
    */
   public synchronized void unpin(Buffer buff) {
      buff.unpin();
      if (!buff.isPinned()) {
         numAvailable++;
         notifyAll();
      }
   }
   
   /**
    * Pins a buffer to the specified block, potentially
    * waiting until a buffer becomes available.
    * If no buffer becomes available within a fixed 
    * time period, then a {@link BufferAbortException} is thrown.
    * @param blk a reference to a disk block
    * @return the buffer pinned to that block
    */
   public synchronized Buffer pin(BlockId blk) {
      try {
         long timestamp = System.currentTimeMillis();
         Buffer buff = tryToPin(blk);
         while (buff == null && !waitingTooLong(timestamp)) {
            wait(MAX_TIME);
            buff = tryToPin(blk);
         }
         if (buff == null)
            throw new BufferAbortException();
         return buff;
      }
      catch(InterruptedException e) {
         throw new BufferAbortException();
      }
   }  
   
   private boolean waitingTooLong(long starttime) {
      return System.currentTimeMillis() - starttime > MAX_TIME;
   }
   
   /**
    * Tries to pin a buffer to the specified block. 
    * If there is already a buffer assigned to that block
    * then that buffer is used;  
    * otherwise, an unpinned buffer from the pool is chosen.
    * Returns a null value if there are no available buffers.
    * @param blk a reference to a disk block
    * @return the pinned buffer
    */
   private Buffer tryToPin(BlockId blk) {
      Buffer buff = findExistingBuffer(blk);
      if (buff == null) {
         buff = chooseUnpinnedBuffer();
         if (buff == null)
            return null;
         buff.assignToBlock(blk);
         
      }
      if (!buff.isPinned())
         numAvailable--;
      buff.pin();
      return buff;
   }
   
   private Buffer findExistingBuffer(BlockId blk) {
      for (Buffer buff : bufferpool) {
         BlockId b = buff.block();
         if (b != null && b.equals(blk))
            return buff;
      }
      return null;
   }
   
   private Buffer chooseUnpinnedBuffer() {
	  switch(strategy) {
		  case NAIVE :
		      return chooseUnpinnedBufferNaive();
		   
		  case FIFO :
			  return chooseUnpinnedBufferFIFO();
		      
		  case LRU :
			  return chooseUnpinnedBufferLRU();
		  
		  case CLOCK :
			  return chooseUnpinnedBufferClock();

	  }
      return null;
   }
   
   private Buffer chooseUnpinnedBufferNaive() {
      for (Buffer buff : bufferpool)
         if (!buff.isPinned())
         return buff;
      return null;
   }
   
   private Buffer chooseUnpinnedBufferFIFO() {
	   Buffer buffer=null;
	   for (Buffer buff : bufferpool)
	          if (!buff.isPinned()) {
	        	  if(buffer==null)
	        		  buffer=buff;
	        	  else {
	        		  if(buff.getIstant_load() < buffer.getIstant_load())
	        			  buffer=buff;
	        	  }
	          }
	   return buffer;

   }
   
   private Buffer chooseUnpinnedBufferLRU() {
	   Buffer buffer=null;	   
	   for (Buffer buff : bufferpool)
	          if (!buff.isPinned()) {
	        	  if(buffer==null)
	        		  buffer=buff;
	        	  else {
	        		  if(buff.getIstant_unpin() < buffer.getIstant_unpin())
	        			  buffer=buff;
	        	  }
	          }
	   return buffer;
   }
   
   private Buffer chooseUnpinnedBufferClock() {
	   ArrayList<Buffer> listBuffer= new ArrayList<>(Arrays.asList(bufferpool));
	   
	   if(lastBufferModified==-1) {
		      for (Buffer buff : bufferpool)
		          if (!buff.isPinned()){
		        	  lastBufferModified=listBuffer.indexOf(buff);
		        	  return buff;
		          }
		      return null;
		   }
	   
	   for(int i=lastBufferModified+1; i<bufferpool.length;i++)
	   {
		   Buffer buff= bufferpool[i];
		   if (!buff.isPinned()){
	        	  lastBufferModified=i;
	        	  return buff;
	       }
	   }
	   for(int i=0; i<lastBufferModified;i++)
	   {
		   Buffer buff= bufferpool[i];
		   if (!buff.isPinned()){
	        	  lastBufferModified=i;
	        	  return buff;
	       }
	   }
	   
	   return null;
	   
   }

	public Buffer[] getBufferpool() {
		return bufferpool;
	}
	
	public void setBufferpool(Buffer[] bufferpool) {
		this.bufferpool = bufferpool;
	}
	
	public void setStrategy(ReplaceStategy strategy) {
		this.strategy = strategy;
	}
	   
	public void setNumAvailable(int numAvailable) {
		this.numAvailable = numAvailable;
	}

	public int getLastBufferModified() {
		return lastBufferModified;
	}

	public void setLastBufferModified(int lastBufferModified) {
		this.lastBufferModified = lastBufferModified;
	}
	
	public void printStatusOfBuffer() {
		StringBuilder sb = new StringBuilder();
		for(int i =0; i<bufferpool.length;i++) {
			Buffer b = bufferpool[i];
			if(b!=null){
				BlockId block = b.block();
				long unpinTime = b.getIstant_unpin();
				long loadTime = b.getIstant_load();
				int pins = bufferpool[i].getPins();
				
				sb.append("bufferpool["+i+"]: ");
				sb.append("PINS: "+pins + ", ");
				sb.append("BLOCK: "+ block.number()+ ", ");
				sb.append("LOAD_TIME: "+ loadTime+ ", ");
				sb.append("UNPIN_TIME: "+ unpinTime+ ", ");
				sb.append("MODIFIED: "+ b.isModified());
			}
			System.out.println(sb.toString());
			sb.setLength(0);
		}
		System.out.println();
	}
	
	
}


