package com.netaporter.test.utils.assertion.objectcomparison;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: cucumber
 * Date: 02/08/12
 * Time: 17:37
 *
 * In case Rest Assured provides us with the needed functionality to check if an object is what is expeceted,
 * this class and the whole assertion framework could be deleted.
 */
@Deprecated
public abstract class BaseClassForAssertingFields {
    protected List<String> ignoreFields = new ArrayList<String>();
    protected List<String> isNotNullFields = new ArrayList<String>();

    public List<String> getIsNotNullFields(){
        return isNotNullFields;
    }

    public void setIsNotNullFields(String isNotNullField){
        isNotNullFields.add(isNotNullField);
    }

        public List<String> getIgnoreFields(){
        return ignoreFields;
    }

    public void setIgnoreFields(String ignoreField){
        ignoreFields.add(ignoreField);
    }
}
