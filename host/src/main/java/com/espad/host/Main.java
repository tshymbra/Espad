/*
 * *************************************************************************************
 *  Copyright (C) 2013 Taras Shymbra, Inc. All rights reserved.                      *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 * *************************************************************************************
 */

package com.espad.host;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.client.EPServiceProvider;

public class Main {

    private static Log log = LogFactory.getLog(Main.class);
    private static final String DEFAULT_EPL_FILE_NAME = "espad.epl";
    
    Setup ms = null;
    
    public static void main(String[] args) {

        try {
            Main main = new Main();
            main.run(args);
        }
        catch (Exception ex) {
            log.error("Unexpected exception encountered: " + ex.getMessage(), ex);
        }
    }

    public void run(String[] args) throws Exception {
    	CommandLineParser parser =  new GnuParser(); 
		final Options gnuOptions = constructGnuOptions();  

		CommandLine cmdLine = parser.parse(gnuOptions, args);
		String eplFileName = DEFAULT_EPL_FILE_NAME; 
		
		if (cmdLine.hasOption("f")) {
			eplFileName = cmdLine.getOptionValue("f");
			log.info("epl file name="+eplFileName);
		}
		
    	ms = new Setup();
        EPServiceProvider engine = ms.setup(eplFileName);
        
        System.out.println("...press enter to finish...");
        try {
            System.in.read();
        } catch (IOException e) {
            log.error("Exception reading keyboard input: " + e.getMessage(), e);
        }
    }
    
	public static Options constructGnuOptions() {
		final Options gnuOptions = new Options();
		gnuOptions.addOption("f", "eplfile", true, "EPL file name");
		return gnuOptions;
	}

}
