package com.company;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    static void ThreadTest() {
        int n = 3;
        CustomQueue<Integer> queue = new CustomQueue<>(() -> 0, n);
        for (int i = 0; i < n; i ++) {
            new Thread(() -> {
                queue.push(1);
            }).start();
        }
    }

    static void Simple() {
        int n = 3;
        CustomQueue<Integer> queue = new CustomQueue<>(() -> 0, n);
        queue.push(1);
    }

    static void Stress() throws InterruptedException {
        int size = 4;
        CustomQueue<Integer> queue = new CustomQueue<>(() -> 0, size);
        int producer_num = 1, consumer_num = 1;
        int producer_iters = 100, consumer_iters = 100;
        ArrayList<Thread> consumers = new ArrayList<>(), producers = new ArrayList<>();
        ArrayList<Integer> producer_container = new ArrayList<>(), consumer_container = new ArrayList<>();
        AtomicInteger balance = new AtomicInteger(0);

        for (int i = 0; i < producer_num; ++i) {
            int finalI = i;
            producers.add(new Thread(() -> {
                for (int k = 0; k < producer_iters; ++k) {
                    producer_container.add(finalI);
                    boolean success = queue.push(finalI);
                    if (success) {
                        balance.incrementAndGet();
                    }
                    System.out.println("pr " + k);
                }
            }));
            producers.get(producers.size() - 1).start();
        }

        for (int i = 0; i < consumer_num; ++i) {
            consumers.add(new Thread(() -> {
                for (int k = 0; k < consumer_iters; ++k) {
                    Integer item = queue.pop();
                    consumer_container.add(item);
                    if (item != null) {
                        balance.decrementAndGet();
                    }
                    System.out.println("cs " + k);
                }
            }));
            consumers.get(consumers.size() - 1).start();
        }

        for (Thread th : consumers) {
            th.join();
        }
        for (Thread th : producers) {
            th.join();
        }

        if (balance.get() < 0) {
            try {
                throw new Exception("Negative balance");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int p : producer_container) {
            boolean exist = false;
            for (int c : consumer_container) {
                if (c == p) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                try {
                    throw new Exception("Popped element wasn't pushed");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Success! \\0/");
    }

    public static void main(String[] args) throws InterruptedException {
        Simple();
        ThreadTest();
        Stress();
    }
}
