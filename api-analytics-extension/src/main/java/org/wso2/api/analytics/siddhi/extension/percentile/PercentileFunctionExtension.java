/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.api.analytics.siddhi.extension.percentile;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.exception.OutOfRangeException;
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
        if(attributeExpressionExecutors[2] instanceof ConstantExpressionExecutor) {
            double percentile = (Double.parseDouble(String.valueOf(((ConstantExpressionExecutor) attributeExpressionExecutors[2]).getValue())))/ 100;
            try {
                zValue = new NormalDistribution().inverseCumulativeProbability(percentile);
            } catch (OutOfRangeException ex){
                throw new ExecutionPlanValidationException("Invalid value found for the 3rd argument of PercentileFunctionExtension, Percentile should be in the range of [0, 100]");
            }
        } else {
            throw new ExecutionPlanValidationException("Invalid parameter type found for the 3rd argument of PercentileFunctionExtension, " +
                    "required  a constant value of type" + Attribute.Type.DOUBLE + " or " + Attribute.Type.INT +" in the range of [0, 100], but found " + attributeExpressionExecutors[1].getReturnType().toString());
        }
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
