package es.artal_benedi.ListaCompra;

import android.database.Cursor;
import android.util.Log;

public class Test {
    //private static long myId = 0;

    public static void runTestProducts(DbAdapter nda){
        // Prueba va bien create
        long id1 = nda.createProduct("producto_prueba", 0.0, 0.0);
        if(id1 < 0){
            Log.d("PRODUCTOS: CASO 1", "ERROR");
        } else {
            Log.d("PRODUCTOS: CASO 1", "BIEN");
        }

        //Prueba va mal create
        try {
            //FUNADA
            long id2 = nda.createProduct(null, 0.0, 0.0);
            if (id2 < 0) {
                Log.d("PRODUCTOS: CASO 2", "BIEN");
            } else {
                Log.d("PRODUCTOS: CASO 2", "MAL");
            }
        } catch (Exception e){
            Log.d("PRODUCTOS: CASO 2", "EXCEPCION");
        }

        try {
            long id3 = nda.createProduct("", 0.0, 0.0);
            if (id3 < 0) {
                Log.d("PRODUCTOS: CASO 3", "BIEN");
            } else {
                Log.d("PRODUCTOS: CASO 3", "MAL");
            }
        } catch (Exception e){
            Log.d("PRODUCTOS: CASO 3", "EXCEPCION");
        }
        //No se puede probar este porque precio y peso no pueden ser null
        // (que seria la prueba equivalente)
        /*
        try {
            long id4 = nda.createProduct("hola", null);
            if (id4 < 0) {
                Log.d("CASO 4", "BIEN");
            } else {
                Log.d("CASO 4", "MAL");
            }
        } catch (Exception e){
            Log.d("CASO 4", "EXCEPCION");
        }

         */


        //Prueba update (solo si ha ido bien create)
        if(id1 > 0) {
            //Prueba va bien
            if(nda.updateProduct(id1,"producto_prueba_mod", 1.0, 1.0)){
                Log.d("PRODUCTOS: CASO 4", "BIEN");
            } else {
                Log.d("PRODUCTOS: CASO 4", "MAL");
            }

            //Prueba va mal
            try {
                if (!nda.updateProduct(id1, null, 1.0, 1.0)) {
                    Log.d("PRODUCTOS: CASO 5", "BIEN");
                } else {
                    Log.d("PRODUCTOS: CASO 5", "MAL");
                }
            } catch (Exception e){
                Log.d("PRODUCTOS: CASO 5", "EXCEPCION");
            }

            try {
                if (!nda.updateProduct(id1, "", 1.0, 1.0)) {
                    Log.d("PRODUCTOS: CASO 6", "BIEN");
                } else {
                    Log.d("PRODUCTOS: CASO 6", "MAL");
                }
            } catch (Exception e){
                Log.d("PRODUCTOS: CASO 6", "EXCEPCION");
            }

            //Lo mismo que antes
            /*
            try {
                if (!nda.updateProduct(id1, "hola", 1.0, 1.0)) {
                    Log.d("CASO 8", "BIEN");
                } else {
                    Log.d("CASO 8", "MAL");
                }
            } catch (Exception e){
                Log.d("CASO 8", "EXCEPCION");
            }
            */
            try {
                if (!nda.updateProduct(-1, "hola", 1.0, 1.0)) {
                    Log.d("PRODUCTOS: CASO 7", "BIEN");
                } else {
                    Log.d("PRODUCTOS: CASO 7", "MAL");
                }
            } catch (Exception e){
                Log.d("PRODUCTOS: CASO 7", "EXCEPCION");
            }

        }

        //Prueba delete (solo si create ha ido bien)
        if(id1>0){
            //Prueba va bien
            if(nda.deleteProduct(id1)){
                Log.d("PRODUCTOS: CASO 8", "BIEN");
            } else {
                Log.d("PRODUCTOS: CASO 8", "MAL");
            }

            //Prueba va mal
            try {
                if (!nda.deleteProduct(-1)) {
                    Log.d("PRODUCTOS: CASO 9", "BIEN");
                } else {
                    Log.d("PRODUCTOS: CASO 9", "MAL");
                }
            } catch (Exception e){
                Log.d("PRODUCTOS: CASO 9", "EXCEPCION");
            }
        }
    }

