package com.netaporter.test.utils.assertion.objectcomparison;

import com.netaporter.test.utils.assertion.objectcomparison.NAPObjectComparator;
import com.netaporter.test.utils.assertion.objectcomparison.NAPisNotNullComparator;
import org.unitils.reflectionassert.ReflectionComparator;
import org.unitils.reflectionassert.ReflectionComparatorMode;
import org.unitils.reflectionassert.comparator.Comparator;
import org.unitils.reflectionassert.comparator.impl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.unitils.reflectionassert.ReflectionComparatorMode.*;
import static org.unitils.util.CollectionUtils.asSet;

/**
 * Created by IntelliJ IDEA.
 * User: cucumber
 * Date: 01/08/12
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */
public class NAPReflectionComparatorFactory {


    /**
     * The LenientDatesComparator singleton insance
     */
    protected static final Comparator LENIENT_DATES_COMPARATOR = new LenientDatesComparator();

    /**
     * The IgnoreDefaultsComparator singleton insance
     */
    protected static final Comparator IGNORE_DEFAULTS_COMPARATOR = new IgnoreDefaultsComparator();

    /**
     * The LenientNumberComparator singleton insance
     */
    protected static final Comparator LENIENT_NUMBER_COMPARATOR = new LenientNumberComparator();

    /**
     * The SimpleCasesComparatorsingleton insance
     */
    protected static final Comparator SIMPLE_CASES_COMPARATOR = new SimpleCasesComparator();

    /**
     * The LenientOrderCollectionComparator singleton insance
     */
    protected static final Comparator LENIENT_ORDER_COMPARATOR = new LenientOrderCollectionComparator();

    /**
     * The CollectionComparator singleton insance
     */
    protected static final Comparator COLLECTION_COMPARATOR = new CollectionComparator();

    /**
     * The MapComparator singleton insance
     */
    protected static final Comparator MAP_COMPARATOR = new MapComparator();

    /**
     * The HibernateProxyComparator singleton insance
     */
    protected static final Comparator HIBERNATE_PROXY_COMPARATOR = new HibernateProxyComparator();

    /**
     * The ObjectComparator singleton insance. New one added by NAP Communities team
     */
    //protected static final Comparator OBJECT_COMPARATOR = new ObjectComparator();
    protected static final Comparator NAP_OBJECT_COMPARATOR = new NAPObjectComparator();

    /**
     * The NAP_ISNOTNULL_COMPARATOR singleton insance. New one added by NAP Communities team
     */
    protected static final Comparator NAP_ISNOTNULL_COMPARATOR= new NAPisNotNullComparator();


    /**
     * Creates a reflection comparator for the given modes.
     * If no mode is given, a strict comparator will be created.
     *
     * @param modes The modes, null for strict comparison
     * @return The reflection comparator, not null
     */
    public static ReflectionComparator createReflectionComparator(ReflectionComparatorMode... modes) {
        List<Comparator> comparators = getComparatorChain(asSet(modes));
        return new ReflectionComparator(comparators);
    }


    /**
     * Creates a comparator chain for the given modes.
     * If no mode is given, a strict comparator will be created.
     *
     * @param modes The modes, null for strict comparison
     * @return The comparator chain, not null
     */
    protected static List<Comparator> getComparatorChain(Set<ReflectionComparatorMode> modes) {
        List<Comparator> comparatorChain = new ArrayList<Comparator>();
        if (modes.contains(LENIENT_DATES)) {
            comparatorChain.add(LENIENT_DATES_COMPARATOR);
        }
        if (modes.contains(IGNORE_DEFAULTS)) {
            comparatorChain.add(IGNORE_DEFAULTS_COMPARATOR);
        }
        //comparatorChain.add(NAP_ISNOTNULL_COMPARATOR);  //added by NAP communities team. Checks this first in chain
        comparatorChain.add(LENIENT_NUMBER_COMPARATOR);
        comparatorChain.add(SIMPLE_CASES_COMPARATOR);
        if (modes.contains(LENIENT_ORDER)) {
            comparatorChain.add(LENIENT_ORDER_COMPARATOR);
        } else {
            comparatorChain.add(COLLECTION_COMPARATOR);
        }
        comparatorChain.add(MAP_COMPARATOR);
        comparatorChain.add(HIBERNATE_PROXY_COMPARATOR);
        comparatorChain.add(NAP_OBJECT_COMPARATOR);
        return comparatorChain;
    }
}
