package com.example.yesid.json_firebase;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yesid on 23/04/2016.
 */
public class CustomAdapter extends BaseAdapter implements OnClickListener{

    private final Context context;
    private List<DataEntry> datos;
    private List<String> ids;
    private Firebase rootRef;

    public CustomAdapter(Context context, List<DataEntry> datos, List<String> ids, Firebase rootRef) {
        this.context=context;
        this.datos=datos;
        this.ids=ids;
        this.rootRef=rootRef;
    }

    @Override
    public int getCount() {
        return datos.size();
    }

    @Override
    public Object getItem(int position) {
        return datos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DataEntry entry = datos.get(position);
        final String key = ids.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_row, null);
        }

        TextView tvNombre = (TextView) convertView.findViewById(R.id.tvField1);
        TextView tvApellido = (TextView) convertView.findViewById(R.id.tvField2);

        tvNombre.setText(String.valueOf(entry.getFirstName()));
        tvApellido.setText(String.valueOf(entry.getLastName()));

        Button btnBorrar = (Button) convertView.findViewById(R.id.btnRemove);
        btnBorrar.setFocusable(false);
        btnBorrar.setFocusableInTouchMode(false);
        btnBorrar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rootRef.child(key).removeValue();
            }
        });

        Button btnEditar = (Button) convertView.findViewById(R.id.btnEdit);
        btnEditar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog editUser = new Dialog(context);
                editUser.setContentView(R.layout.edit_dialog);
                editUser.show();

                final EditText name = (EditText) editUser.findViewById(R.id.editName);
                name.setText(entry.getFirstName());
                final EditText lastName = (EditText) editUser.findViewById(R.id.editLastName);
                lastName.setText(entry.getLastName());

                final Spinner genders = (Spinner) editUser.findViewById(R.id.editGender);

                List<String> gendersArray = new ArrayList<String>();
                gendersArray.add("male");
                gendersArray.add("female");

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, gendersArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                genders.setAdapter(adapter);

                genders.setSelection(adapter.getPosition(entry.getGender()));

                Button cancelBtn = (Button) editUser.findViewById(R.id.cancelBtn);
                Button saveBtn = (Button) editUser.findViewById(R.id.saveUserBtn);

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editUser.dismiss();
                    }
                });

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newName = name.getText().toString();
                        String newLastName = lastName.getText().toString();
                        String newGender = genders.getSelectedItem().toString();

                        Firebase User = rootRef.child(key);
                        Map<String, Object> user = new HashMap<String, Object>();
                        user.put("firstName",newName);
                        user.put("gender",newGender);
                        user.put("lastName",newLastName);
                        User.updateChildren(user);
                        editUser.dismiss();
                    }
                });
            }
        });

        convertView.setTag(entry);

        return convertView;

    }

    @Override
    public void onClick(View v) {

    }
}