    public static void runTestListas(DbAdapter nda){
        // Prueba va bien create
        long id1 = nda.createShoppingList("lista_prueba");
        if(id1 < 0){
            Log.d("LISTAS: CASO 1", "ERROR");
        } else {
            Log.d("LISTAS: CASO 1", "BIEN");
        }

        //Prueba va mal create
        try {
            //FUNADA
            long id2 = nda.createShoppingList(null);
            if (id2 < 0) {
                Log.d("LISTAS: CASO 2", "BIEN");
            } else {
                Log.d("LISTAS: CASO 2", "MAL");
            }
        } catch (Exception e){
            Log.d("LISTAS: CASO 2", "EXCEPCION");
        }

        try {
            long id3 = nda.createShoppingList("");
            if (id3 < 0) {
                Log.d("LISTAS: CASO 3", "BIEN");
            } else {
                Log.d("LISTAS: CASO 3", "MAL");
            }
        } catch (Exception e){
            Log.d("LISTAS: CASO 3", "EXCEPCION");
        }


        //Prueba update (solo si ha ido bien create)
        if(id1 > 0) {
            //Prueba va bien
            if(nda.updateShoppingList(id1,"lista_prueba_mod")){
                Log.d("LISTAS: CASO 4", "BIEN");
            } else {
                Log.d("LISTAS: CASO 4", "MAL");
            }

            //Prueba va mal
            try {
                if (!nda.updateShoppingList(id1, null)) {
                    Log.d("LISTAS: CASO 5", "BIEN");
                } else {
                    Log.d("LISTAS: CASO 5", "MAL");
                }
            } catch (Exception e){
                Log.d("LISTAS: CASO 5", "EXCEPCION");
            }

            try {
                if (!nda.updateShoppingList(id1, "")) {
                    Log.d("LISTAS: CASO 6", "BIEN");
                } else {
                    Log.d("LISTAS: CASO 6", "MAL");
                }
            } catch (Exception e){
                Log.d("LISTAS: CASO 6", "EXCEPCION");
            }

            try {
                if (!nda.updateShoppingList(-1, "hola")) {
                    Log.d("LISTAS: CASO 7", "BIEN");
                } else {
                    Log.d("LISTAS: CASO 7", "MAL");
                }
            } catch (Exception e){
                Log.d("LISTAS: CASO 7", "EXCEPCION");
            }

        }

        //Prueba delete (solo si create ha ido bien)
        if(id1>0){
            //Prueba va bien
            if(nda.deleteShoppingList(id1)){
                Log.d("LISTAS: CASO 8", "BIEN");
            } else {
                Log.d("LISTAS: CASO 8", "MAL");
            }

            //Prueba va mal
            try {
                if (!nda.deleteShoppingList(-1)) {
                    Log.d("LISTAS: CASO 9", "BIEN");
                } else {
                    Log.d("LISTAS: CASO 9", "MAL");
                }
            } catch (Exception e){
                Log.d("LISTAS: CASO 9", "EXCEPCION");
            }
        }
    }

    public static void runTestProductosLista(DbAdapter nda){
        long idl = nda.createShoppingList("lista_prueba");
        long idp = nda.createProduct("producto_prueba", 1.0, 1.0);
        long idp2 = nda.createProduct("producto_prueba2", 1.0, 1.0);
        long idc = 0;
        // Prueba va bien añadir producto
        if((idc = nda.addProductToList(idl, idp, 3))>0){
            Log.d("P_LISTAS: CASO 1", "BIEN");
        } else {
            Log.d("P_LISTAS: CASO 1", "ERROR");
        }

        //Prueba va mal añadir producto
        try {
            //FUNADA
            long id2 = nda.addProductToList(idl, idp2, 0);
            if (id2 < 0) {
                Log.d("P_LISTAS: CASO 2", "BIEN");
            } else {
                Log.d("P_LISTAS: CASO 2", "MAL");
            }
        } catch (Exception e){
            Log.d("P_LISTAS: CASO 2", "EXCEPCION");
        }


        try {
            long id3 = nda.addProductToList(idl, -1, 3);
            if (id3 < 0) {
                Log.d("P_LISTAS: CASO 3", "BIEN");
            } else {
                Log.d("P_LISTAS: CASO 3", "MAL");
            }
        } catch (Exception e){
            Log.d("P_LISTAS: CASO 3", "EXCEPCION");
        }

        try {
            long id3 = nda.addProductToList(-1, idp2, 3);
            if (id3 < 0) {
                Log.d("P_LISTAS: CASO 4", "BIEN");
            } else {
                Log.d("P_LISTAS: CASO 4", "MAL");
            }
        } catch (Exception e){
            Log.d("P_LISTAS: CASO 4", "EXCEPCION");
        }


        //Prueba modificar producto creado
        if(idc > 0) {
            //Prueba va bien modificar producto
            if(nda.updateProductInList(idc, idp2, 3)){
                Log.d("P_LISTAS: CASO 5", "BIEN");
            } else {
                Log.d("P_LISTAS: CASO 5", "MAL");
            }
            //Prueba va bien modificar cantidad producto
            if(nda.updateProductInList(idc, idp2, 2)){
                Log.d("P_LISTAS: CASO 6", "BIEN");
            } else {
                Log.d("P_LISTAS: CASO 6", "MAL");
            }

            //Prueba va mal
            try {
                if (!nda.updateProductInList(idc, idp2, -1)) {
                    Log.d("P_LISTAS: CASO 7", "BIEN");
                } else {
                    Log.d("P_LISTAS: CASO 7", "MAL");
                }
            } catch (Exception e){
                Log.d("P_LISTAS: CASO 7", "EXCEPCION");
            }

            try {
                if (!nda.updateProductInList(idc, -1, 2)) {
                    Log.d("P_LISTAS: CASO 8", "BIEN");
                } else {
                    Log.d("P_LISTAS: CASO 8", "MAL");
                }
            } catch (Exception e){
                Log.d("P_LISTAS: CASO 8", "EXCEPCION");
            }

            try {
                if (!nda.updateProductInList(-1, idp2, 2)) {
                    Log.d("P_LISTAS: CASO 9", "BIEN");
                } else {
                    Log.d("P_LISTAS: CASO 9", "MAL");
                }
            } catch (Exception e){
                Log.d("P_LISTAS: CASO 9", "EXCEPCION");
            }

        }

        //Prueba delete (solo si create ha ido bien)
        if(idc>0){
            //Prueba va bien
            if(nda.deleteProductInList(idc)){
                Log.d("P_LISTAS: CASO 10", "BIEN");
            } else {
                Log.d("P_LISTAS: CASO 10", "MAL");
            }

            //Prueba va mal
            try {
                if (!nda.deleteProductInList(-1)) {
                    Log.d("P_LISTAS: CASO 11", "BIEN");
                } else {
                    Log.d("P_LISTAS: CASO 11", "MAL");
                }
            } catch (Exception e){
                Log.d("LISTAS: CASO 11", "EXCEPCION");
            }

            nda.deleteProduct(idp);
            nda.deleteProduct(idp2);
            nda.deleteShoppingList(idl);
        }
    }

