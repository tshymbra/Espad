package com.espad.box;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.adapter.InputAdapter;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.util.ExecutionPathDebugLog;
import com.espertech.esperio.AdapterInputSource;
import com.espertech.esperio.csv.CSVInputAdapterSpec;
import com.espertech.esperio.http.EsperIOHTTPAdapter;
import com.espertech.esperio.http.config.ConfigurationHTTPAdapter;
import com.espertech.esperio.http.config.GetHandler;
import com.espertech.esperio.http.config.Request;
import com.espertech.esperio.http.config.Service;

public class HTTPInputContextListener implements ServletContextListener {
	private static final Log log = LogFactory
			.getLog(HTTPInputContextListener.class);
	private EsperIOHTTPAdapter httpAdapter = null;
	private InputAdapter inputAdapter = null;
	private AdapterInputSource source = null;
	private CSVInputAdapterSpec spec = null;
	private ConfigurationHTTPAdapter adapterConfig = null;
	private Request request = null;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("ESPAD HTTPInputContextListener contextInitialized entered");
		EPServiceProvider epService = EngineManager
				.getEngineFromServletContext(sce);

		InitHTTPOutputAadapter(epService, "all");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		log.info("ESPAD HTTPInputContextListener contextDestroyed entered");

	}

	private void InitHTTPOutputAadapter(EPServiceProvider epServiceProvider,
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

		// add additional configuration
		// request = new Request();
		// request.setStream(eventName);

		// TODO read host/port from a properties file
		// request.setUri("http://localhost:18079/");
		// adapterConfig.getRequests().add(request);

		log.info("ESPAD HTTPInputContextListener creating EsperIOHTTPAdapter");
		httpAdapter = new EsperIOHTTPAdapter(adapterConfig,
				epServiceProvider.getURI());
		httpAdapter.start();
	}

}
