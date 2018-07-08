package com.example.android.inventoryappstage2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryappstage2.data.DatabaseContract;
import com.example.android.inventoryappstage2.data.DatabaseContract.BookEntry;

import static com.example.android.inventoryappstage2.R.array;
import static com.example.android.inventoryappstage2.R.id;
import static com.example.android.inventoryappstage2.R.layout;
import static com.example.android.inventoryappstage2.R.string;

/**
 * Allows user to create a new book or edit an existing one.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;

    /**
     * Content URI for the existing book(null if it's a new book)
     */
    private Uri mCurrentBookUri;

    /**
     * EditText field to enter the book's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the book's price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the book's quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the book's supplier
     */
    private EditText mSupplierEditText;

    /**
     * EditText field to enter the book's supplier phone
     */
    private EditText mPhoneEditText;

    /**
     * EditText field to enter the book's supplier email
     */
    private EditText mEmailEditText;

    /**
     * Spinner field to enter the book's category
     */
    private Spinner mCategorySpinner;

    /**
     * int for quantity check
     */
    private int givenQuantity;

    /**
     * Books category. The possible valid values are in the DatabaseContract.java file:
     * {@link BookEntry#CATEGORY_ACTION_ADVENTURE},
     * {@link BookEntry#CATEGORY_DRAMA},
     * {@link BookEntry#CATEGORY_HORROR},
     * {@link BookEntry#CATEGORY_CHILDREN},
     * {@link BookEntry#CATEGORY_MYSTERY},
     * {@link BookEntry#CATEGORY_ROMANCE},
     * {@link BookEntry#CATEGORY_SATIRE},
     * {@link BookEntry#CATEGORY_SCIFI},
     * {@link BookEntry#CATEGORY_TRAVEL},
     * or {@link BookEntry#CATEGORY_UNKNOWN}.
     */
    private int mCategory = BookEntry.CATEGORY_UNKNOWN;

    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */
    private boolean mBookHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mBookHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.edit_layout);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // If the intent DOES NOT contain a book content URI, then we know that we are
        // creating a new book
        if (mCurrentBookUri == null) {
            // This is a new book, so change the app bar to say "Add a new book"
            setTitle(getString(string.new_book));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing book, so change app bar to say "Edit book"
            setTitle(getString(string.editor_activity_title_edit_book));

            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(id.edit_book_name);
        mPriceEditText = findViewById(id.edit_book_price);
        mQuantityEditText = findViewById(id.edit_book_quantity);
        mSupplierEditText = findViewById(id.edit_supplier);
        mPhoneEditText = findViewById(id.edit_phone);
        mEmailEditText = findViewById(id.edit_email);
        mCategorySpinner = findViewById(id.spinner_book_category);

        //Button for increasing quantity
        ImageButton mIncrease = findViewById(id.edit_quantity_increase);

        // Button for decreasing quantity
        ImageButton mDecrease = findViewById(id.edit_quantity_decrease);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);
        mEmailEditText.setOnTouchListener(mTouchListener);
        mIncrease.setOnTouchListener(mTouchListener);
        mDecrease.setOnTouchListener(mTouchListener);
        mCategorySpinner.setOnTouchListener(mTouchListener);

        //Increase quantity btn
        mIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String quantity = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantity)) {

                    //If givenQuantity is empty set 1; if is more set ++
                    givenQuantity = 0;
                    mQuantityEditText.setText(String.valueOf(givenQuantity + 1));

                    return;
                } else if (givenQuantity > 0) {
                    mQuantityEditText.setText(String.valueOf(givenQuantity + 1));
                } else {
                    mQuantityEditText.setText(String.valueOf(givenQuantity + 1));
                    return;
                }

            }
        });

        //Decrease quantity btn
        mDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(quantity)) {

                    //Toast information that Quantity can not be empty
                    Toast.makeText(EditorActivity.this, string.quantity_cannot_be_empty, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    givenQuantity = Integer.parseInt(quantity);
                    //To validate if quantity is greater than 0
                    if ((givenQuantity - 1) >= 0) {
                        mQuantityEditText.setText(String.valueOf(givenQuantity - 1));
                    } else {
                        //Toast information that Quantity can not be less then 0
                        Toast.makeText(EditorActivity.this, string.quantity_cannot_be_less_0, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        // Spinner setup
        setupSpinner();

        // Btn phone call
        ImageButton mPhone = findViewById(id.phoneButton);

        mPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PhoneNum = mPhoneEditText.getText().toString().trim();
                orderByPhone(PhoneNum);

            }
        });

        // Btn email
        final ImageButton mEmail = findViewById(id.emailButton);

        mEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inStock = mQuantityEditText.getText().toString().trim();
                String supplier = mSupplierEditText.getText().toString().trim();
                String emailAddress = mEmailEditText.getText().toString().trim();
                String bookName = mNameEditText.getText().toString().trim();

                orderByEmail(emailAddress, bookName, inStock, supplier);

            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the category of the book.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter categorySpinnerAdapter = ArrayAdapter.createFromResource(this,
                array.array_book_category, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mCategorySpinner.setAdapter(categorySpinnerAdapter);

        // Set the integer mSelected to the constant values
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if ((selection.equals(getString(string.category_action_adventure)))) {
                        mCategory = BookEntry.CATEGORY_ACTION_ADVENTURE;
                    } else if (selection.equals(getString(string.category_drama))) {
                        mCategory = BookEntry.CATEGORY_DRAMA;
                    } else if (selection.equals(getString(string.category_horror))) {
                        mCategory = BookEntry.CATEGORY_HORROR;
                    } else if (selection.equals(getString(string.category_children))) {
                        mCategory = BookEntry.CATEGORY_CHILDREN;
                    } else if (selection.equals(getString(string.category_mystery))) {
                        mCategory = BookEntry.CATEGORY_MYSTERY;
                    } else if (selection.equals(getString(string.category_romance))) {
                        mCategory = BookEntry.CATEGORY_ROMANCE;
                    } else if (selection.equals(getString(string.category_satire))) {
                        mCategory = BookEntry.CATEGORY_SATIRE;
                    } else if (selection.equals(getString(string.category_scifi))) {
                        mCategory = BookEntry.CATEGORY_SCIFI;
                    } else if (selection.equals(getString(string.category_travel))) {
                        mCategory = BookEntry.CATEGORY_TRAVEL;
                    } else {
                        mCategory = BookEntry.CATEGORY_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCategory = BookEntry.CATEGORY_UNKNOWN;
            }
        });
    }

    /**
     * Get user input from editor and save book into database.
     */
    private void saveBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space

        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String phoneString = mPhoneEditText.getText().toString().trim();
        String emailString = mEmailEditText.getText().toString().trim();

        // Check if this is supposed to be a new item.
        // Also check if all the fields in the editor are blank.
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierString) &&
                TextUtils.isEmpty(phoneString) && TextUtils.isEmpty(emailString)) {

            Toast.makeText(this, (getString(string.fill_blank)), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nameString)) {
            mNameEditText.setError(getString(string.question_price));
            return;
        }

        if (TextUtils.isEmpty(priceString)) {
            mPriceEditText.setError(getString(string.question_price));
            return;
        }

        if (TextUtils.isEmpty(quantityString)) {
            mQuantityEditText.setError(getString(string.question_quantity));
            return;
        }

        if (TextUtils.isEmpty(supplierString)) {
            mSupplierEditText.setError(getString(string.question_supplier));
            return;
        }

        if (TextUtils.isEmpty(phoneString)) {
            mPhoneEditText.setError(getString(string.question_supplier_phone));
            return;
        }

        if (TextUtils.isEmpty(emailString)) {
            mEmailEditText.setError(getString(string.question_supplier_email));
            return;
        }

        Double priceInt = Double.parseDouble(priceString);
        int quantityInt = Integer.parseInt(quantityString);
        int phoneInt = Integer.parseInt(phoneString);

        // Create a ContentValues object where column names are the keys,
        // and book attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_NAME, nameString);
        values.put(BookEntry.COLUMN_CATEGORY, mCategory);
        values.put(BookEntry.COLUMN_PRICE, priceInt);
        values.put(DatabaseContract.BookEntry.COLUMN_QUANTITY, quantityInt);
        values.put(BookEntry.COLUMN_SUPPLIER, supplierString);
        values.put(DatabaseContract.BookEntry.COLUMN_SUPPLIER_PHONE, phoneInt);
        values.put(BookEntry.COLUMN_SUPPLIER_EMAIL, emailString);

        // Determine if this is a new or existing book by checking if mCurrentBookUri is null or not
        if (mCurrentBookUri == null) {
            // This is a new book, so insert a new book into the provider,
            // returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(string.add_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(string.add_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an existing book, so update the book with content URI: mCurrentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(string.update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(string.update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case id.action_save:
                // Save book to database
                saveBook();
                // Exit activity
                return true;
            // Respond to a click on the "Delete" menu option
            case id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discartButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discartButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all book attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                DatabaseContract.BookEntry._ID,
                BookEntry.COLUMN_NAME,
                BookEntry.COLUMN_CATEGORY,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER,
                BookEntry.COLUMN_SUPPLIER_PHONE,
                BookEntry.COLUMN_SUPPLIER_EMAIL,};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,         // Query the content URI for the current book
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_NAME);
            int categoryColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_CATEGORY);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);
            int emailColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_EMAIL);

            // Extract out the value from the Cursor for the given column index
            final String name = cursor.getString(nameColumnIndex);
            int category = cursor.getInt(categoryColumnIndex);
            double price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            final String supplier = cursor.getString(supplierColumnIndex);
            int phone = cursor.getInt(phoneColumnIndex);
            final String email = cursor.getString(emailColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(Double.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierEditText.setText(supplier);
            mPhoneEditText.setText(Integer.toString(phone));
            mEmailEditText.setText(email);

            // Category is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown...).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (category) {
                case BookEntry.CATEGORY_ACTION_ADVENTURE:
                    mCategorySpinner.setSelection(1);
                    break;
                case BookEntry.CATEGORY_DRAMA:
                    mCategorySpinner.setSelection(2);
                    break;
                case DatabaseContract.BookEntry.CATEGORY_HORROR:
                    mCategorySpinner.setSelection(3);
                    break;
                case BookEntry.CATEGORY_CHILDREN:
                    mCategorySpinner.setSelection(4);
                    break;
                case BookEntry.CATEGORY_MYSTERY:
                    mCategorySpinner.setSelection(5);
                    break;
                case BookEntry.CATEGORY_ROMANCE:
                    mCategorySpinner.setSelection(6);
                    break;
                case BookEntry.CATEGORY_SATIRE:
                    mCategorySpinner.setSelection(7);
                    break;
                case BookEntry.CATEGORY_SCIFI:
                    mCategorySpinner.setSelection(8);
                    break;
                case DatabaseContract.BookEntry.CATEGORY_TRAVEL:
                    mCategorySpinner.setSelection(9);
                    break;
                default:
                    mCategorySpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mPhoneEditText.setText("");
        mEmailEditText.setText("");
        mCategorySpinner.setSelection(0); // Select "Unknown" category
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(string.unsaved_changes);
        builder.setPositiveButton(string.discard, discardButtonClickListener);
        builder.setNegativeButton(string.keep_edit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(string.delete_msg);
        builder.setPositiveButton(string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(string.delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(string.delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    /**
     * Call book's supplier
     *
     * @param phoneNumber - supplier's phone number
     */

    private void orderByPhone(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Send an order via email to supplier
     *
     * @param emailAddress - Supplier's email address
     * @param inStock      - Quantity
     * @param supplier     - Supplier's name
     * @param bookName     - Book's name
     */
    private void orderByEmail(String emailAddress, String bookName, String inStock, String supplier) {
        String subject = getString(string.email_subject) + " " + bookName + " " + getString(string.email_subject2);

        int quantityInStock = Integer.valueOf(inStock);

        //Create general email body text

        //Dear @param supplier
        String emailBody = getString((string.email_body1)) + " " + supplier + ",";

        if (quantityInStock < 1) {
            //Could you tell me when @param bookName will be available again?
            emailBody = emailBody + "\n" + getString((string.email_body4)) + " " + bookName + " " + getString((string.email_body5));

        } else {
            //I would like to order @param bookName book from your offer.
            emailBody = emailBody + "\n" + getString((string.email_body2)) + " " + bookName + " " + getString((string.email_body3));

        }

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{emailAddress});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, emailBody);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}