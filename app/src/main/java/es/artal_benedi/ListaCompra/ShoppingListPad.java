package es.artal_benedi.ListaCompra;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import es.artal_benedi.send.SendAbstraction;
import es.artal_benedi.send.SendAbstractionImpl;


public class ShoppingListPad extends AppCompatActivity {

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
    private static final int SHOW_ID = Menu.FIRST + 3;
    private static final int PRODUCT_LIST_ID = Menu.FIRST + 4;
    private static final int SEND_ID = Menu.FIRST + 5;
    private static final int ORDER_NAME_ID = Menu.FIRST + 6;
    private static final int ORDER_PRICE_ID = Menu.FIRST + 7;
    private static final int ORDER_WEIGHT_ID = Menu.FIRST + 8;
    private static final int PRUEBA_ID = Menu.FIRST + 9;
    private static final int BORRAR_PRUEBA_ID = Menu.FIRST + 10;
    private static final int SOBRECARGA_ID = Menu.FIRST + 11;
    private static final int BORRAR_SOBRECARGA_ID = Menu.FIRST + 12;
    private static final int SOBRECARGA_PRODUCTOS_ID = Menu.FIRST + 13;
    private static final int BORRAR_SOBRECARGA_PRODUCTOS_ID = Menu.FIRST + 14;
    private static final int SOBRECARGA_ASOCIAR = Menu.FIRST + 15;
    private static final int SOBRECARGA_PESO = Menu.FIRST + 16;
    private static final int SOBRECARGA_PRECIO = Menu.FIRST + 17;
    private static final int SOBRECARGA_CANTIDAD = Menu.FIRST + 18;



