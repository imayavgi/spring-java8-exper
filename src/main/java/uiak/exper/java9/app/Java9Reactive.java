package uiak.exper.java9.app;

import uiak.exper.java9.reactive.svcs.EndSubscriber;
import uiak.exper.java9.reactive.svcs.TransformProcessor;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class Java9Reactive {

    public static void main(String args[]) {
        Java9Reactive j9r = new Java9Reactive();
        j9r.whenSubscribeToIt_thenShouldConsumeAll();
        j9r.whenSubscribeAndTransformElements_thenShouldConsumeAll();
    }

    public void whenSubscribeToIt_thenShouldConsumeAll() {

        // given
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
        Flow.Subscriber<String> subscriber = new EndSubscriber<String>();


        publisher.subscribe(subscriber);
        List<String> items = List.of("1", "x", "2", "x", "3", "x");

        // when
        items.forEach(publisher::submit);
        publisher.close();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void whenSubscribeAndTransformElements_thenShouldConsumeAll() {

        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
        TransformProcessor<String, Integer> transformProcessor
                = new TransformProcessor<>(Integer::parseInt);
        EndSubscriber<Integer> subscriber = new EndSubscriber<>();
        List<String> items = List.of("1", "2", "3");


        // when
        publisher.subscribe(transformProcessor);
        transformProcessor.subscribe(subscriber);
        items.forEach(publisher::submit);
        publisher.close();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
