/*
 * *************************************************************************************
 *  Copyright (C) 2013 Taras Shymbra, Inc. All rights reserved.                      *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 * *************************************************************************************
 */

package com.espad.host;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espad.ei.CSVEventInitialiser;
import com.espad.ei.StatementListener;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.util.ExecutionPathDebugLog;
import com.espertech.esperio.http.EsperIOHTTPAdapter;
import com.espertech.esperio.http.config.ConfigurationHTTPAdapter;
import com.espertech.esperio.http.config.GetHandler;
import com.espertech.esperio.http.config.Service;

public class Setup {

	private static Log log = LogFactory.getLog(Setup.class);

	private ConfigurationHTTPAdapter adapterConfig = null;
	private EsperIOHTTPAdapter httpAdapter = null;

	public EPServiceProvider setup(String eplFileName) throws Exception {

		// load config
		String configFile = "esper.host.cfg.xml";
		URL url = Main.class.getClassLoader().getResource(configFile);
		if (url == null) {
			log.error("Error loading configuration file '" + configFile
					+ "' from classpath");
			throw new Exception("Error loading configuration file '"
					+ configFile + "' from classpath");
		}

		Configuration config = new Configuration();
		config.getEngineDefaults().getExecution().setPrioritized(true);
		
		config.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
		config.getEngineDefaults().getLogging().setEnableTimerDebug(false);
		config.getEngineDefaults().getLogging().setEnableQueryPlan(true);
		
		config.configure(url);

		// using Map-event representations by default
		config.getEngineDefaults().getEventMeta().setDefaultEventRepresentation(Configuration.EventRepresentation.MAP);

		EPServiceProvider engine = EPServiceProviderManager
				.getDefaultProvider(config);
		engine.initialize();
		
		// event definitions
		CSVEventInitialiser evInitialiser = new CSVEventInitialiser();
		evInitialiser.sendEventDefinitions(engine);
		log.info("ESPAD CSVEventInitialiser finished sending event defs");

		String eventDefinitionsVersion = evInitialiser.getEventDefinitionsVersion();
		log.info("ESPAD EVENTS DEFINITIONS VERSION " + eventDefinitionsVersion);

		evInitialiser.sendEventDefinitions(engine);
				
		// resolve epl file
		InputStream inputFile = this.getClass().getClassLoader()
				.getResourceAsStream(eplFileName);
		
		if (inputFile == null) {
			throw new RuntimeException(
					"Failed to find file "+eplFileName+" in classpath or relative to classpath");
		}

		try {
			engine.getEPAdministrator().getDeploymentAdmin()
					.readDeploy(inputFile, null, null, null);
		} catch (Exception e) {
			throw new RuntimeException("Error deploying EPL from "+eplFileName+": "
					+ e.getMessage(), e);
		}

		String[] allStatNames = engine.getEPAdministrator().getStatementNames();

		InitHTTPOutputAadapter(engine, "all"); 

		for (final String statementName : allStatNames) {
			log.info("ESPAD STATEMENT " + statementName);
			EPStatement statement = engine.getEPAdministrator().getStatement(
					statementName);

			statement.addListener(new StatementListener(statementName));
		}
		
		return engine;
	}
	
	private void InitHTTPOutputAadapter(EPServiceProvider epServiceProvider,
			String eventName) {
		log.info("ESPAD Initialising HTTP output adapter for " + eventName);
		ExecutionPathDebugLog.setDebugEnabled(true);

		adapterConfig = new ConfigurationHTTPAdapter();
		Service service = new Service();
		service.setNio(true);
		service.setPort(18079);

		adapterConfig.getServices().put("espadhosttunnel", service);
		GetHandler gh = new GetHandler();
		gh.setPattern("*");
		gh.setService("espadhosttunnel");
		adapterConfig.getGetHandlers().add(gh);

		// add additional configuration
		// request = new Request();
		// request.setStream(eventName);

		// TODO read Main/port from a properties file
		// request.setUri("http://localMain:19089/");
		// adapterConfig.getRequests().add(request);

		log.info("ESPAD HTTPInputContextListener creating EsperIOHTTPAdapter");
		httpAdapter = new EsperIOHTTPAdapter(adapterConfig,
				epServiceProvider.getURI());
		httpAdapter.start();
	}

}
