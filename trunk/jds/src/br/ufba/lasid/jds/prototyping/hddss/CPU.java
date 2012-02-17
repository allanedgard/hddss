package br.ufba.lasid.jds.prototyping.hddss;

public abstract class CPU implements IClock{

   double tqueue = 0.0;
   Simulator conteiner;
   long last = 0;

   double procrate = 0.0;
   double loadcost = 0.0;
   int objects[];
   String objectsTAGs[];

   public CPU() {
      objects = new int[256];
      objectsTAGs = new String[256];
   }



   static final String TAG = "cpu";
   IClock _clock;
   
   public void setClock(IClock clock){
      this._clock = clock;
   }

   public IClock getClock(){
      return this._clock;
   }
   public void setProcessingRate(String v){
        procrate = Double.parseDouble(v);
    }
   public void setLoadCost(String v){
        loadcost = Double.parseDouble(v);
    }

   public double getProcessingRate(){
      return procrate;
   }

    public long waitTime(){
        long vclock = _clock.value();
        long dt = vclock - last;
        last = vclock;
        tqueue -= dt;
        tqueue = (tqueue < 0? 0: tqueue);

       return (long)tqueue; //Math.ceil(tqueue);
    }
    
    public long exec(Object data){
//        synchronized(this){
           long vclock = _clock.value();
           long dt = vclock - last;
           last = vclock;
           tqueue -= dt;

           tqueue = tqueue < 0? 0: tqueue;

   //        long at = proc();
           tqueue += (proc(data) + loadcost);
           
//           if(data.getTAG() >= 0){
//              objects[data.getTAG()]++;
//           }
           long at = (long) tqueue; //Math.ceil(tqueue);
           //conteiner.get(RuntimeSupport.Variable.CPUDelayTrace).<DescriptiveStatistics>value().addValue(tqueue);
           //if(at > 0){
              //System.out.println("cpu queue delay ===> " + tqueue + " ### at  ===> " + at);
           //}
            return at;
//        }

    }

    public abstract double proc(Object data);

   public long value() {
      return _clock.value() + waitTime();
   }

   public long tickValue() {
      return _clock.tickValue();
   }

   public void adjustValue(long v) {
      _clock.adjustValue(v);
   }

   public void adjustTickValue(long v) {
      _clock.adjustTickValue(v);
   }

   public void adjustCorrection(long c) {
      _clock.adjustCorrection(c);
   }

}
