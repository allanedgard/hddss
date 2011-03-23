/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufba.lasid.jds.jbft.pbft.comm;

import br.ufba.lasid.jds.management.memory.pages.IPage;

/**
 *
 * @author aliriosa
 */
public class PBFTData extends PBFTServerMessage{

    protected IPage page;
    protected long pageIndex;

    public PBFTData(long pageIndex, IPage page, Object replicaID) {
        setPageIndex(pageIndex);
        setPage(page);
        setReplicaID(replicaID);
    }


    public IPage getPage() {
        return page;
    }

    public void setPage(IPage page) {
        this.page = page;
    }

    public long getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(long pageIndex) {
        this.pageIndex = pageIndex;
    }
    
    @Override
    public String toString() {
        return (
                "<DATA" + ", " +
                 "INDEX = " + getPageIndex() + ", " +
                 "PAGE  = " + getPage() + ", " +
                 "SENDER = " + getReplicaID() + ", " +
                 ">"
        );
    }

}
