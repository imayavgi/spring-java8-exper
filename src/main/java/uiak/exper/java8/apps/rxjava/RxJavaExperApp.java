package uiak.exper.java8.apps.rxjava;


import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DefaultSubscriber;

import java.util.concurrent.*;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * Hello world!
 */
public class RxJavaExperApp {
    public static void main(String[] args) {
        RxJavaExperApp app = new RxJavaExperApp();
//        app.onSameThread();
//        app.subcribeOnThread();
//        app.observeOnThread();
//        app.backPressure();
//        app.simpleEmitter();
//        app.runOnSchedulers();
//        app.concurrentFlow();
//        app.parallelFlow();
        app.atOnceParallelFlow();
    }


    private void atOnceParallelFlow() {
        ExecutorService executorService =
                new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>());
        Scheduler schedulerA = Schedulers.from(executorService);

        Flowable.range(1, 10)
                //.flatMap(v ->
                .concatMapEager(v ->
                        Flowable.just(v)
                                .subscribeOn(schedulerA)
                                .map(w -> w + "  Process on " + Thread.currentThread().getName())
                )
                .blockingSubscribe(s -> System.err.println(" Observer On Thread " + Thread.currentThread().getName() + " " + s));
        schedulerA.shutdown();
    }

    private void parallelFlow() {
        Flowable.range(1, 10)
                .parallel()
                .runOn(Schedulers.computation())
                .map(v -> v + "  Process on " + Thread.currentThread().getName()) //  Process on RxComputationThreadPool-*
                .sequential()
                .blockingSubscribe(s -> System.err.println(" Observer On Thread " + Thread.currentThread().getName() + " " + s));
    }

    private void concurrentFlow() {
        Flowable.range(1, 10)
                .observeOn(Schedulers.computation())
                .map(v -> v * v)
                .blockingSubscribe(s -> System.err.println(" Observer On Thread " + Thread.currentThread().getName() + " " + s));
    }

    private void runOnSchedulers() {

        Flowable.fromCallable(() -> {
            Thread.sleep(1000); //  imitate expensive computation
            return "Observable on : " + Thread.currentThread().getName() + " Observable is Done ";
        })
                .subscribeOn(Schedulers.io()) // Observable or Publisher on : RxCachedThreadScheduler-1
                //.subscribeOn(Schedulers.computation()) // Observable or Publisher on : RxComputationThreadPool-1
                .observeOn(Schedulers.single()) // Observer or Subscriber On Thread RxSingleScheduler-1
                //.subscribe(System.out::println, Throwable::printStackTrace);
                .subscribe(s -> System.err.println(" Observer On Thread " + Thread.currentThread().getName() + " " + s));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void simpleEmitter() {
        Observable.create(emitter -> {
            while (!emitter.isDisposed()) {
                long time = System.currentTimeMillis();
                emitter.onNext(time);
                if (time % 2 != 0) {
                    emitter.onError(new IllegalStateException("Odd millisecond!"));
                    break;
                }
            }
        }).subscribe(System.out::println, Throwable::getCause);
    }

    private void onSameThread() {
        Observable<String> source = Observable.just("Alpha", "Beta", "Gamma");

        Observable<Integer> lengths = source.map(String::length);

        lengths.subscribe(l -> System.out.println("onSameThread: Received " + l + " on thread " + Thread.currentThread().getName()));
    }

    private void subcribeOnThread() {
        Observable<String> source = Observable.just("Alpha", "Beta", "Gamma");

        Observable<Integer> lengths = source
                .subscribeOn(Schedulers.computation())
                .map(String::length);

        lengths.subscribe(sum -> System.out.println("subcribeOnThread: Received " + sum +
                " on thread " + Thread.currentThread().getName()));
    }

    private void observeOnThread() {
        Observable<Integer> source = Observable.range(1, 10);

        source.map(i -> i * 100)
                .doOnNext(i -> System.out.println("Emitting " + i
                        + " on thread " + Thread.currentThread().getName()))
                .observeOn(Schedulers.computation())
                .map(i -> i * 10)
                .subscribe(i -> System.out.println("observeOnThread: Received " + i + " on thread "
                        + Thread.currentThread().getName()));

        sleep(3000);
    }

    // Only flowable supports back presssion function
    // backpression does not seem to work with running observer on different thread.
    private void backPressure() {
        Flowable.range(1, 5)
                .map(i -> i * 10)
                .filter(v -> v % 3 == 0)
                .subscribe(new DefaultSubscriber<Integer>() {
                    @Override
                    public void onStart() {
                        System.out.println("Start!  on thread " + Thread.currentThread().getName());
                        request(1);
                    }

                    @Override
                    public void onNext(Integer t) {
                        if (t == 3) {
                            cancel();
                        }
                        System.out.println(" Cancel at " + t + " on thread " + Thread.currentThread().getName());
                        request(1);
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Done! on thread " + Thread.currentThread().getName());
                    }
                });
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

