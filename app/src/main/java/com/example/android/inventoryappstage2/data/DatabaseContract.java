package com.example.android.inventoryappstage2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Books app.
 */

public final class DatabaseContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryappstage2";
    public static final String PATH_BOOKS = "books";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private DatabaseContract() {
    }

    public static final class BookEntry implements BaseColumns {

        //public static final string for each row of table that we need to refer - inside and or outside this class
        public static final String TABLE_NAME = "books";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_PRICE = "price";

        public static final String COLUMN_SUPPLIER = "supplier";
        public static final String COLUMN_SUPPLIER_PHONE = "phone";
        public static final String COLUMN_SUPPLIER_EMAIL = "email";

        //Possible values for books category
        public static final int CATEGORY_UNKNOWN = 0;
        public static final int CATEGORY_ACTION_ADVENTURE = 1;
        public static final int CATEGORY_DRAMA = 2;
        public static final int CATEGORY_HORROR = 3;
        public static final int CATEGORY_CHILDREN = 4;
        public static final int CATEGORY_MYSTERY = 5;
        public static final int CATEGORY_ROMANCE = 6;
        public static final int CATEGORY_SATIRE = 7;
        public static final int CATEGORY_SCIFI = 8;
        public static final int CATEGORY_TRAVEL = 9;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of book.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single game.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * Returns whether or not the given category is
         * {@link #CATEGORY_ACTION_ADVENTURE},
         * {@link #CATEGORY_DRAMA},
         * {@link #CATEGORY_HORROR},
         * {@link #CATEGORY_CHILDREN},
         * {@link #CATEGORY_MYSTERY},
         * {@link #CATEGORY_ROMANCE},
         * {@link #CATEGORY_SATIRE},
         * {@link #CATEGORY_SCIFI},
         * {@link #CATEGORY_TRAVEL},
         * or {@link #CATEGORY_UNKNOWN}.
         */
        public static boolean isValidCategory(int category) {
            return category == CATEGORY_UNKNOWN || category == CATEGORY_ACTION_ADVENTURE || category == CATEGORY_DRAMA || category == CATEGORY_HORROR
                    || category == CATEGORY_CHILDREN || category == CATEGORY_MYSTERY || category == CATEGORY_ROMANCE
                    || category == CATEGORY_SATIRE || category == CATEGORY_SCIFI || category == CATEGORY_TRAVEL
                    ;
        }
    }
}