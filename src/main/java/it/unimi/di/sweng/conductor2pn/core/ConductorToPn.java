package it.unimi.di.sweng.conductor2pn.core;

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
            return new ConductorToPn();
        }

    }
}
