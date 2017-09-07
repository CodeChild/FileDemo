package com.example.defaultaccount.filedemo.model;


import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by DefaultAccount on 2017/8/29.
 */

public class FileClient {
    private Consumer<String> mConsumer;
    private static RxAppCompatActivity mRxActivity;
    private static class FileClientHolder {
        private static final FileClient INSTANCE = new FileClient();
    }

    private FileClient() {
    }

    public static final FileClient getInstance(RxAppCompatActivity rxActivity) {
        mRxActivity=rxActivity;
        return FileClientHolder.INSTANCE;
    }

    public void writeFile(String dirName, String fileName, String content) {
        Observable.just(fileName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mRxActivity.bindToLifecycle())
                .subscribe(s -> {
                    if (FileUtils.isExternalStorageWritable()) {
                        try {
                            File file = FileUtils.getAlbumStorageDir(dirName);
                            String pathName = file.getPath();
                            String filePath = pathName + "/" + fileName;
                            FileOutputStream outputStream = new FileOutputStream(filePath);
                            outputStream.write(content.getBytes());
                            outputStream.close();
                            this.mConsumer.accept(s);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, throwable -> {
                });
    }
    public void saveFile(String pathName, String content) {
        Observable.just(pathName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mRxActivity.bindToLifecycle())
                .subscribe(s -> {
                    if (FileUtils.isExternalStorageWritable()) {
                        try {
                            File file = new File(pathName);
                            FileOutputStream outputStream = new FileOutputStream(pathName);
                            outputStream.write(content.getBytes());
                            outputStream.close();
                            Toast.makeText(mRxActivity,"文件已保存",Toast.LENGTH_SHORT).show();
//                            this.mConsumer.accept(s);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, throwable -> {
                });
    }
    public void readFile(String pathName) {
        StringBuffer content = new StringBuffer();
        Observable.just(pathName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mRxActivity.bindToLifecycle())
                .subscribe(s -> {
                    if (FileUtils.isExternalStorageReadable()) {
                        try {
                            File file = new File(s);
                            FileInputStream inputStream = new FileInputStream(file);
                            char[] temp = new char[1];
                            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                            while(inputStreamReader.read(temp) != -1){
                                content.append(new String(temp));
                                temp = new char[1];
                            }
                            inputStream.close();
                            inputStreamReader.close();
                            this.mConsumer.accept(content.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, throwable -> {
                });
    }
    public void deleteFile(String pathName){
        Observable.just(pathName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(mRxActivity.bindToLifecycle())
                .subscribe(s -> {
                    if (FileUtils.isExternalStorageWritable()) {
                        File file = new File(pathName);
                        if(file.exists() && file.isFile()){
                            file.delete();
                        }
//                            this.mConsumer.accept(s);
                    }
                }, throwable -> {
                });
    }
    public File[] getFileList(String dirName) {
        File[] files = new File[0];
        if (FileUtils.isExternalStorageReadable()) {
            File file = FileUtils.getAlbumStorageDir(dirName);
            files = file.listFiles();
        }
        return files;
    }

    public FileClient rigistConsumer(Consumer<String> consumer) {
        this.mConsumer = consumer;
        return this;
    }
}
