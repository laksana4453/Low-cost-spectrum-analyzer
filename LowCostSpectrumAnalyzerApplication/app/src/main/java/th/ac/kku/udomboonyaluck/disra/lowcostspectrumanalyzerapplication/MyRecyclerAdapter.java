package th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util.imageForAnalyze;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.Holder>{

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_list,parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.setItem(position);
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
//    private ArrayList<ExampleItem> exampleItems;
    private OnItemClickListener mListener;

    public void setItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    private String[] mDataSet;
    private String[] iDataSet;
    public MyRecyclerAdapter(String[] dataSet,String[] imageDataSet){
        mDataSet = dataSet;
        iDataSet = imageDataSet;
        SaveDataSet.getInstance().saveArraylist(Arrays.asList(mDataSet));
    }
    public class Holder extends RecyclerView.ViewHolder implements
    View.OnClickListener {
        TextView textTitle;
        ImageView spectrum;
//        TextView textDescription;

        public Holder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_name);
            spectrum = itemView.findViewById(R.id.spectrum);
            itemView.setOnClickListener(this);
        }


        public void setItem(int position){
            textTitle.setText(mDataSet[position]);
            spectrum.setImageBitmap(decodeImage(iDataSet[position]));
        }
        public String getName(int position){
            return mDataSet[position];
        }
        public String getImageString(int position){
            return iDataSet[position];
        }

        private Bitmap decodeImage(String encodedImage) {
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return decodedByte;
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onItemClick(getAdapterPosition());
            }

        }
    }

}
