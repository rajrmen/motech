package org.motechproject.server.verboice.domain;

import java.util.LinkedList;
import java.util.List;

public abstract class AggregatedVerboiceAction implements VerboiceVerb {

    List<VerboiceVerb> verboiceVerbs = new LinkedList<VerboiceVerb>();
    
    @Override
    public String toXMLString() {
        final StringBuffer buffer = new StringBuffer();
        for (VerboiceVerb verboiceVerb : verboiceVerbs) {
            buffer.append(verboiceVerb.toXMLString());
        }
        return buffer.toString();
    }


    public AggregatedVerboiceAction playUrl(String url) {
        verboiceVerbs.add(new Play(url));
        return this;
    }

    public AggregatedVerboiceAction playInLoopUrl(String url, int loopCount) {
        verboiceVerbs.add(new Play(url, loopCount));
        return this;
    }

    public AggregatedVerboiceAction say(String text){
        verboiceVerbs.add(new Say(text));
        return this;
    }

    public AggregatedVerboiceAction say(String text,String voice, int loop){
        verboiceVerbs.add(new Say(text,voice,loop));
        return this;
    }

    public AggregatedVerboiceAction gather(String action, int numDigits, char finishOnKey, int timeout){
        verboiceVerbs.add(new Gather(action, numDigits, finishOnKey, timeout));
        return this;
    }

    public AggregatedVerboiceAction redirect(String url) {
        verboiceVerbs.add(new Redirect(url));
        return this;
    }
    public AggregatedVerboiceAction hangup(){
        verboiceVerbs.add(new Hangup());
        return this;
    }
}
