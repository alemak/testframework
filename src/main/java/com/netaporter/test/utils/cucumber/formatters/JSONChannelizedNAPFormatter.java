package com.netaporter.test.utils.cucumber.formatters;

import com.netaporter.test.utils.cucumber.steps.BaseStep;
import com.netaporter.test.utils.enums.SalesChannelEnum;
import cucumber.runtime.formatter.CucumberJSONFormatter;
import gherkin.deps.com.google.gson.Gson;
import gherkin.deps.com.google.gson.GsonBuilder;
import gherkin.deps.net.iharder.Base64;
import gherkin.formatter.JSONFormatter;
import gherkin.formatter.NiceAppendable;
import gherkin.formatter.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Alexei Makarenko
 * Date: 14/06/13
 * Time: 17:52
 * To change this template use File | Settings | File Templates.
 */
public class JSONChannelizedNAPFormatter extends JSONFormatter {
        static Logger logger  = LoggerFactory.getLogger(JSONChannelizedNAPFormatter.class);
        private final List<Map<String, Object>> featureMaps = new ArrayList<Map<String, Object>>();
        private final NiceAppendable out;

        private Map<String, Object> featureMap;
        private String uri;
        private List<Map> beforeHooks = new ArrayList<Map>();

        private enum Phase {step, match, embedding, result, output};
        private boolean inScenarioOutline = false;


    /**
     * In order to handle steps being added all at once, this method determines allows methods to
     * opperator correctly if
     *
     * step
     * match
     * embedding
     * output
     * result
     * step
     * match
     * embedding
     * output
     * result
     *
     * or if
     *
     * step
     * step
     * match
     * embedding
     * output
     * result
     * match
     * embedding
     * output
     * result
     *
     * is called
     *
     * @return the correct step for the current operation based on past method calls to the formatter interface
     */

        private Map getCurrentStep(Phase phase) {
            String target = phase.ordinal() <= Phase.match.ordinal()?Phase.match.name():Phase.result.name();
            Map lastWithValue = null;
            for (Map stepOrHook : getSteps()) {
                if (stepOrHook.get(target) == null) {
                    return stepOrHook;
                } else {
                    lastWithValue = stepOrHook;
                }
            }
            return lastWithValue;
        }


        public JSONChannelizedNAPFormatter(Appendable out) {
            super(out);
            this.out = new NiceAppendable(out);
        }
        @Override
        public void startOfScenarioLifeCycle(Scenario scenario) {
            inScenarioOutline = false;
            super.startOfScenarioLifeCycle(scenario);
        }
        @Override
        public void endOfScenarioLifeCycle(Scenario scenario) {
            // NoOp
        }

        @Override
        public void uri(String uri) {
            this.uri = uri;
        }

        @Override
        public void feature(Feature feature) {
           logger.info("Running feature: '" + feature.getName() + "'");
           if(System.getProperty("region")!=null){
               Feature myFeature = new Feature(feature.getComments(),feature.getTags(),feature.getKeyword(),feature.getName()+ " for region " +
                       System.getProperty("region"), feature.getDescription(), feature.getLine(), feature.getId()+ " for region " +
                       System.getProperty("region"));
               feature = myFeature;
               uri += System.getProperty("region");
            }
            //Channel data will override the region as more specific
           if(System.getProperty("channel")!=null){
                Feature myFeature = new Feature(feature.getComments(),feature.getTags(),feature.getKeyword(),feature.getName()+ " for channel " +
                        System.getProperty("channel"), feature.getDescription(), feature.getLine(), feature.getId()+ " for channel " +
                        System.getProperty("channel"));
               feature = myFeature;
               uri += System.getProperty("channel");
           }
           if("true".equals(System.getProperty("rerun"))){
               Feature myFeature = new Feature(feature.getComments(),feature.getTags(),feature.getKeyword(),feature.getName()+ "(rerun)",
                       feature.getDescription(), feature.getLine(), feature.getId()+ "(rerun)");
               feature = myFeature;
               uri += "rerun";
           }
            featureMap = feature.toMap();
            featureMap.put("uri", uri);
            featureMaps.add(featureMap);
        }

        @Override
        public void background(Background background) {
            getFeatureElements().add(background.toMap());
        }

        @Override
        public void scenario(Scenario scenario) {
            logger.info("Running scenario: '" + scenario.getName()+ "'");
            getFeatureElements().add(scenario.toMap());
            if (beforeHooks.size() > 0) {
                getFeatureElement().put("before", beforeHooks);
                beforeHooks = new ArrayList<Map>();
            }
        }

        @Override
        public void scenarioOutline(ScenarioOutline scenarioOutline) {
            inScenarioOutline = true;
        }

        @Override
        public void examples(Examples examples) {
            // NoOp
        }

