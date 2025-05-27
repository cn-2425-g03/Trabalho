package com.github.cn2425g03.lookup;

import com.google.cloud.compute.v1.*;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

public class Lookup implements HttpFunction {

    private final static String PROJECT_ID = "cn2425-t3-g03";
    private final static String ZONE = "europe-southwest1-a";
    private static final String INSTANCE_GROUP_NAME = "server-instance-group";

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {

        try (
                BufferedWriter writer = httpResponse.getWriter();
                InstancesClient client = InstancesClient.create();
                InstanceGroupManagersClient groupClient = InstanceGroupManagersClient.create();
        ) {

            List<String> ips = new ArrayList<>();

            for (ManagedInstance managedInstance : groupClient
                    .listManagedInstances(PROJECT_ID, ZONE, INSTANCE_GROUP_NAME)
                    .iterateAll()) {

                String instanceUrl = managedInstance.getInstance();
                String instanceName = instanceUrl.substring(instanceUrl.lastIndexOf('/') + 1);

                Instance instance = client.get(PROJECT_ID, ZONE, instanceName);

                if (Instance.Status.RUNNING.name().equals(instance.getStatus())) {
                    String natIP = instance.getNetworkInterfaces(0).getAccessConfigs(0).getNatIP();
                    ips.add(natIP);
                }

            }

            String json = new Gson().toJson(ips);
            writer.write(json);
        }

    }

}
