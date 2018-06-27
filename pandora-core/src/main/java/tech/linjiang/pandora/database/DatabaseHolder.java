package tech.linjiang.pandora.database;

import tech.linjiang.pandora.database.protocol.IDescriptor;
import tech.linjiang.pandora.database.protocol.IDriver;

/**
 * Created by linjiang on 29/05/2018.
 */

public class DatabaseHolder {
    public IDescriptor descriptor;
    public IDriver<? extends IDescriptor> driver;

    public DatabaseHolder(IDescriptor descriptor, IDriver<? extends IDescriptor> driver) {
        this.descriptor = descriptor;
        this.driver = driver;
    }
}
