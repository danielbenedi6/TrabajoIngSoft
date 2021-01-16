package  es.artal_benedi.ListaCompra;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Clase que gestiona la actividad para la creación y edición de productos.
 */
public class ProductEdit extends AppCompatActivity {

    private EditText mNameText;
    private EditText mPriceText;
    private EditText mWeightText;
    private Long mRowId;
    private DbAdapter mDbHelper;

    /**
     * Llamado cuando la actividad es creada. Se encarga de preparar
     * el diseño y los elementos de la actividad, de la conexión con
     * la base de datos y de gestionar los estados guardados y el
     * funcionamiento del botón de la actividad.
     *
     * @param savedInstanceState estado de la instacia guardada
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new DbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.product_edit);
        setTitle(R.string.edit_note);
        System.out.println(mPriceText);
        mNameText = (EditText) findViewById(R.id.name);
        mPriceText = (EditText) findViewById(R.id.price);
        System.out.println(mPriceText);
        mWeightText = (EditText) findViewById(R.id.weight);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(DbAdapter.PRODUCT_KEY_ROWID);
        System.out.println(mRowId);
        if(mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null) ? extras.getLong(DbAdapter.PRODUCT_KEY_ROWID) : null;
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

    /**
     * Busca los datos asociados al producto seleccionado (si existe) y que se quiere
     * modificar y los muestra en los widget correspondientes.
     *
     */
    private void populateFields(){
        if(mRowId != null){
            Cursor note = mDbHelper.fetchProduct(mRowId);
            startManagingCursor(note);
            mNameText.setText(note.getString(note.getColumnIndexOrThrow(DbAdapter.PRODUCT_KEY_NAME)));
            mWeightText.setText(String.valueOf(note.getDouble(note.getColumnIndexOrThrow(DbAdapter.PRODUCT_KEY_PESO))));
            mPriceText.setText(String.valueOf(note.getDouble(note.getColumnIndexOrThrow(DbAdapter.PRODUCT_KEY_PRECIO))));
        }
    }

    /**
     * Guarda el estado de la actividad, que es el rowId del producto.
     *
     * @param outState estado de salida
     */
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(DbAdapter.PRODUCT_KEY_ROWID, mRowId);
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
     * Llamado al reaundar e iniciar la actividad.
     */
    @Override
    protected void onResume(){
        super.onResume();
        //saveState();
    }

    /**
     * Si existe el producto, guarda los cambios realizados en la base de datos.
     * En caso de no existir, lo crea con los datos introducidos.
     */
    private void saveState(){
        String name = mNameText.getText().toString();
        if(name.isEmpty()) name = "producto_temporal";
        System.out.println(name + " klkmanin");
        double price = 0.0;
        double weight = 1.0;
        if (!mPriceText.getText().toString().isEmpty())
            price = Double.parseDouble(mPriceText.getText().toString());
        if (!mWeightText.getText().toString().isEmpty())
            weight = Double.parseDouble(mWeightText.getText().toString());

        if(mRowId == null){
            long id = mDbHelper.createProduct(name, price, weight);
            if(id > 0){
                mRowId = id;
            }
        }else{
            mDbHelper.updateProduct(mRowId,name, price, weight);
        }
    }

}
