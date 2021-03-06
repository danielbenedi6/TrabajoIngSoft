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



    /**
     * Llamado cuando la actividad es creada. Se encarga de preparar
     * el diseño de la actividad y de la conexión con la base de datos.
     *
     * @param savedInstanceState estado de la instacia guardada
     */
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

    /**
     * Busca los datos asociados a la lista de compra seleccionada y que se
     * quiere visualizar y los muestra en los widget correspondientes.
     */
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

    /**
     * Busca todos los productos de la lista en la base de datos y los muestra por pantalla haciendo
     * uso del ListView de la actividad. Cada elemento del ListView tiene el nombre del producto y
     * su cantidad.
     */
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
