package tech.linjiang.pandora.database;

import java.io.File;

import tech.linjiang.pandora.database.protocol.IDescriptor;

/**
 * Created by linjiang on 29/05/2018.
 */

public class DatabaseDescriptor implements IDescriptor {
    public final File file;

    public DatabaseDescriptor(File file) {
        this.file = file;
    }

    @Override
    public String name() {
        return file.getName();
    }
}
