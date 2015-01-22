package com.netaporter.test.utils.cucumber.formatters;


import com.google.gson.*;
import com.google.gson.stream.JsonWriter;

import java.io.*;

/**
 * Created by a.makarenko on 02/12/2014.
 * Merges cucumber JSON report files
 */
public class JSONReportMerger {

    /**
     * Overwrites the results of the test scenario from origFile with
     * result of the same scenario from overrideFile
     * The report files' extensions renamed to 'bak' and the result file gets
     * the name of the original file
     * This is used for rerun formatter to combine the results from rerun with the initial
     * run report.
     * */
    public static void replace (String origFile, String overrideFile){
        JsonArray orig = readJson(origFile);
        JsonArray override = readJson(overrideFile);
        for(JsonElement e : override){
            replaceDuplicateScenarios(orig, e);
        }
        JsonWriter writer = null;
        try {
            Gson gson = new Gson();
            rename(origFile);
            rename(overrideFile);
            writer = new JsonWriter(new FileWriter(new File(origFile)));
            writer.beginArray();
            for(JsonElement je:orig){
                gson.toJson(je, writer);
            }
            writer.endArray();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Read the contents of cucumber json report file
     * */
    static JsonArray readJson(String filePath){
        JsonArray jsonArray = null;
        try{
            FileReader reader = new FileReader(filePath);
            JsonParser jsonParser = new JsonParser();
            jsonArray = (JsonArray) jsonParser.parse(reader);

        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return jsonArray;
    }
    /**
     * Replace the scenario result in the original report to the one from rerun report
     * */
    static void replaceDuplicateScenarios(JsonArray array, JsonElement e){
        for(JsonElement element:array){
            if(isSameFeature(element.getAsJsonObject(), e.getAsJsonObject())) {
                for(JsonElement je: e.getAsJsonObject().getAsJsonArray("elements")){
                    replaceSameScenarios(element.getAsJsonObject().getAsJsonArray("elements"), je.getAsJsonObject());
                }
            }
        }
    }

    /**
     * Check if the test results are for the same feature
     * */
    static boolean isSameFeature(JsonObject first, JsonObject second){
        return first.has("keyword") && first.get("keyword").getAsString().equals("Feature") &&
               second.has("keyword")&& second.get("keyword").getAsString().equals("Feature") &&
               (first.get("id").getAsString() + "(rerun)").equals(second.get("id").getAsString()) &&
               (first.get("name").getAsString()+"(rerun)").equals(second.get("name").getAsString()) &&
               first.get("description").equals(second.get("description")) &&
               first.get("line").equals(second.get("line")) &&
               (first.get("uri").getAsString() + "rerun").equals(second.get("uri").getAsString());

    }

    static void replaceSameScenarios(JsonArray arr, JsonObject o){
         for(int i = 0; i<arr.size()-1; i++){
             if(isSameScenario(arr.get(i).getAsJsonObject(), o)){
                 arr.set(i,o);
                 break;
             }
         }
    }
    /**
     * Check if the test results are for the same scenario
     * */
    static boolean isSameScenario(JsonObject scenario1, JsonObject scenario2) {
        return scenario1.has("type") && scenario1.get("type").getAsString().equals("scenario") &&
                scenario2.has("type") && scenario1.get("type").getAsString().equals("scenario") &&
                scenario1.get("id").equals(scenario2.get("id"))&&
                scenario1.get("name").equals(scenario2.get("name"))&&
                scenario1.get("line").equals(scenario2.get("line"));


    }
    /**
     * Rename the original file to backup (.bak)
     * */
    static boolean rename(String fileName){
        File file = new File(fileName);
        // File (or directory) with new name
        File file2 = new File(fileName.replace(".json", ".bak"));

        // Rename file (or directory)
        boolean success = file.renameTo(file2);
        if (!success) {
            return false;
        }
        else return true;
    }
}
