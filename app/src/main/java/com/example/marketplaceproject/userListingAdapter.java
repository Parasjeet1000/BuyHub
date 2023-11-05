package com.example.marketplaceproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class userListingAdapter extends RecyclerView.Adapter<userListingAdapter.MyViewHolder> {

    Context mcontext;
    Cursor mcursor;

    public userListingAdapter(Context mcontext, Cursor mcursor) {
        this.mcontext = mcontext;
        this.mcursor = mcursor;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titleOutput, priceOutput, dateOutput;
        ImageView pictureOutput;
        Button update, delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleOutput = itemView.findViewById(R.id.adtitle);
            priceOutput = itemView.findViewById(R.id.adprice);
            dateOutput = itemView.findViewById(R.id.addate);
            pictureOutput = itemView.findViewById(R.id.adpic);
            update = itemView.findViewById(R.id.update);
            delete = itemView.findViewById(R.id.delete);


        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.mylistings_card,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(!mcursor.moveToPosition(position)){
            return;
        }
        @SuppressLint("Range") String Title = mcursor.getString(mcursor.getColumnIndex(ListingContract.ListingEntry.TITLE_COL));
        @SuppressLint("Range") String Price = mcursor.getString(mcursor.getColumnIndex(ListingContract.ListingEntry.PRICE_COL));
        @SuppressLint("Range") String Date = mcursor.getString(mcursor.getColumnIndex(ListingContract.ListingEntry.DATE_COL));
        @SuppressLint("Range") byte[] imageBytes = mcursor.getBlob(mcursor.getColumnIndex(ListingContract.ListingEntry.IMAGE_COL));

        holder.titleOutput.setText(Title);
        holder.priceOutput.setText("$ "+Price);
        holder.dateOutput.setText(Date);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        holder.pictureOutput.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {return mcursor.getCount();}

    public void swapCursor(Cursor newCursor) {
        if (mcursor != null) {
            mcursor.close();
        }

        mcursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

}
