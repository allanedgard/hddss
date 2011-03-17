/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.management.memory.cache;

import java.util.Hashtable;

/**
 *
 * @author aliriosa
 */
public class MRUStrategy<K, V>{
    int max;

    Hashtable<K, Entry<K, V>> table = new Hashtable<K, Entry<K, V>>();

    Entry<K, V> first;
    Entry<K, V> last;

    public MRUStrategy(int max) {
        this.max = max;
    }

    public void put(K key, V value){
        Entry<K, V> entry = table.get(key);

        if(entry != null){
            entry.setValue(value);
            hint(entry);
            return;
        }

        if(table.size() == max){
            entry = evict();
            entry.setKey(key);
            entry.setValue(value);
        }else{
            entry = new Entry<K, V>(key, value);
        }

        insert(entry);

        table.put(entry.getKey(), entry);
        
    }

    public V get(K key){
        
        Entry<K, V> entry = table.get(key);
        if(entry != null){
            hint(entry);
            return entry.getValue();
        }

        return null;
    }

    public void remove(K key){
        Entry<K, V> entry = table.get(key);

        if(entry != null){
            remove(entry);
            table.remove(entry.getKey());
        }
        
    }
    
    private void insert(Entry<K, V> entry){
        
       if(first == null){
           first = entry;
           last = entry;
           return;
       }

       entry.setPrevious(last);
       last.setNext(entry);
       last = entry;
       
    }

    private void remove(Entry<K, V> entry){
        
        if(entry == first){
            first = first.getNext();
        }

        if(entry == last){
            last = last.getPrevious();
        }

        Entry<K, V> next = entry.getNext();
        Entry<K, V> prev = entry.getPrevious();

        if(next != null){
            next.setPrevious(prev);
        }

        if(prev != null){
            prev.setNext(next);
        }

        entry.setNext(null);
        entry.setPrevious(null);

    }

    public void clear(){
        table = new Hashtable<K, Entry<K, V>>();
        first = null;
        last = null;
    }

    private void hint(Entry<K, V> entry){
        if(last == null){
            return;
        }

        remove(entry);
        insert(entry);

    }

    private Entry<K, V> evict(){
        Entry<K, V> entry = first;
        remove(entry);
        table.remove(entry.getKey());
        entry.setValue(null);
        return entry;
         
    }

}

class Entry<K, V>{
        K key;
        V value;

        Entry next;
        Entry previous;

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public Entry getNext() {
        return next;
    }

    public void setNext(Entry next) {
        this.next = next;
    }

    public Entry getPrevious() {
        return previous;
    }

    public void setPrevious(Entry previous) {
        this.previous = previous;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

}

