package th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util.imageForAnalyze;

public class ShowDataActivity extends AppCompatActivity {
    final Context context = this;
    public SQLiteDatabaseHandler db;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        db = new SQLiteDatabaseHandler(this);

        recyclerView = findViewById(R.id.recyclerview);
//ตั้งค่า Layout
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
//ตั้งค่า Adapter
        List<Data> datas = db.allDatas();
        Log.d("item name",datas+"");
        if (datas != null) {
            String[] itemsNames = new String[datas.size()];
            String[] images = new String[datas.size()];

            for (int i = 0; i < datas.size(); i++) {
                itemsNames[i] = datas.get(i).getName();
                images[i] = datas.get(i).getPosition();
                Log.d("imageStr",images[i]+"");
            }
            MyRecyclerAdapter adapter = new MyRecyclerAdapter(itemsNames,images);
            recyclerView.setAdapter(adapter);
            recyclerView.addOnItemTouchListener(new RecyclerListener(this,
                    recyclerView, new RecyclerListener.ClickListener() {
                @Override
                public void onClick(View view, final int position) {
                    Intent intent =  new Intent(context,AbsorbActivity.class);
                    intent.putExtra("position_recyclerView",position);
                    startActivity(intent);
                }

                @Override
                public void onLongClick(View view, int position) {
                    dialogDelete(position);
                }
            }));

        }

    }


    public Data searchId(int position){
        List<String> data_recyclerView =  SaveDataSet.getInstance().get_mDataSet();
        String name = data_recyclerView.get(position);
        List<Data> datas = db.allDatas();
        Data show = null;
        if (datas != null) {

            for (int i = 0; i < datas.size(); i++) {
                if(datas.get(i).getName().equals(name)){
                    show = datas.get(i);
                }
            }
//                itemsNames[i] = players.get(i).getName();
        }
        return show;
    }
    private void dialogDelete(final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_delete_data);
        Button BtYes = (Button) dialog.findViewById(R.id.btYes);
        Button BtNo = (Button) dialog.findViewById(R.id.btNo);
        TextView textView = (TextView) dialog.findViewById(R.id.scriptionDialog);
        textView.setText("Do you want to delete this data?");
        dialog.setTitle("Delete data");
        BtYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteOne(searchId(position));
                updateRecyclerView();
                dialog.dismiss();
            }
        });
        BtNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public void addData(int id ,String name,String imageString,int peak,int n , int x1,int y1 ,int x2, int y2){
        Data data = new Data(id, name, imageString, peak, n, x1, y1, x2, y2);
        db.addData(data);
    }
    public void deleteAll(View view){
        db.removeAllDatas();
        updateRecyclerView();
    }
    public void updateRecyclerView() {
        List<Data> datas = db.allDatas();
        if (datas != null) {
            String[] itemsNames = new String[datas.size()];
            String[] images = new String[datas.size()];

            for (int i = 0; i < datas.size(); i++) {
                itemsNames[i] = datas.get(i).getName();
                imageForAnalyze.getInstance().setImageStringAnalyze(datas.get(i).getPosition());
                images[i] = imageForAnalyze.getInstance().getImageStringAnalyze();
            }
            MyRecyclerAdapter adapter = new MyRecyclerAdapter(itemsNames, images);
            recyclerView.setAdapter(adapter);
        }
    }
    public void openCompareActivity(View view){
        Intent intent = new Intent(this,CompareDataActivity.class);
        startActivity(intent);
        finish();


    }
}
