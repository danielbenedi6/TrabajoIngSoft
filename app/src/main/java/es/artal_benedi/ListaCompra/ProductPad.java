package es.artal_benedi.ListaCompra;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Clase que gestiona la actividad en la que se muestran los productos de la base de datos.
 */
public class ProductPad extends AppCompatActivity {

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
    private static final int ORDER_NAME_ID = Menu.FIRST + 3;
    private static final int ORDER_PRICE_ID = Menu.FIRST + 4;
    private static final int ORDER_WEIGHT_ID = Menu.FIRST + 5;
    private static final int PRUEBA_ID = Menu.FIRST + 6;
    private static final int BORRAR_PRUEBA_ID = Menu.FIRST + 7;

    private DbAdapter mDbHelper;
    private ListView mList;


    /**
     * Llamado cuando la actividad es creada. Se encarga de preparar
     * el diseño de la actividad y de la conexión con la base de datos.
     *
     * @param savedInstanceState estado de la instacia guardada
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_pad);

        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
        mList = (ListView)findViewById(R.id.listProducts);
        fillData();

        registerForContextMenu(mList);

    }

    /**
     * Busca todos los productos de la base de datos y los muestra por pantalla
     * haciendo uso del ListView de la actividad. Con esto, tambiénse permite interactuar
     * con cada producto existente.
     */
    private void fillData() {
        fillDataOrdered(null);
    }

    /**
     * Busca todaos los productos de la dase de datos y los muestra por pantalla ordenadas según
     *  el parámetro orderBy haciendo uso del ListView de la actividad. Con esto, también se
     *  permite interactuar con cada producto existente.
     *
     * @param orderBy atributo según el cual ordenar el listado
     */
    private void fillDataOrdered(String orderBy) {
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllProductsOrdered(orderBy);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[] { DbAdapter.PRODUCT_KEY_NAME,
                DbAdapter.PRODUCT_KEY_PESO,   DbAdapter.PRODUCT_KEY_PRECIO};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[] { R.id.productName, R.id.productWeight, R.id.productPrice };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.product_row, notesCursor, from, to);
        mList.setAdapter(notes);
    }

    /**
     * Crea un menú de opciones de la actividad a partir de uno que se le pasa como parámetro
     * y al que le añade opciones.
     *
     * @param menu menú a partir del cual crear el menú de opciones
     * @return verdad si es creado el menú, falso en caso contrario
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, INSERT_ID, Menu.NONE, R.string.menu_insert_product);
        menu.add(Menu.NONE, ORDER_NAME_ID, Menu.NONE, R.string.order_name);
        menu.add(Menu.NONE, ORDER_PRICE_ID, Menu.NONE, R.string.order_price);
        menu.add(Menu.NONE, ORDER_WEIGHT_ID, Menu.NONE, R.string.order_weight);
        menu.add(Menu.NONE, PRUEBA_ID, Menu.NONE, "Test");
        menu.add(Menu.NONE, BORRAR_PRUEBA_ID, Menu.NONE, "Delete Test");
        return result;
    }

    /**
     * Gestiona las acciones a realizar en función de la opción del menú escogida.
     *
     * @param item opción del menú escogida
     * @return verdad si es realizada la operación, falso en caso contrario
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                createProduct();
                return true;
            case ORDER_NAME_ID:
                fillDataOrdered(DbAdapter.PRODUCT_KEY_NAME);
                return true;
            case ORDER_PRICE_ID:
                fillDataOrdered(DbAdapter.PRODUCT_KEY_PRECIO);
                return true;
            case ORDER_WEIGHT_ID:
                fillDataOrdered(DbAdapter.PRODUCT_KEY_PESO);
                return true;
            case PRUEBA_ID:
                Test.casiPetardoTest(mDbHelper);
                fillData();
                return true;
            case BORRAR_PRUEBA_ID:
                Test.borrarCasiPetardoTest(mDbHelper);
                fillData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Crea un menú de de contexto para cada uno de los productos mostrados y a dicho
     * menú le añade opciones, que son operaciones a realizar con el producto seleccionado.
     *
     * @param menu menú de contexto
     * @param v vista
     * @param menuInfo información del menú de contexto
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, DELETE_ID, Menu.NONE, R.string.menu_delete);
        menu.add(Menu.NONE, EDIT_ID, Menu.NONE, R.string.menu_edit);
    }

    /**
     * Gestiona las acciones a realizar en funciñon de la opción del menú
     * de contexto escogida.
     *
     * @param item opción del menú escogida
     * @return verdad si es realizada la operación, falso en caso contrario
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteProduct(info.id);
                fillData();
                return true;
            case EDIT_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                editProduct(info.position, info.id);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Lanza un intent para cambiar a la actividad de creación de notas con el fin de crear
     * una nota.
     */
    private void createProduct() {
        Intent i = new Intent(this, ProductEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    /**
     * Crea un Intent para cambiar a la actividad de edición de proxductos con el fin de
     * modificar el producto seleccionado.
     *
     * @param position posición de la nota en el ListView
     * @param id id de la nota
     */
    protected void editProduct(int position, long id) {
        Intent i = new Intent(this, ProductEdit.class);
        i.putExtra(DbAdapter.PRODUCT_KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    /**
     * Método que se ejecuta cuando se vuelve a la actividad en la que está
     * tras acabar la actividad lanzada. Se encarga de mostrar nuevamente los
     * productos de la base de datos.
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}