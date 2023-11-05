package com.example.marketplaceproject;

import android.provider.BaseColumns;

public class ListingContract {
    public static final class ListingEntry {
        public static final String TABLE_NAME2 ="Listing";
        public static final String ID_COL = "_id";
        public static final String UID2 = "uid";

        public static final String TITLE_COL = "title";
        public static final String PRICE_COL = "price";
        public static final String CATEGORY_COL = "category";
        public static final String CONDITION_COL = "condition";
        public static final String DESCRIPTION_COL = "description";
        public static final String POSTAL_COL = "postal_code";
        public static final String DATE_COL = "date";
        public static final String IMAGE_COL = "image";
        public static final String VIDEO_COL = "video";
    }
}
