package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class CustomQueue<T> {
    int capacity;
    int head;
    int tail;
    Lock headLock; // указывает на первую занятую
    Lock tailLock; // указывает на первую свободную
    AtomicInteger size;
    ArrayList<T> array;
    Supplier<T> supplier;

    public CustomQueue(Supplier<T> supplier, int capacity) {
        headLock = new ReentrantLock();
        tailLock = new ReentrantLock();
        this.capacity = capacity;
        this.head = 0;
        this.tail = 0;
        this.size = new AtomicInteger(0);
        this.supplier = supplier;
        array = new ArrayList<T>(Collections.nCopies(capacity, supplier.get()));
    }

    boolean isEmpty(CustomQueue queue) {
        return size.get() == 0;
    }

    // add item to queue
    boolean push(T item) {
        tailLock.lock();
        if (size.get() == capacity) {
            return false;
        }
        array.set(tail, item);
        tail = nexIndex(tail);
        size.incrementAndGet();
        tailLock.unlock();
        return true;
    }

    // remove head item from queue, retern item
    T pop() {
        headLock.lock();
        if (size.get() == 0) {
            return null;
        }
        T item = array.get(head);
        size.decrementAndGet();
        head = nexIndex(head);
        headLock.unlock();
        return item;
    }

    // return head item
    T peek() {
        headLock.lock();
        if (size.get() == 0) {
            return null;
        }
        T item = array.get(head);
        headLock.unlock();
        return item;
    }

    int nexIndex(int index) {
        return (index + 1) % capacity;
    }

}
