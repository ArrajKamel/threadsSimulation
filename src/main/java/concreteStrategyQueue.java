import java.util.List;

public class concreteStrategyQueue implements Strategy{

    @Override
    public void addTask(List<Server> servers, Task task) {
        Server targetServer = servers.getFirst();
        byte currentIndex = 0;
        byte targetIndex = -1;
        for(Server server : servers){
            if(targetServer.getTasks().size() > server.getTasks().size()) {
                targetServer = server;
                targetIndex = currentIndex ;
            }
            currentIndex++;
        }
        servers.get(targetIndex).addTask(task);
    }
}
