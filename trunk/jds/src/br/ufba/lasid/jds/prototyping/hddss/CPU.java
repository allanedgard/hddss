package br.ufba.lasid.jds.prototyping.hddss;

public abstract class CPU implements IClock{

   double tqueue = 0.0;     // TEMPO NA FILA DE PROCESSAMENTO
   Simulator container;     // SIMULADOR UTILIZADO
   long last = 0;           // UTILIZADO PARA REPRESENTAR O ULTIMO clock PROCESSADO

   double procrate = 0.0;   // TAXA DE PROCESSAMENTO
   double loadcost = 0.0;   //  CUSTO DA CARGA

   public CPU() {
   }

   static final String TAG = "cpu";
   IClock _clock;
   

   
   public void setLoadCost(String v){
       /*
        *   DEFINE O CUSTO DE CARGA
        *   O CUSTO É O CUSTO FIXO DE ENFILEIRAMENTO
        *   NA CPU
        */
        loadcost = Double.parseDouble(v);
    }

   public double getProcessingRate(){
       /*
        *   INFORMA A TAXA DE PROCESSAMENTO
        */
      return procrate;
   }

   
   public void setProcessingRate(String v){
        /*
         *  DETERMINA A TAXA DE PROCESSAMENTO
         */
        procrate = Double.parseDouble(v);
    }   
   
    public long waitTime(){
        long vclock = _clock.value();
        long dt = vclock - last;
        last = vclock;
        tqueue -= dt;
        tqueue = (tqueue < 0? 0: tqueue);

       return (long)tqueue; 
    }
    
    public long exec(Object data){
        /*
         *  AO SIMULAR A EXECUCAO DE UMA ACAO
         *  O PROCESSAMENTO DEPENDERA DO OBJETO
         *  E DO TIPO ESPECIFICO DE CPU MODELADA
         */
//        synchronized(this){
           long vclock = _clock.value();
           long dt = vclock - last;
           last = vclock;
           tqueue -= dt;

           tqueue = tqueue < 0? 0: tqueue;

           tqueue += (proc(data) + loadcost);
           
           long at = (long) tqueue;
            return at;
//        }

    }

    public abstract double proc(Object data);

   public long value() {
      return _clock.value() + waitTime();
   }

   public void setClock(IClock clock){
       /*
        *   ATRIBUI O RELOGIO A CPU
        */
      this._clock = clock;
   }

   public IClock getClock(){
       /*
        *   OBTEM O RELOGIO UTILIZADO PELA CPU
        */
      return this._clock;
   }
   
   public long tickValue() {
       /* 
        *   OBTEM O tick DO RELOGIO
        */       
      return _clock.tickValue();
   }

   public void adjustValue(long v) {
       /* 
        *   AJUSTA MANUALMENTE O clock DO RELOGIO
        */
      _clock.adjustValue(v);
   }

   public void adjustTickValue(long v) {
       /* 
        *   AJUSTA MANUALMENTE O tick DO RELOGIO
        */
      _clock.adjustTickValue(v);
   }

   public void adjustCorrection(long c) {
       /* 
        *   PROGRAMA A CORRECAO DO RELOGIO  CORR
        */
      _clock.adjustCorrection(c);
   }

}
