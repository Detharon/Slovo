package com.dth.service.transfer;

/**
 * {@code TransferModes} describe the format of data that's being transferred.
 *
 * Available modes are:
 * <ul>
 * <li><b>CSV</b> - Comma-separated values.</li>
 * <li><b>XML</b> - eXtensible Markup Language.</li>
 * </ul>
 */
public enum TransferModes {
    CSV("Csv"),
    XML("XML");

    private final String name;

    /**
     * Creates the transfer mode with the given name.
     * 
     * @param name the name of the transfer mode.
     */
    TransferModes(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this transfer mode.
     * 
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the {@code String} representation of this transfer mode, which
     * is simply its name.
     * 
     * @return the {@code String} representation.
     */
    @Override
    public String toString() {
        return name;
    }
}
