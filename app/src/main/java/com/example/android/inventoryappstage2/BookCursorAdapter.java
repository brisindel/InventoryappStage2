package com.example.android.inventoryappstage2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryappstage2.data.DatabaseContract;
import com.example.android.inventoryappstage2.data.DatabaseContract.BookEntry;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */

public class BookCursorAdapter extends CursorAdapter {


    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView mBookName = view.findViewById(R.id.book_name);
        TextView mBookPrice = view.findViewById(R.id.book_price);
        TextView mBookQuantity = view.findViewById(R.id.book_in_stock);
        ImageButton sellButton = view.findViewById(R.id.sell_button);

        // Find the columns of book attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(DatabaseContract.BookEntry.COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(DatabaseContract.BookEntry.COLUMN_PRICE);
        int stockColumnIndex = cursor.getColumnIndex(DatabaseContract.BookEntry.COLUMN_QUANTITY);

        // Read the book attributes from the Cursor for the current pet
        String bookName = cursor.getString(nameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        String bookQuantity = cursor.getString(stockColumnIndex);


        //Add text to display values

        String bookPriceText = "Price: " + bookPrice + " $";
        String bookQuantityText = "In stock: " + bookQuantity + " pcs";

        // Update the TextViews with the attributes for the current book
        mBookName.setText(bookName);
        mBookPrice.setText(bookPriceText);
        mBookQuantity.setText(bookQuantityText);


        final int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
        String currentQuantity = cursor.getString(quantityColumnIndex);
        final int quantityIntCurrent = Integer.valueOf(currentQuantity);

        final int productId = cursor.getInt(cursor.getColumnIndex(DatabaseContract.BookEntry._ID));

        //Sell button which decrease quantity in storage
        sellButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (quantityIntCurrent > 0) {
                    int newQuantity = quantityIntCurrent - 1;
                    Uri quantityUri = ContentUris.withAppendedId(DatabaseContract.BookEntry.CONTENT_URI, productId);

                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_QUANTITY, newQuantity);
                    context.getContentResolver().update(quantityUri, values, null, null);
                } else {
                    Toast.makeText(context, "Book is out of stock!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
