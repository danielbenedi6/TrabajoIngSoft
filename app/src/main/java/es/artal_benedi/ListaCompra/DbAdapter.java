package  es.artal_benedi.ListaCompra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Clase de ayuda al acceso a la base de datos de las listas de compra y los productos.
 * Define unas operaciones básicas para la aplicación y proporciona la habilidad de
 * trabajar con listas de compra o productos específicos.
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
     * Constructor - coge el contexto para permitir a la base de datos ser creada/abierta
     *
     * @param ctx el contexto con el que trabajar
     */
    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Abre la base de datos. Si no puede ser abierta, intenta crear una nueva instacia
     * de ella. Si no puede ser creada, lanza una excepción para indicar error.
     *
     * @return this (referencia propia)
     * @throws SQLException si la base de datos no puede ser abierta ni creada
     */
    public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Cierra la base de datos.
     */
    public void close() {
        mDbHelper.close();
    }


    /**
     * Crea un nuevo producto usando el nombre, precio y peso provistos. El nombre no puede ser
     * la cadena vacía, el precio no puede ser negativo y el peso tiene que ser mayor que 0.
     * Si el producto es creado exitosamente, devuelve el rowId de ese producto. En caso contrario
     * devuelve -1 para indicar error.
     *
     * @param nombre nombre del producto
     * @param precio precio del producto
     * @param peso peso del producto
     * @return rowId o -1 si falla
     */
    public long createProduct(String nombre, double precio, double peso) {
        if(nombre != null && !nombre.isEmpty() && precio >= 0 && peso > 0) {
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
     * Borra el producto de la base de datos que tiene el rowId que se da como parámetro.
     *
     * @param rowId id del producto a borrar
     * @return verdad si es borrado, falso en caso contrario
     */
    public boolean deleteProduct(long rowId) {

        return mDb.delete(DATABASE_TABLE_PRODUCTS, PRODUCT_KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Devuelve un Cursor sobre la lista de todos los productos de la base de datos.
     *
     * @return Cursor sobre los productos
     */
    public Cursor fetchAllProducts() {

        return fetchAllProductsOrdered(null);
    }

    /**
     * Devuelve un Cursor sobre la lista de todos los productos de la base de datos ordenados
     * según uno de sus atributos de manera ascendiente.
     *
     * @return Cursor sobre los productos
     */
    public Cursor fetchAllProductsOrdered(String orderBy) {
        return mDb.query(DATABASE_TABLE_PRODUCTS, new String[] {PRODUCT_KEY_ROWID, PRODUCT_KEY_NAME,
                PRODUCT_KEY_PRECIO, PRODUCT_KEY_PESO}, null, null, null, null, orderBy);
    }

    /**
     * Devuelve un Cursor sobre la lista de todas las listas de compra de la base de datos
     * ordenadas según uno de sus atributos de manera ascendiente.
     *
     * @return Cursor sobre las listad de compra
     */
    public Cursor fetchAllShoppingListsOrdered(String orderBy) {
        return mDb.query(DATABASE_TABLE_LISTS, new String[] {LIST_KEY_ROWID, LIST_KEY_NAME},
                null, null, null, null, orderBy, null);
    }

    /**
     * Devuelve un Cursor sobre la lista de todas las listas de compra de la base de datos.
     *
     * @return Cursor sobre las listas de compra
     */
    public Cursor fetchAllShoppingLists() {

        return fetchAllShoppingListsOrdered(null);
    }

    /**
     * Devuelve un Cursor sobre la lista de los productos pertenecientes a una listas de
     * compra de la base de datos.
     *
     * @return Cursor sobre los productos de una lista
     */
    public Cursor fetchProductsShoppingList(long rowId) throws SQLException{

        return mDb.query(true, DATABASE_TABLE_CONTAINS + " c , " + DATABASE_TABLE_PRODUCTS + " p",
                        new String[] {"c." + CONTAINS_KEY_ROWID + " _id ", PRODUCT_KEY_NAME, CONTAINS_KEY_CANTIDAD},
                        CONTAINS_KEY_LISTA + "=" + rowId + " AND " + CONTAINS_KEY_PRODUCTO + "=p." + PRODUCT_KEY_ROWID,
                        null, null, null, null, null);

    }


    /**
     * Devuelve un Cursor posicionado en el producto cuyo rowId es el mismo que el proporcionado.
     *
     * @param rowId id de la nota a sacar
     * @return Cursor posicionado en el producto deseado, si es encontrado
     * @throws SQLException si ocurre algún fallo
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

    /**
     * Devuelve un Cursor posicionado en la lista de compra cuyo rowId es el mismo que
     * el proporcionado.
     *
     * @param rowId id de la lista de compra a sacar
     * @return Cursor posicionado en la lista de compra deseado, si es encontrada
     * @throws SQLException si ocurre algún fallo
     */
    public Cursor fetchShoppingList(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE_LISTS, new String[] {LIST_KEY_ROWID, LIST_KEY_NAME, LIST_KEY_PRECIO, LIST_KEY_PESO},
                        LIST_KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Devuelve un Cursor posicionado en el producto de una lista de compra cuyo rowId
     * de su relación con dicha lista es el mismo que el proporcionado. Tiene como fin
     * obetener la cantidad de ese producto en la lista.
     *
     * @param rowId id de la relación entre el producto y la lista
     * @return Cursor posicionado en el producto deseado, si es encontrado
     * @throws SQLException si ocurre algún fallo
     */
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

    /**
     * Devuelve un Cursor posicionado en el producto de una lista de compra cuyo rowId
     * de su relación con dicha lista es el mismo que el proporcionado. Tiene como fin
     * obetener el id de ese producto.
     *
     * @param rowId id de la relación entre el producto y la lista
     * @return Cursor posicionado en el producto deseado, si es encontrado
     * @throws SQLException si ocurre algún fallo
     */
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
     * Actualiza el producto con los nuevos datos proporcionados. El producto a actualizar es
     * aquel cuyo rowId coincide con el proporcionado y se modifica el nombre, precio y peso
     * por los pasados como parámetros. El nombre no puede ser cadena vacía, el precio no
     * puede ser negativo y el peso tiene que ser mayor que 0.
     *
     * @param rowId id del producto a actualizar
     * @param nombre nuevo nombre del producto
     * @param precio nuevo precio del producto
     * @param peso nuevo peso del producto
     * @return verdad si el producto fue actualizada con éxito, falso en caso contrario
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

    /**
     * Actualiza la cantidad de un producto en una lista de compra y también el tipo de producto
     * con los nuevos datos proporcionados. El producto de una lista a actualizar es aquel cuyo
     * rowId de la relación entre listas de compra y productos coincide con el proporcionado y
     * se modifica el producto y la cantidad de la relación por los pasados como parámetros.
     * La cantidad tiene que ser mayor que 0.
     *
     * @param idRow id del producto en una lista a actualizar
     * @param idProducto id del nuevo  producto
     * @param cantidad nueva cantidad del producto en la lista
     * @return verdad si la relación fue actualizada con éxito, falso en caso contrario
     */
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

    /**
     * Borra un producto de una lista de compra de la base de datos en la que la id de la relación
     * enttre dicho producto y la lista coincide con el idRow pasado como parámetro.
     *
     * @param idRow id del producto de una lista a borrar
     * @return verdad si es borrado, falso en caso contrario
     */
    public boolean deleteProductInList(long idRow) {
        return mDb.delete(DATABASE_TABLE_CONTAINS, CONTAINS_KEY_ROWID+ "=" + idRow, null) > 0;
    }

    /**
     * Añade un producto a una lista de compra y también el tipo de producto
     * con los nuevos datos proporcionados. El producto de una lista a actualizar es aquel cuyo
     * rowId de la relación entre listas de compra y productos coincide con el proporcionado y
     * se modifica el producto y la cantidad de la relación por los pasados como parámetros.
     * La cantidad tiene que ser mayor que 0.
     *
     * @param idList id dela lista a la que se añade el producto
     * @param idProduct id del producto a añadir
     * @param cantidad cantidad del producto en la lista
     * @return @return rowId o -1 si falla
     */
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


    /**
     * Crea una nueva lista de compra usando el nombre provisto. El nombre no puede ser
     * la cadena vacía. Si la lista de compra es creada exitosamente, devuelve el rowId
     * de esa lista de compra. En caso contrario devuelve -1 para indicar error.
     *
     * @param nombre nombre de la lista de compra
     * @return rowId o -1 si falla
     */
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

    /**
     * Actualiza la lista de compra con el nuevo nombre.  El nombre no puede ser
     * la cadena vacía. La lista de compra a actualizar es aquella cuyo rowId coincide
     * con el proporcionado y se modificada se título y cuerpo por los pasados como parámetros.
     *
     * @param rowId id de la nota a actualizar
     * @param nombre nuevo título de la lista de compra
     * @return verdad si la lista de compra fue actualizada con éxito, falso en caso contrario
     */
    public boolean updateShoppingList(long rowId, String nombre) {
        if(nombre != null && !nombre.isEmpty()) {
            ContentValues args = new ContentValues();
            args.put(LIST_KEY_NAME, nombre);
            return mDb.update(DATABASE_TABLE_LISTS, args, LIST_KEY_ROWID + "=" + rowId, null) > 0;
        } else {
            return false;
        }
    }

    /**
     * Borra la lista de compra de la base de datos que tiene el rowId que se da como parámetro.
     *
     * @param rowId id de la lista de compra a borrar
     * @return verdad si es borrada, falso en caso contrario
     */
    public boolean deleteShoppingList(long rowId) {
        return mDb.delete(DATABASE_TABLE_LISTS, LIST_KEY_ROWID + "=" + rowId, null) > 0;
    }


}