/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.comm;

import br.ufba.lasid.jds.jbft.pbft.comm.PBFTServerMessage;
import java.util.ArrayList;

/**
 *
 * @author aliriosa
 */
public class MessageCollection extends ArrayList<IMessage>{
//   public IMessage get(long seqn){
//      for(IMessage m : this){
//         if(m instanceof PBFTServerMessage){
//            PBFTServerMessage sm = (PBFTServerMessage) m;
//            if(sm.getSequenceNumber() != null){
//               long s = sm.getSequenceNumber();
//               if(s == seqn){
//                  return sm;
//               }
//            }
//         }
//      }
//      return null;
//   }
}
