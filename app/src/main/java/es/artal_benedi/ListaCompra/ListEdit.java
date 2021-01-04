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


    /** Called when the activity is first created. */
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

    private void populateFields(){
        if(mRowId != null){
            System.out.println("RowID (onCreate list_edit): " + Long.toString(mRowId));
            Cursor note = mDbHelper.fetchList(mRowId);
            startManagingCursor(note);
            mNameText.setText(note.getString(note.getColumnIndexOrThrow(DbAdapter.LIST_KEY_NAME)));
            fillData();
        }
    }

    private void fillData(){
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllProductsShoppingList(mRowId);

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

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(DbAdapter.LIST_KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause(){
        super.onPause();
        saveState();
    }

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

    private void saveState(){
        String name = mNameText.getText().toString();
        if(name.isEmpty()) name = "lista_temporal";
        mDbHelper.updateShoppingList(mRowId,name);
        System.out.println("RowID (saveState list_edit): " + Long.toString(mRowId));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, ADD_ID, Menu.NONE, R.string.menu_insert_product);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ADD_ID:
                addProduct();
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

    private void addProduct() {
        Intent i = new Intent(this, AddProduct.class);
        System.out.println("RowID (a add_product): " + Long.toString(mRowId));
        i.putExtra(DbAdapter.LIST_KEY_ROWID, mRowId);
        startActivityForResult(i, ACTIVITY_CREATE);
    }


    protected void editProductInList(int position, long id) {
        Intent i = new Intent(this, AddProduct.class);
        i.putExtra(DbAdapter.CONTAINS_KEY_PRODUCTO, id);
        i.putExtra(DbAdapter.LIST_KEY_ROWID, mRowId);
        startActivityForResult(i, ACTIVITY_EDIT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        System.out.println("RowID ??");
        fillData();
    }
}