package com.espad.box;

import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

public class EngineManager {
    private static final Log log = LogFactory.getLog(EngineManager.class);

	public static EPServiceProvider getEngineFromServletContext(ServletContextEvent servletContextEvent) {
		 EPServiceProvider epService = (EPServiceProvider) servletContextEvent.getServletContext().getAttribute("com.espertech.esper.context-param.serviceprovider");
         if (epService == null) {
             String uri = servletContextEvent.getServletContext().getInitParameter("com.espertech.esper.context-param.uri");
             if (uri == null) {
                 log.error("EPServiceProvider instance or URI has not been provided as part of the context");
                 return null;
             }
             epService = EPServiceProviderManager.getProvider(uri);
         }
         return epService;
	}
}
