package com.netaporter.test.utils.assertion.objectcomparison;

import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparator;
import org.unitils.reflectionassert.ReflectionComparatorMode;
import org.unitils.reflectionassert.difference.Difference;

import static com.netaporter.test.utils.assertion.objectcomparison.NAPReflectionComparatorFactory.createReflectionComparator;

/**
 * Created by IntelliJ IDEA.
 * User: cucumber
 * Date: 31/07/12
 * Time: 11:50
 */
public class NapReflectionAssert extends ReflectionAssert {


    public static void assertReflectionEqualsNAP(String message, Object expected, Object actual, ReflectionComparatorMode... modes) throws AssertionFailedError {

        ReflectionComparator reflectionComparator = createReflectionComparator(modes);

        Difference difference = reflectionComparator.getDifference(expected, actual);
        if (difference != null) {
            Assert.fail(getFailureMessage(message, difference));
        } else{
            Assert.assertEquals(true, true);
        }
    }
}





