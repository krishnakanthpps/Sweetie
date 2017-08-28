package com.sweetcompany.sweetie.model;

import java.util.Map;

/**
 * Created by Eduard on 11-Jul-17.
 */

public class CoupleInfoFB {

    private String partnerUsername;
    private String activeCouple;
    private String creationTime;

    private Map<String, Boolean> archivedCouples;

    public CoupleInfoFB() {}

    public String getPartnerUsername() {
        return partnerUsername;
    }

    public void setPartnerUsername(String partnerUsername) {
        this.partnerUsername = partnerUsername;
    }

    public String getActiveCouple() {
        return activeCouple;
    }

    public void setActiveCouple(String activeCouple) {
        this.activeCouple = activeCouple;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public Map<String, Boolean> getArchivedCouples() {
        return archivedCouples;
    }

    public void setArchivedCouples(Map<String, Boolean> archivedCouples) {
        this.archivedCouples = archivedCouples;
    }
}
