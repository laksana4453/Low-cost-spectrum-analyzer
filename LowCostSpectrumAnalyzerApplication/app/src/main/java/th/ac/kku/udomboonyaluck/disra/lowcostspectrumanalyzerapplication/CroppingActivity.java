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
import th.ac.kku.udomboonyaluck.disra.lowcostspectrumanalyzerapplication.util.sendImage;

public class CroppingActivity extends AppCompatActivity {
    private EditableImage image;
    int text_x,text_y,text_width,text_height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropping);
        final EditPhotoView imageView = (EditPhotoView) findViewById(R.id.editable_image);

        Bitmap bm = sendImage.getInstance().getBitmap();
        image = new EditableImage(bm);
        ScalableBox box1 = new ScalableBox(25,180,640,880);
        List<ScalableBox> boxes = new ArrayList<>();
        boxes.add(box1);
        image.setBoxes(boxes);
        imageView.initView(this, image);

        imageView.setOnBoxChangedListener(new OnBoxChangedListener() {
            @Override
            public void onChanged(int x1, int y1, int x2, int y2) {
                text_x = x1;
                text_y = y1;
                text_width = x2;
                text_height = y2;
            }
        });

        Button button = (Button)findViewById(R.id.rotate_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.rotateImageView();
            }
        });
    }
    public void createCropImage(View view){
        int x = text_x;
        int y = text_y;
        int x2 = text_width;
        int y2 = text_height;
        int width = text_width - x;
        int height = text_height-y;
        Matrix matrix = new Matrix();
        matrix.postScale(0.5f, 0.5f);
        Bitmap croppedBitmap = Bitmap.createBitmap(image.getOriginalImage(), x, y, width, height, matrix, true);
        sendImage.getInstance().setBitmap(croppedBitmap);
        backToSettingPage(x,y,x2,y2);

    }
    public void backToSettingPage(int x1,int y1,int x2,int y2){
        Intent intent = new Intent(this, Configuration.class);
        colletionPeakN.getInstance().collectX1(x1);
        colletionPeakN.getInstance().collectY1(y1);
        colletionPeakN.getInstance().collectX2(x2);
        colletionPeakN.getInstance().collectY2(y2);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),Configuration.class);
        startActivity(intent);
        finish();
    }
}
