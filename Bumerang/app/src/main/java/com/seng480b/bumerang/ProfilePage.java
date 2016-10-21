package com.seng480b.bumerang;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;


public class ProfilePage extends Fragment {

    private String user_id;
    private String name;
    private View myInflatedView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_profile_page, container,false);

        ((Home)getActivity()).setActionBarTitle("Profile");

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),

                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        try {
                            user_id =object.getString("id");
                            Log.d("user id ", user_id);
                            name = object.getString("name");
                            Log.d("user name ", name);
                            TextView t = (TextView) myInflatedView.findViewById(R.id.profileName);
                            t.setText(name);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
        );

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();

        return myInflatedView;
    }


}
