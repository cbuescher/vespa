// Copyright 2017 Yahoo Holdings. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.search.grouping.request;

/**
 * This class represents an maximum-aggregator in a {@link GroupingExpression}. It evaluates to the maximum value that
 * the contained expression evaluated to over all the inputs.
 *
 * @author Simon Thoresen Hult
 */
public class MaxAggregator extends AggregatorNode {

    /**
     * Constructs a new instance of this class.
     *
     * @param exp The expression to aggregate on.
     */
    public MaxAggregator(GroupingExpression exp) {
        super("max", exp);
    }
}
