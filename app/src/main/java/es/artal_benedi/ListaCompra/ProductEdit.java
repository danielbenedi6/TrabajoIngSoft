package  es.artal_benedi.ListaCompra;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ProductEdit extends AppCompatActivity {

    private EditText mNameText;
    private EditText mPriceText;
    private EditText mWeightText;
    private Long mRowId;
    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_edit);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        setTitle(R.string.edit_note);
        System.out.println(mPriceText);
        mNameText = (EditText) findViewById(R.id.name);
        mPriceText = (EditText) findViewById(R.id.price);
        System.out.println(mPriceText);
        mWeightText = (EditText) findViewById(R.id.weight);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(NotesDbAdapter.PRODUCT_KEY_ROWID);
        System.out.println(mRowId);
        if(mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null) ? extras.getLong(NotesDbAdapter.PRODUCT_KEY_ROWID) : null;
        }
        System.out.println(mRowId);
        populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                System.out.println("klk");
                finish();
            }

        });
    }

    private void populateFields(){
        if(mRowId != null){
            Cursor note = mDbHelper.fetchProduct(mRowId);
            startManagingCursor(note);
            mNameText.setText(note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.PRODUCT_KEY_NAME)));
            mWeightText.setText(String.valueOf(note.getDouble(note.getColumnIndexOrThrow(NotesDbAdapter.PRODUCT_KEY_PESO))));
            mPriceText.setText(String.valueOf(note.getDouble(note.getColumnIndexOrThrow(NotesDbAdapter.PRODUCT_KEY_PRECIO))));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.PRODUCT_KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause(){
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume(){
        super.onResume();
        saveState();
    }

    private void saveState(){
        String name = mNameText.getText().toString();
        System.out.println(name + "klkmanin");
        double price = Double.parseDouble(mPriceText.getText().toString());
        double weight = Double.parseDouble(mWeightText.getText().toString());

        if(mRowId == null){
            long id = mDbHelper.createProduct(name, price, weight);
            if(id > 0){
                mRowId = id;
            }
        }else{
            mDbHelper.updateProduct(mRowId,name, price, weight);
        }
    }

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_edit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
     */
}
