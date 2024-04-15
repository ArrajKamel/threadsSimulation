import java.util.List;
import java.util.ArrayList; // This is the missing import

public class Scheduler {
    private List<Server> servers;
    private int numberOfServer ;
    private Strategy strategy ;

    public Scheduler(int numberOfServer , SelectionPolicy selectionPolicy){
        this.servers = new ArrayList<>();
        this.numberOfServer = numberOfServer;
        for(int i = 0; i < this.numberOfServer ; i++){
            Server server = new Server(i);
            Thread t = new Thread(server);
            t.start();
            servers.add(server);
        }
        setUpStrategy(selectionPolicy);
    }

    private void setUpStrategy(SelectionPolicy policy){
        if(policy == SelectionPolicy.SHORTEST_TIME)
            this.strategy = new concreteStrategyTime();
        if(policy == SelectionPolicy.SHORTEST_QUEUE)
            this.strategy = new concreteStrategyQueue();

    }
    public void dispatchTask(Task task){
        this.strategy.addTask(this.servers , task);
    }

    public List<Server> getServers() {
        return servers;
    }

    public int getNumberOfServer() {
        return numberOfServer;
    }

    public Strategy getStrategy() {
        return strategy;
    }
}
