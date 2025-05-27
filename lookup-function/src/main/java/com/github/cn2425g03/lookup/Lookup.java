package com.github.cn2425g03.lookup;

import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstancesClient;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.util.List;

public class Lookup implements HttpFunction {

    private final static String PROJECT_ID = "cn2425-t3-g03";
    private final static String ZONE = "europe-southwest1-a";

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {

        try (BufferedWriter writer = httpResponse.getWriter(); InstancesClient client = InstancesClient.create()) {

            List<String> ips = client.list(PROJECT_ID, ZONE).getPage().getResponse().getItemsList().stream()
                    .filter(instance -> instance.getStatus().equals(Instance.Status.RUNNING.name()))
                    .map(instance -> instance.getNetworkInterfaces(0).getAccessConfigs(0).getNatIP())
                    .toList();

            Gson gson = new Gson();
            String json = gson.toJson(ips);

            writer.write(json);
        }

    }

}
