package com.example.android.inventoryappstage2;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryappstage2.data.DatabaseContract;
import com.example.android.inventoryappstage2.data.DatabaseContract.BookEntry;

/**
 * Displays list of books that were entered and stored in the app.
 */

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int BOOK_LOADER = 0;

    /**
     * Adapter for the ListView
     */
    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        //Insert new item into database and setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the book data
        ListView bookListView = findViewById(R.id.list_books);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of book data in the Cursor.
        // There is no book data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link DetailActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific book that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link BookEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.inv.../books/2"
                // if the book with ID 2 was clicked on.
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentBookUri);

                // Launch the {@link EditorActivity} to display the data for the current book.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded book data into the database. For debugging purposes only.
     */
    private void insertBook() {
        // Create a ContentValues object where column names are the keys,
        // and book's example attributes are the values.
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.BookEntry.COLUMN_NAME, getString(R.string.example_book_name));
        values.put(DatabaseContract.BookEntry.COLUMN_CATEGORY, DatabaseContract.BookEntry.CATEGORY_ACTION_ADVENTURE);
        values.put(DatabaseContract.BookEntry.COLUMN_PRICE, 25);
        values.put(BookEntry.COLUMN_QUANTITY, 10);
        values.put(DatabaseContract.BookEntry.COLUMN_SUPPLIER, getString(R.string.example_supplier));
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, getString(R.string.example_supplier_phone));
        values.put(DatabaseContract.BookEntry.COLUMN_SUPPLIER_EMAIL, getString(R.string.example_supplier_email));

        // Insert a new row for book's example into the provider using the ContentResolver.
        // Use the {@link BookEntry#CONTENT_URI} to indicate that we want to insert
        // into the books database table.
        // Receive the new content URI that will allow us to access example book's data in the future.
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all books in the database.
     */
    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(DatabaseContract.BookEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + getString(R.string.msg_rows_deteted));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.insert_dummy_data:
                insertBook();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                DatabaseContract.BookEntry._ID,
                DatabaseContract.BookEntry.COLUMN_NAME,
                DatabaseContract.BookEntry.COLUMN_PRICE,
                DatabaseContract.BookEntry.COLUMN_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                DatabaseContract.BookEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link BookCursorAdapter} with this new cursor containing updated book data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}