package  es.artal_benedi.ListaCompra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 *
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class NotesDbAdapter {

    public static final String PRODUCT_KEY_NAME = "nombre";
    public static final String PRODUCT_KEY_PRECIO = "precio";
    public static final String PRODUCT_KEY_PESO = "peso";
    public static final String PRODUCT_KEY_ROWID = "_id";

    public static final String LIST_KEY_NAME = "nombre";
    public static final String LIST_KEY_ROWID = "_id";

    public static final String CONTAINS_KEY_LISTA = "lista";
    public static final String CONTAINS_KEY_PRODUCTO = "producto";
    public static final String CONTAINS_KEY_CANTIDAD = "cantidad";

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    //TODO: REVISAR QUE SE HAN CAMBIADO TODO LO RELATIVO A CAMBIAR LA PK DE PRODUCTOS


    /**
     * Database creation sql statement
     *
     * CREATE TABLE productos (
     * _id integer PRIMARY KEY AUTOINCREMENT,
     * nombre TEXT NOT NULL,
     * precio DOUBLE NOT NULL,
     * peso DOUBLE NOT NULL );
     *
     * CREATE TABLE listas (
     * _id integer PRIMARY KEY AUTOINCREMENT,
     * nombre TEXT NOT NULL );
     *
     * CREATE TABLE contiene (
     * producto integer,
     * lista integer,
     * cantidad integer NOT NULL,
     * PRIMARY KEY (producto, lista),
     * FOREIGN KEY (producto) REFERENCES productos(_id) ON DELETE CASCADE,
     * FOREIGN KEY (lista) REFERENCES listas(_id) ON DELETE CASCADE );
     *
     */
    private static final String DATABASE_CREATE =
                    "CREATE TABLE productos (\n" +
                    "nombre TEXT PRIMARY KEY NOT NULL,\n" +
                    "precio DOUBLE NOT NULL,\n" +
                    "peso DOUBLE NOT NULL );\n" +
                    "\n" +
                    "CREATE TABLE listas (\n" +
                    "_id integer PRIMARY KEY AUTOINCREMENT,\n" +
                    "nombre TEXT NOT NULL );\n" +
                    "\n" +
                    "CREATE TABLE contiene (\n" +
                    "producto TEXT,\n" +
                    "lista integer,\n" +
                    "cantidad integer NOT NULL,\n" +
                    "PRIMARY KEY (producto, lista),\n" +
                    "FOREIGN KEY (producto) REFERENCES productos(nombres) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                    "FOREIGN KEY (lista) REFERENCES listas(_id) ON DELETE CASCADE );";

    private static final String DATABASE_NAME = "notepad";
    private static final String DATABASE_TABLE_PRODUCTS = "productos";
    private static final String DATABASE_TABLE_LISTS = "listas";
    private static final String DATABASE_TABLE_CONTAINS = "contiene";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contiene");
            db.execSQL("DROP TABLE IF EXISTS productos");
            db.execSQL("DROP TABLE IF EXISTS listas");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *

     * @return rowId or -1 if failed
     */
    public long createList(String nombre, String[] productos) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(LIST_KEY_NAME, nombre);
        long res = mDb.insert(DATABASE_TABLE_LISTS, null, initialValues);

        //TODO
        /*
          "producto TEXT,\n" +
                    "lista integer,\n" +
                    "cantidad integer NOT NULL,\n"

         */
        if(res != -1){
            for(String producto: productos) {
                ContentValues initial = new ContentValues();

                initial.put(CONTAINS_KEY_LISTA, res);
                initial.put(CONTAINS_KEY_PRODUCTO, producto);
                initial.put(CONTAINS_KEY_CANTIDAD, 0);

                mDb.insert(DATABASE_TABLE_CONTAINS, null, initial);
            }
        }
        return res;
    }

    /**
     * Create a new note product the name, price and weight provided. If the product is
     * successfully created return the new rowId for that product, otherwise return
     * a -1 to indicate failure.
     *
     * @param nombre Name of the product
     * @param peso   Weight of the product
     * @param precio Price of the product
     *
     * @return rowId or -1 if failed
     */
    public long createProduct(String nombre, double precio, double peso) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(PRODUCT_KEY_NAME, nombre);
        initialValues.put(PRODUCT_KEY_PRECIO, precio);
        initialValues.put(PRODUCT_KEY_PESO, peso);

        return mDb.insert(DATABASE_TABLE_PRODUCTS, null, initialValues);
    }

    /**
     * Delete the product with the given productId
     *
     * @param productId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteProduct(String productId) {

        return mDb.delete(DATABASE_TABLE_PRODUCTS, PRODUCT_KEY_NAME + "=" + productId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllProducts() {

        return mDb.query(DATABASE_TABLE_PRODUCTS, new String[] {PRODUCT_KEY_NAME,
               PRODUCT_KEY_PRECIO, PRODUCT_KEY_PESO}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param productId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchProduct(String productId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE_PRODUCTS,
                        new String[] {PRODUCT_KEY_NAME, PRODUCT_KEY_PRECIO, PRODUCT_KEY_PESO},
                        PRODUCT_KEY_NAME + "=" + productId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     *
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateProduct(String nombre, double precio, double peso) {
        ContentValues args = new ContentValues();
        args.put(PRODUCT_KEY_NAME, nombre);
        args.put(PRODUCT_KEY_PRECIO, precio);
        args.put(PRODUCT_KEY_PESO, peso);

        return mDb.update(DATABASE_TABLE_PRODUCTS, args, PRODUCT_KEY_NAME + "=" + nombre, null) > 0;
    }
}