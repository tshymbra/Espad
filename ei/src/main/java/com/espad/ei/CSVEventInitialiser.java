package com.espad.ei;

import java.util.List;

import com.espertech.esper.client.EPServiceProvider;

public class CSVEventInitialiser {

    public List<String> sendEventDefinitions(EPServiceProvider epService)
    {
    	return CSVManager.InitAllEventDefinitionsFromCSV(epService);
    }
    
    public synchronized String getEventDefinitionsVersion() {
		String version = "";
		
		Package aPackage = getClass().getPackage();
        if (aPackage != null) {
            version = aPackage.getImplementationVersion();
            if (version == null) {
                version = aPackage.getSpecificationVersion();
            }
        }
		return version;
    }
}
