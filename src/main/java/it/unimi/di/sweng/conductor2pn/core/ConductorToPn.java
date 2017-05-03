package it.unimi.di.sweng.conductor2pn.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.di.sweng.conductor2pn.data.TBNet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;

public class ConductorToPn {

    private TBNet model = null;

    private ConductorToPn(TBNet model){
        this.model = model;
    }

    public TBNet getModel() {
        return model;
    }

    public void createOutputModel(Writer out) throws IOException {
        XMLOutputGenerator xmlGenerator = new XMLOutputGenerator(model);
        try {
            xmlGenerator.generate(out);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
        out.flush();
        out.close();
    }

    public static class ConductorToPnBuilder {

        private String inputWorkflowPath = null;
        private String inputWorkerTasksPath = null;
        private WorkerGenerator workerStrategy = null;
        private WorkflowGenerator workflowStrategy = null;

        public ConductorToPnBuilder setWorkflowPath(String path) {
            this.inputWorkflowPath = path;
            return this;
        }

        public ConductorToPnBuilder setWorkerTasksPath(String path) {
            this.inputWorkerTasksPath = path;
            return this;
        }

        public ConductorToPnBuilder setWorkerGenerator(WorkerGenerator workerGenerator) {
            this.workerStrategy = workerGenerator;
            return this;
        }

        public ConductorToPnBuilder setWorkflowGenerator(WorkflowGenerator workflowGenerator) {
            this.workflowStrategy = workflowGenerator;
            return this;
        }

        public ConductorToPn build() {
            if(inputWorkerTasksPath == null || workerStrategy == null)
                return null;
            JsonArray workers = null;
            try {
                workers = new JsonParser()
                        .parse(new FileReader(new File(inputWorkerTasksPath)))
                        .getAsJsonArray();
            } catch (FileNotFoundException e) {
                return null;
            }

            TBNet model = new TBNet();
            for(JsonElement w: workers)
                workerStrategy.createWorker(w, model);

            if(inputWorkflowPath != null) {
                JsonObject workflow = null;
                try {
                    workflow = new JsonParser()
                            .parse(new FileReader(new File(inputWorkflowPath)))
                            .getAsJsonObject();

                    workflowStrategy.createWorkflow(workflow, model);
                } catch (FileNotFoundException e) {
                    return null;
                }
            }

            return new ConductorToPn(model);
        }
    }
}
