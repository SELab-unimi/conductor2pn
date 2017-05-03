package it.unimi.di.sweng.conductor2pn.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.di.sweng.conductor2pn.data.TBNet;
import org.apache.commons.cli.*;

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

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("w", "workers", true, "Worker tasks input file");
        options.addOption("s", "systemTasks", true, "System tasks input file");
        options.addOption("o", "output", true, "Output PNML file");
        options.addOption("h", "help", false, "Print this message");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if(cmd.hasOption("h") || !cmd.hasOption("w") || !cmd.hasOption("s") || !cmd.hasOption("o")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("ConductorToPn [options]", options);
            }
            else {
                ConductorToPn conductor2PnEngine = new ConductorToPn.ConductorToPnBuilder()
                        .setWorkerTasksPath(cmd.getOptionValue("w"))
                        .setWorkflowPath(cmd.getOptionValue("s"))
                        .setWorkerGenerator(new TBWorkerGenerator())
                        .setWorkflowGenerator(new TBWorkflowGenerator())
                        .build();

                File outputFile = new File(cmd.getOptionValue("o"));
                outputFile.createNewFile();

                Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(outputFile, false)));
                conductor2PnEngine.createOutputModel(writer);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }
}
