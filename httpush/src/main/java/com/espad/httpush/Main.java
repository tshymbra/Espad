package com.espad.httpush;

import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espad.ei.CSVEventInitialiser;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esperio.csv.CSVInputAdapterSpec;

public class Main {

	/**
	 * @param args
	 */
	private final String engineURI;
	private static final Log log = LogFactory.getLog(Main.class);
	
	public static void main(String[] args) {
        log.info("ESPAD httpush - event pushing over HTTP");
        if (args.length < 1) {
           log.error("Missing event data folder name. Exiting");
           return;
        }
        
        String eventDataFolder = args[0]; 
        
        //Run
        Main main = new Main();
        main.run(eventDataFolder);
	}
	
	public Main() {
		this.engineURI = "EspadHTTPMain";
	}
	
	public void run(String eventsDataFolder)
    {
        // load config - this defines the XML event types to be processed
        String configFile = "esper.xml";
        URL url = Main.class.getClassLoader().getResource(configFile);
        if (url == null) {
        	System.out.println("ESPAD error loading configuration file '" + configFile + "' from classpath");
            return;
        }
        Configuration configuration = new Configuration();
        configuration.configure(url);
 
        // get engine instance
        EPServiceProvider epService = EPServiceProviderManager.getProvider(engineURI, configuration);
 
        CSVEventInitialiser csvEventInitialiser = new CSVEventInitialiser();
         
        List<String> eventDefinitions = csvEventInitialiser.sendEventDefinitions(epService);

        /*EPStatement stmt = epService.getEPAdministrator().createEPL
        (
        	//"select name, value from Event.win:length(100)"
        	"select * from pattern [every o=SomeEvent -> b=FollowerEvent]"
        );
        
        stmt.addListener(new UpdateListener() {
			
			public void update(EventBean[] arg0, EventBean[] arg1) {
	            log.info("[ESPAD FIRED event listener ");
				
			}
		});*/

        Coordinator coordinator = new Coordinator();
        coordinator.InitPlayback(epService,eventsDataFolder, eventDefinitions);
        //HTTPEventPusher sd = new HTTPEventPusher();


    }
}
