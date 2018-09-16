package uiak.exper;

import org.junit.Test;

import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyCompletableFutureExp {

    //@Test
    public void simpleCase() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cfs = CompletableFuture.completedFuture(" SIMPLE FUTURE MESSAGE ");
        String s = cfs.get();
        System.err.println(s);
    }

    @Test
    public void supplyAsyncTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService =
                new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>());
        CompletableFuture<String> cfs2 = CompletableFuture.supplyAsync(() -> {
            try {
                System.err.println("-----------> "+ System.currentTimeMillis() + " SUPPLY SYNC JOB");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Thread.currentThread().getName() + " SUPPLY SYNC\n";
        }, executorService);
        executorService.shutdown();
        System.err.println(cfs2.get( ));
    }

    //@Test
    public void supplyApplyAcceptTest() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture
                = CompletableFuture.supplyAsync(() -> Thread.currentThread().getName() + " -- SUPPLY SYNC -- ");

        CompletableFuture<String> apply = completableFuture
                .thenApply(s -> s + Thread.currentThread().getName() + " -- THEN APPLY --");

        CompletableFuture<Void> accept = completableFuture
                .thenAccept(s -> System.out.println("ACCEPT : Computation returned: " + s));

        System.err.println(apply.get());
    }

    //@Test
    public void supplyThenRunTest() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture
                = CompletableFuture.supplyAsync(() -> Thread.currentThread().getName() + " -- SUPPLY SYNC -- ");

        CompletableFuture<Void> future = completableFuture
                .thenRun(() -> System.out.println("Computation finished."));

        future.get();
    }

    //@Test
    public void composableTest() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture
                = CompletableFuture.supplyAsync(() -> "--"+ Thread.currentThread().getName())
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + "--"+ Thread.currentThread().getName()));

        System.err.println(completableFuture.get());
    }

    //@Test
    public void concurrentFutures() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future1
                = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future2
                = CompletableFuture.supplyAsync(() -> "Beautiful");
        CompletableFuture<String> future3
                = CompletableFuture.supplyAsync(() -> "World");

        CompletableFuture<Void> combinedFuture
                = CompletableFuture.allOf(future1, future2, future3);

        System.err.println(combinedFuture.get());
    }

    //@Test
    public void streamFutures() throws ExecutionException, InterruptedException {

        CompletableFuture<String> future1
                = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future2
                = CompletableFuture.supplyAsync(() -> "Beautiful");
        CompletableFuture<String> future3
                = CompletableFuture.supplyAsync(() -> "World");

        String combined = Stream.of(future1, future2, future3)
                .map(CompletableFuture::join)
                .collect(Collectors.joining(" "));

        System.err.println(combined);

    }


    //@Test
    public void complexCases() throws ExecutionException, InterruptedException {
        CompletableFuture<String> cfs = CompletableFuture.completedFuture(" SIMPLE FUTURE MESSAGE II ");
        CompletableFuture<String> cfs2 = CompletableFuture.supplyAsync(() -> {
            try {
                System.err.println("-----------> "+ System.currentTimeMillis() + " SUPPLY SYNC JOB");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Thread.currentThread().getName() + " END OF CFS2 RESULT\n";
        });

        System.err.println(" Started cfs2 .. going to do then Run ");

        cfs2.thenRun(() -> {
            System.err.println("-----------> " + Thread.currentThread().getName() + " " + System.currentTimeMillis() + " THEN RUN JOB");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.err.println(" Going to start cfs3 ");

        CompletableFuture<String> cfs3 = cfs2.thenCombine(cfs, (s1, s2) -> s1 + s2);

        System.err.println(" Finished then Combine cfs3 .. going to join and accept ");

        System.err.println(Thread.currentThread().getName() + "  " + System.currentTimeMillis() + " MAIN THREAD ");
        cfs3.join();

        cfs3.thenAccept(s1 -> System.err.println(
                "-----------> " + Thread.currentThread().getName() + " " + System.currentTimeMillis()  + " Finally Got " + s1));
        //System.out.println(cfs2.get());
    }

}
