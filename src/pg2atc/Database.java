package pg2atc;
import java.util.*;

public class Database
{  
   public Database()
   {
      readerCount = 0;
      dbReading = false;
      dbWriting = false;
   }

   // readers and writers will call this to nap
   public static void napping()
   {
     int sleepTime = (int) (NAP_TIME * Math.random() );
     try { Thread.sleep(sleepTime*1000); } 
     catch(InterruptedException e) {}
   }


   // reader will call this when they start reading
   public synchronized int startRead()
   { 
      //Added an additional condition to this while loop to keep readers waiting
      //if writerQueue is not empty
      while (dbWriting == true || !writerQueue.isEmpty())
      {
         try { wait(); }
         catch(InterruptedException e) {}
      }

      ++readerCount;

      // if I am the first reader tell all others
      // that the database is being read     
      if (readerCount == 1)
      {
         dbReading = true;
      } 
     
      return readerCount;
   }

   // reader will call this when they finish reading
   public synchronized int endRead()
   { 
      --readerCount;

      // if I am the last reader tell all others
      // that the database is no longer being read   
      if (readerCount == 0)
      {
         dbReading = false;
      }

      notifyAll();

	System.out.println("Reader Count = " + readerCount);

      return readerCount;
   }
   
   // writer will call this when they start writing
    public synchronized void startWrite()
   { 
      //When startWrite is called, add to the writerQueue to indicate that
      //a writer is waiting to write
      writerQueue.add(1); 
      
      while (dbReading == true || dbWriting == true)
      {
         try { wait(); }
         catch(InterruptedException e) {}
      }

      // once there are either no readers or writers
      // indicate that the database is being written
      dbWriting = true;
   }

   // writer will call this when they finish writing
   public synchronized void endWrite()
   {  
      //When a writer is finished writing, remove one entry from the writerQueue
      writerQueue.remove();
       
      dbWriting = false;

      notifyAll();
   }

   // the number of active readers
   private int readerCount;
   
   // flags to indicate whether the database is
   // being read or written
   private boolean dbReading;
   private boolean dbWriting;
   
   //Queue to keep track of the number of requests by writers to write
   Queue<Integer> writerQueue = new LinkedList<>();
   
   private static final int NAP_TIME = 5;
}
