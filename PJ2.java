// A simple semaphore example. 
import java.util.Random;
import java.util.concurrent.*; 
 
class PJ2{ 
 

  public static void main(String args[]) { 
    int i = 0;
    final int ElevatorCap = 7;
    int totalPersons = 49; // Amount of People waiting for elevator
    //Initialize threads
    Semaphore Eentries = new Semaphore(0, true); 
    Person [] person = new Person[Shared.totalPersons];
    Thread [] p = new Thread[Shared.totalPersons];
    Elevator e = new Elevator(Eentries, "Elevator");
    Thread elevator = new Thread(e);
    ////////////////////
    // Starting elevator
    elevator.start();
    //Starting 49 threads waiting in line
    for(i =0 ;i<totalPersons; i++){
    person[i] = new Person(Eentries,i);
    p[i] = new Thread(person[i]); 
    p[i].start();
    } 
    /////////////////////////
  }
}
// A shared resources between two threads. 
class Shared { 
  static int totalPersons = 49;// Amount of People waiting for elevator
  static int floor = 1; // Current floor
  static Semaphore EReady = new Semaphore(7); // Signal Elevator ready to move from floor 1
  static Semaphore Emoved= new Semaphore(0,true); // Signal elevator moved from one floor to another
} 
 
// Person threads
class Person implements Runnable { 
  int n = 0; // person's identification
  Semaphore Eentries ; //signal the elevator is not yet full
  int floor = 0;
  Person(Semaphore Eentries ,int n) { 
    this.n = n; 
    this.Eentries = Eentries;
    Random rand = new Random();
    this.floor = rand.nextInt(9) + 2; //Random generate the floor person want to go
  } 
 
  public void run() { 
    try {  
      Eentries.acquire(); // to register to elevator one person just go in on floor 1
      
      if(Shared.floor == 1){// Persons going in on floor 1
         System.out.println("Person "+ n +" enters elevator to go to floor " + floor);
         Shared.EReady.release();
      }

      while(Shared.floor != 10){ //stop the thread when the elevator visited all 10 floor
        Shared.Emoved.acquire(); // waiting for elevator to move
        if(Shared.floor == floor) //leave elevator if arrive
        {
          System.out.println("Person "+ n +" leaves elevator"); 
        }
          Shared.EReady.release();
      }
    } catch (InterruptedException exc) { 
      System.out.println(exc); 
    } 
      
  } 
} 
class Elevator implements Runnable { 
  String name; // name Elevator
  Semaphore Eentries ; 
  Elevator(Semaphore s, String n) { 
    Eentries = s; 
    name = n; 
  } 
  public void run() { 
    try { 
      for(int i=0; i< 7;i++){
      Shared.EReady.acquire(7); //waiting signals from 7 persons  each bring 1 permet
      System.out.println("Elevator opens at floor "+ Shared.floor);
      Eentries.release(7); // allow 7 persons to go in
      Shared.EReady.acquire(7); // waiting until the elevator is at full capacity
      System.out.println("Elevator door closes");

      while(Shared.floor <10){ //starting to go from floor 2 to 10
      Shared.floor++; //going up
      System.out.println("Elevator opens at floor "+ Shared.floor);
      Shared.Emoved.release(7); //Stop at each floor and check on each person in elevator
      Shared.EReady.acquire(7);
      System.out.println("Elevator door closes");
      }

      Shared.floor = 1; //when visit all floors, go back to 1
      Shared.EReady.release(7); // get new 7 persons in
      }//or(int i=0; i< 7){
    } catch (InterruptedException exc) { 
      System.out.println(exc); 
    } 
    // Release the permit. 
      System.out.println("Simulation done"); 
      Eentries.release(); 
  } 
}
