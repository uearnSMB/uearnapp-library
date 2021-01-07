package smarter.uearn.money.chats.model;

import java.io.Serializable;

public class Channel implements Serializable {
    private ChannelDetails TECH;
    private ChannelDetails TL;
    private ChannelDetails SME;
    private ChannelDetails GROUP;

    public ChannelDetails getTECH() {
        return TECH;
    }

    public void setTECH(ChannelDetails TECH) {
        this.TECH = TECH;
    }

    public ChannelDetails getTL() {
        return TL;
    }

    public void setTL(ChannelDetails TL) {
        this.TL = TL;
    }

    public ChannelDetails getSME() {
        return SME;
    }

    public void setSME(ChannelDetails SME) {
        this.SME = SME;
    }

    public ChannelDetails getGROUP() {
        return GROUP;
    }

    public void setGROUP(ChannelDetails GROUP) {
        this.GROUP = GROUP;
    }
}
