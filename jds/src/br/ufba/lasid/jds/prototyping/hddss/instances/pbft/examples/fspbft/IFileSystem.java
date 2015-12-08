/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.prototyping.hddss.instances.pbft.examples.fspbft;

/**
 *
 * @author aliriosa
 */
public interface IFileSystem {

    public enum Mode{
        OpenForReading, OpenForWriting, OpenForAppending,
        OpenForReadingAndWriting, CleanupAndOpenForReadingAndWritting,
        OpenForReadingWritingAndAppending
    }

    public long fopen(String filename, Mode mode);
    public long fclose(long fp);
    public long fprint(String text, long fp);
    public String fgets(long fp);
    public long fseek(long fp, long offset, long origin);



}
