/*
 * *************************************************************************************
 *  Copyright (C) 2013 Taras Shymbra, Inc. All rights reserved.                      *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 * *************************************************************************************
 */

package com.espad.host;

import com.espertech.esper.client.EPServiceProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;


public class HostMain {

    private static Log log = LogFactory.getLog(HostMain.class);

    public static void main(String[] args) {

        try {
            HostMain main = new HostMain();
            main.run();
        }
        catch (RuntimeException ex) {
            log.error("Unexpected exception encountered: " + ex.getMessage(), ex);
        }
    }

    public void run() {
        HostSetup ms = new HostSetup();
        EPServiceProvider engine = ms.setup();
 
        System.out.println("...press enter to finish...");
        try {
            System.in.read();
        } catch (IOException e) {
            log.error("Exception reading keyboard input: " + e.getMessage(), e);
        }
        
        engine.destroy();
    }
}
