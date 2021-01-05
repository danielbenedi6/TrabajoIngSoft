package es.artal_benedi.ListaCompra;

import android.app.Activity;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class AddProduct extends AppCompatActivity {

    private EditText mAmountText;
    private Spinner mSpinner;
    private Long mRowId;
    private Long mProductId;
    private Long mContainsId;
    private DbAdapter mDbHelper;

    private static final String CONTAINS_SERIALIZABLE = "CONTAINS_ID";

    private class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            System.out.println("AL MENOS APARECE " + Long.toString(id));
            mProductId = id;
        }

        public void onNothingSelected(AdapterView<?> parent) {
            mProductId = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new DbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.activity_add_producto);
        setTitle(R.string.edit_note);

        mAmountText = (EditText) findViewById(R.id.editTextNumber);
        mSpinner = (Spinner) findViewById(R.id.spinner);

        SpinnerActivity mSpinnerActivity = new SpinnerActivity();
        mSpinner.setOnItemSelectedListener(mSpinnerActivity);

        Button confirmButton = (Button) findViewById(R.id.confirmProduct);

        mRowId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(DbAdapter.LIST_KEY_ROWID);
        mContainsId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(CONTAINS_SERIALIZABLE);

        System.out.println(mRowId);
        Bundle extras = getIntent().getExtras();
        if(mRowId == null) {
            mRowId = (extras != null) ? extras.getLong(DbAdapter.LIST_KEY_ROWID) : null;
        }
        if(mContainsId == null) {
            mContainsId = (extras != null) ? extras.getLong(DbAdapter.CONTAINS_KEY_PRODUCTO) : null;
            if(mContainsId <= 0){
               mContainsId = null;
            }
        }
        //System.out.println("RowID (add_product): " + Long.toString(mRowId));
        //System.out.println("ContainID (add_product): " + Long.toString(mContainsId));
        fillData();
        populateFields();


        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                System.out.println("klk canchero");
                finish();
            }

        });
    }

    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllProducts();

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[] { DbAdapter.PRODUCT_KEY_NAME };

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[] { R.id.SpinnerProduct };

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.spinner_row, notesCursor, from, to);
        mSpinner.setAdapter(notes);

        if(mContainsId != null) {
            System.out.println("filldata(): " + mContainsId);
            System.out.println("filldata(): " + notes.getCount());
            notesCursor.moveToFirst();
            boolean encontrado = false;
            for(int i = 0; i<notesCursor.getCount() && !encontrado; i++){
                if(mDbHelper.fetchProductIdInList(mContainsId).intValue() == notesCursor.getColumnIndexOrThrow(DbAdapter.PRODUCT_KEY_ROWID)){
                    mSpinner.setSelection(i);
                    encontrado = true;
                }
                notesCursor.moveToNext();
            }

        }
    }

    private void populateFields(){
        if(mContainsId != null){
            Cursor note = mDbHelper.fetchAmount(mContainsId);
            startManagingCursor(note);
            mAmountText.setText(note.getString(note.getColumnIndexOrThrow(DbAdapter.CONTAINS_KEY_CANTIDAD)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(DbAdapter.LIST_KEY_ROWID, mRowId);
        outState.putSerializable(CONTAINS_SERIALIZABLE, mContainsId);
    }

    @Override
    protected void onPause(){
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume(){
        super.onResume();
        //saveState();
    }

    private void saveState(){
        System.out.format("AL MENOS LLEGA %s\n", mContainsId);
        String name = mAmountText.getText().toString();
        int amount = 0;
        if (!mAmountText.getText().toString().isEmpty())
            amount = Integer.parseInt(mAmountText.getText().toString());


        if((mRowId != null && (mProductId != null && mProductId >= 0) && mContainsId == null)
                ||  (mRowId != null && (mProductId != null && mProductId >= 0) && mContainsId != null && mContainsId < 0)){

            long id = mDbHelper.addProductToList(mRowId, mProductId, amount);
            if (id>0){
                mContainsId = id;
            }
            System.out.format("AL MENOS SE EJECUTA INSERT %s, %s, %s, %s \n", mProductId, mRowId, amount, mContainsId);
        }else if (mContainsId != null && mContainsId >= 0){
            if(mDbHelper.updateProductInList(mContainsId, mProductId, amount)){
                System.out.println("AL MENOS NO FALLA");
            }
            System.out.format("AL MENOS SE EJECUTA UPDATE %s, %s, %s, %s \n", mProductId, mRowId, amount, mContainsId);
        }
    }
}