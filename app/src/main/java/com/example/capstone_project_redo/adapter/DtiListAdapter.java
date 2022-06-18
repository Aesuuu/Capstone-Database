package com.example.capstone_project_redo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone_project_redo.R;
import com.example.capstone_project_redo.model.DtiListModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;


public class DtiListAdapter extends FirebaseRecyclerAdapter<DtiListModel, DtiListAdapter.viewHolder> {

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public DtiListAdapter(@NonNull FirebaseRecyclerOptions<DtiListModel> options) {
        super(options);
        this.notifyDataSetChanged();
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull DtiListModel model) {
        holder.category.setText(model.getCategory());
        holder.product.setText(model.getProduct());
        holder.brand.setText(model.getBrand());
        holder.unit.setText(model.getUnit());
        holder.srp.setText(model.getSrp());

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_dti_items, parent, false);
        return new viewHolder(view);
    }


    public class viewHolder extends RecyclerView.ViewHolder {

        TextView category, product, brand, unit, srp;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            category = (TextView)itemView.findViewById(R.id.tv_dtiCategory);
            product = (TextView)itemView.findViewById(R.id.tv_dtiProduct);
            brand = (TextView)itemView.findViewById(R.id.tv_dtiBrand);
            unit = (TextView)itemView.findViewById(R.id.tv_dtiUnit);
            srp = (TextView)itemView.findViewById(R.id.tv_dtiSrp);


        }
    }
}
