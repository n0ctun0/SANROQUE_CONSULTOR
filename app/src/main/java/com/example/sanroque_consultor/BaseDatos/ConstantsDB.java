package com.example.sanroque_consultor.BaseDatos;

public class ConstantsDB {
    //General
    public static final String DB_NAME = "sanroque12.db";
    public static final int DB_VERSION = 12;

    //TABLA PRODUCTOS

    public static final String TABLA_PRODUCTO = "Producto";
    public static final String PRO_CODIGOPRODUCTO = "_codigoproducto";
    public static final String PRO_DESCRIPCION1 = "DescArticulo_1";
    public static final String PRO_DESCRIPCION2 = "DescArticulo_2";
    public static final String PRO_CODPROD = "CodProd";
    public static final String PRO_CODBARRAS= "CodBarras";
    public static final String PRO_PRECIO = "Precio";
    public static final String PRO_STOCK = "Stock";
    public static final String PRO_IP = "IP";
    public static final String PRO_PRODUCTO = "Producto";
    public static final String PRO_SUC = "Suc";
    public static final String PRO_MENSAJE= "Mensaje";
    public static final String PRO_ESTADO = "Estado";
    public static final String PRO_OFFAVAILABLE = "Off_available";
    public static final String PRO_TXTOFERTA  = "txt_oferta";






    public static final String TABLA_PRODUCTO_SQL =
            "CREATE TABLE  " + TABLA_PRODUCTO + "(" +
                    PRO_CODIGOPRODUCTO + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PRO_DESCRIPCION1   + " TEXT," +
                    PRO_DESCRIPCION2 + " TEXT," +
                    PRO_CODPROD + " TEXT," +
                    PRO_CODBARRAS + " TEXT," +
                    PRO_PRECIO + " TEXT," +
                    PRO_STOCK + " TEXT," +
                    PRO_IP + " TEXT," +
                    PRO_PRODUCTO + " TEXT," +
                    PRO_SUC + " TEXT," +
                    PRO_MENSAJE + " TEXT," +
                    PRO_ESTADO + " TEXT," +
                    PRO_OFFAVAILABLE + " TEXT," +
                    PRO_TXTOFERTA   + " TEXT);" ;




}
