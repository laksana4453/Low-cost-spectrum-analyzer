package th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util.colletionArraylist;
import th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util.colletionPeakN;
import th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util.imageForAnalyze;

public class CompareDataActivity extends AppCompatActivity {
    TextView mItemSelected;
    private String[] listItems;
    private boolean[] checkedItems;
    public SQLiteDatabaseHandler db;
    private ArrayList<Integer> mUserItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private LineChart chart;
    private String[] images;
    private String[] itemsNames;
    private Boolean setData = Boolean.FALSE;
    private List<Double> firstData,secondData;
    private List<List<Double>> listOfLists = new ArrayList<List<Double>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_data);
        db = new SQLiteDatabaseHandler(this);
        List<Data> datas = db.allDatas();
        listItems = new String[datas.size()];
        for(int i=0;i < datas.size();i++){
            listItems[i] = datas.get(i).getName();
        }

        checkedItems = new boolean[listItems.length];
        showDialogList();
        //set recyclerView
        recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        chart = (LineChart) findViewById(R.id.chart1);

//        recyclerView.addOnItemTouchListener(new RecyclerListener(this,
//                recyclerView, new RecyclerListener.ClickListener() {
//            @Override
//            public void onClick(View view, final int position) {
//
//                listOfLists.add( analyzeImage(decodeImage((images[position]))));
//                Log.d("laksana","add" + position);
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));






    }

    private void createMutiGraph(List<List<Double>> listOfLists) {
        setChart();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        for(int i = 0;i<listOfLists.size();i++){

              dataSets.add(setValue(listOfLists.get(i),itemsNames[i]));
        }
        LineData data = new LineData(dataSets);
        // set data
        chart.setData(data);


    }

    public void showDialogList() {
        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(CompareDataActivity.this);
        mBuilder.setTitle("list data");
        mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {

                if(isChecked){
                    mUserItems.add(position);
                }else{
                    mUserItems.remove((Integer.valueOf(position)));
                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
//                String item = "";
                itemsNames = new String[mUserItems.size()];
                images = new String[mUserItems.size()];
                for (int i = 0; i < mUserItems.size(); i++) {
                    itemsNames[i] = listItems[mUserItems.get(i)];
                    images[i] = search_imageString(listItems[mUserItems.get(i)]);
                    listOfLists.add( analyzeImage(decodeImage((images[i]))));

                }
                MyRecyclerAdapter adapter = new MyRecyclerAdapter(itemsNames,images);
                recyclerView.setAdapter(adapter);
//                mItemSelected.setText(item);
            }
        });

        mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                    mUserItems.clear();
                    mItemSelected.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
    public String search_imageString(String name){
        List<Data> datas = db.allDatas();
        Data data = null;
        if (datas != null) {

            for (int i = 0; i < datas.size(); i++) {
                if(datas.get(i).getName().equals(name)){
                    data = datas.get(i);
                }
            }
        }
        return data.getPosition();
    }

    private Bitmap decodeImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    public List<Double> analyzeImage(Bitmap bitmap){
        chart.setVisibility(View.VISIBLE);
            Bitmap imageBitmap = bitmap;
            //covert image to string and collect imageString
            double  R ,G ,B;
            int colorPixel;
            int width = imageBitmap.getWidth();
            int height = imageBitmap.getHeight();
            double[][] imageArray= new double[width][3];

            for(int x = 0; x < width; x++ ){
                for(int y = 0; y < height;y++){
                    colorPixel = imageBitmap.getPixel(x,y);
                    R = Color.red(colorPixel);
                    G = Color.green(colorPixel);
                    B = Color.blue(colorPixel);

                    R =  ((0.07*R)+(0.52*G)+(0.23*B))/255;
                    imageArray[x][0] = R/255;
                    imageArray[x][1] = R/255;
                    imageArray[x][2] = R/255;


                }
            }

            double[][] M = new double[][] { { 0.49, 0.31, 0.20 }, { 0.17697, 0.81240, 0.01063 }, { 0.00, 0.01, 0.99 } };
            double X,Y,Z;
            List<Double> Xarr= new ArrayList<Double>();
            List<Double> Yarr= new ArrayList<Double>();
            List<Double> Zarr= new ArrayList<Double>();

            for(int i = 0;i< width;i++ ){
                X = M[0][0]*imageArray[i][2] + M[0][1]*imageArray[i][1] + M[0][2]*imageArray[i][0];
                Y = M[1][0]*imageArray[i][2] + M[1][1]*imageArray[i][1] + M[1][2]*imageArray[i][0];
                Z = M[2][0]*imageArray[i][2] + M[2][1]*imageArray[i][1] + M[2][2]*imageArray[i][0];
                Xarr.add(X*1000);
                Yarr.add(Y*10);
                Zarr.add(Z*10);
            }
            return Xarr;
    }
    private void setChart() {
        chart.setBackgroundColor(Color.WHITE);

        // disable description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // set listeners

        chart.setDrawGridBackground(false);

        // create marker to display box when values are selected
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

        // Set the marker to the chart
        mv.setChartView(chart);
        chart.setMarker(mv);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        chart.setPinchZoom(true);
        // draw points over time
        chart.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // draw legend entries as lines
        l.setForm(Legend.LegendForm.LINE);
/////////////////////////////////////////////////////////////////

    }

    public double calculate(double x) {
        List<Data> datas = db.allDatas();
        int peak = datas.get(0).getHeight();
        int n = datas.get(0).getN();
        double d = 1.0/(600*Math.pow(10,3));
        double lamda = 460/Math.pow(10,9);
        double l = Math.sqrt(Math.pow((d*peak)/(n*lamda),2)- Math.pow(peak,2));
        double sin = x/Math.sqrt(Math.pow(x,2)+Math.pow(l,2));
        return   (d*sin)/n *Math.pow(10,9);
    }
    private LineDataSet setValue(List<Double> Yaxis,String nameGraph) {
        Random rnd = new Random();
        int GraphColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        // add point in graph
        ArrayList<Entry> values = new ArrayList<>();

        for(int i = 0 ; i < Yaxis.size() ; i++){

            Float y = Float.valueOf("" + Yaxis.get(i));
            Double x = Double.valueOf(i);
//            Log.d("123","peak-x : "+peak[0]);

            values.add(new Entry( Float.valueOf((float) calculate(x)),y));

        }



//        values.add(new Entry(0, 4));

        LineDataSet set;
            // create a dataset and give it a type
            set = new LineDataSet(values, nameGraph);
            set.setDrawIcons(false);
            // draw dashed line
            set.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            set.setColor(GraphColor);
            set.setCircleColor(GraphColor);

            // line thickness and point size
            set.setLineWidth(1f);
            set.setCircleRadius(3f);

            // draw points as solid circles
            set.setDrawCircleHole(false);

            // customize legend entry
            set.setFormLineWidth(1f);
            set.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set.setFormSize(15.f);

            // text size of values
            set.setValueTextSize(9f);

            // draw selection line as dashed
            set.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            set.setDrawFilled(true);
            set.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                //Not set the graph color
                set.setFillColor(Color.WHITE);
            } else {
                set.setFillColor(GraphColor);
            }

//
//            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//            dataSets.add(set1); // add the data sets
//            dataSets.add(set2); // add the data sets
//
//            // create a data object with the data sets
//            LineData data = new LineData(dataSets);
//
//            // set data
//            chart.setData(data);

        return set;
    }
    public void showData(View view){
        createMutiGraph(listOfLists);
    }
}
