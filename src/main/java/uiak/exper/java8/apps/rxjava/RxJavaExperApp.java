package uiak.exper.java8.apps.rxjava;


import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DefaultSubscriber;

/**
 * Hello world!
 */
public class RxJavaExperApp {
    public static void main(String[] args) {
        RxJavaExperApp app = new RxJavaExperApp();
        app.onSameThread();
        app.subcribeOnThread();
        app.observeOnThread();
        app.backPressure();
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

    private void backPressure() {
        Flowable.range(1, 5)
                .map(i -> i * 10)
                .subscribe(new DefaultSubscriber<Integer>() {
                    @Override public void onStart() {
                        System.out.println("Start!  on thread " + Thread.currentThread().getName() );
                        request(1);
                    }
                    @Override public void onNext(Integer t) {
                        if (t == 3) {
                            cancel();
                        }
                        System.out.println(" Cancel at " + t + " on thread " + Thread.currentThread().getName());
                        request(1);
                    }
                    @Override public void onError(Throwable t) {
                        t.printStackTrace();
                    }
                    @Override public void onComplete() {
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

