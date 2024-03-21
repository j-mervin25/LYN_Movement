package com.example.lyn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadUtility {

    private Activity activity;
    private ProgressDialog dialog;
    private String serverURL = "https://trial.gssmp.org/MobileUpload.php";
    private String serverUploadDirectoryPath = "https://trial.gssmp.org/uploads/";
    private OkHttpClient client;

    public UploadUtility(Activity activity) {
        this.activity = activity;
        this.client = new OkHttpClient();
    }

    public void uploadFile(String sourceFilePath, String uploadedFileName, String arguments) {
        uploadFile(new File(sourceFilePath), uploadedFileName, arguments);
    }


    public void uploadFile(final File sourceFile, final String uploadedFileName, final String arguments) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String mimeType = getMimeType(sourceFile);
                if (mimeType == null) {
                    Log.e("File error", "Not able to get mime type");
                    return;
                }

                final String fileName = (uploadedFileName == null) ? sourceFile.getName() : uploadedFileName;

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toggleProgressDialog(true);
                    }
                });

                try {
                    RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("uploaded_file", fileName, RequestBody.create(MediaType.parse(mimeType), sourceFile))
                            .build();

                    Log.e("argument error","" + serverURL+"?"+arguments);
                    Request request = new Request.Builder().url(serverURL+"?"+arguments).post(requestBody).build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            Log.e("File upload", "Failed");
                            showToast("File uploading failed");
                            toggleProgressDialog(false);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseBody = response.body().string();
                            Log.d("File upload", "Server response: " + responseBody);

                            if (response.isSuccessful()) {
                                Log.d("File upload", "Success, path: " + serverUploadDirectoryPath + fileName);
                                showToast("File" + serverUploadDirectoryPath + fileName);
                            } else {
                                Log.e("File upload", "Failed");
                                showToast("File uploading failed");
                            }
                            toggleProgressDialog(false);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("File upload", "Failed");
                    showToast("File uploading failed");
                    toggleProgressDialog(false);
                }
            }
        }).start();
    }

    private String getMimeType(File file) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getPath());
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void toggleProgressDialog(final boolean show) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    dialog = ProgressDialog.show(activity, "", "Uploading file...", true);
                } else {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }
        });
    }
}