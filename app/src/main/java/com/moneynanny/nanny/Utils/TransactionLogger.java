package com.moneynanny.nanny.Utils;

/**
 * Created by nikolay on 06.04.17.
 */
class TransactionLogger {
    private StringBuilder forward;
    private StringBuilder backward;

    public TransactionLogger() {
        forward = new StringBuilder();
        backward = new StringBuilder();
    }

    public void addTransaction(int sum) {
        forward.append(sum);
        forward.append(", ");
        backward.insert(0, ", ");
        backward.insert(0, sum);
    }

    public String getForward() { return  forward.toString(); }
    public String getBackward() { return  backward.toString(); }
}
