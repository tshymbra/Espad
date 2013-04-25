package com.espad.host;

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

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HostSetup {

	private static Log log = LogFactory.getLog(HostSetup.class);

	private ConfigurationHTTPAdapter adapterConfig = null;
	private EsperIOHTTPAdapter httpAdapter = null;
	
	public EPServiceProvider setup() {

        Configuration config = new Configuration();
        config.getEngineDefaults().getExecution().setPrioritized(true);
        //config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
        
        EPServiceProvider engine = EPServiceProviderManager.getDefaultProvider(config);
        engine.initialize();

        //event definitions from CSV  
        CSVEventInitialiser evInitialiser = new CSVEventInitialiser();
		evInitialiser.sendEventDefinitions(engine);
		log.info("ESPAD CSVEventInitialiser finished sending event defs");

        // Resolve "espad.epl" file.
        InputStream inputFile = this.getClass().getClassLoader().getResourceAsStream("espad.epl");
        if (inputFile == null) {
            throw new RuntimeException("Failed to find file 'espad.epl' in classpath or relative to classpath");
        }

        try {
            engine.getEPAdministrator().getDeploymentAdmin().readDeploy(inputFile, null, null, null);
        }
        catch (Exception e) {
            throw new RuntimeException("Error deploying EPL from 'espad.epl': " + e.getMessage(), e);
        }

		String[] allStatNames = engine.getEPAdministrator()
				.getStatementNames();
		
		for (final String statementName : allStatNames) {
			log.info("ESPAD STATEMENT " + statementName);
			EPStatement statement = engine.getEPAdministrator()
					.getStatement(statementName);

			statement.addListener(new StatementListener(statementName));
		}
		
		initHTTPOutputAadapter(engine, "all");
		
        return engine;
    }
	
	private void initHTTPOutputAadapter(EPServiceProvider epServiceProvider,
			String eventName) {
		log.info("ESPAD Initialising HTTP output adapter");
		ExecutionPathDebugLog.setDebugEnabled(true);

		adapterConfig = new ConfigurationHTTPAdapter();
		Service service = new Service();
		service.setNio(true);
		service.setPort(18079);

		adapterConfig.getServices().put("espadtunnel", service);
		GetHandler gh = new GetHandler();
		gh.setPattern("*");
		gh.setService("espadtunnel");
		adapterConfig.getGetHandlers().add(gh);

		log.info("ESPAD HTTPInputContextListener creating EsperIOHTTPAdapter");
		httpAdapter = new EsperIOHTTPAdapter(adapterConfig,
				epServiceProvider.getURI());
		httpAdapter.start();
	}
}

