package com.espad.box;

import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espad.ei.StatementListener;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.map.MapEventBean;

public class StatementsListener implements ServletContextListener {
	private static final Log log = LogFactory.getLog(StatementsListener.class);

	public void contextDestroyed(ServletContextEvent arg0) {

	}

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		log.info("ESPAD ScenStatementsListener contextInitialized entered");
		EPServiceProvider epService = EngineManager
				.getEngineFromServletContext(servletContextEvent);

		String[] allStatNames = epService.getEPAdministrator()
				.getStatementNames();
		for (final String statementName : allStatNames) {
			log.info("ESPAD STATEMENT " + statementName);
			EPStatement statement = epService.getEPAdministrator()
					.getStatement(statementName);

			statement.addListener(new StatementListener(statementName));
		}
	}


}
