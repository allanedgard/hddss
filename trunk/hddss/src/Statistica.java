/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
public class Statistica {

private long n;

private double media;

private long soma;

private double max;

private double x[];

private double min;

private double variancia;

Statistica() {
    n = 0;
    media = 0;
    soma = 0;
    variancia = 0;
    max = 0;
    min = 0;
    x = new double[10000000];
}

double getMax() {
    return max;
}

double getMin() {
    return min;
}

double getStandardDeviation() {
    return Math.sqrt(variancia);
    //return Math.sqrt(getS(n)/(n-1));
}

double getS(int i) {
    /*
     * B.P. Welford, Technometrics, 4,(1962), 419-42
     * Incremental Standard Deviation
     */
    if (i==1) {
        return 0;
    }
    else
        return getS(i-1) + ( x[i-1]- getM(i-1) ) * ( x[i-1] - getM(i) );
}

double getM(int i) {
    /*
     * B.P. Welford, Technometrics, 4,(1962), 419-42
     * Incremental Standard Deviation
     */
    if (i==1) {
        return x[0];
    }
    else
        return getM(i-1) + ( x[i-1]- getM(i-1) ) / i;
}

long getN() {
    return n;
}

double getMean() {
    return (double) soma / n;
    //return getM(n);
}

synchronized void  addValue(int a) {
    if (n==0) {
        n = 1;
        variancia = 0;
        soma=a;
        media=a;
        //x[0]=a;
        max = a;
        min = a;
    }
    else {
        double media_ant = (double) soma / n;
        media = (double) (soma +a) / (n+1);
        double variancia_ant = variancia;
        //media = ((media_ant*n)+a) / (n+1);
        double x = n*(variancia_ant+Math.pow(media_ant, 2));
        variancia =  ( (x+Math.pow(a,2) ) / (n+1) ) - Math.pow(media,2);
        //[n]=a;
        if (a < min) min = a;
        if (a > max) max = a;
        soma = soma+a;
        n=n+1;
    }


}

    public static void main(String[] args) {
        Statistica t = new Statistica();
        Randomize r = new Randomize();
        for (int i=1; i< 1000000; i++) {
         t.addValue(5);
         t.addValue(4);
         t.addValue(3);
         System.out.println(r.lognormal(15,10)+1);
        }
        System.out.println("media = "+t.getMean());
        System.out.println("std = "+t.getStandardDeviation());
        System.out.println("max = "+t.getMax());
        System.out.println("min = "+t.getMin());
        System.out.println("N = "+t.getN());



    }

}