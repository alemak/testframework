package com.netaporter.test.utils.assertion.objectcomparison;

import org.unitils.reflectionassert.ReflectionComparator;
import org.unitils.reflectionassert.comparator.Comparator;
import org.unitils.reflectionassert.difference.Difference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.unitils.util.CollectionUtils.convertToCollection;

/**
 * Created by IntelliJ IDEA.
 * User: cucumber
 * Date: 01/08/12
 * Time: 14:32
 * To change this template use File | Settings | File Templates.
 */
public class NAPisNotNullComparator implements Comparator {


    /**
     * Returns true if the left object is a map where the key matches an item in the list of values
     *
     * @param left  The left object
     * @param right The right object
     * @return True if left is null, false or 0
     */
    public boolean canCompare(Object left, Object right) {

        //firstly check if left object is a map
        if ((left instanceof Map)) {

            //get the key and list of not null fields
            Map<?, ?> leftMap = (Map<?, ?>) left;
            Object keyName = new Object();
            for (Map.Entry<?, ?> leftEntry : leftMap.entrySet()) {
                keyName = leftEntry.getKey();    //if this is a genuine check for isNotNull then there will only be 1 key
                if (leftEntry.getValue() instanceof ArrayList){
                    List<Object> notNullFieldNames = new ArrayList<Object>(convertToCollection(leftEntry.getValue()));

                    //check if the key name (field to check) is in the list of fields set aside to check if they are not null
                    if (notNullFieldNames.contains(keyName)){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Always returns null: both objects are equal.
     *
     * @param left                 The left object
     * @param right                The right object
     * @param onlyFirstDifference  True if only the first difference should be returned
     * @param reflectionComparator The root comparator for inner comparisons, not null
     * @return null
     */
    public Difference compare(Object left, Object right, boolean onlyFirstDifference, ReflectionComparator reflectionComparator) {

        if (right == null){
            return new Difference("Is Not Null comparator. The actual value is null when it should have been not null", "Not Null", right);
        }

        return null;
    }
}
