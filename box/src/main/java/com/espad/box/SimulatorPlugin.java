package com.espad.box;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.plugin.PluginLoader;
import com.espertech.esper.plugin.PluginLoaderInitContext;

public class SimulatorPlugin implements PluginLoader {

	private static final Log log = LogFactory.getLog(SimulatorPlugin.class);

	private static final String ENGINE_URI = "engineURI";
	private String engineURI;

	public void destroy() {
		log.info("ESPAD SimulatorPlugin stopped.");
	}

	public void init(PluginLoaderInitContext context) {
		log.info("ESPAD SimulatorPlugin init");

		if (context.getProperties().getProperty(ENGINE_URI) != null) {
			engineURI = context.getProperties().getProperty(ENGINE_URI);
		} else {
			engineURI = context.getEpServiceProvider().getURI();
		}
	}

	public void postInitialize() {
		log.info("ESPAD SimulatorPlugin postInitialize");
	}
}
