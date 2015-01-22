package com.netaporter.test.utils.cucumber.glue;

import com.netaporter.test.utils.cucumber.formatters.JSONReportMerger;
import com.netaporter.test.utils.cucumber.formatters.JunitReportMerger;
import net.masterthought.cucumber.ReportBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by a.makarenko on 1/13/14.
 * This class is used to generate Cucumber HTML reports when test is not run on Jenkins
 * and the Cucumber-reporting Jenkins plugin is not available (http://www.masterthought.net/section/cucumber-reporting)
 * Adds a shutdown hook that will look for json reports in ${jsonReportPaths} and generate HTML report in ${jsonReportOutputPath}
 */
@Component
public class ReportGeneratorShutdownHook extends Thread {
    @Value("#{'${jsonReportPaths}'.split(',')}")
    private List<String> reportPaths;
    @Value("${reportOutputPath}")
    private String reportOutputPath;
    static Logger logger = LoggerFactory.getLogger(ReportGeneratorShutdownHook.class);

    public void run(){
            File reportOutputDirectory = null;
            String now  = DateTime.now().toString("yyyyMMddHHmmss");

            if(!reportOutputPath.equals("${reportOutputPath}")){
                reportOutputDirectory = new File(reportOutputPath + now);
            }
            else{
                reportOutputDirectory = new File("test-results/cucumber-reporting/cucumber-html-reports" + now);
            }
            List<String> jsonReportFiles = new ArrayList<String>();
            for(String path:reportPaths){
                if(path.equals("${jsonReportPaths}")) {
                    logger.info("Json report location path not specified - defaulting to 'test-results/cucumber/cucumber.json'");
                    jsonReportFiles.add("test-results/cucumber/cucumber.json");
                }
                else{
                    if(new File(path).isFile()){
                        jsonReportFiles.add(path);
                        logger.info("Adding json report location path: " + path);
                    }
                }
            }

            try {
                ReportBuilder reportBuilder = new ReportBuilder(jsonReportFiles,reportOutputDirectory,"","local build","cucumber-jvm",false, false,true,true,false,"",true);
                logger.info("Generating cucumber html report from json files to " + reportOutputDirectory);
                reportBuilder.generateReports();
            }catch(FileNotFoundException e){
                logger.error("Could not find the cucumber json reports to process. Make sure you have " +
                        "correctly specified the paths to the reports in spring config");
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        //If using rerun formatter - we want to have a combined report of the initial run and rerun
        //so that the tests passed during rerun are not marked as failed.
            logger.debug("Checking if this is rerun :" + System.getProperty("rerun"));
            if(System.getProperty("rerun").equals("true")){
                logger.info("Looking for the reports");
                for(String report: jsonReportFiles){
                   //Assuming that the rerun json file name contains 'rerun'
                   //Look for the rerun result
                    if(report.contains("rerun")) {
                        //resolve path to original report
                        String rerunpath = report;
                        report = report.replace("rerun", "");
                        if (new File(report).exists()) {
                            logger.info("Report found at: " + report + ", searching for rerun results...");
                            if (new File(rerunpath).exists()) {
                                logger.info("found rerun file at: " + rerunpath);
                                logger.info("Trying to merge " + rerunpath + " with " + report);
                                JSONReportMerger.replace(report, rerunpath);
                            }

                        }
                    }
                }

                //Create the merged report of initial run and rerun
                try {
                    //After the previous stepo all rerun files should be renamed
                    for(int i = 0; i<jsonReportFiles.size(); i++){
                        if(jsonReportFiles.get(i).contains("rerun")){
                            jsonReportFiles.set(i, jsonReportFiles.get(i).replace("rerun", ""));
                        }
                    }
                    ReportBuilder reportBuilder = new ReportBuilder(jsonReportFiles, new File(reportOutputDirectory.toString() + "-merge"),"","local build","cucumber-jvm",false, false,true,true,false,"",true);
                    logger.info("Generating cucumber html report from merged json files to " + reportOutputDirectory + "-merge");
                    reportBuilder.generateReports();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(System.getProperty("junit.result.file.path")!=null){
                    String rerunpath = System.getProperty("junit.result.file.path");
                    String path = rerunpath.replace("rerun", "");

                    if(new File(path).exists()){
                        logger.info("Found junit report at: " + path);
                        if(new File(rerunpath).exists()){
                            logger.info("Found rerun junit report at: " + rerunpath);
                            logger.info("Trying to merge junit reports...");
                            JunitReportMerger.replace(path, rerunpath);
                        }
                    }

                }


            }
        }
    }

