package uk.ac.aston.cs3mdd.outfitapp.ui.clothing;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import uk.ac.aston.cs3mdd.outfitapp.R;

public class AddClothingFragment extends Fragment {
    private Button confirmButton;
    private Button cancelButton;
    private Button chooseImageButton;
    private ImageView imageView;
    private AutoCompleteTextView autoCompleteClothingTypeTextView;
    private AutoCompleteTextView autoCompleteColourTextView;
    private EditText editText;
    private ClothingDbViewModel clothingDbViewModel;
    private String type;
    private String colour;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_clothing, container, false);

        confirmButton = view.findViewById(R.id.confirmButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        imageView = view.findViewById(R.id.imageView);
        chooseImageButton = view.findViewById(R.id.selectImageButton);
        autoCompleteClothingTypeTextView = view.findViewById(R.id.autoCompleteClothingTypeTextView);
        editText = view.findViewById(R.id.editTextBrand);
        autoCompleteColourTextView = view.findViewById(R.id.autoCompleteColourTextView);
        clothingDbViewModel = new ViewModelProvider(requireActivity()).get(ClothingDbViewModel.class);

        String[] typesArray = getResources().getStringArray(R.array.clothing_types);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, typesArray);
        autoCompleteClothingTypeTextView.setAdapter(typeAdapter);

        String[] coloursArray = getResources().getStringArray(R.array.colours);
        ArrayAdapter<String> colourAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, coloursArray);
        autoCompleteColourTextView.setAdapter(colourAdapter);

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToClothingFragment();
            }
        });

        autoCompleteClothingTypeTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                type = (String) parent.getItemAtPosition(position);
            }

        });

        autoCompleteColourTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                colour = (String) parent.getItemAtPosition(position);
            }

        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = imageView.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

                Uri imageUri = getImageUri(requireContext(), bitmap);
                String brand = editText.getText().toString();

                ClothingItem clothingItem = new ClothingItem();
                clothingItem.image = imageUri;
                clothingItem.type = type;
                clothingItem.colour = colour;
                clothingItem.brand = brand;
                clothingItem.tags = null;

                clothingDbViewModel.insertClothingItem(clothingItem);
                navigateToClothingFragment();
            }
        });


        return view;
    }

    private void navigateToClothingFragment() {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_navigation_add_clothing_to_navigation_clothing);
    }

    private ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    new ActivityResultCallback<Uri>() {
                        @Override
                        public void onActivityResult(Uri result) {
                            if (result != null) {
                                // Set the selected image to the ImageView
                                imageView.setImageURI(result);
                            }
                        }
                    });

    private void openImagePicker() {
        imagePickerLauncher.launch("image/*");
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
