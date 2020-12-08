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

public class ProductPad extends AppCompatActivity {

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
    private static final int ORDER_NAME_ID = Menu.FIRST + 3;
    private static final int ORDER_PRICE_ID = Menu.FIRST + 4;
    private static final int ORDER_WEIGHT_ID = Menu.FIRST + 5;

    private DbAdapter mDbHelper;
    private ListView mList;


    /** Called when the activity is first created. */
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

    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllProducts();

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[] { DbAdapter.PRODUCT_KEY_NAME,
                DbAdapter.PRODUCT_KEY_PESO,   DbAdapter.PRODUCT_KEY_PRECIO};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[] { R.id.text1 };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
        mList.setAdapter(notes);
    }

    private void fillDataOrdered(String orderBy) {
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllProductsOrdered(orderBy);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[] { DbAdapter.PRODUCT_KEY_NAME,
                DbAdapter.PRODUCT_KEY_PESO,   DbAdapter.PRODUCT_KEY_PRECIO};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[] { R.id.text1 };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
        mList.setAdapter(notes);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, INSERT_ID, Menu.NONE, R.string.menu_insert_product);
        menu.add(Menu.NONE, ORDER_NAME_ID, Menu.NONE, R.string.order_name);
        menu.add(Menu.NONE, ORDER_PRICE_ID, Menu.NONE, R.string.order_price);
        menu.add(Menu.NONE, ORDER_WEIGHT_ID, Menu.NONE, R.string.order_weight);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                createProduct();
                return true;
            case ORDER_NAME_ID:
                //TODO realmente podemos quitar fillData, ya que
                // fillData() = fillDataOrdered(null)
                fillDataOrdered(DbAdapter.PRODUCT_KEY_NAME);
                return true;
            case ORDER_PRICE_ID:
                //TODO hacerlo bien
                fillDataOrdered(DbAdapter.PRODUCT_KEY_PRECIO);
                return true;
            case ORDER_WEIGHT_ID:
                //TODO hacerlo bien
                fillDataOrdered(DbAdapter.PRODUCT_KEY_PESO);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, DELETE_ID, Menu.NONE, R.string.menu_delete);
        menu.add(Menu.NONE, EDIT_ID, Menu.NONE, R.string.menu_edit);
    }

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

    private void createProduct() {
        Intent i = new Intent(this, ProductEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }


    protected void editProduct(int position, long id) {
        Intent i = new Intent(this, ProductEdit.class);
        i.putExtra(DbAdapter.PRODUCT_KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}