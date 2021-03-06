package es.artal_benedi.ListaCompra;

import android.database.Cursor;
import android.util.Log;

public class Test {

    /**
     * Realizará las pruebas para las clases de equivalencia para los productos: crear, actualizar
     * y borrar.
     *
     * @param nda Objeto del adaptador de la BD
     */
    public static void runTestProducts(DbAdapter nda){
        // Prueba va bien create
        long id1 = nda.createProduct("producto_prueba", 1.0, 1.0);
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
            long id3 = nda.createProduct("", 1.0, 1.0);
            if (id3 < 0) {
                Log.d("PRODUCTOS: CASO 3", "BIEN");
            } else {
                Log.d("PRODUCTOS: CASO 3", "MAL");
            }
        } catch (Exception e){
            Log.d("PRODUCTOS: CASO 3", "EXCEPCION");
        }

        try {
            long id3 = nda.createProduct("producto_prueba", 0.0, 1.0);
            if (id3 < 0) {
                Log.d("PRODUCTOS: CASO 4", "BIEN");
            } else {
                Log.d("PRODUCTOS: CASO 4", "MAL");
            }
        } catch (Exception e){
            Log.d("PRODUCTOS: CASO 4", "EXCEPCION");
        }

        try {
            long id3 = nda.createProduct("producto_prueba", 1.0, 0.0);
            if (id3 < 0) {
                Log.d("PRODUCTOS: CASO 5", "BIEN");
            } else {
                Log.d("PRODUCTOS: CASO 5", "MAL");
            }
        } catch (Exception e){
            Log.d("PRODUCTOS: CASO 5", "EXCEPCION");
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
                Log.d("PRODUCTOS: CASO 6", "BIEN");
            } else {
                Log.d("PRODUCTOS: CASO 6", "MAL");
            }

            //Prueba va mal
            try {
                if (!nda.updateProduct(id1, null, 1.0, 1.0)) {
                    Log.d("PRODUCTOS: CASO 7", "BIEN");
                } else {
                    Log.d("PRODUCTOS: CASO 7", "MAL");
                }
            } catch (Exception e){
                Log.d("PRODUCTOS: CASO 7", "EXCEPCION");
            }

            try {
                if (!nda.updateProduct(id1, "", 1.0, 1.0)) {
                    Log.d("PRODUCTOS: CASO 8", "BIEN");
                } else {
                    Log.d("PRODUCTOS: CASO 8", "MAL");
                }
            } catch (Exception e){
                Log.d("PRODUCTOS: CASO 8", "EXCEPCION");
            }

            try {
                if (!nda.updateProduct(id1, "hola", 0.0, 1.0)) {
                    Log.d("PRODUCTOS: CASO 9", "BIEN");
                } else {
                    Log.d("PRODUCTOS: CASO 9", "MAL");
                }
            } catch (Exception e){
                Log.d("PRODUCTOS: CASO 9", "EXCEPCION");
            }

            try {
                if (!nda.updateProduct(id1, "hola", 1.0, 0.0)) {
                    Log.d("PRODUCTOS: CASO 10", "BIEN");
                } else {
                    Log.d("PRODUCTOS: CASO 10", "MAL");
                }
            } catch (Exception e){
                Log.d("PRODUCTOS: CASO 10", "EXCEPCION");
            }

