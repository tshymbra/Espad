package com.espad.box;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espad.ei.CSVEventInitialiser;
import com.espertech.esper.client.EPServiceProvider;

public class EventsInitializer implements ServletContextListener {

	private static final Log log = LogFactory.getLog(EventsInitializer.class);

	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void contextInitialized(ServletContextEvent servletContextEvent) {

		log.info("ESPAD BOX contextInitialized entered");
		try {
			EPServiceProvider epService = EngineManager
					.getEngineFromServletContext(servletContextEvent);

			CSVEventInitialiser sss = new CSVEventInitialiser();
			sss.sendEventDefinitions(epService);
			log.info("ESPAD BOX CSVEventInitialiser finished sending event defs");
		} catch (Throwable t) {
			log.error("Error: " + t.getMessage(), t);
		}
	}
}
