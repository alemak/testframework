package com.netaporter.test.utils.assertion.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.JsonLoader;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import com.github.fge.jsonschema.main.JsonSchema;

public class JSONSchemaMatcher extends BaseMatcher<String> {

    public static Matcher<String> matchesJSONSchema(String jsonSchema) {
        return new JSONSchemaMatcher(jsonSchema);
    }

    /*public static Matcher<String> matchesJSONSchema(InputStream jsonSchema) {
        return null;
    }

    public static Matcher<String> matchesJSONSchema(Reader jsonSchema) {
        return null;
    }

    public static Matcher<String> matchesJSONSchema(File jsonSchema) {
        return null;
    }*/

    private JsonSchema schema;
    private ProcessingReport processingReport;

    public JSONSchemaMatcher(String jsonSchema)  {
        try {
            JsonNode schemaNode = JsonLoader.fromString(jsonSchema);
            schema = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode);
        } catch (ProcessingException e) {
            // Converting to runtime exception to fail the test
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean matches(Object o) {
        if ((o != null) && (o instanceof String)) {
            try {
                processingReport = schema.validate(JsonLoader.fromString((String) o));
                return processingReport.isSuccess();
            } catch (ProcessingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("matches the JSON Schema");
        // TODO review how to neatly report mismatches.  RestAssured doesn't seem to call describeMismatch() for forcing it here
        describeMismatch(null, description);
    }

    // @Override Can re-override when update to hamcrest 1.3
    public void describeMismatch(Object item, Description description) {
        // TODO handle other validation fails
        // TODO show all validation fails (rather than failing fast on first exception)
        if((processingReport != null) && (!processingReport.isSuccess())) {
            ProcessingMessage nextMessage;
            JsonNode nextMessageNode;
            for ( Iterator<ProcessingMessage> reportIter = processingReport.iterator(); reportIter.hasNext(); ) {
                nextMessage = reportIter.next();
                nextMessageNode = nextMessage.asJson();

                description.appendText("\n\t" + nextMessage.getMessage());
                description.appendText("\n\t\tObject: " + nextMessageNode.get("instance").get("pointer").asText());
                if (nextMessageNode.get("keyword").asText().equals("required")) {
                    description.appendText("\n\t\tRequired: " + nextMessageNode.get("required"));
                    description.appendText("\n\t\tMissing: " + nextMessageNode.get("missing"));

                } else if (nextMessageNode.get("keyword").asText().equals("type")) {
                    description.appendText("\n\t\tExpected: " + nextMessageNode.get("expected"));
                    description.appendText("\n\t\tFound: " + nextMessageNode.get("found"));

                }



            }
        }
    }
}