    public static void casiPetardoTest(DbAdapter nda){
        boolean error = false;
        int i;
        long id = 0;
        for(i=0; i<1000 && !error; i++){
            if((id = nda.createProduct("PRUEBA_VOLUMEN: "+i, 10.0, 10.0))<0) {
                Log.d("PRODUCTOS: CASO VOLUMEN", "MAL");
                error = true;
            }
        }
        /*
        for(int j=0; j<i ; j++){
            if(!nda.deleteNote(id)){
                Log.d("CASO VOLUMEN", "MAL");
                error = true;
            }
            id--;
        }*/
        if(!error){
            Log.d("PRODUCTOS: CASO VOLUMEN", "BIEN");
        }
    }

    public static void casiPetardoListasTest(DbAdapter nda){
        boolean error = false;
        int i;
        long id = 0;
        for(i=0; i<100 && !error; i++){
            if((id = nda.createShoppingList("PRUEBA_VOLUMEN: "))<0) {
                Log.d("LISTAS: CASO VOLUMEN", "MAL");
                error = true;
            }
        }
        /*
        for(int j=0; j<i ; j++){
            if(!nda.deleteNote(id)){
                Log.d("CASO VOLUMEN", "MAL");
                error = true;
            }
            id--;
        }*/
        if(!error){
            Log.d("LISTAS: CASO VOLUMEN", "BIEN");
        }
    }

    public static void borrarCasiPetardoListasTest(DbAdapter nda){
        Cursor cursor = nda.fetchAllShoppingLists();
        try{
            while(cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndexOrThrow(DbAdapter.LIST_KEY_NAME)).contains("PRUEBA_VOLUMEN: ")) {
                    nda.deleteProduct(cursor.getLong(cursor.getColumnIndexOrThrow(DbAdapter.LIST_KEY_ROWID)));
                }
            }
        } catch(Exception e){
            Log.d("LISTAS: CASO VOLUMEN", "EXCEPCION AL BORRAR");

        } finally {
            cursor.close();
        }

    }

    public static void borrarCasiPetardoTest(DbAdapter nda){
        Cursor cursor = nda.fetchAllProducts();
        try{
            while(cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndexOrThrow(DbAdapter.PRODUCT_KEY_NAME)).contains("PRUEBA_VOLUMEN: ")) {
                    nda.deleteProduct(cursor.getLong(cursor.getColumnIndexOrThrow(DbAdapter.PRODUCT_KEY_ROWID)));
                }
            }
        } catch(Exception e){
            Log.d("PRODUCTOS: CASO VOLUMEN", "EXCEPCION AL BORRAR");

        } finally {
            cursor.close();
        }

    }
}
