package org.motechproject;

import org.apache.log4j.Logger;

public class HelloMotech {

    private static Logger logger = Logger.getLogger("HelloMotech");

    public HelloMotech() {
        logger.info("Hello Motech. " + this.getClass().getClassLoader());
        System.out.println("Hello Motech. " + this.getClass().getClassLoader());
    }
}
