package hello_server;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

public class ThreadPool {
    private final ArrayList<Worker> workers;
    private final BlockingQueue<Runnable> sender;

    public ThreadPool(long size) {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(10);

        this.workers = new ArrayList<>();
        for (var i = 0; i < size; ++i) {
            this.workers.add(new Worker(i, queue));
        }
        this.sender = queue;
    }

    public void execute(Runnable item) {
        try {
            this.sender.put(item);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        // Send a poisonPill for each worker so they each exit
        for (var i = 0; i < this.workers.size(); ++i) {
            this.sender.put(poisonPill);
        }

        for (var worker : this.workers) {
            System.out.println("Shutting down worker " + worker.id);
            worker.thread.join();
        }
    }

    static class Worker {
        public final long id;
        public final Thread thread;

        Worker(long id, BlockingQueue<Runnable> receiver) {
            this.id = id;
            this.thread = new Thread(() -> {
                while (true) {
                    try {
                        var job = receiver.take();
                        if (job == null || job == poisonPill) {
                            System.out.println("Worker " + id + " disconnected; shutting down.");
                            break;
                        }

                        var msg = String.format("Worker %d got a job; executing.", id);
                        System.out.println(msg);

                        job.run();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            this.thread.start();
        }
    }

    private static final Runnable poisonPill = () -> {
    };
}
