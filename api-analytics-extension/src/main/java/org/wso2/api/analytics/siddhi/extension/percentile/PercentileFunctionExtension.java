package org.wso2.api.analytics.siddhi.extension.percentile;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.wso2.siddhi.core.config.ExecutionPlanContext;
import org.wso2.siddhi.core.executor.ConstantExpressionExecutor;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.executor.function.FunctionExecutor;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.exception.ExecutionPlanValidationException;


public class PercentileFunctionExtension extends FunctionExecutor {
    private double zValue;

    //avgResponseTime, sdResponseTime, 95

    @Override
    protected void init(ExpressionExecutor[] attributeExpressionExecutors, ExecutionPlanContext executionPlanContext) {
        if (attributeExpressionExecutors.length != 3) {
            throw new ExecutionPlanValidationException("Invalid no of arguments passed to PercentileFunctionExtension, required 3, " +
                    "but found " + attributeExpressionExecutors.length);
        }

        if (attributeExpressionExecutors[0].getReturnType() != Attribute.Type.DOUBLE ) {
            throw new ExecutionPlanValidationException("Invalid parameter type found for the 1st argument of PercentileFunctionExtension, " +
                    "required " + Attribute.Type.DOUBLE + " but found " + attributeExpressionExecutors[0].getReturnType().toString());
        }

        if (attributeExpressionExecutors[1].getReturnType() != Attribute.Type.DOUBLE ) {
            throw new ExecutionPlanValidationException("Invalid parameter type found for the 2nd argument of PercentileFunctionExtension, " +
                    "required " + Attribute.Type.DOUBLE + " but found " + attributeExpressionExecutors[1].getReturnType().toString());
        }

        double percentile = (double) Integer.parseInt(String.valueOf(((ConstantExpressionExecutor) attributeExpressionExecutors[2]).getValue()))/100;
        zValue = new NormalDistribution().inverseCumulativeProbability(percentile);

    }

    //X =  mean+ z*sd
    @Override
    protected Object execute(Object[] data) {
        double mean = (Double)data[0];
        double sd  = (Double) data[1];
        return  (mean+(zValue*sd));

    }

    @Override
    protected Object execute(Object data) {
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Attribute.Type getReturnType() {
        return Attribute.Type.DOUBLE;
    }

    @Override
    public Object[] currentState() {
        return new Object[0];
    }

    @Override
    public void restoreState(Object[] state) {

    }
}
