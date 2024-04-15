import java.util.List;

public class concreteStrategyTime implements Strategy{

    @Override
    public void addTask(List<Server> servers, Task task) {
        Server targetServer = servers.getFirst();
        //getting the id of the target server to add the task to it which has the minimum waiting time
        for(Server server : servers){
            if(targetServer.getWaitingPeriod().get() > server.getWaitingPeriod().get()) {
                targetServer = server;
            }
        }

        for(Server s : servers)
            if(s.getId() == targetServer.getId()) {
                for (Task t : s.getTasks())
                    task.setSpentTime(task.getSpentTime() + t.getServiceTime());
                task.setSpentTime(task.getServiceTime() + task.getSpentTime());
                servers.get(s.getId()).addTask(task);
                break;
            }


    }
}
