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
public class DbAdapter {

    public static final String PRODUCT_KEY_NAME = "nombre";
    public static final String PRODUCT_KEY_PRECIO = "precio";
    public static final String PRODUCT_KEY_PESO = "peso";
    public static final String PRODUCT_KEY_ROWID = "_id";

    public static final String LIST_KEY_NAME = "nombre";
    public static final String LIST_KEY_ROWID = "_id";
    public static final String LIST_KEY_PRECIO = "precio";
    public static final String LIST_KEY_PESO = "peso";

    public static final String CONTAINS_KEY_LISTA = "lista";
    public static final String CONTAINS_KEY_PRODUCTO = "producto";
    public static final String CONTAINS_KEY_CANTIDAD = "cantidad";
    public static final String CONTAINS_KEY_ROWID = "_id";

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

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
     * );
     *
     * CREATE TABLE contiene (
     * _id integer PRIMARY KEY AUTOINCREMENT,
     * producto integer,
     * lista integer,
     * cantidad integer NOT NULL,
     * FOREIGN KEY (producto) REFERENCES productos(_id) ON DELETE CASCADE,
     * FOREIGN KEY (lista) REFERENCES listas(_id) ON DELETE CASCADE );
     *
     */
    private static final String DATABASE_CREATE_PRODUCTOS =
            "CREATE TABLE productos ( " +
                    "_id integer PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT NOT NULL, " +
                    "precio DOUBLE NOT NULL, " +
                    "peso DOUBLE NOT NULL );";

    private static final String DATABASE_CREATE_LISTAS =
            "CREATE TABLE listas ( " +
            "_id integer PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT NOT NULL,  " +
            "precio DOUBLE NOT NULL, " +
            "peso DOUBLE NOT NULL);";

    private static final String DATABASE_CREATE_CONTIENE =
            "CREATE TABLE contiene ( " +
            "_id integer PRIMARY KEY AUTOINCREMENT, " +
            "producto integer, " +
            "lista integer, " +
            "cantidad integer NOT NULL, " +
            "FOREIGN KEY (producto) REFERENCES productos(_id) ON DELETE CASCADE, " +
            "FOREIGN KEY (lista) REFERENCES listas(_id) ON DELETE CASCADE );";

    private static final String TRIGGER_INSERT_CONTIENE =
            "CREATE TRIGGER IF NOT EXISTS actualizarPesoPrecioInsert " +
            "AFTER  INSERT ON contiene " +
            "FOR EACH ROW " +
            "BEGIN " +
            " UPDATE LISTAS" +
            " SET precio = (SELECT sum(p.precio*cantidad) FROM contiene, productos p WHERE lista == NEW.lista AND producto == p._id), " +
            "  peso = (SELECT sum(p.peso*cantidad) FROM contiene, productos p WHERE lista == NEW.lista AND producto == p._id) " +
            " WHERE _id == NEW.lista;" +
            "END";

    private static final String TRIGGER_UPDATE_CONTIENE = 
            "CREATE TRIGGER IF NOT EXISTS actualizarPesoPrecioUpdate " +
            "AFTER  UPDATE ON contiene " +
            "FOR EACH ROW " +
            "BEGIN " +
            " UPDATE LISTAS" +
            " SET precio = (SELECT sum(p.precio*cantidad) FROM contiene, productos p WHERE lista == NEW.lista AND producto == p._id), " +
            "  peso = (SELECT sum(p.peso*cantidad) FROM contiene, productos p WHERE lista == NEW.lista AND producto == p._id) " +
            " WHERE _id == NEW.lista;" +
            "END";

    private  static final String TRIGGER_DELETE_CONTIENE =
            "CREATE TRIGGER IF NOT EXISTS actualizarPesoPrecioDelete " +
            "BEFORE DELETE ON contiene " +
            "FOR EACH ROW " +
            "BEGIN " +
            " UPDATE LISTAS " +
            " SET precio = precio - (OLD.cantidad*(SELECT precio FROM productos p WHERE p._id == OLD.producto)), " +
            "  peso = peso - (OLD.cantidad*(SELECT peso FROM productos p WHERE p._id == OLD.producto)) " +
            " WHERE _id == OLD.lista;" +
            "END";

    private static final String TRIGGER_UPDATE_PRODUCTOS =
            "CREATE TRIGGER IF NOT EXISTS actualizarPesoPrecioProductosUpdate " +
            "AFTER  UPDATE ON productos " +
            "FOR EACH ROW " +
            "BEGIN " +
            " UPDATE Listas" +
            " SET precio  = ( SELECT sum(p.precio * c.cantidad) FROM contiene c, productos p WHERE c.lista == _id AND c.producto == p._id ), " +
            "   peso  = ( SELECT sum(p.peso * c.cantidad) FROM contiene c, productos p WHERE c.lista == _id AND c.producto == p._id ) " +
            " WHERE _id IN (SELECT lista FROM contiene WHERE producto == NEW._id);" +
            "END";

    private static final String TRIGGER_DELETE_PRODUCTS =
            "CREATE TRIGGER IF NOT EXISTS actualizarPesoPrecioProductosUpdate " +
            "AFTER  DELETE ON productos " +
            "FOR EACH ROW " +
            "BEGIN " +
            " UPDATE Listas" +
            " SET precio  = ( SELECT sum(p.precio * c.cantidad) FROM contiene c, productos p WHERE c.lista == OLD._id AND c.producto == p._id ), " +
            "   peso  = ( SELECT sum(p.peso * c.cantidad) FROM contiene c, productos p WHERE c.lista == OLD._id AND c.producto == p._id ) " +
            " WHERE _id IN (SELECT lista FROM contiene WHERE producto == OLD._id);" +
            "END";
    
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
            Log.println(Log.VERBOSE, "DATABASEADAPTER", "se crea wey");
            db.execSQL(DATABASE_CREATE_PRODUCTOS);
            db.execSQL(DATABASE_CREATE_LISTAS);
            db.execSQL(DATABASE_CREATE_CONTIENE);
            db.execSQL(TRIGGER_DELETE_CONTIENE);
            db.execSQL(TRIGGER_DELETE_PRODUCTS);
            db.execSQL(TRIGGER_INSERT_CONTIENE);
            db.execSQL(TRIGGER_UPDATE_CONTIENE);
            db.execSQL(TRIGGER_UPDATE_PRODUCTOS);
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
    public DbAdapter(Context ctx) {
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
    public DbAdapter open() throws SQLException {
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
    public long createProduct(String nombre, double precio, double peso) {
        if(nombre != null && !nombre.isEmpty() && precio > 0 && peso > 0) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(PRODUCT_KEY_NAME, nombre);
            initialValues.put(PRODUCT_KEY_PRECIO, precio);
            initialValues.put(PRODUCT_KEY_PESO, peso);
            return mDb.insert(DATABASE_TABLE_PRODUCTS, null, initialValues);
        } else{
            return -1;
        }


    }

    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteProduct(long rowId) {

        return mDb.delete(DATABASE_TABLE_PRODUCTS, PRODUCT_KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllProducts() {

        return fetchAllProductsOrdered(null);
    }

    public Cursor fetchAllProductsOrdered(String orderBy) {
        return mDb.query(DATABASE_TABLE_PRODUCTS, new String[] {PRODUCT_KEY_ROWID, PRODUCT_KEY_NAME,
                PRODUCT_KEY_PRECIO, PRODUCT_KEY_PESO}, null, null, null, null, orderBy);
    }

    public Cursor fetchAllShoppingListsOrdered(String orderBy) {
        return mDb.query(DATABASE_TABLE_LISTS, new String[] {LIST_KEY_ROWID, LIST_KEY_NAME},
                null, null, null, null, orderBy, null);
    }

    public Cursor fetchAllShoppingLists() {

        return fetchAllShoppingListsOrdered(null);
    }

    public Cursor fetchAllProductsShoppingList(long rowId) throws SQLException{

        return mDb.query(true, DATABASE_TABLE_CONTAINS + " c , " + DATABASE_TABLE_PRODUCTS + " p",
                        new String[] {"c." + CONTAINS_KEY_ROWID + " _id ", PRODUCT_KEY_NAME, CONTAINS_KEY_CANTIDAD},
                        CONTAINS_KEY_LISTA + "=" + rowId + " AND " + CONTAINS_KEY_PRODUCTO + "=p." + PRODUCT_KEY_ROWID,
                        null, null, null, null, null);

    }


    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchProduct(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE_PRODUCTS, new String[] {PRODUCT_KEY_ROWID,
                                PRODUCT_KEY_NAME, PRODUCT_KEY_PRECIO, PRODUCT_KEY_PESO}, PRODUCT_KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor fetchList(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE_LISTS, new String[] {LIST_KEY_ROWID, LIST_KEY_NAME, LIST_KEY_PRECIO, LIST_KEY_PESO},
                        LIST_KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor fetchAmount(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE_CONTAINS, new String[] {CONTAINS_KEY_ROWID, CONTAINS_KEY_CANTIDAD},
                        CONTAINS_KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Long fetchProductIdInList(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE_CONTAINS, new String[] {CONTAINS_KEY_PRODUCTO},
                        CONTAINS_KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            return mCursor.getLong(0);
        }
        return null;

    }



    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @param rowId id of note to update
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateProduct(long rowId, String nombre, double precio, double peso) {
        if(nombre != null && !nombre.isEmpty() && precio > 0 && peso > 0) {
            ContentValues args = new ContentValues();
            args.put(PRODUCT_KEY_NAME, nombre);
            args.put(PRODUCT_KEY_PRECIO, precio);
            args.put(PRODUCT_KEY_PESO, peso);
            return mDb.update(DATABASE_TABLE_PRODUCTS, args, PRODUCT_KEY_ROWID + "=" + rowId, null) > 0;
        } else {
            return false;
        }
    }

    public boolean updateProductInList(long idRow, long idProducto, int cantidad) {
        if(idRow > 0 && idProducto > 0 && cantidad > 0) {
            ContentValues args = new ContentValues();
            args.put(CONTAINS_KEY_CANTIDAD, cantidad);
            args.put(CONTAINS_KEY_PRODUCTO, idProducto);
            return mDb.update(DATABASE_TABLE_CONTAINS, args, CONTAINS_KEY_ROWID + "=" + idRow, null) > 0;
        } else {
            return false;
        }
    }

    public boolean deleteProductInList(long idRow) {
        return mDb.delete(DATABASE_TABLE_CONTAINS, CONTAINS_KEY_ROWID+ "=" + idRow, null) > 0;
    }

    public long addProductToList(long idList, long idProduct, int cantidad){
        if(cantidad > 0 && idList > 0 && idProduct > 0) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(CONTAINS_KEY_LISTA, idList);
            initialValues.put(CONTAINS_KEY_PRODUCTO, idProduct);
            initialValues.put(CONTAINS_KEY_CANTIDAD, cantidad);
            return mDb.insert(DATABASE_TABLE_CONTAINS, null, initialValues);
        } else {
            return -1;
        }
    }


    public long createShoppingList(String nombre){
        if(nombre != null && !nombre.isEmpty()) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(LIST_KEY_NAME, nombre);
            initialValues.put(LIST_KEY_PESO, 0.0); //Como la nota xd salu2
            initialValues.put(LIST_KEY_PRECIO, 0.0);
            return mDb.insert(DATABASE_TABLE_LISTS, null, initialValues);
        } else {
            return -1;
        }
    }

    public boolean updateShoppingList(long rowId, String nombre) {
        if(nombre != null && !nombre.isEmpty()) {
            ContentValues args = new ContentValues();
            args.put(LIST_KEY_NAME, nombre);
            return mDb.update(DATABASE_TABLE_LISTS, args, LIST_KEY_ROWID + "=" + rowId, null) > 0;
        } else {
            return false;
        }
    }


    public boolean deleteShoppingList(long rowId) {
        return mDb.delete(DATABASE_TABLE_LISTS, LIST_KEY_ROWID + "=" + rowId, null) > 0;
    }


}