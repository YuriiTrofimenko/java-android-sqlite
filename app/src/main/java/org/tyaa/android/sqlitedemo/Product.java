package org.tyaa.android.sqlitedemo;

public class Product {

    /**
     * Название Продукта
     */
    public String name;
    /**
     * Стоимость Продукта
     */
    public double price;
    /**
     * Вес Продукта
     */
    public int weight;
    /**
     * Идентификатор строки Продукта в таблице Products
     * Базы Данных
     */
    public int id;

    public Product(String name, double price,
                   int weight, int id)
    {
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "" + this.id + ": " + this.name + ": " + this.price + ": " + this.weight;
    }
}
