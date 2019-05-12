package contact.mikki.anahatha.com.contact.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import contact.mikki.anahatha.com.contact.R;
import contact.mikki.anahatha.com.contact.model.Contact;
import contact.mikki.anahatha.com.contact.ui.activity.ViewContactActivity;
import contact.mikki.anahatha.com.contact.ui.shapes.CircleTransform;

/**
 * Created by Swathi on 11/05/19.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>  {

    private List<Contact> contacts;
    private List<Contact> contactListFiltered;
    List<Contact> temp;
    Context context;

    public ContactAdapter(List<Contact> itemsData, Context context)
    {
        this.contacts = itemsData;
        this.context=context;
    }

    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, null);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        final String name=contacts.get(position).getName();

        viewHolder.txtViewTitle.setText(name);
        viewHolder.txtViewPhone.setText(contacts.get(position).getPhone());

        Picasso.with(context).load(contacts.get(position).getContactImage()).placeholder(R.drawable.ic_action_name).resize(120,120).transform(new CircleTransform()).into(viewHolder.imgViewIcon);

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener(){

    @Override
    public void onClick(View v) {


        Intent intent=new Intent(context, ViewContactActivity.class);
        intent.putExtra("contactName",name);
        intent.putExtra("contactPhone",contacts.get(position).getPhone());
        intent.putExtra("contactImageURI",contacts.get(position).getContactImage());

        context.startActivity(intent);
            }
            });
            }
    @Override
    public int getItemCount() {
            return contacts.size();
            }
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView txtViewTitle,txtViewPhone;
        public ImageView imgViewIcon;
        public LinearLayout linearLayout;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.contact_name);
            txtViewPhone = (TextView) itemLayoutView.findViewById(R.id.contact_phone);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.contact_image_view);
            linearLayout = itemLayoutView.findViewById(R.id.contact_item);
        }
    }

    //search filter

    public List<Contact> filter(String values)
    {
        List<Contact> filteredList=new ArrayList<>();

       for(int i=0;i<contacts.size();i++)
       {
           if(contacts.get(i).getName().toLowerCase().contains(values) || contacts.get(i).getPhone().toLowerCase().contains(values))
           {
               filteredList.add(contacts.get(i));
           }

       }

        return filteredList;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    }
