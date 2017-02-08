package com.dth.service.transfer;

public enum TransferModes {
        CSV("Csv"),
        XML("XML");

        private final String name;

        TransferModes(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
