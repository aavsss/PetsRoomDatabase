package com.example.android.pets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetEntry;

import java.util.List;

public class PetAdapter extends ArrayAdapter<PetEntry> {

    /**
     * Constructs a new {@link PetAdapter}.
     *
     * @param context The context
     */
    public PetAdapter(Context context, List<PetEntry> petEntries){
        super(context, 0, petEntries);
    }


    /**
     * Get a View that displays the data at the specified position in the data set.
     *
     * @param position    Existing view, returned earlier by newView() method
     * @param convertView app context
     * @param parent  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        PetEntry pet = getItem(position);

        //Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView)listItemView.findViewById(R.id.name);
        TextView summaryTextVIew = (TextView)listItemView.findViewById(R.id.summary);

        //Read the pet attributes from the current pet
        String petName = pet.getName();
        String petBreed = pet.getBreed();

        // If the pet breed is empty string or null, then use some default text
        // that says "Unknown breed", so the TextView isn't blank.
        if (TextUtils.isEmpty(petBreed)){
            petBreed = getContext().getResources().getString(R.string.unknown_breed);
        }

        //Update the TextViews with the attributes for the current pet
        nameTextView.setText(petName);
        summaryTextVIew.setText(petBreed);

        return listItemView;
    }
}
