import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable{
    private final int id ;
    private final BlockingDeque<Task> tasks ;
    private final AtomicInteger waitingPeriod ;

    public Server(int id){
        this.tasks = new LinkedBlockingDeque<>();
        this.waitingPeriod = new AtomicInteger(0);
        this.id = id;
    }

    public BlockingDeque<Task> getTasks() {
        return tasks;
    }
    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    public void addTask(Task task){
        try {
            this.tasks.put(task);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            System.out.println("Thread was interrupted during put operation.");
        }
        this.waitingPeriod.addAndGet(task.getServiceTime());
    }

    public int getId(){
        return this.id;
    }

    @Override
    public void run() {
        while (true){
            Task task = this.tasks.peek();
            if(task != null) {
                if(task.getId() == -1)
                    break;

                try {
                    Thread.sleep(task.getServiceTime() * 200L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                this.waitingPeriod.addAndGet(task.getServiceTime() * -1);
                this.tasks.poll();
            }

        }
        }
    }