            try {
                if (!nda.updateProduct(-1, "producto_prueba", 1.0, 1.0)) {
                    Log.d("PRODUCTOS: CASO 11", "BIEN");
                } else {
                    Log.d("PRODUCTOS: CASO 11", "MAL");
                }
            } catch (Exception e){
                Log.d("PRODUCTOS: CASO 11", "EXCEPCION");
            }

        }

        //Prueba delete (solo si create ha ido bien)
        if(id1>0){
            //Prueba va bien
            if(nda.deleteProduct(id1)){
                Log.d("PRODUCTOS: CASO 12", "BIEN");
            } else {
                Log.d("PRODUCTOS: CASO 12", "MAL");
            }

            //Prueba va mal
            try {
                if (!nda.deleteProduct(-1)) {
                    Log.d("PRODUCTOS: CASO 13", "BIEN");
                } else {
                    Log.d("PRODUCTOS: CASO 13", "MAL");
                }
            } catch (Exception e){
                Log.d("PRODUCTOS: CASO 13", "EXCEPCION");
            }
        }
    }

    /**
     * Realizará las pruebas para las clases de equivalencia para las listas: crear, actualizar
     * y borrar.
     *
     * @param nda Objeto del adaptador de la BD
     */
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

    /**
     * Realizará las pruebas para las clases de equivalencia para los productos en una lista de la
     * compra: crear, actualizar y borrar.
     *
     * @param nda Objeto del adaptador de la BD
     */
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


    /**
     * Crea 1000 Productos para poder realizar las pruebas de volumen
     *
     * @param nda Objeto del adaptador de la BD
     */
    public static void casiPetardoTest(DbAdapter nda){
        boolean error = false;
        int i = 0;
        long id = 0;
        for(i=0; i<1000 && !error; i++){
            if((id = nda.createProduct("PRUEBA_VOLUMEN: "+i, 10.0, 10.0))<0) {
                Log.d("PRODUCTOS: CASO VOLUMEN", "MAL");
                error = true;
            }
        }
        System.out.println("CasiPetardoTest: "+i);
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

    /**
     * Crea 1000 Listas para poder realizar las pruebas de volumen
     *
     * @param nda Objeto del adaptador de la BD
     */
    public static void casiPetardoListasTest(DbAdapter nda){
        boolean error = false;
        int i = 0;
        long id = 0;
        for(i=0; i<100 && !error; i++){
            if((id = nda.createShoppingList("PRUEBA_VOLUMEN: "+i))<0) {
                Log.d("LISTAS: CASO VOLUMEN", "MAL");
                error = true;
            }
        }
        System.out.println("CasiPetardoListasTest: "+i);
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

    /**
     * Borra las listas creadas para poder realizar las pruebas de volumen
     *
     * @param nda Objeto del adaptador de la BD
     */
    public static void borrarCasiPetardoListasTest(DbAdapter nda){
        Cursor cursor = nda.fetchAllShoppingLists();
        try{
            int i =0;
            while(cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndexOrThrow(DbAdapter.LIST_KEY_NAME)).contains("PRUEBA_VOLUMEN: ")) {
                    nda.deleteShoppingList(cursor.getLong(cursor.getColumnIndexOrThrow(DbAdapter.LIST_KEY_ROWID)));
                    i++;
                }
            }
            System.out.println("BorrarCasiPetardoListasTest: "+i);
        } catch(Exception e){
            Log.d("LISTAS: CASO VOLUMEN", "EXCEPCION AL BORRAR");

        } finally {
            cursor.close();
        }

    }

    /**
     * Borra los productos creados para poder realizar las pruebas de volumen
     *
     * @param nda Objeto del adaptador de la BD
     */
    public static void borrarCasiPetardoTest(DbAdapter nda){
        Cursor cursor = nda.fetchAllProducts();
        try{
            int i =0;
            while(cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndexOrThrow(DbAdapter.PRODUCT_KEY_NAME)).contains("PRUEBA_VOLUMEN: ")) {
                    nda.deleteProduct(cursor.getLong(cursor.getColumnIndexOrThrow(DbAdapter.PRODUCT_KEY_ROWID)));
                    i++;
                }
            }
            System.out.println("BorrarCasiPetardoTest: "+i);
        } catch(Exception e){
            Log.d("PRODUCTOS: CASO VOLUMEN", "EXCEPCION AL BORRAR");

        } finally {
            cursor.close();
        }

    }


    /**
     * Realiza una prueba de sobrecarga para descubrir dónde está el límite de caracteres en el
     * nombre de una lista de la compra
     *
     * @param nda Objeto del adaptador de la BD
     */
    public static void crearTestSobrecargaLongitudNombre(DbAdapter nda){
        long id, count = 0;
        String append = "aaaaaaaaaa";
        System.out.println("Longitud del texto = 2^n*10+19");
        do{
            id = nda.createShoppingList("PRUEBA SOBRECARGA: " + append);
            append += append;
            count++;
            System.out.println("n = "+ count);
        }while(id > 0);
        System.out.println("Resultado final: n = "+ count);
    }

    /**
     * Realiza una prueba de sobrecarga para descubrir dónde está el límite de lista de la compra
     * que soporta el sistema.
     *
     * @param nda Objeto del adaptador de la BD
     */
    public static void crearTestSobrecargaNumeroListas(DbAdapter nda){
        long id, count = 0;
        System.out.println("Número de listas");
        try {
            do {
                id = nda.createShoppingList("PRUEBA SOBRECARGA: ");
                count++;
                System.out.println("n = " + count);
            } while (id > 0);
            System.out.println("Resultado final: n = " + count);
        }catch(Exception e) {
            System.out.println("Resultado final: n = " + count);
        }
    }

    /**
     * Realiza una prueba de sobrecarga para descubrir dónde está el límite de productos
     * que soporta el sistema.
     *
     * @param nda Objeto del adaptador de la BD
     */
    public static void crearTestSobrecargaNumeroProductos(DbAdapter nda){
        long id, count = 0;
        System.out.println("Número de productos");
        try {
            do {
                id = nda.createProduct("PRODUCTO SOBRECARGA", 1, 1);
                count++;
                System.out.println("n = " + count);
            } while (id > 0);
            System.out.println("Resultado final: n = " + count);
        }catch(Exception e) {
            System.out.println("Resultado final: n = " + count);
        }
    }


    /**
     * Realiza una prueba de sobrecarga para descubrir dónde está el límite de productos que puede
     * haber en las listas de la compra que soporta el sistema.
     *
     * @param nda Objeto del adaptador de la BD
     */
    public static void TestSobrecargaAsociarProductosListas(DbAdapter nda){
        Cursor productos = nda.fetchAllProducts();
        Cursor listas = nda.fetchAllShoppingLists();
        while(listas.moveToNext()){
            long idLista = listas.getLong(listas.getColumnIndexOrThrow(DbAdapter.LIST_KEY_ROWID));
            productos.moveToFirst();
            do{
                long idProducto = productos.getLong(productos.getColumnIndexOrThrow(DbAdapter.PRODUCT_KEY_ROWID));
                nda.addProductToList(idLista, idProducto, 1);
            }while(productos.moveToNext());
            System.out.println("Todos los productos añadidos en lista: " + idLista);
        }
    }

    /**
     * Realiza una prueba de sobrecarga para descubrir dónde está el límite peso que puede tener un
     * producto.
     *
     * @param nda Objeto del adaptador de la BD
     */
    public static void TestSobrecargaPeso(DbAdapter nda){
        long peso = 1, id;
        do{
            System.out.println("Next peso = " + peso);
            id = nda.createProduct("SOBRECARGA PESO", 1, peso);
            peso *= 2;
        }while(id > 0);
    }

    /**
     * Realiza una prueba de sobrecarga para descubrir dónde está el límite precio que puede tener un
     * producto.
     *
     * @param nda Objeto del adaptador de la BD
     */
    public static void TestSobrecargaPrecio(DbAdapter nda){
        long precio = 1, id;
        do{
            System.out.println("Next precio = " + precio);
            id = nda.createProduct("SOBRECARGA precio", precio, 1);
            precio *= 2;
        }while(id > 0);
    }

    /**
     * Realiza una prueba de sobrecarga para descubrir dónde está el límite cantidad de un producto
     * que puede tener una lista de la compra.
     *
     * @param nda Objeto del adaptador de la BD
     */
    public static void TestSobrecargaCantidad(DbAdapter nda){
        int cantidad = 1;
        long id;
        long idLista = nda.createShoppingList("SOBRECARGA CANTIDAD");
        long idProducto = nda.createProduct("SOBRECARGA CANTIDAD", 1, 1);
        do{
            System.out.println("Next cantidad = " + cantidad);
            id = nda.addProductToList(idLista, idProducto, cantidad);
            cantidad *= 2;
        }while(id > 0);
    }

    /*
Test de Sobrecarga:
 - Longitud del nombre. Empieza a fallar con 2^17*10+19 caracteres, saca excepción en 2^23*10+19 (poco más de 2^26). Según documentación oficial el límite de la BD está en 10^9 (casi 2^30) aunque se puede aumentar hasta 2^31 - 1. El tamaño máximo de JAVA de un string es 2^31-1
 - Peso máximo de un producto/lista: Cuando llega a 2^62 pasa a números negativos pero realmente es 2^63, la siguiente iteración se queda en 0. Sucede un overflow
 - Precio máximo de un producto/lista: Sucede lo mismo que en el caso anterior
 - Cantidad de un producto: La Sum(2^n,0,30) va bien. Al intentar sumar 2^31 lo toma como negativo y se para.
 - Número de Listas de la compra vacías: A eso de los 1500 sale mensaje de no responde, pero sigue creando. A las 100.623 me he cansado y lo he parado. Seguía creando al mismo ritmo así que supongo que no fallaría pronto. El sistema las visualiza bien. Según documentación el límite teórico está en 2^64, pero el tamaño de la BD está en 281 TB así que dificilmente puede pasar de los 2*10^13.
 - Número de Productos: Igual que antes a los 1500 mensaje de no respone, pero sigue creando. Al igual que antes, a los 100.110 me cansé y lo dejé.
 - La asociación de los 100.110 productos a las 100.623 listas para estar funcionando, pero le cuesta. He probado asociar todos a una lista de la compra.

Fuentes:
https://www.sqlite.org/limits.html
https://docs.oracle.com/javase/7/docs/api/java/lang/String.html#length()
     */
}
