/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.report;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author aliriosa
 */
public class Reporter {
   TreeMap<String, Report> reports = new TreeMap<String, Report>();

   public void addReport(Report report){
      reports.put(report.getName(), report);
   }

   public void newCountReport(String name){
      addReport(new CountReport(-1, name));
   }

   public void newStatsReport(String name){
      addReport(new StatsReport(-1, name));
   }

   public Report getReport(String name){
      return reports.get(name);
   }

//   public void insert(String name, double value){
//      Report r = getReport(name);
//      if(r == null){
//         r = new StatsReport(-1, name);
//         addReport(r);
//      }
//
//      r.insert(value);
//   }

//   public void insert(String name){
//      Report r = getReport(name);
//      if(r == null){
//         r = new CountReport(-1, name);
//         addReport(r);
//      }
//
//      r.insert();
//   }

   public void count(String name){
      Report r = getReport(name);
      if(r == null){
         r = new CountReport(-1, name);
         addReport(r);
      }

      r.insert();
   }

   public void stats(String name, double value){
      Report r = getReport(name);
      if(r == null){
         r = new StatsReport(-1, name);
         addReport(r);
      }

      r.insert(value);
   }

   public void assign(String name, double value){
      Report r = getReport(name);
      if(r == null){
         r = new ValuedReport(-1, name);
         addReport(r);
      }

      r.insert(value);
   }

   public List<ValuedReport> getValuedReports(){

      ArrayList<ValuedReport> counts = new ArrayList<ValuedReport>();
      for(Report r : reports.values()){
         if(r instanceof ValuedReport){
            counts.add((ValuedReport)r);
         }
      }
      return counts;
   }

   public List<CountReport> getCountReports(){
      
      ArrayList<CountReport> counts = new ArrayList<CountReport>();
      for(Report r : reports.values()){
         if(r instanceof CountReport){
            counts.add((CountReport)r);
         }
      }
      return counts;
   }

   public List<StatsReport> getStatsReports(){

      ArrayList<StatsReport> stats = new ArrayList<StatsReport>();
      for(Report r : reports.values()){
         if(r instanceof StatsReport){
            stats.add((StatsReport)r);
         }
      }

      return stats;
      
   }

   public void report2UnformattedTable(PrintStream out){
      out.print(count2Table() + "\n" + valued2Table() + "\n" + stats2Table());
   }

   public String valued2Table(){
        String outString = "";
        /* for each count report in reporter database */
        outString += "name; value \n";
        for(ValuedReport r : getValuedReports()){
           outString += r.getName() + ";" + r.getResult() + "\n";
        }

        return outString;
   }
   
   public String count2Table(){
        String outString = "";
        /* for each count report in reporter database */
        outString += "name; total\n";
        for(CountReport r : getCountReports()){
           outString += r.getName() + ";" + r.getResult() + "\n";
        }

        return outString;
   }

   public String stats2Table(){
        String outString = "";

        /* for each statistical report in reporter database */
        outString += "name; mean; std; maximum; minimun\n";

        for(StatsReport r : getStatsReports()){
            DescriptiveStatistics stats = r.getResult();
            outString += r.getName() + "; " + stats.getMean() + "; " + stats.getStandardDeviation() + "; " + stats.getMax() + "; " +stats.getMin() + "\n";
        }

        return outString;
   }

   public void report2FormattedTable(PrintStream out){
        /* for each count report in reporter database */
        out.println(format(count2Table()) + "\n" + format(valued2Table()) + "\n" + format(stats2Table()));

   }

   public String format(String table){
        Hashtable<Integer, Integer> header = new Hashtable<Integer, Integer>();

        StringTokenizer linesTokens = new StringTokenizer(table);
        while(linesTokens.hasMoreElements()){
           String line = linesTokens.nextToken("\n");
           if(line.indexOf(";") > 0){
              StringTokenizer tokens = new StringTokenizer(line);
              int h = 0;
              while(tokens.hasMoreElements()){
                 Integer head = header.get(h);
                 if(head == null){
                    head = 0;
                    header.put(h, head);
                 }
                 String token = tokens.nextToken(";");
                 if(token.length() > head){
                     header.put(h, token.length());
                 }
                 h++;
              }
           }
        }

        String lineSep = "";
        String[] fields = new String[header.size()];

        String formattedString = "";

        for(int h = 0; h < header.size(); h++ ){
           fields[h] = "";
           int headSize = header.get(h);
           for(int i = 0; i <=headSize; i++){
              lineSep += "-";
              if(i < headSize){
               fields[h] += " ";
              }
           }

        }

        lineSep = "\n" + lineSep + "-\n";
        StringTokenizer lines = new StringTokenizer(table);
        while(lines.hasMoreElements()){
           formattedString += lineSep;
           String line = lines.nextToken("\n");
           StringTokenizer tokens = new StringTokenizer(line);
           int h = 0;
           while(tokens.hasMoreElements()){
              String token = tokens.nextToken(";");
              String field = fields[h];
              formattedString += "|" + field.substring(0, field.length() - token.length()) + token;
              h++;
           }
           formattedString += "|";
        }

        if(formattedString.length() > 0){
         formattedString += lineSep;
        }

        return formattedString;
      
   }
}
