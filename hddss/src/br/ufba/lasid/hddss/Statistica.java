package br.ufba.lasid.hddss;

/**
 * This class allows to summarize a set of integer values in order to
 * provide some statistics about it
 * @author allanedgard
 */
public class Statistica {

private long n;

private double mean;

private long sum;

private double max;

private double x[];

private double min;

private double variance;

Statistica() {
    n = 0;
    mean = 0;
    sum = 0;
    variance = 0;
    max = 0;
    min = 0;
    x = new double[10000000];
}

/**
 * return the maximum value of the items evaluated
 * @return the maximum value of the items evaluated
 */
double getMax() {
    return max;
}

/**
 * return the minimum value of the items evaluated
 * @return the minimum value of the items evaluated
 */
double getMin() {
    return min;
}


/**
 * calculate the standard deviation of the items evaluated
 * @return returns the standard deviation of the first i items
 */
double getStandardDeviation() {
    return Math.sqrt(variance);
}




/**
 * return the total of items evaluated
 * @return the total of items evaluated
 */
long getN() {
    return n;
}

/**
 * calculate the mean of the items evaluated
 * @return the mean of the items evaluated
 */
double getMean() {
    return (double) sum / n;
    //return getM(n);
}

/**
 * add a value to the items evaluated
 * @param a - the value
 */
synchronized void  addValue(int a) {
    if (n==0) {
        n = 1;
        variance = 0;
        sum=a;
        mean=a;
        //x[0]=a;
        max = a;
        min = a;
    }
    else {
        double mean_ant = (double) sum / n;
        mean = (double) (sum +a) / (n+1);
        double variance_ant = variance;
        //media = ((media_ant*n)+a) / (n+1);
        double x = n*(variance_ant+Math.pow(mean_ant, 2));
        variance =  ( (x+Math.pow(a,2) ) / (n+1) ) - Math.pow(mean,2);
        //[n]=a;
        if (a < min) min = a;
        if (a > max) max = a;
        sum = sum+a;
        n=n+1;
    }


}

}