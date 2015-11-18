package br.com.tonholosolutions.clattermapping.orm;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created on 12/11/2015.
 * @author rafaeltonholo
 */
@Database(name = ClatterDatabase.NAME, version = ClatterDatabase.VERSION)
public class ClatterDatabase {
    public static final String NAME = "ClatterDB";
    public static final int VERSION = 1;
}