        @Override
        public void step(Step step) {
            if (!inScenarioOutline) {
                String stepName;
                if (System.getProperty("channel") != null) {
                    if (step.getName().contains("," + BaseStep.channelIdPlaceholder + ",")) {
                        stepName = step.getName().replace("," + BaseStep.channelIdPlaceholder + ",", String.valueOf(SalesChannelEnum.valueOf(System.getProperty("channel")).getId()));
                    }
                    stepName = step.getName().replace(BaseStep.channelIdPlaceholder, String.valueOf(SalesChannelEnum.valueOf(System.getProperty("channel")).getId()));
                } else {
                    stepName = step.getName();
                }
                Step myStep = new Step(step.getComments(), step.getKeyword(), stepName, step.getLine(), step.getRows(), step.getDocString());
                getSteps().add(myStep.toMap());
            }
        }

        @Override
        public void match(Match match) {
            if(!inScenarioOutline)
            getCurrentStep(Phase.match).put("match", match.toMap());
        }

        @Override
        public void embedding(String mimeType, byte[] data) {
            final Map<String, String> embedding = new HashMap<String, String>();
            embedding.put("mime_type", mimeType);
            embedding.put("data", Base64.encodeBytes(data));
            getEmbeddings().add(embedding);
        }

        @Override
        public void write(String text) {
            getOutput().add(text);
        }

        @Override
        public void result(Result result) {
            if(!inScenarioOutline)
            getCurrentStep(Phase.result).put("result", result.toMap());
        }

        @Override
        public void before(Match match, Result result) {
            beforeHooks.add(buildHookMap(match,result));
        }

        @Override
        public void after(Match match, Result result) {
            List<Map> hooks = getFeatureElement().get("after");
            if (hooks == null) {
                hooks = new ArrayList<Map>();
                getFeatureElement().put("after", hooks);
            }
            hooks.add(buildHookMap(match,result));
        }

        private Map buildHookMap(final Match match, final Result result) {
            final Map hookMap = new HashMap();
            hookMap.put("match", match.toMap());
            hookMap.put("result", result.toMap());
            return hookMap;
        }

        public void appendDuration(final int timestamp) {
            if(!inScenarioOutline) {
                final Map result = (Map) getCurrentStep(Phase.result).get("result");
                // check to make sure result exists (scenario outlines do not have results yet)
                if (result != null) {
                    //convert to nanoseconds
                    final long nanos = timestamp * 1000000000L;
                    result.put("duration", nanos);
                }
            }
        }

        @Override
        public void eof() {
        }

        @Override
        public void done() {
            out.append(gson().toJson(featureMaps));
            // We're *not* closing the stream here.
            // https://github.com/cucumber/gherkin/issues/151
            // https://github.com/cucumber/cucumber-jvm/issues/96
        }

        @Override
        public void close() {
            out.close();
        }

        @Override
        public void syntaxError(String state, String event, List<String> legalEvents, String uri, Integer line) {
            throw new UnsupportedOperationException();
        }

        private List<Map<String, Object>> getFeatureElements() {
            List<Map<String, Object>> featureElements = (List) featureMap.get("elements");
            if (featureElements == null) {
                featureElements = new ArrayList<Map<String, Object>>();
                featureMap.put("elements", featureElements);
            }
            return featureElements;
        }

        private Map<Object, List<Map>> getFeatureElement() {
            if (getFeatureElements().size() > 0) {
                return (Map) getFeatureElements().get(getFeatureElements().size() - 1);
            } else {
                return null;
            }
        }

        private List<Map> getAllExamples() {
            List<Map> allExamples = getFeatureElement().get("examples");
            if (allExamples == null) {
                allExamples = new ArrayList<Map>();
                getFeatureElement().put("examples", allExamples);
            }
            return allExamples;
        }

        private List<Map> getSteps() {
            List<Map> steps = getFeatureElement().get("steps");
            if (steps == null) {
                steps = new ArrayList<Map>();
                getFeatureElement().put("steps", steps);
            }
            return steps;
        }

        private List<Map<String, String>> getEmbeddings() {
            if(!inScenarioOutline) {
                List<Map<String, String>> embeddings = (List<Map<String, String>>) getCurrentStep(Phase.embedding).get("embeddings");
                if (embeddings == null) {
                    embeddings = new ArrayList<Map<String, String>>();
                    getCurrentStep(Phase.embedding).put("embeddings", embeddings);
                }
                return embeddings;
            }
            return null;
        }

        private List<String> getOutput() {
            if(!inScenarioOutline) {
                List<String> output = (List<String>) getCurrentStep(Phase.output).get("output");
                if (output == null) {
                    output = new ArrayList<String>();
                    getCurrentStep(Phase.output).put("output", output);
                }
                return output;
            }
            return null;
        }

        protected Gson gson() {
            return new GsonBuilder().setPrettyPrinting().create();
        }
    }


