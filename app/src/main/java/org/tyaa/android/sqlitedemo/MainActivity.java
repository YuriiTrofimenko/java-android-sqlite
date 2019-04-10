package org.tyaa.android.sqlitedemo;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MySQLiteOpenHelper dbHelper;
    private ArrayAdapter<Product> adapter;
    private AlertDialog dialog;
    private int curItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.dbHelper = new MySQLiteOpenHelper(this);

        ListView listView = (ListView) this.findViewById(R.id.mainListView);
        ArrayList<Product> lstProducts = new ArrayList<>();

        final SQLiteDatabase db = this.dbHelper.
                getWritableDatabase();
        Cursor cursor = db.query(
                MySQLiteOpenHelper.tblNameProducts
                , null
                , null
                , null
                , null
                , null
                , "name");
        if (cursor.moveToFirst()) {

            int indexId = cursor.getColumnIndex("id");
            int indexName = cursor.getColumnIndex("name");
            int indexPrice = cursor.getColumnIndex("price");
            int indexWeight = cursor.getColumnIndex("weight");

            do {
                lstProducts.add(new Product
                        (cursor.getString(indexName),
                                cursor.getDouble(indexPrice),
                                cursor.getInt(indexWeight),
                                cursor.getInt(indexId))
                );
            }
            while (cursor.moveToNext());
            cursor.close();
        } else
            Log.d("Error", "Невозможно позиционироваться на первую строку Курсора");
        this.adapter = new ArrayAdapter<Product>(this,
                R.layout.product_item, R.id.nameTextView,
                lstProducts);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (MainActivity.this.curItem != -1){

                    //TODO
                }
                MainActivity.this.curItem = position;
                MainActivity.this.dialog.show();
            }
        });

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setNegativeButton("Отменить",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Toast.makeText(MainActivity.this,
                                "Отменено", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setPositiveButton("Применить",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //-- Чтение данных Продукта ---------------------
                        try {
                            String productNameStr =
                                    ((EditText) ((AlertDialog) dialog).
                                            findViewById(R.id.nameEditText)).
                                            getText().toString();
                            String productPriceStr =
                                    ((EditText) ((AlertDialog) dialog).
                                            findViewById(R.id.priceEditText)).
                                            getText().toString();
                            String productWeightStr =
                                    ((EditText) ((AlertDialog) dialog).
                                            findViewById(R.id.weightEditText)).
                                            getText().toString();
                            if (productNameStr.isEmpty() ||
                                    productPriceStr.isEmpty() ||
                                    productWeightStr.isEmpty()) {

                                throw new Exception("Заполнены не все поля");
                            }
                            double productPrice = Double.
                                    parseDouble(productPriceStr);
                            int productWeight = Integer.
                                    parseInt(productWeightStr);
                            //-- Занесение в Базу Данных нового товара ------
                            ContentValues row = new ContentValues();
                            row.put("name", productNameStr);
                            row.put("price", productPrice);
                            row.put("weight", productWeight);
                            //-- Определение что необходимо сделать —
                            //-- добавить строку или заменить
                            String strHiddenId = ((TextView)
                                    ((AlertDialog) dialog).
                                            findViewById(R.id.hiddenTextView)).
                                    getText().toString();
                            if (strHiddenId.isEmpty()) {
                                //-- Добавление продукта ------------------------
                                long rowID = db.insert(
                                        MySQLiteOpenHelper.
                                                tblNameProducts, null, row);
                                if (rowID == -1)
                                    throw new Exception(
                                            "Строка не добавлена в таблицу (rowID = -1)");
                                Toast.makeText(MainActivity.this,
                                        "Продукт успешно добавлен",
                                        Toast.LENGTH_SHORT).show();
                                MainActivity.this.adapter.add(
                                        new Product(productNameStr,
                                                productPrice, productWeight,
                                                (int) rowID));
                            } else {
                                //-- Обновление продукта ------------------------
                                int cnt = db.update(
                                        MySQLiteOpenHelper.
                                                tblNameProducts, row, "id=?",
                                        new String[]{strHiddenId});
                                Toast.makeText(MainActivity.this,
                                        "Обновлено строк: " + cnt,
                                        Toast.LENGTH_SHORT).show();
                                if (cnt > 0) {
                                    //-- Внесение изменений в список ListView -------
                                    Product product =
                                            MainActivity.this.adapter.
                                                    getItem(MainActivity.this.
                                                            curItem);
                                    product.name = productNameStr;
                                    product.price = productPrice;
                                    product.weight = productWeight;
                                    MainActivity.this.adapter.remove(product);
                                    MainActivity.this.adapter.insert(product, MainActivity.this.curItem);
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this,
                                    "Данные введены неправильно: " +
                                            e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d("Debug", e.getMessage());
                        }
                    }
                });
        //-- Диалоговое окно для ввода данных -----------
        this.dialog = builder.create();
        this.dialog.setView(this.getLayoutInflater().
                inflate(R.layout.dialog_add_item, null, true));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //-- Получение доступа к БД ---------------------
        final SQLiteDatabase db = this.dbHelper.
                getWritableDatabase();
        //-- Обработка выбранного пункта меню -----------
        int id = item.getItemId();
        switch (id) {
            /*
             * Добавление записи в БД
             * ----------------------------------------------
             */
            case R.id.action_add: {
                //-- Диалоговое окно "Добавить новый продукт" ---
                this.dialog.setTitle("Добавить новый Продукт");
                //-- Показываем Диалоговое окно -----------------
                this.dialog.show();
                //-- Очистка текстовых полей Диалогового окна ---
                if (this.dialog.findViewById(R.
                        id.nameEditText) != null) {
                    ((EditText) this.dialog.findViewById(
                            R.id.nameEditText)).setText("");
                    ((EditText) this.dialog.findViewById(
                            R.id.priceEditText)).setText("");
                    ((EditText) this.dialog.findViewById(
                            R.id.weightEditText)).setText("");
                    ((TextView) this.dialog.findViewById(
                            R.id.hiddenTextView)).setText("");
                }
            }
            break;
            /*
             * Удаление записи из БД
             * ----------------------------------------------
             */
            case R.id.action_del:
                //-- Находим текущий выбранный продукт ----------
                if (this.curItem == -1) {
                    Toast.makeText(this, "Продукт не выбран",
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                //-- Находим текущий выделенный элемент списка --
                Product product = this.adapter.getItem(this.curItem);
                //-- Удаляем из таблицы Базы Данных -------------
                int rowAffected = db.delete(
                        MySQLiteOpenHelper.tblNameProducts,
                        "id=?", new String[]{String.
                                valueOf(product.id)});
                Toast.makeText(this,
                        "Удалено строк: " + rowAffected,
                        Toast.LENGTH_SHORT).show();
                //-- Внесение изменений в список ListView (lvMain) --
                this.adapter.remove(product);
                break;
            /*
             * Обновление записи из БД
             * ----------------------------------------------
             */
            case R.id.action_edt:
                //-- Диалоговое окно "Редактировать Продукт" ----
                this.dialog.setTitle("Редактировать Продукт");
                //-- Находим текущий выбранный продукт ----------
                if (this.curItem == -1) {
                    Toast.makeText(this, "Продукт не выбран",
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                Product P1 = this.adapter.getItem(this.curItem);
                //-- Показываем Диалоговое окно -----------------
                this.dialog.show();
                //-- Инициализация текстовых полей Диалогового окна
                if (this.dialog.findViewById(R.
                        id.nameEditText) != null) {
                    ((EditText) this.dialog.findViewById(
                            R.id.nameEditText)).setText(P1.name);
                    ((EditText) this.dialog.findViewById(
                            R.id.priceEditText)).setText(String.
                            valueOf(P1.price));
                    ((EditText) this.dialog.findViewById(
                            R.id.weightEditText)).setText(String.
                            valueOf(P1.weight));
                    ((TextView) this.dialog.findViewById(
                            R.id.hiddenTextView)).setText(String.
                            valueOf(P1.id));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
