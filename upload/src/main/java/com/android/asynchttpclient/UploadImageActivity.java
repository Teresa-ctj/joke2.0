package com.android.asynchttpclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.asynchttpclient.uploadimage.FileUploadAsyncTask;
import com.android.asynchttpclient.uploadimage.ImageTools;
import com.android.asynchttpclient.uploadimage.ImageUri;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 上传图片步骤:
 * 1.授予权限
 * 2.选择相片 (拍照/相册)
 * 3.上传 (图片/书籍信息)
 * 4.更新 ListView
 */
public class UploadImageActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int TAKE_PHOTO = 0;
    private static final int CHOOSE_PHOTO = 1;

    private static final int SCALE = 5;// 照片缩小比例
    private String uploadFile;

    @InjectView(R.id.imageView)
    ImageView imageView;
    @InjectView(R.id.txtIsbn)
    EditText txtIsbn;
    @InjectView(R.id.txtTitle)
    EditText txtTitle;
    @InjectView(R.id.txtAuthor)
    EditText txtAuthor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        ButterKnife.inject(this);
    }

    @OnClick(R.id.btnChoosePhoto)
    public void choosePhotoClick() {
        // 显示相片操作(0 拍照 / 1 选择相片)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("图片来源:");
        builder.setNegativeButton("取消", null);
        builder.setItems(new String[]{"拍照", "相册"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case TAKE_PHOTO:
                                Intent openCameraIntent = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                Uri imageUri = Uri.fromFile(new File(Environment
                                        .getExternalStorageDirectory(), "image.jpg"));
                                // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(openCameraIntent, TAKE_PHOTO);
                                break;

                            case CHOOSE_PHOTO:
                                Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                openAlbumIntent.setType("image/*");
                                startActivityForResult(openAlbumIntent, CHOOSE_PHOTO);
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    @OnClick(R.id.btnUpload)
    public void uploadImageClick() {
        // 上传
        if (TextUtils.isEmpty(uploadFile)) {
            Toast.makeText(this, "请先选择书籍封面", Toast.LENGTH_SHORT).show();
        } else {
            File file = new File(uploadFile);
            new FileUploadAsyncTask(this).execute(file);

            String param = "?isbn=" + txtIsbn.getText().toString() + "&title=" + txtTitle.getText().toString() + "&author=" + txtAuthor.getText().toString() + "&image=/images" + uploadFile.substring(uploadFile.lastIndexOf("/"), uploadFile.length());
            AndroidClient.post("BookServlet" + param, null, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO:
                    // 将保存在本地的图片取出并缩小后显示在界面上
                    Bitmap bitmap = BitmapFactory.decodeFile(Environment
                            .getExternalStorageDirectory() + "/image.jpg");
                    Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth()
                            / SCALE, bitmap.getHeight() / SCALE);
                    // 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                    bitmap.recycle();

                    // 将处理过的图片显示在界面上，并保存到本地
                    imageView.setImageBitmap(newBitmap);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
                    String filename = sdf.format(new Date());

                    ImageTools.savePhotoToSDCard(newBitmap, Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                            .getAbsolutePath()
                            + "/Camera", "IMG_" + filename);
                    uploadFile = Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                            .getAbsolutePath()
                            + "/Camera/" + "IMG_" + filename + ".png";
                    break;

                case CHOOSE_PHOTO:
                    ContentResolver resolver = getContentResolver();
                    // 照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {
                        // 使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver,
                                originalUri);
                        if (photo != null) {
                            // 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth()
                                    / SCALE, photo.getHeight() / SCALE);
                            // 释放原始图片占用的内存，防止out of memory异常发生
                            photo.recycle();

                            imageView.setImageBitmap(smallBitmap);
                            uploadFile = ImageUri.getImageAbsolutePath(this, originalUri);
                        }
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(this, "读取文件,出错啦", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            Log.v(TAG, "............." + uploadFile);
        }
    }

    // -----------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload_image, menu);
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
