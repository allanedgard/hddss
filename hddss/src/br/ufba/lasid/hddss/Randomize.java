package br.ufba.lasid.hddss;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author allan
 */
public class Randomize {
    
    java.util.Random z; 
    
    public Randomize() {
        z = new java.util.Random();
    }
    
    public Randomize (int seed) {
        z = new java.util.Random();        
        z.setSeed(seed);
    }
    
    public double expntl (double x) {
        return (-x * Math.log(z.nextDouble()));
    }
    
    public double erlang(double x, double s) {
        int i, k; double w;
        w=x/s; k=(int) (w*w);
        w=1.0; for (i=0; i<k; i++) w*=z.nextDouble();
        return(-(x/k)*Math.log(w));
    }
    
    public int irandom(int i,int n) { /* 'random' returns an integer equiprobably selected from the   */
      /* set of integers i, i+1, i+2, . . , n.                        */
      n-=i; n=(int) ((n+1.0)*z.nextDouble());
      return(i+n);
    }
    
    public double hyperx(double x, double s)
    { /* 'hyperx' returns a psuedo-random variate from Morse's two-   */
      /* stage hyperexponential distribution with mean x and standard */
      /* deviation s, s>x.  */
      double cv,w,p;
      cv=s/x; w=cv*cv; p=0.5*(1.0-( (double) Math.sqrt((w-1.0)/(w+1.0))));
      w=(z.nextDouble()>p)? (x/(1.0-p)):(x/p);
      return(-0.5*w*Math.log(z.nextDouble()));
    }
    
    public double uniform(double a, double b)
    { /* 'uniform' returns a psuedo-random variate from a uniform     */
      /* distribution with lower bound a and upper bound b.           */
      return(a+(b-a)*z.nextDouble());
    }

    public double lognormal(double x, double s) {
        double xn, sn;
        xn = Math.log(x)-.5*Math.log(1 + (s*s/(x*x)));
        sn = Math.log( (s*s/(x*x)) +1);
        return Math.exp(  normal(xn,sn) );
    }
    
    public double uniform()
    { /* 'uniform' returns a psuedo-random variate from a uniform     */
      /* distribution with lower bound a and upper bound b.           */
      return(z.nextDouble());
    }
        
    
    public double normal(double x,double s)
    { /* 'normal' returns a psuedo-random variate from a normal dis-  */
      /* tribution with mean x and standard deviation s.              */
      double v1,v2,w,z1; double z2=0;
      if (z2!=0)
        {z1=z2; z2=0.0;}  /* use value from previous call */
        else
          {
            do
              {v1=2*z.nextDouble()-1.0; v2=2*z.nextDouble()-1.0; w=v1*v1+v2*v2;}
            while (w>=1.0);
	    w=(double) (Math.sqrt((-2*Math.log(w))/w)); z1=v1*w; z2=v2*w;
          }
      return(x+z1*s);
  }
    
    
}
