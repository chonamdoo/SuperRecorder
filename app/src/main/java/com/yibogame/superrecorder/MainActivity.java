package com.yibogame.superrecorder;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;

import cn.gavinliu.android.ffmpeg.box.FFmpegBox;
import cn.gavinliu.android.ffmpeg.box.commands.BaseCommand;
import cn.gavinliu.android.ffmpeg.box.commands.Command;
import cn.gavinliu.android.ffmpeg.box.commands.CutGifCommand;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
//        RxPermissions rxPermissions = new RxPermissions(this);
//        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE);

        findViewById(R.id.btn).setOnClickListener(v -> {
            Log.d(TAG, "onClick!");
            String base = Environment.getExternalStorageDirectory().getPath();
//            base = "/storage/emulated/0";
            Log.d(TAG, "base=" + base);

            File file = new File(base + "/input.mp4");
            try {
                Log.d(TAG, file.exists() + "," + file.canRead() + "," + file.canWrite() + "," + file.getName() + ",size=" + getFileSize(file));
            } catch (Exception e) {
                e.printStackTrace();
            }


            String[] commands = new String[9];
            commands[0] = "ffmpeg";
            commands[1] = "-i";
            commands[2] = base + "/input.mp4";
            commands[3] = "-i";
            commands[4] = base + "/input.mp3";
            commands[5] = "-strict";
            commands[6] = "-2";
            commands[7] = "-y";
            commands[8] = base + "/merge.mp4";

//            int ret = FFmpegNativeBridge.runCommand(new String[]{"ffmpeg",
//                    "-i", "/storage/emulated/0/AzRecorderFree/2017_10_13_14_57_59.mp4",
//                    "-y",
//                    "-c:v", "libx264",
//                    "-c:a", "aac",
//                    "-vf", "scale=-2:640",
//                    "-preset", "ultrafast",
//                    "-b:v", "450k",
//                    "-b:a", "96k",
//                    "/storage/emulated/0/Download/a.mp4"});
//            Log.d(TAG, "ret = " + ret);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    FFmpegNativeBridge.runCommand(commands, new FFmpegNativeBridge.KitInterface() {
//                        @Override
//                        public void onStart() {
//                            Log.d(TAG, "onStart!");
//                        }
//
//                        @Override
//                        public void onProgress(int i) {
//                            Log.d(TAG, "onProgress=" + i);
//                        }
//
//                        @Override
//                        public void onEnd(int i) {
//                            Log.d(TAG, "onEnd!");
//                        }
//                    });
//                }
//            }).start();

            Command command = new CutGifCommand.Builder()
                    .setVideoFile(base + "/input.mp4")
                    .setGifFile(base + "/bbb.gif")
                    .setStartTime(1)
                    .setDuration(10)
                    .setWidth(480)
                    .setHeight(270)
                    .build();
//
////            new FFmpegBox().execute(command);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    int ret = FFmpegBox.getInstance().execute(command);
//                    Log.d(TAG, "ret=" + ret);
//                }
//            }).start();
            File file1 = new File(base + "/input.mp3");
            Log.d("Tag", "canWrite=" + file1.canWrite()+",path="+file1.getAbsolutePath());
            String a = String.format("-i " + base + "/jmgc.mp3 -acodec mp3lame -i " + base + "/rwlznsb.mp3 -acodec mp3lame -filter_complex amix=inputs=2:duration=first:dropout_transition=2 -f mp3 " + file1.getAbsolutePath());
            BaseCommand baseCommand = new BaseCommand(a) {
                @Override
                public String getCommand() {
                    return super.getCommand();
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    int ret = FFmpegBox.getInstance().execute(baseCommand);
                    Log.d(TAG, "ret=" + ret);
                }
            }).start();

        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
//            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }
}
