package io.github.willmerkz.team2PVP.pair;

// pair logic
public class Pair<T> {

    private T first;
    private T second;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public void setSecond(T second) {
        this.second = second;
    }

    public int size() {
        return (first == null ? 0 : 1) + (second == null ? 0 : 1);
    }

    public T getFirstNotNull() {
        return first == null ? second : first;
    }

}
