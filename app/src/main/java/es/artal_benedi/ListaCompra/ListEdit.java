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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Clase que gestiona la actividad para la creación y edición de listas de compra.
 */
public class ListEdit extends AppCompatActivity {

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int ADD_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;

    private DbAdapter mDbHelper;
    private ListView mList;
    private EditText mNameText;
    private Long mRowId;


    /**
     * Llamado cuando la actividad es creada. Se encarga de preparar
     * el diseño y los elementos de la actividad, de la conexión con
     * la base de datos y de gestionar los estados guardados y el
     * funcionamiento del botón de la actividad.
     *
     * @param savedInstanceState estado de la instacia guardada
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_edit);

        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
        mList = (ListView)findViewById(R.id.products_list);
        mNameText = (EditText) findViewById(R.id.name);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(DbAdapter.LIST_KEY_ROWID);
        System.out.println(mRowId);
        if(mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null) ? extras.getLong(DbAdapter.LIST_KEY_ROWID) : null;
        }

        populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                System.out.println("klk");
                finish();
            }

        });
        registerForContextMenu(mList);

    }

    /**
     * Busca los datos asociados a la lista de compra seleccionada (si existe) y que se
     * quiere modificar y los muestra en los widget correspondientes.
     *
     */
    private void populateFields(){
        if(mRowId != null){
            System.out.println("RowID (onCreate list_edit): " + Long.toString(mRowId));
            Cursor note = mDbHelper.fetchShoppingList(mRowId);
            startManagingCursor(note);
            mNameText.setText(note.getString(note.getColumnIndexOrThrow(DbAdapter.LIST_KEY_NAME)));
            fillData();
        }
    }

    /**
     * Busca todos los productos de la lista de compra seleccionada (si existe) de la base
     * de datos y los muestra por pantalla haciendo uso del ListView de la actividad.
     * Con esto, tambiénse permite interactuar con cada producto de la lista.
     */
    private void fillData(){
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchProductsShoppingList(mRowId);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[] { DbAdapter.PRODUCT_KEY_NAME, DbAdapter.CONTAINS_KEY_CANTIDAD};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[] { R.id.product, R.id.amount };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
        if(!notes.isEmpty()){
            TextView noProduct = (TextView)findViewById(R.id.empty);
            noProduct.setVisibility(View.INVISIBLE);
        }
        mList.setAdapter(notes);
    }

    /**
     * Guarda el estado de la actividad, que es el rowId de la lista de compra.
     *
     * @param outState estado de salida
     */
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(DbAdapter.LIST_KEY_ROWID, mRowId);
    }

    /**
     * Llamado al pausar la actividad, momento en el que se guarda el
     * estado de la actividad.
     */
    @Override
    protected void onPause(){
        super.onPause();
        saveState();
    }

    /**
     * Llamado al al reaundar e iniciar la actividad, momento en el que se crea la lista
     * si no existe ya estado de la actividad.
     */
    @Override
    protected void onResume(){
        super.onResume();
        String name = mNameText.getText().toString();
        if(name.isEmpty()) name = "lista_temporal";
        if(mRowId == null){
            long id = mDbHelper.createShoppingList(name);
            if(id > 0){
                mRowId = id;
            }
        }
    }

    /**
     * Si existe la lista de compra, guarda los cambios realizados en la base de datos.
     * En caso de no existir, la crea con los datos introducidos.
     */
    private void saveState(){
        String name = mNameText.getText().toString();
        if(name.isEmpty()) name = "lista_temporal";
        mDbHelper.updateShoppingList(mRowId,name);
        System.out.println("RowID (saveState list_edit): " + Long.toString(mRowId));
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
        menu.add(Menu.NONE, ADD_ID, Menu.NONE, R.string.menu_insert_product);
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
            case ADD_ID:
                addProduct();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Crea un menú de de contexto para cada uno de los productos de la lista mostrados y a dicho
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
                mDbHelper.deleteProductInList(info.id);
                fillData();
                return true;
            case EDIT_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                editProductInList(info.position, info.id);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Lanza un intent para cambiar a la actividad de añadir productos a una lista de compra con
     * el objetivo de añadir un producto (y su cantidad) a la lista y le añade al Intent el rowId
     * de la lista.
     */
    private void addProduct() {
        Intent i = new Intent(this, AddProduct.class);
        System.out.println("RowID (a add_product): " + Long.toString(mRowId));
        i.putExtra(DbAdapter.LIST_KEY_ROWID, mRowId);
        startActivityForResult(i, ACTIVITY_CREATE);
    }


    /**
     * Crea un intent para cambiar a la actividad de añadir productos a una lista de compra con
     * el objetivo de añadir un producto (y su cantidad) a la lista y le añade al Intent el rowId
     * de la lista y el rowId de la relación.
     *
     * @param position posición del producto de la lista de compra en el ListView
     * @param id id de la relación entre la lista de compra y el producto
     */
    protected void editProductInList(int position, long id) {
        Intent i = new Intent(this, AddProduct.class);
        i.putExtra(DbAdapter.CONTAINS_KEY_PRODUCTO, id);
        i.putExtra(DbAdapter.LIST_KEY_ROWID, mRowId);
        startActivityForResult(i, ACTIVITY_EDIT);
    }


    /**
     * Método que se ejecuta cuando se vuelve a la actividad en la que está
     * tras acabar la actividad lanzada. Se encarga de mostrar nuevamente los
     * productos de la lista.
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        System.out.println("RowID ??");
        fillData();
        //TODO añadir populateFields()??
    }
}