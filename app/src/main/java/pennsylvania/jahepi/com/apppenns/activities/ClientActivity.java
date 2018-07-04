package pennsylvania.jahepi.com.apppenns.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.adapters.AddressAdapter;
import pennsylvania.jahepi.com.apppenns.adapters.ClientAdapter;
import pennsylvania.jahepi.com.apppenns.entities.Address;
import pennsylvania.jahepi.com.apppenns.entities.Client;

/**
 * Created by javier.hernandez on 09/03/2016.
 * Class Activity to select the client from the task.
 */
public class ClientActivity extends AuthActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "ClientActivity";

    private ClientAdapter clientAdapter;
    private AddressAdapter addressAdapter;
    private AutoCompleteTextView clientTextView;
    private ListView addressListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_search);
        clientAdapter = new ClientAdapter(getApplicationContext(), R.layout.generic_item);
        addressAdapter = new AddressAdapter(getApplicationContext(), R.layout.generic_item);

        addressListView = (ListView) findViewById(R.id.addressListView);
        addressListView.setAdapter(addressAdapter);

        clientTextView = (AutoCompleteTextView) findViewById(R.id.clientTextView);
        clientTextView.setAdapter(clientAdapter);

        clientTextView.setOnItemClickListener(this);
        addressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent();
                intent.putExtra(CustomApplication.GENERIC_INTENT, addressAdapter.getItem(position));
                setResult(AddTaskActivity.REQUEST_CODE, intent);
                finish();
            }
        });

        Button backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(AddTaskActivity.REQUEST_CODE, getIntent());
                finish();
            }
        });

        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(AddTaskActivity.REQUEST_CODE, getIntent());
                finish();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Client client = clientAdapter.getItem(position);
        ArrayList<Address> addresses = application.getAddresses(client);
        addressAdapter.clear();
        addressAdapter.addAll(addresses);
        addressAdapter.notifyDataSetChanged();
    }
}