    private DbAdapter mDbHelper;
    private ListView mList;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list_pad);

        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
        mList = (ListView) findViewById(R.id.list);
        fillData();
        Test.runTestListas(mDbHelper);
        Test.runTestProducts(mDbHelper);
        Test.runTestProductosLista(mDbHelper);
        registerForContextMenu(mList);

    }

    private void fillData() {
        fillDataOrdered(null);
    }

    private void fillDataOrdered(String orderBy) {
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllShoppingListsOrdered(orderBy);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[] { DbAdapter.LIST_KEY_NAME };

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[] { R.id.ItemList };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.shoppinglistpad_row, notesCursor, from, to);
        mList.setAdapter(notes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, INSERT_ID, Menu.NONE, R.string.action_add_list);
        menu.add(Menu.NONE, PRODUCT_LIST_ID, Menu.NONE, R.string.action_product_list);
        menu.add(Menu.NONE, ORDER_NAME_ID, Menu.NONE, R.string.order_name);
        menu.add(Menu.NONE, ORDER_PRICE_ID, Menu.NONE, R.string.order_price);
        menu.add(Menu.NONE, ORDER_WEIGHT_ID, Menu.NONE, R.string.order_weight);
        /*menu.add(Menu.NONE, PRUEBA_ID, Menu.NONE, "Test");
        menu.add(Menu.NONE, BORRAR_PRUEBA_ID, Menu.NONE, "Delete Test");
        menu.add(Menu.NONE, SOBRECARGA_ID, Menu.NONE, "Sobrecarga Listas");
        menu.add(Menu.NONE, BORRAR_SOBRECARGA_ID, Menu.NONE, "Eliminar Sobrecarga Listas");
        menu.add(Menu.NONE, SOBRECARGA_PRODUCTOS_ID, Menu.NONE, "Sobrecarga Productos");
        menu.add(Menu.NONE, BORRAR_SOBRECARGA_PRODUCTOS_ID, Menu.NONE, "Eliminar Sobrecarga Productos");
        menu.add(Menu.NONE, SOBRECARGA_ASOCIAR, Menu.NONE, "Asociar Productos y Listas");
        menu.add(Menu.NONE, SOBRECARGA_PESO, Menu.NONE, "Sobrecarga Peso");
        menu.add(Menu.NONE, SOBRECARGA_PRECIO, Menu.NONE, "Sobrecarga Precio");
        menu.add(Menu.NONE, SOBRECARGA_CANTIDAD, Menu.NONE, "Sobrecarga Cantidad");*/
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                System.out.println("no deberia entrar aqui");
                createList();
                return true;
            case PRODUCT_LIST_ID:
                System.out.println("deberia entrar aqui");
                showProducts();
                return true;
            case ORDER_NAME_ID:
                fillDataOrdered(DbAdapter.LIST_KEY_NAME);
                return true;
            case ORDER_PRICE_ID:
                fillDataOrdered(DbAdapter.LIST_KEY_PRECIO);
                return true;
            case ORDER_WEIGHT_ID:
                fillDataOrdered(DbAdapter.LIST_KEY_PESO);
                return true;
            /*case PRUEBA_ID:
                Test.casiPetardoListasTest(mDbHelper);
                fillData();
                return true;
            case BORRAR_PRUEBA_ID:
                Test.borrarCasiPetardoListasTest(mDbHelper);
                fillData();
                return true;
            case SOBRECARGA_ID:
                Test.crearTestSobrecargaNumeroListas(mDbHelper);
                fillData();
                return true;
            case BORRAR_SOBRECARGA_ID:
                Test.borrarTestSobrecargaNumeroListas(mDbHelper);
                fillData();
                return true;
            case SOBRECARGA_PRODUCTOS_ID:
                Test.crearTestSobrecargaNumeroProductos(mDbHelper);
                fillData();
                return true;
            case BORRAR_SOBRECARGA_PRODUCTOS_ID:
                Test.borrarTestSobrecargaNumeroProductos(mDbHelper);
                fillData();
                return true;
            case SOBRECARGA_ASOCIAR:
                Test.TestSobrecargaAsociarProductosListas(mDbHelper);
                fillData();
                return true;
            case SOBRECARGA_PESO:
                Test.TestSobrecargaPeso(mDbHelper);
                fillData();
                return true;
            case SOBRECARGA_PRECIO:
                Test.TestSobrecargaPrecio(mDbHelper);
                fillData();
                return true;
            case SOBRECARGA_CANTIDAD:
                Test.TestSobrecargaCantidad(mDbHelper);
                fillData();
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, DELETE_ID, Menu.NONE, R.string.delete_list);
        menu.add(Menu.NONE, EDIT_ID, Menu.NONE, R.string.edit_list);
        menu.add(Menu.NONE, SHOW_ID, Menu.NONE, R.string.show_list);
        menu.add(Menu.NONE, SEND_ID, Menu.NONE, R.string.send_list);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteShoppingList(info.id);
                fillData();
                return true;
            case EDIT_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                editList(info.position, info.id);
                return true;
            case SHOW_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                showList(info.position, info.id);
                return true;
            case SEND_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                sendList(info.position, info.id);
                return true;

        }
        return super.onContextItemSelected(item);
    }

    private void createList() {
        Intent i = new Intent(this, ListEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }


    protected void editList(int position, long id) {
        Intent i = new Intent(this, ListEdit.class);
        i.putExtra(DbAdapter.LIST_KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    protected void showList(int position, long id) {
        Intent i = new Intent(this, ShoppingListShow.class);
        i.putExtra(DbAdapter.LIST_KEY_ROWID, id);
        startActivityForResult(i, 0);
    }

    private void showProducts(){
        Intent i = new Intent(this, ProductPad.class);
        startActivityForResult(i, 0);
    }

    private void sendList(int position, long id){
        SendAbstraction sa = new SendAbstractionImpl(this, "EMAIL");
        String subject = "", body = "";
        Cursor cursor = mDbHelper.fetchShoppingList(id);
        if(cursor.moveToFirst()) {
            subject = cursor.getString(cursor.getColumnIndex(DbAdapter.LIST_KEY_NAME));
        }
        cursor = mDbHelper.fetchProductsShoppingList(id);
        try {
            while (cursor.moveToNext()) {
                body += cursor.getString(cursor.getColumnIndex(DbAdapter.CONTAINS_KEY_CANTIDAD))+" x "+
                        cursor.getString(cursor.getColumnIndex(DbAdapter.PRODUCT_KEY_NAME))+"\n";
            }
        } finally {
            cursor.close();
        }
        sa.send(subject, body);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

}
