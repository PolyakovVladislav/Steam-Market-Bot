package com.example.steammarketbot;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Disposable disposable = runRx(1)
                .subscribe(
                        result -> log(String.valueOf(result)),
                        throwable -> log("error"),
                        () -> log("nothing"));

    }

    private Flowable<String> runRx(int val) {
        return Flowable.create(emitter -> {
            emitter.onComplete();
            emitter.onNext(String.valueOf(val));
            emitter.onError(new Exception());
        }, BackpressureStrategy.BUFFER);
    }

    private void log(String msg) {
        Log.d("myTag", msg);
    }

}