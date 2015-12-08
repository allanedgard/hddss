package br.ufba.lasid.jds.prototyping.hddss;

import java.lang.reflect.Method;


public class Randomize {
    IntegrationR R;
    java.util.Random z; 

    String dist;
    Object obj;
    Class genericClass;
    int paramInt1, paramInt2;
    double paramDouble1, paramDouble2;
    String paramString1, paramString2;
    int TYPE;
    Method method;
    double [] X;
    int next;
    String funcao;
    Tracing trace;
    
    public Randomize() {
        z = new java.util.Random();
        next =0;
        funcao="";
    }
    
    public Randomize (int seed) {
        z = new java.util.Random();        
        z.setSeed(seed);
        next = 0;
        funcao="";
    }
    
    
    
    public void setDistribution(String dt) {
        /*
         *  
         */
        dist = dt;
        int i,j;
        int next;
        try {
            genericClass = Class.forName("br.ufba.lasid.jds.prototyping.hddss.Randomize");
            obj = genericClass.newInstance();
            i = dist.indexOf('(');
            String methodName = dist.substring(0, i);
            int b, c;
            b=i+1;
            do {
                c = b;
                b=dist.indexOf(')', b+1);
            } while (b>0);
            //String parameters = dist.substring(i+1,dist.indexOf(')'));
            String parameters = dist.substring(i+1,c);
            if (parameters.length() ==0) {
                TYPE=9;
                method = genericClass.getMethod(methodName);
                return;
            }
            i = parameters.indexOf(','); 
            if ( (i<0) || methodName.equals("R") ) {
                i = parameters.indexOf('\"');
                j = parameters.substring(1).indexOf('\"');
                if  ( (i==0) && (j==parameters.length()-2) ) {
                  String x = parameters.substring(1, parameters.length()-1);
                  TYPE = 2;
                  paramString1 = x;
                  method = genericClass.getMethod(methodName, String.class);
                } else {
                    i=parameters.indexOf('.');
                    if (i >=0) {
                        TYPE = 1;
                        paramDouble1 = Double.parseDouble(parameters);
                        method = genericClass.getMethod(methodName, double.class);
                    } else {
                        TYPE = 3;
                        paramInt1 = Integer.parseInt(parameters);
                        method = genericClass.getMethod(methodName, int.class);
                    }
                 } 
                
            } else {
                i = parameters.indexOf(',');
                String param1 = parameters.substring(0, i);
                String param2 = parameters.substring(i+1);
                i = param1.indexOf('\"');
                j = param1.substring(1).indexOf('\"');
                if  ( (i==0) && (j==param1.length()-2) ) {
                  String x = parameters.substring(1, param1.length()-1);
                  TYPE = 5;
                  paramString1 = x;
                } else {
                    i=param1.indexOf('.');
                    if (i >=0) {
                        TYPE = 4;
                        paramDouble1 = Double.parseDouble(param1);
                    } else {
                        TYPE = 6;
                        paramInt1 = Integer.parseInt(param1);                    
                    }
                }
                i = param2.indexOf('\"');
                j = param2.substring(1).indexOf('\"');
                if  ( (i==0) && (j==param2.length()-2) ) {
                  String x = param2.substring(1, param2.length()-1);
                  paramString1 = x;
                  switch (TYPE) {
                      case 4: // DBL, STR
                                TYPE = 45;
                                method = genericClass.getMethod(methodName, double.class, String.class);
                                break;
                      case 5: // STR, STR
                                TYPE = 55;
                                method = genericClass.getMethod(methodName, String.class, String.class);
                                break;
                      case 6: // INT, STR
                                TYPE = 65;
                                method = genericClass.getMethod(methodName, int.class, String.class);
                                break;
                  }
                  
                  
                } else {
                    i=param2.indexOf('.');
                    if (i >=0) {
                        paramDouble2 = Double.parseDouble(param2);
                        switch (TYPE) {
                            case 4: // DBL, DBL
                                TYPE = 44;
                                method = genericClass.getMethod(methodName, double.class, double.class);
                                break;
                            case 5: // STR, DBL
                                TYPE = 54;
                                method = genericClass.getMethod(methodName, String.class, double.class);
                                break;
                            case 6: // INT, DBL
                                TYPE = 64;
                                method = genericClass.getMethod(methodName, int.class, double.class);
                                break;
                        }
                    } else {
                        paramInt2 = Integer.parseInt(param2);                    
                        switch (TYPE) {
                            case 4: // DBL, INT
                                TYPE = 46;
                                method = genericClass.getMethod(methodName, int.class, double.class);
                                break;
                            case 5: // STR, DBL
                                TYPE = 56;
                                method = genericClass.getMethod(methodName, String.class, int.class);
                                break;
                            case 6: // INT, DBL
                                TYPE = 66;
                                method = genericClass.getMethod(methodName, int.class, int.class);
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) 
        {
        };  
    }    
    
   public double genericDistribution() {
        double dly=0.0;
        try {
            switch (TYPE) {
                case 9:
                     dly = (Double) method.invoke(this);
                     break;
                case 1: 
                     dly = (Double) method.invoke(this, paramDouble1);
                     break;
                case 2:
                     dly = (Double) method.invoke(this, paramString1);
                     break;
                case 3:
                     dly = (Double) method.invoke(this, paramInt1);
                     break;
                case 44: 
                     dly = (Double) method.invoke(this, paramDouble1, paramDouble2);
                     break;
                case 45:
                     dly = (Double) method.invoke(this, paramDouble1, paramString2);
                     break;
                case 46:
                     dly = (Double) method.invoke(this, paramDouble1, paramInt2);
                     break;
                case 54: 
                     dly = (Double) method.invoke(this, paramString1, paramDouble2);
                     break;
                case 55: 
                     dly = (Double) method.invoke(this, paramString1, paramString2);
                     break;
                case 56: 
                     dly = (Double) method.invoke(this, paramString1, paramInt2);
                     break;
                case 64: 
                     dly = (Double) method.invoke(this, paramInt1, paramDouble2);
                     break;
                case 65:
                     dly = (Double) method.invoke(this, paramInt1, paramString2);
                     break;
                case 66:
                     dly = (Double) method.invoke(this, paramInt1, paramInt2);
                     break;
                default: 
                    dly = 0.0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dly;
    }

    public double tracing(String filename, int column) {
        if (trace == null) {
            trace = new Tracing();
        }
        trace.setColumn(column);
        trace.setFilename(filename);
        return trace.getValue();
    }
   
    public double R(String funcao) {
        double n;
        if (X == null)
            X = R1(funcao);
        if (!this.funcao.equals(funcao)) next = 0; // RESTART Sequence
        this.funcao=funcao;
        n = X[next];
        next = (next + 1) % X.length;
        return n;
    }

    public double[] R1(String funcao) {
        double [] x;
        x=IntegrationR.getInstance().evaluateDoubleArray(funcao);
        return x;
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
    
    public double irandom(int i,int n) { /* 'random' returns an integer equiprobably selected from the   */
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
