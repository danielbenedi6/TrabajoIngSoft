package es.artal_benedi.ListaCompra;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ShoppingListShow extends AppCompatActivity {

    private DbAdapter mDbHelper;
    private ListView mList;
    private TextView mNameText;
    private TextView mPriceText;
    private TextView mWeightText;
    private Long mRowId;



    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
        mList = (ListView)findViewById(R.id.show_products_list);
        mNameText = (TextView) findViewById(R.id.show_name);
        mPriceText = (TextView) findViewById(R.id.show_price);
        mWeightText = (TextView) findViewById(R.id.show_weight);

        Bundle extras = getIntent().getExtras();
        mRowId = (extras != null) ? extras.getLong(DbAdapter.LIST_KEY_ROWID) : null;

        populateFields();

    }

    private void populateFields(){
        if(mRowId != null){
            Cursor note = mDbHelper.fetchShoppingList(mRowId);
            startManagingCursor(note);
            mNameText.setText(note.getString(note.getColumnIndexOrThrow(DbAdapter.LIST_KEY_NAME)));
            mPriceText.setText(Double.toString(note.getDouble(note.getColumnIndexOrThrow(DbAdapter.LIST_KEY_PRECIO))));
            mWeightText.setText(Double.toString(note.getDouble(note.getColumnIndexOrThrow(DbAdapter.LIST_KEY_PESO))));
            fillData();
        }
    }

    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchProductsShoppingList(mRowId);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{DbAdapter.PRODUCT_KEY_NAME, DbAdapter.CONTAINS_KEY_CANTIDAD};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.product, R.id.amount};

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
        mList.setAdapter(notes);
    }

}
