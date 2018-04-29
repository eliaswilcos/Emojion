package edu.illinois.cs.cs125.emojion;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.graphics.*;
import android.widget.*;
import android.provider.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;

public class MainActivity extends AppCompatActivity {

    private int a;

    private FaceServiceClient faceServiceClient = new FaceServiceRestClient( "https://westcentralus.api.cognitive.microsoft.com/face/v1.0\n" +
            "\n", "92c9d0e5e2db49469e15a2180609eccf");

    private final int PICK_IMAGE = 1;
    private ProgressDialog detectionProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallIntent = new Intent(Intent.ACTION_GET_CONTENT);
                gallIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(gallIntent, "Select Picture"), PICK_IMAGE);
            }
        });

        detectionProgressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                imageView.setImageBitmap(bitmap);

                // This is the new addition.
                detectAndFrame(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Bitmap drawFaceRectanglesOnBitmap(Bitmap originalBitmap, Face[] faces) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        int stokeWidth = 2;
        paint.setStrokeWidth(stokeWidth);
        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);
            }
        }
        return bitmap;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    private void checkEmotion(Face[] faces) {
        String face = faces[0].toString();
        JsonParser parser = new JsonParser();
        JsonObject result = parser.parse(face).getAsJsonObject();
        JsonArray attributes = result.getAsJsonArray("faceAttributes");
        JsonArray emotions = attributes.get(0).getAsJsonArray();
        JsonObject anger = emotions.get(0).getAsJsonObject();
        double anger1 = anger.get("anger").getAsDouble();
        JsonObject contempt = emotions.get(1).getAsJsonObject();
        double contempt1 = contempt.get("contempt").getAsDouble();
        JsonObject disgust = emotions.get(2).getAsJsonObject();
        double disgust1 = disgust.get("disgust").getAsDouble();
        JsonObject fear = emotions.get(3).getAsJsonObject();
        double fear1 = fear.get("fear").getAsDouble();
        JsonObject happiness = emotions.get(4).getAsJsonObject();
        double happiness1 = happiness.get("happiness").getAsDouble();
        JsonObject neutral = emotions.get(5).getAsJsonObject();
        double neutral1 = neutral.get("neutral").getAsDouble();
        JsonObject sadness = emotions.get(6).getAsJsonObject();
        double sadness1 = sadness.get("sadness").getAsDouble();
        JsonObject surprise = emotions.get(7).getAsJsonObject();
        double surprise1 = surprise.get("surprise").getAsDouble();
        double [] emotionArray = new double[]{anger1, contempt1, disgust1, fear1, happiness1, neutral1, sadness1, surprise1};
        double largest = 0;
        int emotion = 0;
        for (int i = 0; i < 8; i++) {
            if (emotionArray[i] > largest) {
                largest = emotionArray[i];
                emotion = i;
            }
        }
        if (emotion == 0) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView1);
            Bitmap bitmap = getBitmapFromURL("https://cdn.shopify.com/s/files/1/1061/1924/files/Very_Angry_Emoji.png?9898922749706957214");
            imageView.setImageBitmap(bitmap);
            return;
        }
        if (emotion == 1) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView1);
            Bitmap bitmap = getBitmapFromURL("https://i.pinimg.com/originals/b0/5d/05/b05d05aae302296c24ccc845b36aedce.jpg");
            imageView.setImageBitmap(bitmap);
            return;
        }
        if (emotion == 2) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView1);
            Bitmap bitmap = getBitmapFromURL("https://emojipedia-us.s3.amazonaws.com/thumbs/120/apple/129/nauseated-face_1f922.png");
            imageView.setImageBitmap(bitmap);
            return;
        }
        if (emotion == 3) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView1);
            Bitmap bitmap = getBitmapFromURL("http://s3.amazonaws.com/pix.iemoji.com/images/emoji/apple/ios-11/256/face-screaming-in-fear.png");
            imageView.setImageBitmap(bitmap);
            return;
        }
        if (emotion == 4) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView1);
            Bitmap bitmap = getBitmapFromURL("https://cdn.shopify.com/s/files/1/1061/1924/products/Happy_Emoji_Icon_5c9b7b25-b215-4457-922d-fef519a08b06_large.png?v=1513251037");
            imageView.setImageBitmap(bitmap);
            return;
        }
        if (emotion == 5) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView1);
            Bitmap bitmap = getBitmapFromURL("https://cdn.shopify.com/s/files/1/1061/1924/files/Neutral_Face_Emoji.png?9898922749706957214");
            imageView.setImageBitmap(bitmap);
            return;
        }
        if (emotion == 6) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView1);
            Bitmap bitmap = getBitmapFromURL("http://www.freepngimg.com/thumb/sad_emoji/36857-1-sad-emoji-thumb.png");
            imageView.setImageBitmap(bitmap);
            return;
        }
        if (emotion == 7) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView1);
            Bitmap bitmap = getBitmapFromURL("https://cdn.shopify.com/s/files/1/1061/1924/products/Surprised_Face_Emoji_7113e110-82a7-493a-9bb1-7bdca77a661a_large.png?v=1480481057");
            imageView.setImageBitmap(bitmap);
            return;
        }
        return;
    }

    // Detect faces by uploading face images
    // Frame faces after detection

    private void detectAndFrame(final Bitmap imageBitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());
        AsyncTask<InputStream, String, Face[]> detectTask =
                new AsyncTask<InputStream, String, Face[]>() {
                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
                            publishProgress("Detecting...");
                            Face[] result = faceServiceClient.detect(params[0], true, false,
                                    new FaceServiceClient.FaceAttributeType[] {FaceServiceClient.FaceAttributeType.Emotion});
                            if (result == null)
                            {
                                publishProgress("Detection Finished. Nothing detected");
                                return null;
                            }
                            publishProgress(
                                    String.format("Detection Finished. %d face(s) detected",
                                            result.length));
                            return result;
                        } catch (Exception e) {
                            publishProgress("Detection failed");
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        detectionProgressDialog.show();
                    }

                    @Override
                    protected void onProgressUpdate(String... progress) {
                        detectionProgressDialog.setMessage(progress[0]);
                    }

                    @Override
                    protected void onPostExecute(Face[] result) {
                        detectionProgressDialog.dismiss();
                        if (result == null) {
                            TextView text = findViewById(R.id.textView);
                            text.setText("result is null");
                            return;
                        }
                        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                        checkEmotion(result);
                        imageBitmap.recycle();
                    }
                };
        detectTask.execute(inputStream);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
