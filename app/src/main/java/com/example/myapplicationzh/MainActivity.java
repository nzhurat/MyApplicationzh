package com.example.myapplicationzh;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText editText1;
    private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText1 = findViewById(R.id.input1);
        output = findViewById(R.id.output);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String studentNumber = editText1.getText().toString();
                Thread thread = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            Socket socket = new Socket("se2-submission.aau.at", 20080);
                            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                            outputStream.writeBytes(studentNumber + "\n");
                            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            final String result = reader.readLine();
                            socket.close();
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    output.setText(result);
                                }
                            });
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Toast.makeText(MainActivity.this, "Помилка підключення до сервера", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                thread.start();
            }
        });
    }
}
