package it.unimi.di.sweng.conductor2pn.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.di.sweng.conductor2pn.data.TBNet;

import java.io.*;

public class ConductorToPn {

    public static final String TIMEOUT_POLICY = "timeoutPolicy";

    private TBNet model = null;

    private ConductorToPn(TBNet model){
        this.model = model;
    }

    public void createOutputModel(Writer out) throws IOException {
        out.write("TO DO");
        out.flush();
        out.close();
    }

    public static class ConductorToPnBuilder {

        private String inputWorkflowPath = null;
        private String inputWorkerTasksPath = null;

        public ConductorToPnBuilder setWorkflowPath(String path) {
            this.inputWorkflowPath = path;
            return this;
        }

        public ConductorToPnBuilder setWorkerTasksPath(String path) {
            this.inputWorkerTasksPath = path;
            return this;
        }

        public ConductorToPn build() {
            if(inputWorkerTasksPath == null)
                return null;
            JsonArray workers = null;
            try {
                workers = new JsonParser()
                        .parse(new FileReader(new File(inputWorkerTasksPath)))
                        .getAsJsonArray();
            } catch (FileNotFoundException e) {
                return null;
            }

            TBNet workflowModel = new TBNet();
            for(JsonElement w: workers)
                workflowModel.createWorker(w);

            if(inputWorkflowPath != null) {
                JsonObject workflow = null;
                try {
                    workflow = new JsonParser()
                            .parse(new FileReader(new File(inputWorkflowPath)))
                            .getAsJsonObject();

                    workflowModel.createWorkflow(workflow);
                } catch (FileNotFoundException e) {
                    return null;
                }
            }

            return new ConductorToPn(workflowModel);
        }
    }
}
