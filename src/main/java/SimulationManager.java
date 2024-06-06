import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

public class SimulationManager implements Runnable{
    private int timeLimit = 60 ;
    private  int maxProcessingTime = 7 ;
    private  int minProcessingTime = 1 ;
    private  int maxArrivalTime = 40;
    private int minArrivalTime = 2 ;
    private int numberOfClients = 50 ;
    private int numberOfServer = 5;
    private SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME ;

    private Scheduler scheduler;
    private List<Task> generatedTasks ;

    public void startSimulation(){
        this.scheduler = new Scheduler(numberOfServer , selectionPolicy);
        //frame to display the simulation
        generateNRandomTasks();
    }

    public void generateNRandomTasks(){
        generatedTasks = new ArrayList<>();
        int taskAT, taskST;
        Random generator = new Random();
        for(int i = 0 ; i < numberOfClients ; i++){
            taskST = generator.nextInt(maxProcessingTime - minProcessingTime +1) + minProcessingTime;
            taskAT = generator.nextInt(this.maxArrivalTime - this.minArrivalTime + 1) + minArrivalTime;
            generatedTasks.add(new Task(i+1 , taskAT , taskST));
        }
    }

    private List<Task> extractTaskToSendToServers(int currentTime){
        List<Task> tasksToBeProcessed = new ArrayList<>();
        for(int i = 0; i < generatedTasks.size() ; i++){
            Task t = generatedTasks.get(i) ;
            if(t.getArrivalTime() <= currentTime) {
                tasksToBeProcessed.add(t);
                generatedTasks.remove(t);
            }
        }
        return tasksToBeProcessed;
    }

    private void printingWaitingClients(StringBuilder lineBuilder){
        for(Task t : generatedTasks){
            t.printTask();
            System.out.print(" ");
            lineBuilder.append("(").append(t.getId()).append(",").append(t.getArrivalTime()).append(",").append(t.getServiceTime()).append(")");
        }
        System.out.println();
    }

    private void printingServers(StringBuilder lineBuilder , List<String> lines){
        for(Server s : this.scheduler.getServers()){
            System.out.print("Queue " + (s.getId() + 1) + ": ");
            if(s.getTasks().isEmpty()) {
                System.out.println("closed");
                lines.add("Queue " +(s.getId()+1) + ": " + "closed");
                lineBuilder.setLength(0);
            }
            else {
                for (Task t : s.getTasks()) {
                    t.printTask();
                    lineBuilder.append("(").append(t.getId()).append(",").append(t.getArrivalTime()).append(",").append(t.getServiceTime()).append(")");
                    System.out.print(" ");
                }
                lines.add("Queue " + (s.getId() + 1) + ": " + lineBuilder);
                lineBuilder.setLength(0);
                System.out.println();
            }
        }
        lines.add("\n");
    }

    private void terminateServers(){
        Task terminator = new Task(-1,0,0);
        for(int i = 0 ; i < numberOfServer ; i++)
            scheduler.getServers().get(i).addTask((terminator));
    }

    public int pushTheTasksIntoTheSuitableServers(List<Task> tasksToBeProcessed ){
        int totalWaitPeriod  = 0;
        if(!tasksToBeProcessed.isEmpty())
            for(Task t : tasksToBeProcessed) {
                scheduler.dispatchTask(t);
                totalWaitPeriod += t.getSpentTime();
            }
        return totalWaitPeriod ;
    }

    public void writeInFile(String path , List<String> lines){
        try {
            Files.write(Paths.get(path), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file:");
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run() {
        int currentTime = 0  ,totalWaitPeriod = 0;
        float averageWaitingTime = 0;
        String path = "/Users/kamelarraj/Desktop/second_year/secondSem/FundamentalProgrammingTechniques(PT)/labs/assignment_2/mainFolder/PT2024_30421_Arraj_Kamel_Assignment_2/testing.txt";
        List<String> lines = new ArrayList<>();
        StringBuilder lineBuilder = new StringBuilder();
        while (currentTime < timeLimit){
            System.out.println("\n" + "Time :" + currentTime);
            lines.add("Time : " +currentTime);
            System.out.println("waiting clients : ");

            //extract the task which they have to go inside the servers
            List<Task> tasksToBeProcessed = extractTaskToSendToServers(currentTime);

            //printing the waiting clients
            printingWaitingClients(lineBuilder);
            lines.add("waiting clients: " + lineBuilder);
            lineBuilder.setLength(0);
            //push the tasks into the suitable servers using dispatchTask method form the scheduler
            totalWaitPeriod += pushTheTasksIntoTheSuitableServers(tasksToBeProcessed);
            printingServers(lineBuilder , lines);
            writeInFile(path , lines);
            currentTime++;
            try {
                sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //termination task
        terminateServers();
        averageWaitingTime = (float)totalWaitPeriod / this.numberOfClients;
        lines.add("Average waiting time : " + averageWaitingTime);
        writeInFile(path , lines);
        System.out.println("Average : " + averageWaitingTime);
    }

    public static void main(String[] args){

        int integer = 0;
        Scanner reader = new Scanner(System.in);
        SimulationManager simulationManager = new SimulationManager();

        System.out.println("enter the number of client : ");
        integer = reader.nextInt();
        simulationManager.numberOfClients = integer;

        System.out.println("enter the number of servers : ");
        integer = reader.nextInt();
        simulationManager.numberOfServer = integer;

        System.out.println("enter the simulation time : ");
        integer = reader.nextInt();
        simulationManager.timeLimit = integer;

        System.out.println("enter the min arrival time : ");
        integer = reader.nextInt();
        simulationManager.minArrivalTime = integer;

        System.out.println("enter the max arrival time  : ");
        integer = reader.nextInt();
        simulationManager.maxArrivalTime = integer;

        System.out.println("enter the min processing time : ");
        integer = reader.nextInt();
        simulationManager.minProcessingTime = integer;

        System.out.println("enter the max processing time : ");
        integer = reader.nextInt();
        simulationManager.maxProcessingTime = integer;

        simulationManager.startSimulation();
        Thread t = new Thread(simulationManager);
        t.start();

    }

}
