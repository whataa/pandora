package tech.linjiang.android.pandora.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Created by linjiang on 2018/3/13.
 */

@Entity
public class Drink {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "band_name")
    public String name;

    public int flavor;

    public String color;

    public int type;

    @Ignore
    public int mark;

    @Embedded(prefix = "ingredient_")
    public Ingredient ingredient;

    /**
     * 成分
     */
    public static class Ingredient {
        public float water;
        public float energy;
        public float carbon;
    }
}
