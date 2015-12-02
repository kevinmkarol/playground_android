package com.q2qtheater.kevin.playgroundapplication.Fragments;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.q2qtheater.kevin.playgroundapplication.ContentManager;
import com.q2qtheater.kevin.playgroundapplication.InformationType;
import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.InstallationImage;
import com.q2qtheater.kevin.playgroundapplication.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 *
 * A fragment which displays the installation map image.
 */
public class Installation extends Fragment {
    private ImageView installationImage = null;
    private Context applicationContext = null;

    public Installation(Context context) {
        // Required empty public constructor
        applicationContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_installation, container, false);
        installationImage = (ImageView) view.findViewById(R.id.installationImage);
        Bitmap bitmap = loadImage();
        if(bitmap != null){
            installationImage.setImageBitmap(bitmap);
        }
        installationImage.invalidate();
        return view;
    }

    public Bitmap loadImage(){
        Bitmap bitmap = null;
        ArrayList<InstallationImage> imageWrappers = ContentManager.getProgramInformation(InformationType.INSTALLATION_IMAGES);
        for(InstallationImage image : imageWrappers) {
            if (image.getImageName().equals("fullMap")) {
                bitmap = ContentManager.getImageFromFile(image.getImageName());
                break;
            }
        }
        return  bitmap;
    }

}
