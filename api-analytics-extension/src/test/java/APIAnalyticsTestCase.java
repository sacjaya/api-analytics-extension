/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.log4j.Logger;
import org.junit.Test;
import org.wso2.api.analytics.siddhi.extension.percentile.PercentileFunctionExtension;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.core.util.EventPrinter;

public class APIAnalyticsTestCase {
    private static Logger logger = Logger.getLogger(APIAnalyticsTestCase.class);
    protected static SiddhiManager siddhiManager;


    @Test
    public void testProcess() throws Exception {
        siddhiManager = new SiddhiManager();
        siddhiManager.setExtension("analytics:percentile",PercentileFunctionExtension.class);


        String streams = "  define stream statResponseStream (consumerKey STRING ,context STRING, api_version STRING,api STRING, resourcePath STRING, method STRING,version STRING, response INT, responseTime LONG,serviceTime LONG,backendTime LONG,user STRING,eventTime LONG,tenantDomain STRING," +
                "host STRING,apiPublisher STRING,application STRING, applicationId STRING,cacheHit BOOL, responseSize LONG,protocol STRING); "  ;
        String executionPlan = "@info(name = 'query1') " +
                "from statResponseStream  " +
                "select api_version, avg(responseTime) as avgResponseTime, stddev(responseTime) as sdResponseTime " +
                "group by api_version " +
                "insert into rawDataStream ; "       +

                "from rawDataStream " +
                "select api_version , analytics:percentile(avgResponseTime, sdResponseTime, 50) as percentile " +
                "insert into percentileInfoStream ; "
                ;


        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + executionPlan);

        executionPlanRuntime.addCallback("percentileInfoStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
            }
        });


        InputHandler inputHandler = executionPlanRuntime
                .getInputHandler("statResponseStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{"consumerKey" ,"context", "api_version" ,"api", "resourcePath", "method","version", 123, 100l ,25l ,34l ,"user", 67l ,"tenantDomain" ,"host","apiPublisher","application", "applicationId",true, 34l,"protocol"});
        inputHandler.send(new Object[]{"consumerKey" ,"context", "api_version" ,"api", "resourcePath", "method","version", 123, 200l ,25l ,34l ,"user", 67l ,"tenantDomain" ,"host","apiPublisher","application", "applicationId",true, 34l,"protocol"});
        Thread.sleep(1000000);
        executionPlanRuntime.shutdown();
    }
}
