package com.example.arduino_control.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.arduino_control.Preset;
import com.example.arduino_control.R;

import java.util.List;

public class ListOfPresetsAdapter extends ArrayAdapter<Preset> {
    Context context;
    List<Preset> listOfPresets;

    public ListOfPresetsAdapter(@NonNull Context context, int resource, List<Preset> listOfPresets) {
        super(context, resource);
        this.context = context;
        this.listOfPresets = listOfPresets;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        ListOfPresetsAdapter.ViewHolder holder;
        if (convertView == null){
            v = LayoutInflater.from(context).inflate(R.layout.item_preset, parent);
            holder = new ViewHolder();

            holder.name = v.findViewById(R.id.nameOfPreset);

            v.setTag(holder);
        } else {
            holder = (ListOfPresetsAdapter.ViewHolder) v.getTag();
        }
        holder.name.setText(listOfPresets.get(position).getName());

        return super.getView(position, convertView, parent);
    }

    private static class ViewHolder{
        TextView name;
    }
}
