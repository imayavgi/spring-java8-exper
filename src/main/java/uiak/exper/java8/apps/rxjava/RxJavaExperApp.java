package uiak.exper.java8.apps.rxjava;


import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Hello world!
 */
public class RxJavaExperApp {
    public static void main(String[] args) {
        RxJavaExperApp app = new RxJavaExperApp();
        app.onSameThread();
        app.subOnThread();
        app.obsOnThread();
    }

    private void onSameThread() {
        Observable<String> source = Observable.just("Alpha", "Beta", "Gamma");

        Observable<Integer> lengths = source.map(String::length);

        lengths.subscribe(l -> System.out.println("Received " + l + " on thread " + Thread.currentThread().getName()));
    }

    private void subOnThread() {
        Observable<String> source = Observable.just("Alpha", "Beta", "Gamma");

        Observable<Integer> lengths = source
                .subscribeOn(Schedulers.computation())
                .map(String::length);

        lengths.subscribe(sum -> System.out.println("Received " + sum +
                " on thread " + Thread.currentThread().getName()));
    }

    private void obsOnThread() {
        Observable<Integer> source = Observable.range(1, 10);

        source.map(i -> i * 100)
                .doOnNext(i -> System.out.println("Emitting " + i
                        + " on thread " + Thread.currentThread().getName()))
                .observeOn(Schedulers.computation())
                .map(i -> i * 10)
                .subscribe(i -> System.out.println("Received " + i + " on thread "
                        + Thread.currentThread().getName()));

        sleep(3000);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

