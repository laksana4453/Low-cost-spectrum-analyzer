package th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.handler.OnBoxChangedListener;
import th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.model.ScalableBox;
import th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util.colletionPeakN;
import th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util.imageForAnalyze;
import th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util.sendImage;

public class CroppingAutoActivity extends AppCompatActivity {
    private EditableImage image;
    ImageView mImage;
    int value_x1 = 0,value_y1 = 0,value_x2 = 0,value_y2 = 0;
    private  SQLiteDatabaseHandler db;
    // find data object of default
    public Data find_Position_of_Crop(){
        String name  = "Default";
        List<Data> datas = db.allDatas();
        Data dataDefault = null;
        if (datas != null) {

            for (int i = 0; i < datas.size(); i++) {
                if(datas.get(i).getName().equals(name)){
                    dataDefault = datas.get(i);
                }
            }
//                itemsNames[i] = players.get(i).getName();
        }
        return dataDefault;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropping_auto);
        final EditPhotoView imageView = (EditPhotoView) findViewById(R.id.editable_image);
        db = new SQLiteDatabaseHandler(this);
        Data dataDefault = find_Position_of_Crop();

            value_x1 = dataDefault.getX1();
            value_y1 = dataDefault.getY1();
            value_x2 = dataDefault.getX2();
            value_y2 = dataDefault.getY2();


        Bitmap bm = sendImage.getInstance().getBitmap();
        image = new EditableImage(bm);
        ScalableBox box1 = new ScalableBox(value_x1,value_y1,value_x2,value_y2);
        List<ScalableBox> boxes = new ArrayList<>();
        boxes.add(box1);
        image.setBoxes(boxes);
        imageView.initView(this, image);


        Button button = (Button)findViewById(R.id.rotate_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.rotateImageView();
            }
        });
    }
    public void createCropImage(View view){
        int x = value_x1;
        int y = value_y1;
        int x2 = value_x2;
        int y2 = value_y2;
        int width = x2 - x;
        int height = y2 - y;
        Matrix matrix = new Matrix();
        matrix.postScale(0.5f, 0.5f);
        Bitmap croppedBitmap = Bitmap.createBitmap(image.getOriginalImage(), x, y, width, height, matrix, true);
        imageForAnalyze.getInstance().setBitmap(croppedBitmap);
        backToAnalyzeGraph();

    }
    public void backToAnalyzeGraph(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }
}
