package com.netaporter.test.utils.cucumber;

import com.netaporter.test.utils.cucumber.glue.ReportGeneratorShutdownHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 *
 */
@Component
@Scope("cucumber-glue")
public class ScenarioSession {
static Logger logger = LoggerFactory.getLogger(ScenarioSession.class);
@Autowired
ReportGeneratorShutdownHook hook;

    //Creates the cucumber test report with result summary when running locally (similar to Jenkins plugin)
    //To enable - run with -DgenerateReport=true VM option and set the paths to json reports in the spring properties file
    @PostConstruct
    public void generateReport(){
        if(System.getProperty("generateReport")!= null && System.getProperty("generateReport").equals("true")){

            try{
                Runtime.getRuntime().addShutdownHook(hook);
            }catch (IllegalArgumentException e){
                logger.debug("Report Generator shutdown hook already registered - skipping");
            }
        }
    }
    private Map<String, Object> sessionData = new HashMap<String, Object>();

    public void putData(String key, Object value) {
        sessionData.put(key, value);
    }


    public void removeData(String key) {
        try {
            sessionData.remove(key);
        } catch (NullPointerException ignored) {
            }
    }

    public <T> T getData(String key){
        try{
            return (T)sessionData.get(key);
        }
        catch (Exception e){return null;}
    }

	public void addCollectionData(String key, Object value) {
		Collection<Object> data = (Collection<Object>) sessionData.get(key);
		if(data == null) {
			data = new ArrayList<Object>();
			sessionData.put(key, data);
		}
		data.add(value);
	}

    public <T> List<T> getCollectionData(List<String> keys){
        List<T> dataList = new ArrayList<T>();
        for(String key : keys){
            if (sessionData.containsKey(key)){
                dataList.add((T) sessionData.get(key));
            }
        }
        return dataList;
    }
}
