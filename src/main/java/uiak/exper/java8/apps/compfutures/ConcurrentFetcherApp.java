package uiak.exper.java8.apps.compfutures;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ConcurrentFetcherApp {

    public static void main(String args[]) {
        CompletableFuture<String> nameFromCacheFetcher = new CompletableFuture<>().supplyAsync(
                () -> {
                    try {
                        System.out.println(" Getting customer name from Cache ..");
                        Thread.sleep(1000);
                        System.out.println(" GOT  customer name from Cache ..");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //return null;
                    return Thread.currentThread().getName() + " CACHED_CUST_NAME";
                }
        );

        CompletableFuture<String> nameFromDBFetcher = new CompletableFuture<>().supplyAsync(
                () -> {
                    try {
                        System.out.println(" Getting customer name from DB ..");
                        Thread.sleep(2000);
                        System.out.println(" GOT customer name from DB ..");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return Thread.currentThread().getName() + " DB_CUST_NAME";
                }
        );

        try {
            String custName = (String) CompletableFuture.anyOf(nameFromCacheFetcher, nameFromDBFetcher).get();
            if (custName == null || custName.length() ==0 ) {
                System.out.println(" Cache is empty .. need to wait for DB ");
                custName = nameFromDBFetcher.get();
            }
            System.err.println(custName);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(" Wait for process to complete and hit ENTER ");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


