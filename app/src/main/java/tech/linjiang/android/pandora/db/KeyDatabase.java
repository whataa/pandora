package tech.linjiang.android.pandora.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import tech.linjiang.android.pandora.MyApp;
import tech.linjiang.android.pandora.db.dao.KeyDao;
import tech.linjiang.android.pandora.db.entity.KeyValue;


/**
 * Created by linjiang on 2018/3/13.
 */

@Database(
        entities = {
                KeyValue.class
        },
        version = 1,
        exportSchema = false)
public abstract class KeyDatabase extends RoomDatabase {

    public static final String TAG = "KeyDatabase";

    public abstract KeyDao keyDao();


    private static KeyDatabase db =
            Room.databaseBuilder(MyApp.getContext(), KeyDatabase.class, "key.db")
                    .allowMainThreadQueries()
                    .addMigrations(
                            new MIGRATE_1_2()
                    )
                    .build();

    public static KeyDatabase get() {
        return db;
    }

    private static class MIGRATE_1_2 extends Migration {

        MIGRATE_1_2() {
            super(1, 2);
        }

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    }
}
