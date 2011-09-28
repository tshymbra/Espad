package com.espad.httpush;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.adapter.InputAdapter;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.util.ExecutionPathDebugLog;
import com.espertech.esperio.AdapterInputSource;
import com.espertech.esperio.csv.CSVInputAdapter;
import com.espertech.esperio.csv.CSVInputAdapterSpec;
import com.espertech.esperio.http.EsperIOHTTPAdapter;
import com.espertech.esperio.http.config.ConfigurationHTTPAdapter;
import com.espertech.esperio.http.config.Request;

public class CSVOverHTTPPusher {
	private static final Log log = LogFactory.getLog(CSVOverHTTPPusher.class);
	private EsperIOHTTPAdapter httpAdapter = null;
	private InputAdapter inputAdapter = null;
	private AdapterInputSource source = null;
	private CSVInputAdapterSpec spec = null;
	
	public CSVInputAdapterSpec getSpec() {
		return spec;
	}

	private ConfigurationHTTPAdapter adapterConfig = null;
	private Request request = null;

	private void InitHTTPOutputAadapter(EPServiceProvider epServiceProvider,
			String eventName) {
		log.info("ESPAD initialising HTTP output adapter");
		ExecutionPathDebugLog.setDebugEnabled(true);
		
		log.info("ESPAD CSVOverHTTPPusher debug enabled "+log.isDebugEnabled());
		
		adapterConfig = new ConfigurationHTTPAdapter();

		// add additional configuration
		request = new Request();
		request.setStream(eventName);

		// TODO read host/port from properties file
		request.setUri("http://localhost:18079/");
		adapterConfig.getRequests().add(request);

		log.info("ESPAD CSVOverHTTP creating http adapter");
		httpAdapter = new EsperIOHTTPAdapter(adapterConfig,
				epServiceProvider.getURI());
		httpAdapter.start();
	}

	public InputAdapter getInputAdpater() {
		return inputAdapter;
	}

	public boolean TunnelCSVOverHTTP(EPServiceProvider epServiceProvider,String eventsDataFolder,String eventTypeName,String[] propertyOrder) {
		/*
		 * EPStatement stmt = epServiceProvider.getEPAdministrator().createEPL (
		 * "select * from "+eventName );
		 */

		
		/*
		Map<String, Object> definedPropertyTypes = adapterSpec.getPropertyTypes();
		if (null == definedPropertyTypes) {
			log.info("ESPAD CSVOverHTTP no property types specified for "+ eventTypeName);
			return false;
		}
		*/
		
		InitHTTPOutputAadapter(epServiceProvider, eventTypeName);
		String eventDataFilePath = eventsDataFolder + "/" + eventTypeName
				+ ".csv";
		URL url = CSVOverHTTPPusher.class.getClassLoader().getResource(
				eventDataFilePath);
		try {
			if (null == url) {
				log.info("ESPAD CSVOverHTTP no event data resource found for "
						+ eventTypeName + " in " + eventDataFilePath);
				return false;
			}

			File file = new File(url.toURI());
			log.info("ESPAD CSVOverHTTP event data found in "
					+ file.getAbsolutePath());

			source = new AdapterInputSource(eventDataFilePath);

			log.info("ESPAD CSVOverHTTP created source for "+eventDataFilePath+" source resetable "+ source.isResettable());
			
			spec = new CSVInputAdapterSpec(source, eventTypeName);

			//String [] propertyOrderCopy = new String[definedPropertyOrder.length - 1];
		    //System.arraycopy( definedPropertyOrder, 0, propertyOrderCopy, 0, definedPropertyOrder.length );
			
			//spec.setPropertyOrder(propertyOrderCopy);
			//spec.setPropertyTypes(definedPropertyTypes);
		    spec.setPropertyOrder(propertyOrder);
			//log.info("ESPAD CSVOverHTTP has property order of size " + spec.getPropertyOrder().length);
			
			spec.setTimestampColumn("timestamp");
			// spec.setUsingEngineThread(true);

			log.info("ESPAD CSVOverHTTP creating CSVInputAdapter for epServiceProvider "+ epServiceProvider);

			inputAdapter = new CSVInputAdapter(epServiceProvider, spec);
			log.info("ESPAD CSVOverHTTP CSVInputAdapter created for "+ spec.geteventTypeName());

			// inputAdapter.start(); // method blocks unless engine thread
			// option is set

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			log.info("ESPAD URISyntaxException " + e.getMessage());
			e.printStackTrace();
		}

		return true;

	}
}
