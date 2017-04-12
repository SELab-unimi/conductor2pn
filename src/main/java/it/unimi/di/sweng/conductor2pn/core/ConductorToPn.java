package it.unimi.di.sweng.conductor2pn.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ConductorToPn {

    private ConductorToPn(){

    }

    public static class ConductorToPnBuilder {

        private String inputWorkflowPath = null;

        public ConductorToPnBuilder setWorkflowPath(String path) {
            this.inputWorkflowPath = path;
            return this;
        }

        public ConductorToPn build() {
            if(inputWorkflowPath == null)
                return null;
            JsonObject json = null;
            try {
                json = new JsonParser()
                        .parse(new FileReader(new File(inputWorkflowPath)))
                        .getAsJsonObject();
            } catch (FileNotFoundException e) {
                return null;
            }
            return new ConductorToPn();
        }

    }
}
