import java.util.*;
import java.io.*;

public class CPU_Simulator {
    /**
     * Function simulating first come first served algorithm
     * @param q queue taking input processes
     * @return returns an array of completed processes 
     */
    public static Process[] FirstComeFirstServe(Queue<Process> q)
    {
        System.out.println("-------------------------------------------------");
        System.out.println("        First Come First Served Scheduling       ");
        System.out.println("-------------------------------------------------");

        Process compl[] = new Process[q.size()];
        int t=0;
        int sz=0;
    
        while(!q.isEmpty())
        {
            Process top = q.remove();
            if(top.arrivalTime > t) t = top.arrivalTime;
            top.startTime = t;
            t += top.burstTime;
            top.remainTime-=top.burstTime;
            top.finishTime = t;
            compl[sz]=top;
            sz++;
            System.out.println("["+Integer.toString(top.startTime) + "-" + Integer.toString(top.finishTime)+ "]\t"+ top.pID +" running");
        }
        return compl;
    }
    
    /**
     * Function simulating round robin algorithm
     * @param T queue taking input processes
     * @param slide slide time for Round Robin algorithm
     * @return returns an array of completed processes 
     */
    public static Process[] RoundRobin(Queue<Process> T,int slide)
    {
        System.out.println("-------------------------------------------------");
        System.out.println("              Round Robin Scheduling             ");
        System.out.println("-------------------------------------------------");

        Process compl[] = new Process[T.size()];
        Queue<Process> q = new LinkedList<Process>();

        q.add(T.remove());

        int t=0;
        while(true)
        {
            if(q.isEmpty() && T.isEmpty()) break;
            if(q.isEmpty())
            {
                while(q.isEmpty()==true)
                {
                    while(T.isEmpty()==false && T.peek().arrivalTime <= t)
                    {
                        q.add(T.remove());
                    }
                    t+=1;
                }
                continue;
            }
            if(!q.isEmpty()){
                Process top = q.remove();

                if(top.remainTime == top.burstTime)
                {
                    top.startTime = t;
                }

                if(top.remainTime <= slide && top.remainTime > 0)
                {
                    int temp=t;
                    t+= top.remainTime;
                    top.remainTime = 0;
                    top.finishTime = t;
                    compl[top.pID_int] = top;
                    System.out.println("["+ Integer.toString(temp)+"-"+Integer.toString(top.finishTime)+ "]\t"+ top.pID +" running");
                    while(T.isEmpty()==false && T.peek().arrivalTime <= t)
                    {
                        q.add(T.remove());
                    }
                    continue;
                }
                else if(top.remainTime > slide)
                {
                    top.remainTime -= slide;
                    t += slide;
                    while(T.isEmpty()==false && T.peek().arrivalTime <= t)
                    {
                        q.add(T.remove());
                    }
                    q.add(top);
                    System.out.println("["+ Integer.toString(t-slide)+"-"+Integer.toString(t)+ "]\t"+ top.pID +" running");
                    continue;
                }
            }
            
            
        }
        return compl;
    }
    /**
     * Function To calculate Print the turnaround response and wait times along with their averages
     * @param compl Process Array storing the completed processes
     * @param sz size of the array
     */
    public static void printTimes(Process compl[],int sz)
    {
        System.out.println("\nTurnaround times:\n");
        double av_turn=0;
        double av_wait=0;
        double av_resp=0;
        for(int i=0;i<sz;i++)
        {
            compl[i].calctimes();
            av_turn += compl[i].turnTime;
            System.out.println("\t" + compl[i].pID +" = " + Integer.toString(compl[i].turnTime));
        }
        System.out.println("\nWait times:\n");
        for(int i=0;i<sz;i++)
        {
            av_wait += compl[i].waitTime;
            System.out.println("\t"+compl[i].pID +" = " + Integer.toString(compl[i].waitTime));
        }
        System.out.println("\nResponse times:\n");
        for(int i=0;i<sz;i++)
        {
            av_resp += compl[i].respTime;
            System.out.println("\t"+compl[i].pID +" = " + Integer.toString(compl[i].respTime));
        }
        av_resp/=sz;
        av_wait/=sz;
        av_turn/=sz;
        System.out.printf("Average turnaround time: %.3f\n",av_turn);
        System.out.printf("Average wait time: %.3f\n",av_wait);
        System.out.printf("Average Response time: %.3f\n",av_resp);
    }
    public static void main(String[] args) throws FileNotFoundException
    {
        File file = new File(args[0]);
        Scanner sc = new Scanner(file);
        Queue<Process> q = new LinkedList<Process>();
        Queue<Process> q2 = new LinkedList<Process>();
        int slide=Integer.parseInt(args[1]);
        
        while(sc.hasNextLine()) {
            String ID=sc.next();
            String arr=sc.next();
            String burst=sc.next();
            
            q.add(new Process(ID, arr,burst));
            q2.add(new Process(ID, arr,burst));
        }

        sc.close();
        int sz = q.size();
        

        System.out.println("-------------------------------------------------");
        System.out.println("           CPU Scheduling Simulation             ");
        System.out.println("-------------------------------------------------\n\n");

        
        Process compl_FCFS[] = FirstComeFirstServe(q);
        printTimes(compl_FCFS,sz);
    
        Process compl_RR[] = RoundRobin(q2,slide);
        printTimes(compl_RR,sz);
        
        System.out.println("\n\n-------------------------------------------------");
        System.out.println(" Project done by [Place your full name here]     ");
        System.out.println("-------------------------------------------------\n\n");
    }
}

/**
 * Class to store the process as an object
 */
class Process {

    String pID;
    int pID_int;
    int arrivalTime;
    int burstTime;
    int startTime;
    int finishTime;
    int remainTime;
    int respTime;
    int turnTime;
    int waitTime;
    /**
     * Constructor
     * @param ID string processes ID
     * @param arr string arrival time
     * @param burst string burst time
     */
    public Process(String ID, String arr , String burst) {
        this.pID = ID ;
        this.pID_int = Integer.parseInt(ID.substring(1));
        this.arrivalTime = Integer.parseInt(arr);
        this.burstTime = Integer.parseInt(burst);
        this.remainTime = Integer.parseInt(burst);
    }
    /**
     * Function to calculate turnaround reponse and wait times.
     */
    public void calctimes()
    {   
        this.turnTime = this.finishTime - this.arrivalTime;
        this.respTime = this.startTime - this.arrivalTime;
        this.waitTime = this.turnTime - this.burstTime;
    }
}