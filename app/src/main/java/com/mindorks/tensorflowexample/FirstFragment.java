package com.mindorks.tensorflowexample;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

public class FirstFragment extends Fragment {

    private static int PERMISSIONS_REQUEST_CODE = 10;
    private static String[] PERMISSIONS_REQUIRED = {Manifest.permission.CAMERA};

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the rec_item for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);

        // If permissions have already been granted, proceed
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPermissions(requireContext())) {
                    // If permissions have already been granted, proceed
                    Navigation.findNavController(requireActivity(), R.id.FirstFragment).navigate(R.id.activity_main);
                }
                // Request camera-related permissions
                requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Takes the user to the success fragment when permission is granted
                Navigation.findNavController(requireActivity(), R.id.FirstFragment).navigate(R.id.activity_main);
            } else {
                Toast.makeText(getContext(), "Permission request denied", Toast.LENGTH_LONG).show();
                NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        }
    }

    /**
     * Convenience method used to check if all permissions required by this app are granted
     *
     * @return
     */
    boolean hasPermissions(Context context) {

        int size = PERMISSIONS_REQUIRED.length;
        int count = 0;
        for (String element : PERMISSIONS_REQUIRED) {
            if (ContextCompat.checkSelfPermission(context, element) == PackageManager.PERMISSION_GRANTED)
                count++;
        }
        return count == size;
    }
}
