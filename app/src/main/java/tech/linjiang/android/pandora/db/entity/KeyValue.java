package tech.linjiang.android.pandora.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by linjiang on 2018/3/13.
 */

@Entity
public class KeyValue {
    public KeyValue() {
    }

    @Ignore
    public KeyValue(String key, boolean value) {
        this.key = key;
        this.value = value;
    }

    @PrimaryKey
    @NonNull
    public String key;

    public boolean value = true;
}
