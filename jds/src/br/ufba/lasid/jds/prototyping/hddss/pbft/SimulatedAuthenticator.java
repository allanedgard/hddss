/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.pbft;

import br.ufba.lasid.jds.comm.IMessage;
import br.ufba.lasid.jds.comm.SignedMessage;
import br.ufba.lasid.jds.prototyping.hddss.Agent;
import br.ufba.lasid.jds.security.SHA1withDSASunMessageAuthenticator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignedObject;

/**
 *
 * @author aliriosa
 */
public class SimulatedAuthenticator extends SHA1withDSASunMessageAuthenticator{
   Agent agent;
   public SimulatedAuthenticator(Agent agent) throws NoSuchAlgorithmException, NoSuchProviderException {
      super();
      this.agent = agent;
   }

   @Override
   public synchronized boolean check(SignedMessage m) {
      exec(m);
      return super.check(m);
   }

   @Override
   public synchronized SignedMessage encrypt(IMessage data) throws Exception {
      exec(data);
      return super.encrypt(data);
   }

   @Override
   public boolean check(SignedObject data) {
      //cpu.exec(data);
      return super.check(data);
   }

   @Override
   public boolean check(SignedObject data, PublicKey key) {
//      cpu.exec(data);
      return super.check(data, key);
   }

   @Override
   public boolean checkDisgest(Object data) {
      try{
         exec(data);
      }catch(Exception e){
         e.printStackTrace();
         System.exit(0);
      }
      return super.checkDisgest(data);
   }

   @Override
   public Object decrypt(SignedObject data) throws Exception {
//      cpu.exec(data);
      return super.decrypt(data);
   }

   @Override
   public SignedObject encrypt(Object data) throws Exception {
      try{
         exec(data);
      }catch(Exception e){
         e.printStackTrace();
         System.exit(0);
      }
      return super.encrypt(data);
   }

   @Override
   public synchronized String getDigest(Object data) throws Exception {
      try{
         exec(data);
      }catch(Exception e){
         e.printStackTrace();
         System.exit(0);
      }
      return super.getDigest(data);
   }

   @Override
   public Object makeDisgest(Object data) {
      try{
         exec(data);
      }catch(Exception e){
         e.printStackTrace();
         System.exit(0);
      }
      return super.makeDisgest(data);
   }

   private void exec(Object data) {
      this.agent.exec(data);
   }

}
